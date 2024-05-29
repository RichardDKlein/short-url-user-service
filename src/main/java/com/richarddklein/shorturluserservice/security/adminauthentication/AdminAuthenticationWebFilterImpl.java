/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.security.adminauthentication;

import org.springframework.http.HttpMethod;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

public class AdminAuthenticationWebFilterImpl extends AdminAuthenticationWebFilter {
    public AdminAuthenticationWebFilterImpl(
            AdminAuthenticationManager adminAuthenticationManager,
            AdminAuthenticationConverter adminAuthenticationConverter,
            AdminAuthenticationFailureHandler adminAuthenticationFailureHandler) {

        super(adminAuthenticationManager);

        setServerAuthenticationConverter(adminAuthenticationConverter);
        setAuthenticationFailureHandler(adminAuthenticationFailureHandler);

        setRequiresAuthenticationMatcher(
                ServerWebExchangeMatchers.pathMatchers(
                        HttpMethod.POST,
                        "/dbinit", "/shorturl/users/dbinit"));
    }
}
