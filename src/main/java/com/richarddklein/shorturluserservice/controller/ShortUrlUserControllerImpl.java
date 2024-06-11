/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.controller;

import java.security.Principal;

import com.richarddklein.shorturlcommonlibrary.aws.ParameterStoreReader;
import com.richarddklein.shorturluserservice.controller.response.ShortUrlUserStatus;
import com.richarddklein.shorturluserservice.dto.UsernameAndPassword;
import com.richarddklein.shorturluserservice.entity.ShortUrlUser;
import com.richarddklein.shorturluserservice.controller.response.StatusAndJwtTokenResponse;
import com.richarddklein.shorturluserservice.controller.response.StatusAndShortUrlUserResponse;
import com.richarddklein.shorturluserservice.service.ShortUrlUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;

import com.richarddklein.shorturluserservice.controller.response.StatusResponse;
import reactor.core.publisher.Mono;

/**
 * The production implementation of the Short URL User Controller
 * interface.
 */
@RestController
@RequestMapping({"/shorturl/users", "/"})
public class ShortUrlUserControllerImpl implements ShortUrlUserController {
    private final ParameterStoreReader parameterStoreReader;
    private final ShortUrlUserService shortUrlUserService;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    /**
     * General constructor.
     *
     * @param parameterStoreReader Dependency injection of a class instance
     *                             that is able to read the AWS Parameter
     *                             Store.
     * @param shortUrlUserService Dependency injection of a class instance
     *                            that is to play the role of the Short URL
     *                            User service layer.
     */
    public ShortUrlUserControllerImpl(ParameterStoreReader parameterStoreReader,
                                      ShortUrlUserService shortUrlUserService) {

        this.parameterStoreReader = parameterStoreReader;
        this.shortUrlUserService = shortUrlUserService;
    }

    @Override
    public ResponseEntity<StatusResponse>
    initializeShortUrlUserRepository(ServerHttpRequest request) {
        if (isRunningLocally(request.getRemoteAddress().getHostString())) {
            shortUrlUserService.initializeShortUrlUserRepository();
            StatusResponse response = new StatusResponse(
                    ShortUrlUserStatus.SUCCESS,
                    "Initialization of Short URL User table "
                            + "completed successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            StatusResponse response = new StatusResponse(
                    ShortUrlUserStatus.NOT_ON_LOCAL_MACHINE,
                    "Initialization of the Short URL User "
                            + "table can be done only when the service is "
                            + "running on your local machine");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public Mono<ResponseEntity<StatusResponse>>
    signup(ShortUrlUser shortUrlUser) {
        return shortUrlUserService.signup(shortUrlUser)
        .map(shortUrlUserStatus -> {
            HttpStatus httpStatus;
            StatusResponse statusResponse;
            if (shortUrlUserStatus ==
                    ShortUrlUserStatus.USER_ALREADY_EXISTS) {
                httpStatus = HttpStatus.CONFLICT;
                statusResponse = new StatusResponse(
                        ShortUrlUserStatus.USER_ALREADY_EXISTS,
                        String.format("User '%s' already exists",
                                shortUrlUser.getUsername())
                );
            } else if (shortUrlUserStatus ==
                    ShortUrlUserStatus.MISSING_PASSWORD) {
                httpStatus = HttpStatus.BAD_REQUEST;
                statusResponse = new StatusResponse(
                        ShortUrlUserStatus.MISSING_PASSWORD,
                        "A non-empty password must be specified"
                );
            } else {
                httpStatus = HttpStatus.OK;
                statusResponse = new StatusResponse(
                        ShortUrlUserStatus.SUCCESS,
                        String.format("User '%s' successfully created",
                                shortUrlUser.getUsername())
                );
            }
            return new ResponseEntity<>(statusResponse, httpStatus);
        });
    }

    @Override
    public Mono<ResponseEntity<StatusAndJwtTokenResponse>>
    login(UsernameAndPassword usernameAndPassword) {
        return shortUrlUserService.login(usernameAndPassword)
        .map(statusAndJwtToken -> {
            HttpStatus httpStatus;
            StatusResponse statusResponse;
            ShortUrlUserStatus shortUrlUserStatus =
                    statusAndJwtToken.getStatus();
            String jwtToken = statusAndJwtToken.getJwtToken();

            if (shortUrlUserStatus == ShortUrlUserStatus.NO_SUCH_USER) {
                httpStatus = HttpStatus.UNAUTHORIZED;
                statusResponse = new StatusResponse(
                        ShortUrlUserStatus.NO_SUCH_USER,
                        String.format(
                                "User '%s' does not exist",
                                usernameAndPassword.getUsername())
                );
            } else if (shortUrlUserStatus ==
                    ShortUrlUserStatus.WRONG_PASSWORD) {
                httpStatus = HttpStatus.UNAUTHORIZED;
                statusResponse = new StatusResponse(
                        ShortUrlUserStatus.WRONG_PASSWORD,
                        "The supplied password is not correct"
                );
            } else {
                httpStatus = HttpStatus.OK;
                statusResponse = new StatusResponse(
                        ShortUrlUserStatus.SUCCESS,
                        String.format(
                                "User '%s' successfully logged in",
                                usernameAndPassword.getUsername())
                );
            }
            StatusAndJwtTokenResponse statusAndJwtTokenResponse =
                    new StatusAndJwtTokenResponse(
                            statusResponse, jwtToken);

            return new ResponseEntity<>(
                    statusAndJwtTokenResponse, httpStatus);
        });
    }

    @Override
    public Mono<ResponseEntity<StatusAndShortUrlUserResponse>>
    getUserDetails(Mono<Principal> principalMono) {
        return shortUrlUserService.getUserDetails(principalMono)
        .map(statusAndShortUrlUser -> {
            ShortUrlUserStatus shortUrlUserStatus =
                    statusAndShortUrlUser.getStatus();
            ShortUrlUser shortUrlUser =
                    statusAndShortUrlUser.getShortUrlUser();

            HttpStatus httpStatus;
            StatusResponse statusResponse;

            if (shortUrlUserStatus != ShortUrlUserStatus.SUCCESS) {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                statusResponse = new StatusResponse(
                        shortUrlUserStatus,
                        "Internal server error"
                );
            } else {
                httpStatus = HttpStatus.OK;
                statusResponse = new StatusResponse(
                        ShortUrlUserStatus.SUCCESS,
                        "User details successfully retrieved"
                );
            }
            StatusAndShortUrlUserResponse statusAndShortUrlUserResponse =
                    new StatusAndShortUrlUserResponse(
                            statusResponse, shortUrlUser);

            return new ResponseEntity<>(
                    statusAndShortUrlUserResponse, httpStatus);
        });
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
