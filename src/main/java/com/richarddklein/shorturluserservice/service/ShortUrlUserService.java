/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.service;

import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.*;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.entity.ShortUrlUser;
import com.richarddklein.shorturlcommonlibrary.service.status.ShortUrlStatus;
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
    ShortUrlStatus
    initializeShortUrlUserRepository();

    Mono<StatusAndJwtToken>
    getAdminJwtToken();

    Mono<ShortUrlStatus>
    signup(ShortUrlUser shortUrlUser);

    Mono<StatusAndJwtToken>
    login(UsernameAndPassword usernameAndPassword);

    Mono<StatusAndShortUrlUser>
    getSpecificUser(String username);

    Mono<StatusAndShortUrlUserArray>
    getAllUsers();

    Mono<ShortUrlStatus>
    changePassword(UsernameOldPasswordAndNewPassword
                   usernameOldPasswordAndNewPassword);

    Mono<ShortUrlStatus>
    deleteSpecificUser(String username);

    Mono<ShortUrlStatus>
    deleteAllUsers();

    Mono<ShortUrlStatus>
    simulateExpiredJwtToken(boolean enabled);
}
