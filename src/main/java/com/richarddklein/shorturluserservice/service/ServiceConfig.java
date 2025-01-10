/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.service;

import com.richarddklein.shorturlcommonlibrary.environment.HostUtils;
import com.richarddklein.shorturlcommonlibrary.environment.ParameterStoreAccessor;
import com.richarddklein.shorturlcommonlibrary.security.util.JwtUtils;
import com.richarddklein.shorturluserservice.dao.ShortUrlUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The Service @Configuration class.
 *
 * <p>Tells Spring how to construct instances of classes that are needed
 * to implement the Service package.</p>
 */
@Configuration
public class ServiceConfig {
    @Autowired
    ShortUrlUserDao shortUrlUserDao;

    @Autowired
    ParameterStoreAccessor parameterStoreAccessor;

    @Autowired
    HostUtils hostUtils;

    @Autowired
    JwtUtils jwtUtils;

    @Bean
    public ShortUrlUserService
    shortUrlUserService() {
        return new ShortUrlUserServiceImpl(
                shortUrlUserDao,
                parameterStoreAccessor,
                hostUtils,
                jwtUtils);
    }
}
