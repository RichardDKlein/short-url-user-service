/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.service;

import java.security.Principal;

import com.richarddklein.shorturlcommonlibrary.security.dto.UsernameAndRole;
import com.richarddklein.shorturlcommonlibrary.security.util.JwtUtils;
import com.richarddklein.shorturluserservice.dto.StatusAndJwtToken;
import com.richarddklein.shorturluserservice.dto.StatusAndShortUrlUser;
import com.richarddklein.shorturluserservice.dto.UsernameAndPassword;
import com.richarddklein.shorturluserservice.dto.UsernameOldPasswordAndNewPassword;
import com.richarddklein.shorturluserservice.entity.ShortUrlUser;
import com.richarddklein.shorturluserservice.controller.response.ShortUrlUserStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.richarddklein.shorturluserservice.dao.ShortUrlUserDao;
import reactor.core.publisher.Mono;

/**
 * The production implementation of the Short URL User Service interface.
 */
@Service
public class ShortUrlUserServiceImpl implements ShortUrlUserService {
    private final ShortUrlUserDao shortUrlUserDao;
    private final JwtUtils jwtUtils;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    /**
     * General constructor.
     *
     * @param shortUrlUserDao Dependency injection of a class instance that is
     *                        to play the role of the Short URL User Data Access
     *                        Object (DAO).
     * @param jwtUtils Dependency injection of a class instance that is to able
     *                 to provide utilities to handle JWT authentication tokens.
     */
    public ShortUrlUserServiceImpl(
            ShortUrlUserDao shortUrlUserDao,
            JwtUtils jwtUtils) {

        this.shortUrlUserDao = shortUrlUserDao;
        this.jwtUtils = jwtUtils;
    }

    // Initialization of the Short URL User repository is performed rarely,
    // and then only by the Admin from a local machine. Therefore, we do not
    // need to use reactive (asynchronous) programming techniques here. Simple
    // synchronous logic will work just fine.
    @Override
    public void initializeShortUrlUserRepository() {
        shortUrlUserDao.initializeShortUrlUserRepository();
    }

    @Override
    public Mono<ShortUrlUserStatus>
    signup(ShortUrlUser shortUrlUser) {
        return shortUrlUserDao.signup(shortUrlUser);
    }

    @Override
    public Mono<StatusAndJwtToken>
    login(UsernameAndPassword usernameAndPassword) {
        return shortUrlUserDao.login(usernameAndPassword)
        .map(statusAndRole -> {
            if (statusAndRole.getStatus() !=
                    ShortUrlUserStatus.SUCCESS) {
                return new StatusAndJwtToken(
                        statusAndRole.getStatus(), null);
            }
            String jwtToken = jwtUtils.generateToken(
                    new UsernameAndRole(
                            usernameAndPassword.getUsername(),
                            statusAndRole.getRole()));
            return new StatusAndJwtToken(
                    ShortUrlUserStatus.SUCCESS, jwtToken);
        });
    }

    @Override
    public Mono<StatusAndShortUrlUser>
    getUserDetails(Mono<Principal> principalMono) {
        return principalMono.flatMap(auth -> {
            Authentication authentication = (Authentication)auth;
            String username = authentication.getName();
            return shortUrlUserDao.getShortUrlUser(username)
            .map(shortUrlUser -> {
                ShortUrlUserStatus shortUrlUserStatus;
                if (shortUrlUser == null) {
                    shortUrlUserStatus = ShortUrlUserStatus.NO_SUCH_USER;
                } else {
                    shortUrlUserStatus = ShortUrlUserStatus.SUCCESS;
                    shortUrlUser.setPassword(null);
                }
                return new StatusAndShortUrlUser(
                        shortUrlUserStatus, shortUrlUser);
            });
        });
    }

    @Override
    public Mono<ShortUrlUserStatus>
    changePassword(UsernameOldPasswordAndNewPassword
            usernameOldPasswordAndNewPassword) {

        return shortUrlUserDao.changePassword(
                usernameOldPasswordAndNewPassword);
    }

    @Override
    public Mono<ShortUrlUserStatus>
    deleteUser(UsernameAndPassword usernameAndPassword) {
        return shortUrlUserDao.deleteUser(usernameAndPassword);
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------
}
