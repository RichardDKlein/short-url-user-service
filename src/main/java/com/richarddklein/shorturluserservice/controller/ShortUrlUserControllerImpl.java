/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.controller;

import java.security.Principal;
import java.util.List;

import com.richarddklein.shorturluserservice.controller.response.*;
import com.richarddklein.shorturluserservice.dto.UsernameAndPassword;
import com.richarddklein.shorturluserservice.dto.UsernameOldPasswordAndNewPassword;
import com.richarddklein.shorturluserservice.entity.ShortUrlUser;
import com.richarddklein.shorturluserservice.service.ShortUrlUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

/**
 * The production implementation of the Short URL User Controller
 * interface.
 */
@RestController
@RequestMapping({"/shorturl/users", "/"})
public class ShortUrlUserControllerImpl implements ShortUrlUserController {
    private final ShortUrlUserService shortUrlUserService;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    /**
     * General constructor.
     *
     * @param shortUrlUserService Dependency injection of a class instance
     *                            that is to play the role of the Short URL
     *                            User service layer.
     */
    public ShortUrlUserControllerImpl(
            ShortUrlUserService shortUrlUserService) {

        this.shortUrlUserService = shortUrlUserService;
    }

    @Override
    public ResponseEntity<StatusResponse>
    initializeShortUrlUserRepository(ServerHttpRequest request) {
        ShortUrlUserStatus shortUrlUserStatus = shortUrlUserService
                .initializeShortUrlUserRepository(request);

        HttpStatus httpStatus;
        String message;

        switch (shortUrlUserStatus) {
            case SUCCESS:
                httpStatus = HttpStatus.OK;
                message = "Initialization of Short URL User table "
                        + "completed successfully";
                break;

            case NOT_ON_LOCAL_MACHINE:
                httpStatus = HttpStatus.FORBIDDEN;
                message = "Initialization of the Short URL User "
                        + "table can be done only when the service is "
                        + "running on your local machine";
                break;

            default:
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                message = "An unknown error occurred";
        }

        return new ResponseEntity<>(new StatusResponse(
                shortUrlUserStatus, message), httpStatus);
    }

    @Override
    public Mono<ResponseEntity<StatusAndShortUrlUserArrayResponse>>
    getAllUsers(Mono<Principal> principalMono) {
        return shortUrlUserService.getAllUsers(principalMono)
            .map(statusAndShortUrlUserArray -> {
                ShortUrlUserStatus shortUrlUserStatus = statusAndShortUrlUserArray.getStatus();
                List<ShortUrlUser> users = statusAndShortUrlUserArray.getShortUrlUsers();

                HttpStatus httpStatus;
                String message;

                switch (shortUrlUserStatus) {
                    case SUCCESS:
                        httpStatus = HttpStatus.OK;
                        message = "All users successfully retrieved";
                        break;

                    case MUST_BE_ADMIN:
                        httpStatus = HttpStatus.UNAUTHORIZED;
                        message = "Must be an admin to perform this operation";
                        break;

                    default:
                        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                        message = "An unknown error occurred";
                        break;
                }

                return new ResponseEntity<>(new StatusAndShortUrlUserArrayResponse(
                        new StatusResponse(shortUrlUserStatus, message), users),
                        httpStatus);
            });
    }

    @Override
    public Mono<ResponseEntity<StatusResponse>>
    signup(ShortUrlUser shortUrlUser) {
        return shortUrlUserService.signup(shortUrlUser)
        .map(shortUrlUserStatus -> {
            HttpStatus httpStatus;
            String message;

            switch (shortUrlUserStatus) {
                case SUCCESS:
                    httpStatus = HttpStatus.OK;
                    message = String.format(
                            "User '%s' successfully created",
                            shortUrlUser.getUsername());
                    break;

                case MISSING_PASSWORD:
                    httpStatus = HttpStatus.BAD_REQUEST;
                    message = "A non-empty password must be specified";
                    break;

                case MISSING_USERNAME:
                    httpStatus = HttpStatus.BAD_REQUEST;
                    message = "A non-empty username must be specified";
                    break;

                case USER_ALREADY_EXISTS:
                    httpStatus = HttpStatus.CONFLICT;
                    message = String.format(
                            "User '%s' already exists",
                            shortUrlUser.getUsername());
                    break;

                default:
                    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    message = "An unknown error occurred";
                    break;
            }

            return new ResponseEntity<>(new StatusResponse(
                    shortUrlUserStatus, message), httpStatus);
        });
    }

