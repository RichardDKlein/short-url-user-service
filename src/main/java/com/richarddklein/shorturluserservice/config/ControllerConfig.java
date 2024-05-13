/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.richarddklein.shorturluserservice.controller.ShortUrlUserController;
import com.richarddklein.shorturluserservice.controller.ShortUrlUserControllerImpl;

/**
 * The Controller @Configuration class.
 *
 * <p>Tells Spring how to construct instances of classes that are needed
 * to implement the Controller package.</p>
 */
@Configuration
public class ControllerConfig {
    @Autowired
    ServiceConfig serviceConfig;

    @Bean
    public ShortUrlUserController
    shortUrlReservationController() {
        return new ShortUrlUserControllerImpl(serviceConfig.shortUrlUserService());
    }
}
