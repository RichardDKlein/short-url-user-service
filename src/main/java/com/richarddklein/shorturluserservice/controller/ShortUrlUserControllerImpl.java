/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.controller;

import java.util.Objects;

import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.ShortUrlUserStatus;
import com.richarddklein.shorturluserservice.dto.*;
import com.richarddklein.shorturluserservice.entity.ShortUrlUser;
import com.richarddklein.shorturluserservice.service.ShortUrlUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

import static com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.ShortUrlUserStatus.SUCCESS;

/**
 * The production implementation of the Short URL User Controller
 * interface.
 */
@RestController
@RequestMapping({"/short-url/users", "/"})
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
    public ResponseEntity<Status>
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

        return new ResponseEntity<>(
                new Status(shortUrlUserStatus, message),
                httpStatus);
    }

    @Override
    public Mono<ResponseEntity<StatusAndJwtToken>>
    getAdminJwtToken() {
        return shortUrlUserService.getAdminJwtToken()
        .map(statusAndJwtToken -> {

            ShortUrlUserStatus shortUrlUserStatus =
                    statusAndJwtToken.getStatus().getStatus();

            HttpStatus httpStatus;
            String message;

            if (Objects.requireNonNull(shortUrlUserStatus) == SUCCESS) {
                httpStatus = HttpStatus.OK;
                message = "Admin JWT token successfully generated";
            } else {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                message = "An unknown error occurred";
            }
            statusAndJwtToken.getStatus().setMessage(message);

            return new ResponseEntity<>(statusAndJwtToken, httpStatus);
        });
    }

    @Override
    public Mono<ResponseEntity<StatusAndShortUrlUser>>
    getSpecificUser(@PathVariable String username) {
        return shortUrlUserService.getSpecificUser(username)
        .map(statusAndShortUrlUser -> {

            ShortUrlUserStatus shortUrlUserStatus =
                    statusAndShortUrlUser.getStatus().getStatus();

            HttpStatus httpStatus;
            String message;

            switch (shortUrlUserStatus) {
                case SUCCESS:
                    httpStatus = HttpStatus.OK;
                    message = "User successfully retrieved";
                    break;
                case NO_SUCH_USER:
                    httpStatus = HttpStatus.NOT_FOUND;
                    message = String.format("User '%s' does not exist",
                            username);
                    break;
                default:
                    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    message = "An unknown error occurred";
                    break;
            }
            statusAndShortUrlUser.getStatus().setMessage(message);

            return new ResponseEntity<>(statusAndShortUrlUser, httpStatus);
        });
    }

    @Override
    public Mono<ResponseEntity<StatusAndShortUrlUserArray>>
    getAllUsers() {
        return shortUrlUserService.getAllUsers()
        .map(statusAndShortUrlUserArray -> {

            ShortUrlUserStatus shortUrlUserStatus =
                    statusAndShortUrlUserArray.getStatus().getStatus();

            HttpStatus httpStatus;
            String message;

            if (Objects.requireNonNull(shortUrlUserStatus) == SUCCESS) {
                httpStatus = HttpStatus.OK;
                message = "All users successfully retrieved";
            } else {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                message = "An unknown error occurred";
            }
            statusAndShortUrlUserArray.getStatus().setMessage(message);

            return new ResponseEntity<>(statusAndShortUrlUserArray, httpStatus);
        });
    }

    @Override
    public Mono<ResponseEntity<Status>>
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

            return new ResponseEntity<>(
                    new Status(shortUrlUserStatus, message),
                    httpStatus);
        });
    }

    @Override
    public Mono<ResponseEntity<StatusAndJwtToken>>
    login(UsernameAndPassword usernameAndPassword) {
        return shortUrlUserService.login(usernameAndPassword)
        .map(statusAndJwtToken -> {

            ShortUrlUserStatus shortUrlUserStatus =
                    statusAndJwtToken.getStatus().getStatus();

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
            statusAndJwtToken.getStatus().setMessage(message);

            return new ResponseEntity<>(statusAndJwtToken, httpStatus);
        });
    }

    @Override
    public Mono<ResponseEntity<Status>>
    changePassword(UsernameOldPasswordAndNewPassword
                   usernameOldPasswordAndNewPassword) {

        return shortUrlUserService.changePassword(
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
                    new Status(shortUrlUserStatus, message),
                    httpStatus);
        });
    }

    @Override
    public Mono<ResponseEntity<Status>>
    deleteSpecificUser(@PathVariable String username) {
        return shortUrlUserService.deleteSpecificUser(username)
        .map(shortUrlUserStatus -> {

            HttpStatus httpStatus;
            String message;

            switch (shortUrlUserStatus) {
                case SUCCESS:
                    httpStatus = HttpStatus.OK;
                    message = String.format(
                            "User '%s' successfully deleted",
                            username);
                    break;

                case NO_SUCH_USER:
                    httpStatus = HttpStatus.NOT_FOUND;
                    message = String.format(
                            "User '%s' does not exist",
                            username);
                    break;

                default:
                    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    message = "An unknown error occurred";
                    break;
            };

            return new ResponseEntity<>(
                    new Status(shortUrlUserStatus, message),
                    httpStatus);
        });
    }

    @Override
    public Mono<ResponseEntity<Status>>
    deleteAllUsers() {
        return shortUrlUserService.deleteAllUsers()
        .map(shortUrlUserStatus -> {

            HttpStatus httpStatus;
            String message;

            if (Objects.requireNonNull(shortUrlUserStatus) == SUCCESS) {
                httpStatus = HttpStatus.OK;
                message = "All users successfully deleted";
            } else {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                message = "An unknown error occurred";
            }

            return new ResponseEntity<>(
                    new Status(shortUrlUserStatus, message),
                    httpStatus);
        });
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------
}
