/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.security.jwttokenauthentication;

import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

public class JwtTokenAuthenticationManagerImpl implements JwtTokenAuthenticationManager {
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        System.out.println("====> Entering JwtTokenAuthenticationManagerImpl ...");
        return Mono.just(authentication);
    }
}
