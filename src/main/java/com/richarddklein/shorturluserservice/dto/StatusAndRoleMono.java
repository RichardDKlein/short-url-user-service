/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.dto;

import com.richarddklein.shorturluserservice.controller.response.ShortUrlUserStatus;
import reactor.core.publisher.Mono;

public class StatusAndRoleMono {
    private Mono<ShortUrlUserStatus> statusMono;
    private Mono<String> roleMono;

    @SuppressWarnings("unused")
    public StatusAndRoleMono(
            Mono<ShortUrlUserStatus> statusMono,
            Mono<String> roleMono) {

        this.statusMono = statusMono;
        this.roleMono = roleMono;
    }

    public Mono<ShortUrlUserStatus> getStatusMono() {
        return statusMono;
    }

    public void setStatusMono(Mono<ShortUrlUserStatus> statusMono) {
        this.statusMono = statusMono;
    }

    public Mono<String> getRoleMono() {
        return roleMono;
    }

    public void setRoleMono(Mono<String> roleMono) {
        this.roleMono = roleMono;
    }
}
