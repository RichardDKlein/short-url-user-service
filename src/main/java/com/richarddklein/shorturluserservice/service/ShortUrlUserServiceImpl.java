/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.service;

import com.richarddklein.shorturlcommonlibrary.environment.HostUtils;
import com.richarddklein.shorturlcommonlibrary.environment.ParameterStoreAccessor;
import com.richarddklein.shorturlcommonlibrary.security.dto.UsernameAndRole;
import com.richarddklein.shorturlcommonlibrary.security.util.JwtUtils;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.*;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.entity.ShortUrlUser;
import com.richarddklein.shorturlcommonlibrary.service.status.ShortUrlStatus;
import com.richarddklein.shorturlcommonlibrary.service.status.Status;
import org.springframework.stereotype.Service;

import com.richarddklein.shorturluserservice.dao.ShortUrlUserDao;
import reactor.core.publisher.Mono;

import static com.richarddklein.shorturlcommonlibrary.service.status.ShortUrlStatus.*;

/**
 * The production implementation of the Short URL User Service interface.
 */
@Service
public class ShortUrlUserServiceImpl implements ShortUrlUserService {
    private final ShortUrlUserDao shortUrlUserDao;
    private final ParameterStoreAccessor parameterStoreAccessor;
    private final HostUtils hostUtils;
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
     * @param jwtUtils        Dependency injection of a class instance that is able
     *                        to provide utilities to handle JWT authentication tokens.
     */
    public ShortUrlUserServiceImpl(
            ShortUrlUserDao shortUrlUserDao,
            ParameterStoreAccessor parameterStoreAccessor,
            HostUtils hostUtils,
            JwtUtils jwtUtils) {

        this.shortUrlUserDao = shortUrlUserDao;
        this.parameterStoreAccessor = parameterStoreAccessor;
        this.hostUtils = hostUtils;
        this.jwtUtils = jwtUtils;
    }

    // Initialization of the Short URL User repository is performed rarely,
    // and then only by the Admin from a local machine. Therefore, we do not
    // need to use reactive (asynchronous) programming techniques here. Simple
    // synchronous logic will work just fine.
    @Override
    public ShortUrlStatus
    initializeShortUrlUserRepository() {
        if (!hostUtils.isRunningLocally()) {
            return NOT_ON_LOCAL_MACHINE;
        }
        shortUrlUserDao.initializeShortUrlUserRepository();
        return SUCCESS;
    }

    @Override
    public Mono<StatusAndJwtToken> getAdminJwtToken() {
        return parameterStoreAccessor.getAdminUsername()
            .flatMap(adminUsername -> jwtUtils.generateToken(
                new UsernameAndRole(adminUsername, "ADMIN"))
                .map(jwtToken -> new StatusAndJwtToken(
                    new Status(SUCCESS), jwtToken)));
    }

    @Override
    public Mono<ShortUrlStatus>
    signup(ShortUrlUser shortUrlUser) {
        String username = shortUrlUser.getUsername();
        String password = shortUrlUser.getPassword();

        if (username == null || username.isBlank()) {
            return Mono.just(MISSING_USERNAME);
        }

        if (password == null || password.isBlank()) {
            return Mono.just(MISSING_PASSWORD);
        }
        return shortUrlUserDao.signup(shortUrlUser);
    }

    @Override
    public Mono<StatusAndJwtToken>
    login(UsernameAndPassword usernameAndPassword) {
        String username = usernameAndPassword.getUsername();
        String password = usernameAndPassword.getPassword();

        if (username == null || username.isBlank()) {
            return Mono.just(new StatusAndJwtToken(
                    new Status(MISSING_USERNAME), null));
        }

        if (password == null || password.isBlank()) {
            return Mono.just(new StatusAndJwtToken(
                    new Status(MISSING_PASSWORD), null));
        }

        return shortUrlUserDao.login(usernameAndPassword).flatMap(statusAndRole -> {
            if (statusAndRole.getStatus().getStatus() != SUCCESS) {
                return Mono.just(new StatusAndJwtToken(
                        statusAndRole.getStatus(), null));
            }

            return jwtUtils.generateToken(new UsernameAndRole(
                    usernameAndPassword.getUsername(), statusAndRole.getRole()))
                .map(jwtToken -> new StatusAndJwtToken(
                        new Status(SUCCESS), jwtToken));
        });
    }

    @Override
    public Mono<StatusAndShortUrlUser>
    getSpecificUser(String username) {
        return shortUrlUserDao.getSpecificUser(username)
            .map(shortUrlUser -> {
                shortUrlUser.setPassword(null);
                return new StatusAndShortUrlUser(
                        new Status(SUCCESS), shortUrlUser);
            })
            .onErrorResume(e -> Mono.just(new StatusAndShortUrlUser(
                    new Status(NO_SUCH_USER), null)));
    }

    @Override
    public Mono<StatusAndShortUrlUserArray>
    getAllUsers() {
        return shortUrlUserDao.getAllUsers();
    }

    @Override
    public Mono<ShortUrlStatus>
    changePassword(UsernameOldPasswordAndNewPassword
                   usernameOldPasswordAndNewPassword) {

        String username = usernameOldPasswordAndNewPassword.getUsername();
        String oldPassword = usernameOldPasswordAndNewPassword.getOldPassword();
        String newPassword = usernameOldPasswordAndNewPassword.getNewPassword();

        if (username == null || username.isBlank()) {
            return Mono.just(MISSING_USERNAME);
        }

        if (oldPassword == null || oldPassword.isBlank()) {
            return Mono.just(MISSING_OLD_PASSWORD);
        }

        if (newPassword == null || newPassword.isBlank()) {
            return Mono.just(MISSING_NEW_PASSWORD);
        }
        return shortUrlUserDao.changePassword(usernameOldPasswordAndNewPassword);
    }

    @Override
    public Mono<ShortUrlStatus>
    deleteSpecificUser(String username) {
        return shortUrlUserDao.deleteSpecificUser(username);
    }

    @Override
    public Mono<ShortUrlStatus>
    deleteAllUsers() {
        return shortUrlUserDao.deleteAllUsers();
    }

    @Override
    public Mono<ShortUrlStatus> simulateExpiredJwtToken(boolean enabled) {
        jwtUtils.setShouldSimulateExpiredToken(enabled);
        return Mono.just(SUCCESS);
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------
}
