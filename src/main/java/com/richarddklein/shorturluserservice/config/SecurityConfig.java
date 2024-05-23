/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.config;

import com.richarddklein.shorturluserservice.dao.ParameterStoreReader;
import com.richarddklein.shorturluserservice.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    @Bean
    public PasswordEncoder
    passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
