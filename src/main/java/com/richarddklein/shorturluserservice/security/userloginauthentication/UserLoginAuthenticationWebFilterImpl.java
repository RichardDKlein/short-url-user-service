/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.security.userloginauthentication;

import com.richarddklein.shorturluserservice.security.adminauthentication.AdminAuthenticationConverter;
import com.richarddklein.shorturluserservice.security.adminauthentication.AdminAuthenticationFailureHandler;
import com.richarddklein.shorturluserservice.security.adminauthentication.AdminAuthenticationManager;
import com.richarddklein.shorturluserservice.security.adminauthentication.AdminAuthenticationWebFilter;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

public class UserLoginAuthenticationWebFilterImpl extends UserLoginAuthenticationWebFilter {
    public UserLoginAuthenticationWebFilterImpl(
            UserLoginAuthenticationManager userLoginAuthenticationManager,
            UserLoginAuthenticationConverter userLoginAuthenticationConverter,
            UserLoginAuthenticationFailureHandler userLoginAuthenticationFailureHandler) {

        super(userLoginAuthenticationManager);

        setServerAuthenticationConverter(userLoginAuthenticationConverter);
        setAuthenticationFailureHandler(userLoginAuthenticationFailureHandler);

        setRequiresAuthenticationMatcher(
                ServerWebExchangeMatchers.pathMatchers(
                        HttpMethod.POST,
                        "/login", "/shorturl/users/login"));
    }
}