    @Override
    public Mono<ResponseEntity<StatusAndJwtTokenResponse>>
    login(UsernameAndPassword usernameAndPassword) {
        return shortUrlUserService.login(usernameAndPassword)
        .map(statusAndJwtToken -> {
            ShortUrlUserStatus shortUrlUserStatus =
                    statusAndJwtToken.getStatus();
            String jwtToken = statusAndJwtToken.getJwtToken();

            HttpStatus httpStatus;
            String message;

            switch (shortUrlUserStatus) {
                case SUCCESS:
                    httpStatus = HttpStatus.OK;
                    message = String.format(
                            "User '%s' successfully logged in",
                            usernameAndPassword.getUsername());
                    break;

                case MISSING_PASSWORD:
                    httpStatus = HttpStatus.BAD_REQUEST;
                    message = "A non-empty password must be specified";
                    break;

                case MISSING_USERNAME:
                    httpStatus = HttpStatus.BAD_REQUEST;
                    message = "A non-empty username must be specified";
                    break;

                case NO_SUCH_USER:
                    httpStatus = HttpStatus.UNAUTHORIZED;
                    message = String.format(
                            "User '%s' does not exist",
                            usernameAndPassword.getUsername());
                    break;

                case WRONG_PASSWORD:
                    httpStatus = HttpStatus.UNAUTHORIZED;
                    message = "The specified password is not correct";
                    break;

                default:
                    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    message = "An unknown error occurred";
                    break;
            }

            return new ResponseEntity<>(
                    new StatusAndJwtTokenResponse(new StatusResponse(
                            shortUrlUserStatus, message), jwtToken),
                    httpStatus);
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
            String message;

            if (shortUrlUserStatus == ShortUrlUserStatus.SUCCESS) {
                httpStatus = HttpStatus.OK;
                message = "User details successfully retrieved";
            } else {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                message = "An unknown error occurred";
            }

            return new ResponseEntity<>(new StatusAndShortUrlUserResponse(
                    new StatusResponse(shortUrlUserStatus, message),
                    shortUrlUser), httpStatus);
        });
    }

    @Override
    public Mono<ResponseEntity<StatusResponse>>
    changePassword(
            Mono<Principal> principalMono,
            UsernameOldPasswordAndNewPassword
                    usernameOldPasswordAndNewPassword) {

        return shortUrlUserService.changePassword(
                principalMono,
                usernameOldPasswordAndNewPassword)

        .map(shortUrlUserStatus -> {
            String username =
                    usernameOldPasswordAndNewPassword.getUsername();

            HttpStatus httpStatus;
            String message;

            switch (shortUrlUserStatus) {
                case SUCCESS:
                    httpStatus = HttpStatus.OK;
                    message = String.format(
                            "Password successfully changed for user '%s'",
                            username);
                    break;

                case MISSING_NEW_PASSWORD:
                    httpStatus = HttpStatus.BAD_REQUEST;
                    message = "A non-empty new password must be specified";
                    break;

                case MISSING_OLD_PASSWORD:
                    httpStatus = HttpStatus.BAD_REQUEST;
                    message = "The old password must be specified";
                    break;

                case MISSING_USERNAME:
                    httpStatus = HttpStatus.BAD_REQUEST;
                    message = "A non-empty username must be specified";
                    break;

                case NO_SUCH_USER:
                    httpStatus = HttpStatus.UNAUTHORIZED;
                    message = String.format(
                            "User '%s' does not exist", username);
                    break;

                case USER_CONFIRMATION_MISMATCH:
                    httpStatus = HttpStatus.UNAUTHORIZED;
                    message = String.format(
                            "User '%s' doesn't match the user in the auth token",
                        username);
                    break;

                case WRONG_PASSWORD:
                    httpStatus = HttpStatus.UNAUTHORIZED;
                    message = "The specified password is not correct";
                    break;

                default:
                    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    message = "An unknown error occurred";
                    break;
            };

            return new ResponseEntity<>(
                    new StatusResponse(shortUrlUserStatus, message),
                    httpStatus);
        });
    }

    @Override
    public Mono<ResponseEntity<StatusResponse>>
    deleteUser(
            Mono<Principal> principalMono,
            UsernameAndPassword usernameAndPassword) {

        return shortUrlUserService.deleteUser(
                principalMono, usernameAndPassword)

        .map(shortUrlUserStatus -> {
            String username = usernameAndPassword.getUsername();

            HttpStatus httpStatus;
            String message;

            switch (shortUrlUserStatus) {
                case SUCCESS:
                    httpStatus = HttpStatus.OK;
                    message = String.format(
                            "User '%s' successfully deleted",
                            username);
                    break;

                case MISSING_PASSWORD:
                    httpStatus = HttpStatus.BAD_REQUEST;
                    message = "A non-empty password must be specified";
                    break;

                case MISSING_USERNAME:
                    httpStatus = HttpStatus.BAD_REQUEST;
                    message = "A non-empty username must be specified";
                    break;

                case NO_SUCH_USER:
                    httpStatus = HttpStatus.UNAUTHORIZED;
                    message = String.format(
                            "User '%s' does not exist", username);
                    break;

                case USER_CONFIRMATION_MISMATCH:
                    httpStatus = HttpStatus.UNAUTHORIZED;
                    message = String.format(
                            "User '%s' doesn't match the user in the auth token",
                            username);
                    break;

                case WRONG_PASSWORD:
                    httpStatus = HttpStatus.UNAUTHORIZED;
                    message = "The specified password is not correct";
                    break;

                default:
                    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    message = "An unknown error occurred";
                    break;
            };

            return new ResponseEntity<>(
                    new StatusResponse(shortUrlUserStatus, message),
                    httpStatus);
        });
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------
}
