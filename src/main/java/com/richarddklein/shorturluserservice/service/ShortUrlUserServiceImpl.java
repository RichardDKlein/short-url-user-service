/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.service;

import java.util.Objects;

import com.richarddklein.shorturlcommonlibrary.aws.ParameterStoreAccessor;
import com.richarddklein.shorturlcommonlibrary.security.dto.UsernameAndRole;
import com.richarddklein.shorturlcommonlibrary.security.util.JwtUtils;
import com.richarddklein.shorturlcommonlibrary.status.ShortUrlUserStatus;
import com.richarddklein.shorturluserservice.dto.*;
import com.richarddklein.shorturluserservice.entity.ShortUrlUser;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import com.richarddklein.shorturluserservice.dao.ShortUrlUserDao;
import reactor.core.publisher.Mono;

/**
 * The production implementation of the Short URL User Service interface.
 */
@Service
public class ShortUrlUserServiceImpl implements ShortUrlUserService {
    private final ShortUrlUserDao shortUrlUserDao;
    private final ParameterStoreAccessor parameterStoreAccessor;
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
     * @param jwtUtils        Dependency injection of a class instance that is to able
     *                        to provide utilities to handle JWT authentication tokens.
     */
    public ShortUrlUserServiceImpl(
            ShortUrlUserDao shortUrlUserDao,
            ParameterStoreAccessor parameterStoreAccessor,
            JwtUtils jwtUtils) {

        this.shortUrlUserDao = shortUrlUserDao;
        this.parameterStoreAccessor = parameterStoreAccessor;
        this.jwtUtils = jwtUtils;
    }

    // Initialization of the Short URL User repository is performed rarely,
    // and then only by the Admin from a local machine. Therefore, we do not
    // need to use reactive (asynchronous) programming techniques here. Simple
    // synchronous logic will work just fine.
    @Override
    public ShortUrlUserStatus
    initializeShortUrlUserRepository(ServerHttpRequest request) {
        if (!isRunningLocally(Objects.requireNonNull(
                request.getRemoteAddress()).getHostString())) {

            return ShortUrlUserStatus.NOT_ON_LOCAL_MACHINE;
        }

        shortUrlUserDao.initializeShortUrlUserRepository();
        return ShortUrlUserStatus.SUCCESS;
    }

    @Override
    public Mono<StatusAndJwtToken> getAdminJwtToken() {
        return parameterStoreAccessor.getAdminUsername()
                .flatMap(adminUsername -> jwtUtils.generateToken(
                        new UsernameAndRole(adminUsername, "ADMIN"))
                        .map(jwtToken -> new StatusAndJwtToken(
                                new Status(ShortUrlUserStatus.SUCCESS), jwtToken)));
    }

    @Override
    public Mono<StatusAndShortUrlUser>
    getSpecificUser(String username) {
        return shortUrlUserDao.getSpecificUser(username)
                .map(shortUrlUser -> {
                    shortUrlUser.setPassword(null);
                    return new StatusAndShortUrlUser(
                            new Status(ShortUrlUserStatus.SUCCESS), shortUrlUser);
                })
                .onErrorResume(e -> Mono.just(new StatusAndShortUrlUser(
                        new Status(ShortUrlUserStatus.NO_SUCH_USER), null)));
    }

    @Override
    public Mono<StatusAndShortUrlUserArray>
    getAllUsers() {
        return shortUrlUserDao.getAllUsers();
    }

    @Override
    public Mono<ShortUrlUserStatus>
    signup(ShortUrlUser shortUrlUser) {
        String username = shortUrlUser.getUsername();
        String password = shortUrlUser.getPassword();

        if (username == null || username.isBlank()) {
            return Mono.just(ShortUrlUserStatus.MISSING_USERNAME);
        }

        if (password == null || password.isBlank()) {
            return Mono.just(ShortUrlUserStatus.MISSING_PASSWORD);
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
                new Status(ShortUrlUserStatus.MISSING_USERNAME), null));
        }

        if (password == null || password.isBlank()) {
            return Mono.just(new StatusAndJwtToken(
                new Status(ShortUrlUserStatus.MISSING_PASSWORD), null));
        }

        return shortUrlUserDao.login(usernameAndPassword).flatMap(statusAndRole -> {
            if (statusAndRole.getStatus().getStatus() != ShortUrlUserStatus.SUCCESS) {
                return Mono.just(new StatusAndJwtToken(
                        statusAndRole.getStatus(), null));
            }

            return jwtUtils.generateToken(new UsernameAndRole(
                usernameAndPassword.getUsername(), statusAndRole.getRole()))
                    .map(jwtToken -> new StatusAndJwtToken(
                            new Status(ShortUrlUserStatus.SUCCESS), jwtToken));
        });
    }

    @Override
    public Mono<ShortUrlUserStatus>
    changePassword(UsernameOldPasswordAndNewPassword
                   usernameOldPasswordAndNewPassword) {

        String username = usernameOldPasswordAndNewPassword.getUsername();
        String oldPassword = usernameOldPasswordAndNewPassword.getOldPassword();
        String newPassword = usernameOldPasswordAndNewPassword.getNewPassword();

        if (username == null || username.isBlank()) {
            return Mono.just(ShortUrlUserStatus.MISSING_USERNAME);
        }

        if (oldPassword == null || oldPassword.isBlank()) {
            return Mono.just(ShortUrlUserStatus.MISSING_OLD_PASSWORD);
        }

        if (newPassword == null || newPassword.isBlank()) {
            return Mono.just(ShortUrlUserStatus.MISSING_NEW_PASSWORD);
        }
        return shortUrlUserDao.changePassword(usernameOldPasswordAndNewPassword);
    }

    @Override
    public Mono<ShortUrlUserStatus>
    deleteSpecificUser(String username) {
        return shortUrlUserDao.deleteSpecificUser(username);
    }

    @Override
    public Mono<ShortUrlUserStatus>
    deleteAllUsers() {
        return shortUrlUserDao.deleteAllUsers();
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------

    /**
     * Is the service running locally?
     *
     * <p>Determine whether the Short URL User Service is running on your local
     * machine, or in the AWS cloud.</p>
     *
     * @param hostString The host that sent the HTTP request.
     * @return 'true' if the service is running locally, 'false' otherwise.
     */
    private boolean isRunningLocally(String hostString) {
        return hostString.contains("localhost");
    }
}
