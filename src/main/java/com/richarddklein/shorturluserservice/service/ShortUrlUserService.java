/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.service;

import com.richarddklein.shorturluserservice.dto.*;
import com.richarddklein.shorturluserservice.entity.ShortUrlUser;
import com.richarddklein.shorturluserservice.dto.ShortUrlUserStatus;
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

    Mono<StatusAndJwtToken>
    getAdminJwtToken();

    Mono<StatusAndShortUrlUser>
    getSpecificUser(String username);

    Mono<StatusAndShortUrlUserArray>
    getAllUsers();

    Mono<ShortUrlUserStatus>
    signup(ShortUrlUser shortUrlUser);

    Mono<StatusAndJwtToken>
    login(UsernameAndPassword usernameAndPassword);

    Mono<ShortUrlUserStatus>
    changePassword(UsernameOldPasswordAndNewPassword
                   usernameOldPasswordAndNewPassword);

    Mono<ShortUrlUserStatus>
    deleteSpecificUser(String username);

    Mono<ShortUrlUserStatus>
    deleteAllUsers();
}
