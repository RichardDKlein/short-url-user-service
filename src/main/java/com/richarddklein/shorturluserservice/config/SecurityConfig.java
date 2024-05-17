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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

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
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)

                // Authorize all endpoints unconditionally.
                .authorizeExchange(authorize -> authorize.anyExchange().permitAll())

                .build();
    }

    @Bean
    public AuthenticationWebFilter
    adminAuthenticationWebFilter() {
        AuthenticationWebFilter filter =
                new AuthenticationWebFilter(adminAuthenticationManager());

        filter.setServerAuthenticationConverter(adminAuthenticationConverter());
        filter.setAuthenticationFailureHandler(adminAuthenticationFailureHandler());
        filter.setRequiresAuthenticationMatcher(
                ServerWebExchangeMatchers.pathMatchers(
                        HttpMethod.POST,
                        "/dbinit", "/shorturl/users/dbinit"));

        return filter;
    }

    @Bean
    public ReactiveAuthenticationManager
    adminAuthenticationManager() {
        return authentication -> {
            System.out.println("====> Entering ReactiveAuthenticationManager ...");
            if (authentication.getPrincipal().equals(parameterStoreReader.getAdminUsername()) &&
                    authentication.getCredentials().equals(parameterStoreReader.getAdminPassword())) {
                return Mono.just(authentication);
            } else {
                return Mono.error(new BadCredentialsException("Invalid username or password"));
            }
        };
    }

    @Bean
    public ServerAuthenticationConverter
    adminAuthenticationConverter() {
        return exchange -> {
            System.out.println("====> Entering ServerAuthenticationConverter ...");
            String authorizationHeader =
                    exchange.getRequest().getHeaders().getFirst("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
                return Mono.error(
                        new MissingAuthorizationHeaderException("Missing authorization header"));
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
    adminAuthenticationFailureHandler() {
        return (exchange, exception) -> {
            System.out.println("====> Entering ServerAuthenticationFailureHandler ...");
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
