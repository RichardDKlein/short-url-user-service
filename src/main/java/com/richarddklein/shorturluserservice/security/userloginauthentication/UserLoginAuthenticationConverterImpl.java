/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.security.userloginauthentication;

import java.nio.charset.StandardCharsets;

import com.richarddklein.shorturluserservice.entity.ShortUrlUser;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UserLoginAuthenticationConverterImpl implements UserLoginAuthenticationConverter {
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        System.out.println("====> Entering UserLoginAuthenticationConverterImpl ...");

//        ServerHttpRequest request = exchange.getRequest();
//        Mono<DataBuffer> body = DataBufferUtils.join(request.getBody());

        return DataBufferUtils.join(exchange.getRequest().getBody()).flatMap(dataBuffer -> {
            try {
                // Read the request body
                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(bytes);
                // Release the buffer
                DataBufferUtils.release(dataBuffer);
                // Parse the request body to ShortUrlUser
                String body = new String(bytes, StandardCharsets.UTF_8);
                ObjectMapper objectMapper = new ObjectMapper();
                ShortUrlUser shortUrlUser = objectMapper.readValue(body, ShortUrlUser.class);
                String username = shortUrlUser.getUsername();
                String plaintextPassword = shortUrlUser.getPassword();
                // Return the Authentication object
                return Mono.just(new UsernamePasswordAuthenticationToken(username, plaintextPassword));
            } catch (Exception e) {
                System.out.println("====> Exception: " + e.getMessage());
                return Mono.error(e);
            }
        });
    }
}
