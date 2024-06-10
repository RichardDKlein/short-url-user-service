/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.service;

import java.security.Principal;

import com.richarddklein.shorturlcommonlibrary.security.util.JwtUtils;
import com.richarddklein.shorturluserservice.dto.StatusAndJwtTokenMono;
import com.richarddklein.shorturluserservice.dto.StatusAndRoleMono;
import com.richarddklein.shorturluserservice.dto.StatusAndShortUrlUserMono;
import com.richarddklein.shorturluserservice.dto.UsernameAndPasswordMono;
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
    signup(Mono<ShortUrlUser> shortUrlUserMono) {
        return shortUrlUserDao.signup(shortUrlUserMono);
    }

    @Override
    public Mono<StatusAndJwtTokenMono>
    login(Mono<UsernameAndPasswordMono> usernameAndPasswordDtoMono) {
        Mono<StatusAndRoleMono> statusAndRoleDtoMono =
                shortUrlUserDao.login(usernameAndPasswordDtoMono);
        return statusAndRoleDtoMono.map(statusAndRoleMono -> {
            if (statusAndRoleMono.getStatus() != ShortUrlUserStatus.SUCCESS) {
                return new StatusAndJwtTokenMono(statusAndRoleMono.getStatus(), null);
            }
            Mono<String> usernameMono =
                    usernameAndPasswordDtoMono.map(UsernameAndPasswordMono::getUsername);
            Mono<String> roleMono = Mono.just(statusAndRoleMono.getRole());
            Mono<String> jwtTokenMono = jwtUtils.generateToken(usernameMono, roleMono);
            return new StatusAndJwtTokenMono(ShortUrlUserStatus.SUCCESS, jwtTokenMono);
        });
    }

    @Override
    public Mono<StatusAndShortUrlUserMono>
    getUserDetails(Mono<Principal> principalMono) {
        return principalMono.map(auth -> {
            Authentication authentication = (Authentication)auth;
            String username = authentication.getName();
            ShortUrlUser shortUrlUser = shortUrlUserDao.getUserDetails(username);
            ShortUrlUserStatus shortUrlUserStatus;
            if (shortUrlUser == null) {
                shortUrlUserStatus = ShortUrlUserStatus.NO_SUCH_USER;
            } else {
                shortUrlUserStatus = ShortUrlUserStatus.SUCCESS;
                shortUrlUser.setPassword(null);
            }
            return new StatusAndShortUrlUserMono(ShortUrlUserStatus.SUCCESS, shortUrlUser);
        });
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------
}
