/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.dto;

import reactor.core.publisher.Mono;

@SuppressWarnings("unused")
public class UsernameAndPassword {
    private Mono<String> usernameMono;
    private Mono<String> passwordMono;

    public UsernameAndPassword(
            Mono<String> usernameMono,
            Mono<String> passwordMono) {

        this.usernameMono = usernameMono;
        this.passwordMono = passwordMono;
    }

    public Mono<String> getUsernameMono() {
        return usernameMono;
    }

    public void setUsernameMono(Mono<String> usernameMono) {
        this.usernameMono = usernameMono;
    }

    public Mono<String> getPasswordMono() {
        return passwordMono;
    }

    public void setPasswordMono(Mono<String> passwordMono) {
        this.passwordMono = passwordMono;
    }
}
