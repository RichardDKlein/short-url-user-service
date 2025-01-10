/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.controller;

import com.richarddklein.shorturluserservice.service.ShortUrlUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The Controller @Configuration class.
 *
 * <p>Tells Spring how to construct instances of classes that are needed
 * to implement the Controller package.</p>
 */
@Configuration
public class ControllerConfig {
    @Autowired
    ShortUrlUserService shortUrlUserService;

    @Bean
    public ShortUrlUserController
    shortUrlReservationController() {
        return new ShortUrlUserControllerImpl(shortUrlUserService);
    }
}
