/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.controller;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.richarddklein.shorturluserservice.dao.ParameterStoreReader;
import com.richarddklein.shorturluserservice.response.ShortUrlUserStatus;
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
    initializeShortUrlUserRepository(
//            @RequestHeader("Authorization") String authorizationHeader,
            ServerHttpRequest request) {

        System.out.println("====> Entering initializeShortUrlUserRepository()...");
//        ShortUrlUserStatus status = validateAdminCredentials(authorizationHeader);
//        if (status == ShortUrlUserStatus.MISSING_AUTHORIZATION_HEADER) {
//            StatusResponse response = new StatusResponse(
//                    ShortUrlUserStatus.MISSING_AUTHORIZATION_HEADER,
//                    "The request does not contain a Basic "
//                            + "Authorization header");
//            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
//        } else if (status == ShortUrlUserStatus.INVALID_ADMIN_CREDENTIALS) {
//            StatusResponse response = new StatusResponse(
//                    ShortUrlUserStatus.INVALID_ADMIN_CREDENTIALS,
//                    "The Basic Authorization header in the request "
//                            + "does not contain valid Admin credentials");
//            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
//        }

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

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------

    /**
     * Validate that a Basic Authorization header is present in the HTTP Request,
     * and that it contains valid Admin credentials.
     *
     * @param authorizationHeader The Authorization header contained in the HTTP
     *                            Request.
     * @return A status code indicating whether the Authorization header is present
     * and contains valid Admin credentials.
     */
    private ShortUrlUserStatus validateAdminCredentials(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
            return ShortUrlUserStatus.MISSING_AUTHORIZATION_HEADER;
        }

        String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();
        byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(decodedBytes, StandardCharsets.UTF_8);

        String[] values = credentials.split(":", 2);
        String adminUsername = values[0];
        String adminPassword = values[1];

        if (!adminUsername.equals(parameterStoreReader.getAdminUsername())
                || !adminPassword.equals(parameterStoreReader.getAdminPassword())) {
            return ShortUrlUserStatus.INVALID_ADMIN_CREDENTIALS;
        }
        return ShortUrlUserStatus.SUCCESS;
    }

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
