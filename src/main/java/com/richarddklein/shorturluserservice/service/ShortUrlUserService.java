/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.service;

import java.security.Principal;

import com.richarddklein.shorturluserservice.dto.StatusAndJwtToken;
import com.richarddklein.shorturluserservice.dto.StatusAndShortUrlUser;
import com.richarddklein.shorturluserservice.dto.UsernameAndPassword;
import com.richarddklein.shorturluserservice.entity.ShortUrlUser;
import com.richarddklein.shorturluserservice.controller.response.ShortUrlUserStatus;
import reactor.core.publisher.Mono;

/**
 * The Short URL User Service interface.
 *
 * <p>Specifies the methods that must be implemented by any class that
 * provides service-layer functionality to the Short URL User Service.</p>
 */
public interface ShortUrlUserService {
    /**
     * Initialize the Short URL User repository.
     *
     * <p>This is a synchronous method. It will return only when the
     * initialization has completed successfully, or has failed.</p>
     */
    void initializeShortUrlUserRepository();

    Mono<ShortUrlUserStatus>
    signup(Mono<ShortUrlUser> shortUrlUserMono);

    Mono<StatusAndJwtToken>
    login(Mono<UsernameAndPassword> usernameAndPasswordDtoMono);

    Mono<StatusAndShortUrlUser>
    getUserDetails(Mono<Principal> principalMono);
}
