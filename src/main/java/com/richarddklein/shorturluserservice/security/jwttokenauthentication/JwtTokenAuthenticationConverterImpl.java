/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.security.jwttokenauthentication;

import java.util.Collections;
import java.util.List;

import com.richarddklein.shorturluserservice.exception.InvalidJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import io.jsonwebtoken.Claims;

import com.richarddklein.shorturluserservice.exception.MissingAuthorizationHeaderException;
import com.richarddklein.shorturluserservice.security.util.JwtUtils;

public class JwtTokenAuthenticationConverterImpl implements JwtTokenAuthenticationConverter {
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        System.out.println("====> Entering JwtTokenAuthenticationConverterImpl ...");

        String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return Mono.error(new MissingAuthorizationHeaderException("Missing authorization header"));
        }

        String jwtToken = authorizationHeader.substring("Bearer ".length()).trim();

        try {
            Claims claims = jwtUtils.getClaimsFromToken(jwtToken);
            System.out.printf("====> claims = %s\n", claims);
            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            List<SimpleGrantedAuthority> authorities =
                    Collections.singletonList(new SimpleGrantedAuthority(role));
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            return Mono.just(authenticationToken);
        } catch (Exception e) {
            System.out.println("====> " + e.getMessage());
            int indexOfColon = e.getMessage().indexOf(':');
            String errorMsg = (indexOfColon < 0) ?
                    e.getMessage() : e.getMessage().substring(0, indexOfColon);
            return Mono.error(new InvalidJwtException(errorMsg));
        }
    }
}
