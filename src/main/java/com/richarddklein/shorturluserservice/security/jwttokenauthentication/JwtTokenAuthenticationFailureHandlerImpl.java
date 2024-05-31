/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.security.jwttokenauthentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richarddklein.shorturluserservice.exception.InvalidJwtException;
import com.richarddklein.shorturluserservice.exception.MissingAuthorizationHeaderException;
import com.richarddklein.shorturluserservice.response.ShortUrlUserStatus;
import com.richarddklein.shorturluserservice.response.StatusResponse;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import reactor.core.publisher.Mono;

public class JwtTokenAuthenticationFailureHandlerImpl implements JwtTokenAuthenticationFailureHandler {
    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange,
                                              AuthenticationException exception) {

        System.out.println("====> Entering JwtTokenAuthenticationFailureHandler ...");
        ShortUrlUserStatus status = null;
        String message = null;

        if (exception instanceof MissingAuthorizationHeaderException) {
            status = ShortUrlUserStatus.MISSING_AUTHORIZATION_HEADER;
            message = "The request does not contain a Basic Authorization header";
        } else if (exception instanceof InvalidJwtException) {
            status = ShortUrlUserStatus.EXPIRED_JWT_TOKEN;
            message = "The provided JWT token has expired";
        }

        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        StatusResponse errorResponse = new StatusResponse(status, message);
        byte[] responseBytes;
        try {
            responseBytes = new ObjectMapper().writeValueAsBytes(errorResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        DataBuffer buffer = response.bufferFactory().wrap(responseBytes);

        return response.writeWith(Mono.just(buffer))
                .doOnError(error -> DataBufferUtils.release(buffer));
    }
}
