/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.security.jwttokenauthentication;

import org.springframework.http.HttpMethod;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

public class JwtTokenAuthenticationWebFilterImpl extends JwtTokenAuthenticationWebFilter {
    public JwtTokenAuthenticationWebFilterImpl(
            JwtTokenAuthenticationManager jwtTokenAuthenticationManager,
            JwtTokenAuthenticationConverter jwtTokenAuthenticationConverter,
            JwtTokenAuthenticationFailureHandler jwtTokenAuthenticationFailureHandler) {

        super(jwtTokenAuthenticationManager);

        setServerAuthenticationConverter(jwtTokenAuthenticationConverter);
        setAuthenticationFailureHandler(jwtTokenAuthenticationFailureHandler);

        setRequiresAuthenticationMatcher(
                ServerWebExchangeMatchers.pathMatchers(
                        HttpMethod.GET,
                        "/validate", "/shorturl/users/validate"));
    }
}
