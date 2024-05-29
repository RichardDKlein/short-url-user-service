/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.security.userloginauthentication;

import com.richarddklein.shorturluserservice.entity.ShortUrlUser;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UserLoginAuthenticationConverterImpl implements UserLoginAuthenticationConverter {
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        System.out.println("====> Entering UserLoginAuthenticationConverterImpl ...");
        return exchange.getRequest().getBody().next().flatMap(dataBuffer -> {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(bytes);
                DataBufferUtils.release(dataBuffer);
                ShortUrlUser shortUrlUser = objectMapper.readValue(bytes, ShortUrlUser.class);
                String username = shortUrlUser.getUsername();
                String plaintextPassword = shortUrlUser.getPassword();
                return Mono.just(new UsernamePasswordAuthenticationToken(username, plaintextPassword));
            } catch (Exception e) {
                return Mono.error(e);
            }
        });
    }
}
