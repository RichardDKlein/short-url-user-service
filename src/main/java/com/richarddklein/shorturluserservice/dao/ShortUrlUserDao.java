/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.dao;

import com.richarddklein.shorturluserservice.dto.StatusAndRole;
import com.richarddklein.shorturluserservice.dto.StatusAndShortUrlUserArray;
import com.richarddklein.shorturluserservice.dto.UsernameAndPassword;
import com.richarddklein.shorturluserservice.dto.UsernameOldPasswordAndNewPassword;
import com.richarddklein.shorturluserservice.entity.ShortUrlUser;
import com.richarddklein.shorturluserservice.controller.response.ShortUrlUserStatus;
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

    Mono<StatusAndShortUrlUserArray>
    getAllUsers();

    Mono<ShortUrlUserStatus>
    signup(ShortUrlUser shortUrlUser);

    Mono<StatusAndRole>
    login(UsernameAndPassword usernameAndPassword);

    Mono<ShortUrlUser>
    getShortUrlUser(String username);

    Mono<ShortUrlUserStatus>
    changePassword(
            UsernameOldPasswordAndNewPassword
            usernameOldPasswordAndNewPassword);

    Mono<ShortUrlUserStatus>
    deleteUser(
            UsernameAndPassword usernameAndPassword,
            String role);
}
