/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richarddklein.shorturluserservice.dao.ParameterStoreReader;
import com.richarddklein.shorturluserservice.exception.MissingAuthorizationHeaderException;
import com.richarddklein.shorturluserservice.response.ShortUrlUserStatus;
import com.richarddklein.shorturluserservice.response.StatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.security.web.server.authorization.AuthorizationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;

/**
 * The Security @Configuration class.
 *
 * <p>Tells Spring how to construct instances of classes that are needed
 * to implement security (using Spring Security).</p>
 */
@Order(1)
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Autowired
    ParameterStoreReader parameterStoreReader;

    @Bean
    public SecurityWebFilterChain
    securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // Disable default security.
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)

                // Add custom security.
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)

                // Enable security only for database initialization endpoint.
                .authorizeExchange(authorize -> authorize
                        .pathMatchers("/dbinit", "/shorturl/users/dbinit")
                        .access(reactiveAuthorizationManager())
                        .anyExchange().permitAll())

                .build();
    }

    @Bean
    public AuthenticationWebFilter
    authenticationWebFilter() {
        AuthenticationWebFilter filter =
                new AuthenticationWebFilter(reactiveAuthenticationManager());

        filter.setServerAuthenticationConverter(serverAuthenticationConverter());
        filter.setAuthenticationFailureHandler(serverAuthenticationFailureHandler());
        filter.setRequiresAuthenticationMatcher(
                ServerWebExchangeMatchers.pathMatchers("/dbinit"));

        return filter;
    }

    @Bean
    public ReactiveAuthorizationManager<AuthorizationContext>
    reactiveAuthorizationManager() {
        return (authentication, object) ->
            authentication.map(auth -> new AuthorizationDecision(true));
    }

    @Bean
    public ReactiveAuthenticationManager
    reactiveAuthenticationManager() {
        return authentication -> {
            System.out.println("====> Entering ReactiveAuthenticationManager...");
            if (authentication == null) {
                return Mono.error(
                        new MissingAuthorizationHeaderException("Missing authorization header"));
            }
            UsernamePasswordAuthenticationToken authToken =
                    (UsernamePasswordAuthenticationToken)authentication;

            if (authToken.getPrincipal().equals(parameterStoreReader.getAdminUsername()) &&
                    authToken.getCredentials().equals(parameterStoreReader.getAdminPassword())) {
                return Mono.empty();
            } else {
                return Mono.error(new BadCredentialsException("Invalid username or password"));
            }
        };
    }

    @Bean
    public ServerAuthenticationConverter
    serverAuthenticationConverter() {
        return exchange -> {
            System.out.println("====> Entering ServerAuthenticationConverter...");
            String authorizationHeader =
                    exchange.getRequest().getHeaders().getFirst("Authorization");
            System.out.printf("====> authorizationHeader = %s\n", authorizationHeader);
            if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
                return Mono.empty();
            }
            String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();
            byte[] decodedBytes = java.util.Base64.getDecoder().decode(base64Credentials);
            String[] credentials = new String(decodedBytes).split(":", 2);
            String username = credentials[0];
            String password = credentials[1];
            return Mono.just(new UsernamePasswordAuthenticationToken(username, password));
        };
    }

    @Bean
    public ServerAuthenticationFailureHandler
    serverAuthenticationFailureHandler() {
        return (exchange, exception) -> {
            ShortUrlUserStatus status = null;
            String message = null;

            if (exception instanceof MissingAuthorizationHeaderException) {
                status = ShortUrlUserStatus.MISSING_AUTHORIZATION_HEADER;
                message = "The request does not contain a Basic Authorization header";
            } else if (exception instanceof BadCredentialsException) {
                status = ShortUrlUserStatus.INVALID_ADMIN_CREDENTIALS;
                message = "The Authorization header does not contain valid Admin credentials";
            }

            ServerHttpResponse response = exchange.getExchange().getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            StatusResponse errorResponse = new StatusResponse(status, message);
            byte[] responseBytes;
            try {
                responseBytes = new ObjectMapper().writeValueAsBytes(errorResponse);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            DataBuffer buffer = response.bufferFactory().wrap(responseBytes);

            return response.writeWith(Mono.just(buffer))
                    .doOnError(error -> DataBufferUtils.release(buffer));
        };
    }

    @Bean
    public BCryptPasswordEncoder
    passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
