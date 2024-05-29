/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.security.adminauthentication;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

public class AdminAuthenticationWebFilter extends AuthenticationWebFilter {
    public AdminAuthenticationWebFilter(ReactiveAuthenticationManager authenticationManager) {
        super(authenticationManager);
    }
}
