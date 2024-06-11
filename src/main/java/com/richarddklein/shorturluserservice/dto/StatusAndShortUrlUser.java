/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.dto;

import com.richarddklein.shorturluserservice.controller.response.ShortUrlUserStatus;
import com.richarddklein.shorturluserservice.entity.ShortUrlUser;
import reactor.core.publisher.Mono;

@SuppressWarnings("unused")
public class StatusAndShortUrlUser {
    private Mono<ShortUrlUserStatus> statusMono;
    private Mono<ShortUrlUser> shortUrlUserMono;

    public StatusAndShortUrlUser(
            Mono<ShortUrlUserStatus> statusMono,
            Mono<ShortUrlUser> shortUrlUserMono) {

        this.statusMono = statusMono;
        this.shortUrlUserMono = shortUrlUserMono;
    }

    public Mono<ShortUrlUserStatus> getStatusMono() {
        return statusMono;
    }

    public void setStatusMono(Mono<ShortUrlUserStatus> statusMono) {
        this.statusMono = statusMono;
    }

    public Mono<ShortUrlUser> getShortUrlUserMono() {
        return shortUrlUserMono;
    }

    public void setShortUrlUserMono(Mono<ShortUrlUser> shortUrlUserMono) {
        this.shortUrlUserMono = shortUrlUserMono;
    }
}
