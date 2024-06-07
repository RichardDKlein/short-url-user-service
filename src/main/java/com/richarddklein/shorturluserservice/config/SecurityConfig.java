/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.config;

import com.richarddklein.shorturlcommonlibrary.aws.ParameterStoreReader;
import com.richarddklein.shorturluserservice.security.adminauthentication.*;
import com.richarddklein.shorturluserservice.security.jwttokenauthentication.*;
import com.richarddklein.shorturluserservice.security.util.JwtUtils;
import com.richarddklein.shorturluserservice.security.util.JwtUtilsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

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

    // ------------------------------------------------------------------------
    // ADMIN AUTHENTICATION WEB FILTER
    // ------------------------------------------------------------------------

    @Bean
    public AdminAuthenticationWebFilter
    adminAuthenticationWebFilter() {
        return new AdminAuthenticationWebFilterImpl(
                adminAuthenticationManager(),
                adminAuthenticationConverter(),
                adminAuthenticationFailureHandler());
    }

    @Bean
    public AdminAuthenticationManager
    adminAuthenticationManager() {
        return new AdminAuthenticationManagerImpl();
    }

    @Bean
    public AdminAuthenticationConverter
    adminAuthenticationConverter() {
        return new AdminAuthenticationConverterImpl();
    }

    @Bean
    public AdminAuthenticationFailureHandler
    adminAuthenticationFailureHandler() {
        return new AdminAuthenticationFailureHandlerImpl();
    }

    // ------------------------------------------------------------------------
    // JWT TOKEN AUTHENTICATION WEB FILTER
    // ------------------------------------------------------------------------

    @Bean
    public JwtTokenAuthenticationWebFilter
    jwtTokenAuthenticationWebFilter() {
        return new JwtTokenAuthenticationWebFilterImpl(
                jwtTokenAuthenticationManager(),
                jwtTokenAuthenticationConverter(),
                jwtTokenAuthenticationFailureHandler());
    }

    @Bean
    @Primary
    public JwtTokenAuthenticationManager
    jwtTokenAuthenticationManager() {
        return new JwtTokenAuthenticationManagerImpl();
    }

    @Bean
    public JwtTokenAuthenticationConverter
    jwtTokenAuthenticationConverter() {
        return new JwtTokenAuthenticationConverterImpl();
    }

    @Bean
    public JwtTokenAuthenticationFailureHandler
    jwtTokenAuthenticationFailureHandler() {
        return new JwtTokenAuthenticationFailureHandlerImpl();
    }

    // ------------------------------------------------------------------------
    // SECURITY UTILITIES
    // ------------------------------------------------------------------------

    @Bean
    public PasswordEncoder
    passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtUtils
    jwtUtils() {
        return new JwtUtilsImpl(parameterStoreReader);
    }
}
