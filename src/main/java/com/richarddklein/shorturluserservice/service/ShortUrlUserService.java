/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.service;

import java.security.Principal;

import com.richarddklein.shorturluserservice.dto.*;
import com.richarddklein.shorturluserservice.entity.ShortUrlUser;
import com.richarddklein.shorturluserservice.controller.response.ShortUrlUserStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
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
    ShortUrlUserStatus
    initializeShortUrlUserRepository(ServerHttpRequest request);

    Mono<StatusAndShortUrlUserArray>
    getAllUsers(Mono<Principal> principalMono);

    Mono<ShortUrlUserStatus>
    signup(ShortUrlUser shortUrlUser);

    Mono<StatusAndJwtToken>
    login(UsernameAndPassword usernameAndPassword);

    Mono<StatusAndShortUrlUser>
    getUserDetails(Mono<Principal> principalMono);

    Mono<ShortUrlUserStatus>
    changePassword(
            Mono<Principal> principalMono,
            UsernameOldPasswordAndNewPassword
                    usernameOldPasswordAndNewPassword);

    Mono<ShortUrlUserStatus>
    deleteUser(
            Mono<Principal> principalMono,
            UsernameAndPassword usernameAndPassword);
}
