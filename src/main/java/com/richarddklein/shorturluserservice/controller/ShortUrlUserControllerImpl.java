/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.controller;

import com.richarddklein.shorturluserservice.dao.ParameterStoreReader;
import com.richarddklein.shorturluserservice.entity.ShortUrlUser;
import com.richarddklein.shorturluserservice.response.ShortUrlUserStatus;
import com.richarddklein.shorturluserservice.response.StatusAndJwtTokenResponse;
import com.richarddklein.shorturluserservice.service.ShortUrlUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;

import com.richarddklein.shorturluserservice.response.StatusResponse;

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
    public ResponseEntity<StatusAndJwtTokenResponse>
    signup(ShortUrlUser shortUrlUser) {
        Object[] statusAndJwtToken = shortUrlUserService.signup(shortUrlUser);

        HttpStatus httpStatus;
        StatusResponse statusResponse;
        ShortUrlUserStatus shortUrlUserStatus =
                (ShortUrlUserStatus)statusAndJwtToken[0];
        String jwtToken = (String)statusAndJwtToken[1];

        if (shortUrlUserStatus == ShortUrlUserStatus.USER_ALREADY_EXISTS) {
            httpStatus = HttpStatus.CONFLICT;
            statusResponse = new StatusResponse(
                    ShortUrlUserStatus.USER_ALREADY_EXISTS,
                    String.format("User already exists")
            );
        } else if (shortUrlUserStatus ==
                ShortUrlUserStatus.MISSING_PASSWORD) {
            httpStatus = HttpStatus.BAD_REQUEST;
            statusResponse = new StatusResponse(
                    ShortUrlUserStatus.MISSING_PASSWORD,
                    String.format("A password must be specified")
            );
        } else {
            httpStatus = HttpStatus.OK;
            statusResponse = new StatusResponse(
                    ShortUrlUserStatus.SUCCESS,
                    "User account successfully created"
            );
        }

        StatusAndJwtTokenResponse statusAndJwtTokenResponse =
                new StatusAndJwtTokenResponse(statusResponse, jwtToken);

        return new ResponseEntity<>(statusAndJwtTokenResponse, httpStatus);
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------

    /**
     * Is the service running locally?
     *
     * Determine whether the Short URL User Service is running on your local
     * machine, or in the AWS cloud.
     *
     * @param hostString The host that sent the HTTP request.
     * @return 'true' if the service is running locally, 'false' otherwise.
     */
    private boolean isRunningLocally(String hostString) {
        return hostString.contains("localhost");
    }
}
