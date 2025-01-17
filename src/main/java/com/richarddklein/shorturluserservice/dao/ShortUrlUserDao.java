/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.dao;

import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.*;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.entity.ShortUrlUser;
import com.richarddklein.shorturlcommonlibrary.service.status.ShortUrlStatus;
import reactor.core.publisher.Mono;

/**
 * The Short URL User DAO (Data Access Object) interface.
 *
 * <p>Specifies the methods that must be implemented by any class that
 * provides access to the data repository of the Short URL User Service.</p>
 */

public interface ShortUrlUserDao {
    /**
     * Initialize the Short URL User repository.
     *
     * <p>Delete any existing Short URL User table from the repository,
     * and create a new one, containing a single item for the admin.</p>
     */
    void initializeShortUrlUserRepository();

    Mono<ShortUrlStatus>
    signup(ShortUrlUser shortUrlUser);

    Mono<StatusAndRole>
    login(UsernameAndPassword usernameAndPassword);

    Mono<ShortUrlUser>
    getSpecificUser(String username);

    Mono<StatusAndShortUrlUserArray>
    getAllUsers();

    Mono<ShortUrlStatus>
    changePassword(
            UsernameOldPasswordAndNewPassword
            usernameOldPasswordAndNewPassword);

    Mono<ShortUrlStatus>
    deleteSpecificUser(String username);

    Mono<ShortUrlStatus>
    deleteAllUsers();
}
