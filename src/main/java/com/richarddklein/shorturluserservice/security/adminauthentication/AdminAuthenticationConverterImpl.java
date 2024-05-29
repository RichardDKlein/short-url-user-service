/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.security.adminauthentication;

import com.richarddklein.shorturluserservice.exception.MissingAuthorizationHeaderException;
import com.richarddklein.shorturluserservice.security.adminauthentication.AdminAuthenticationConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class AdminAuthenticationConverterImpl implements AdminAuthenticationConverter {
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        System.out.println("====> Entering AdminAuthenticationConverterImpl ...");
        String authorizationHeader =
                exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
            return Mono.error(
                    new MissingAuthorizationHeaderException("Missing authorization header"));
        }
        String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();
        byte[] decodedBytes = java.util.Base64.getDecoder().decode(base64Credentials);
        String[] credentials = new String(decodedBytes).split(":", 2);
        String username = credentials[0];
        String password = credentials[1];
        return Mono.just(new UsernamePasswordAuthenticationToken(username, password));
    }
}
