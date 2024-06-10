/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.dto;

import com.richarddklein.shorturluserservice.controller.response.ShortUrlUserStatus;
import reactor.core.publisher.Mono;

@SuppressWarnings("unused")
public class StatusAndJwtTokenMono {
    private Mono<ShortUrlUserStatus> statusMono;
    private Mono<String> jwtTokenMono;

    public StatusAndJwtTokenMono(
            Mono<ShortUrlUserStatus> statusMono,
            Mono<String> jwtTokenMono) {

        this.statusMono = statusMono;
        this.jwtTokenMono = jwtTokenMono;
    }

    public Mono<ShortUrlUserStatus> getStatusMono() {
        return statusMono;
    }

    public void setStatusMono(Mono<ShortUrlUserStatus> statusMono) {
        this.statusMono = statusMono;
    }

    public Mono<String> getJwtTokenMono() {
        return jwtTokenMono;
    }

    public void setJwtTokenMono(Mono<String> jwtTokenMono) {
        this.jwtTokenMono = jwtTokenMono;
    }
}
