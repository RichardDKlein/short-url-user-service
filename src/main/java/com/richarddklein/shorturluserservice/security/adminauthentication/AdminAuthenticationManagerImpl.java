/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.security.adminauthentication;

import com.richarddklein.shorturlcommonlibrary.aws.ParameterStoreReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

public class AdminAuthenticationManagerImpl implements AdminAuthenticationManager {
    @Autowired
    ParameterStoreReader parameterStoreReader;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        System.out.println("====> Entering AdminAuthenticationManagerImpl ...");
        if (authentication.getPrincipal().equals(parameterStoreReader.getAdminUsername()) &&
                authentication.getCredentials().equals(parameterStoreReader.getAdminPassword())) {
            return Mono.just(authentication);
        } else {
            return Mono.error(new BadCredentialsException("Invalid username or password"));
        }
    }
}
