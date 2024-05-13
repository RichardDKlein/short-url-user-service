/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * The Security @Configuration class.
 *
 * <p>Tells Spring how to construct instances of classes that are needed
 * to implement security.</p>
 */
@Configuration
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder
    passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
