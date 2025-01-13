/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.controller;

import java.util.Objects;

import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.dto.*;
import com.richarddklein.shorturlcommonlibrary.service.shorturluserservice.entity.ShortUrlUser;
import com.richarddklein.shorturlcommonlibrary.service.status.ShortUrlStatus;
import com.richarddklein.shorturlcommonlibrary.service.status.Status;
import com.richarddklein.shorturluserservice.service.ShortUrlUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

import static com.richarddklein.shorturlcommonlibrary.service.status.ShortUrlStatus.SUCCESS;

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
    initializeShortUrlUserRepository() {
        ShortUrlStatus shortUrlUserStatus =
                shortUrlUserService.initializeShortUrlUserRepository();

        HttpStatus httpStatus;
        String message;

        switch (shortUrlUserStatus) {
            case SUCCESS -> {
                httpStatus = HttpStatus.OK;
                message = "Initialization of Short URL User table "
                        + "completed successfully";
            }
            case NOT_ON_LOCAL_MACHINE -> {
                httpStatus = HttpStatus.FORBIDDEN;
                message = "Initialization of the Short URL User "
                        + "table can be done only when the service is "
                        + "running on your local machine";
            }
            default -> {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                message = "An unknown error occurred";
            }
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
                ShortUrlStatus shortUrlUserStatus =
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
    public Mono<ResponseEntity<Status>>
    signup(ShortUrlUser shortUrlUser) {
        return shortUrlUserService.signup(shortUrlUser)
            .map(shortUrlUserStatus -> {
                HttpStatus httpStatus;
                String message;

                switch (shortUrlUserStatus) {
                    case SUCCESS -> {
                        httpStatus = HttpStatus.OK;
                        message = String.format(
                                "User '%s' successfully created",
                                shortUrlUser.getUsername());
                    }
                    case MISSING_PASSWORD -> {
                        httpStatus = HttpStatus.BAD_REQUEST;
                        message = "A non-empty password must be specified";
                    }
                    case MISSING_USERNAME -> {
                        httpStatus = HttpStatus.BAD_REQUEST;
                        message = "A non-empty username must be specified";
                    }
                    case USER_ALREADY_EXISTS -> {
                        httpStatus = HttpStatus.CONFLICT;
                        message = String.format(
                                "User '%s' already exists",
                                shortUrlUser.getUsername());
                    }
                    default -> {
                        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                        message = "An unknown error occurred";
                    }
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
                ShortUrlStatus shortUrlUserStatus =
                        statusAndJwtToken.getStatus().getStatus();

                HttpStatus httpStatus;
                String message;

                switch (shortUrlUserStatus) {
                    case SUCCESS -> {
                        httpStatus = HttpStatus.OK;
                        message = String.format(
                                "User '%s' successfully logged in",
                                usernameAndPassword.getUsername());
                    }
                    case MISSING_PASSWORD -> {
                        httpStatus = HttpStatus.BAD_REQUEST;
                        message = "A non-empty password must be specified";
                    }
                    case MISSING_USERNAME -> {
                        httpStatus = HttpStatus.BAD_REQUEST;
                        message = "A non-empty username must be specified";
                    }
                    case NO_SUCH_USER -> {
                        httpStatus = HttpStatus.UNAUTHORIZED;
                        message = String.format(
                                "User '%s' does not exist",
                                usernameAndPassword.getUsername());
                    }
                    case WRONG_PASSWORD -> {
                        httpStatus = HttpStatus.UNAUTHORIZED;
                        message = "The specified password is not correct";
                    }
                    default -> {
                        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                        message = "An unknown error occurred";
                    }
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
                ShortUrlStatus shortUrlUserStatus =
                        statusAndShortUrlUser.getStatus().getStatus();

                HttpStatus httpStatus;
                String message;

                switch (shortUrlUserStatus) {
                    case SUCCESS -> {
                        httpStatus = HttpStatus.OK;
                        message = "User successfully retrieved";
                    }
                    case NO_SUCH_USER -> {
                        httpStatus = HttpStatus.NOT_FOUND;
                        message = String.format("User '%s' does not exist",
                                username);
                    }
                    default -> {
                        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                        message = "An unknown error occurred";
                    }
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
                ShortUrlStatus shortUrlUserStatus =
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
    changePassword(UsernameOldPasswordAndNewPassword
                   usernameOldPasswordAndNewPassword) {

        return shortUrlUserService.changePassword(usernameOldPasswordAndNewPassword)
            .map(shortUrlUserStatus -> {
                String username = usernameOldPasswordAndNewPassword.getUsername();

                HttpStatus httpStatus;
                String message;

                switch (shortUrlUserStatus) {
                    case SUCCESS -> {
                        httpStatus = HttpStatus.OK;
                        message = String.format(
                                "Password successfully changed for user '%s'",
                                username);
                    }
                    case MISSING_NEW_PASSWORD -> {
                        httpStatus = HttpStatus.BAD_REQUEST;
                        message = "A non-empty new password must be specified";
                    }
                    case MISSING_OLD_PASSWORD -> {
                        httpStatus = HttpStatus.BAD_REQUEST;
                        message = "The old password must be specified";
                    }
                    case MISSING_USERNAME -> {
                        httpStatus = HttpStatus.BAD_REQUEST;
                        message = "A non-empty username must be specified";
                    }
                    case NO_SUCH_USER -> {
                        httpStatus = HttpStatus.UNAUTHORIZED;
                        message = String.format(
                                "User '%s' does not exist", username);
                    }
                    case WRONG_PASSWORD -> {
                        httpStatus = HttpStatus.UNAUTHORIZED;
                        message = "The specified password is not correct";
                    }
                    default -> {
                        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                        message = "An unknown error occurred";
                    }
                }

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
                    case SUCCESS -> {
                        httpStatus = HttpStatus.OK;
                        message = String.format(
                                "User '%s' successfully deleted",
                                username);
                    }
                    case NO_SUCH_USER -> {
                        httpStatus = HttpStatus.NOT_FOUND;
                        message = String.format(
                                "User '%s' does not exist",
                                username);
                    }
                    default -> {
                        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                        message = "An unknown error occurred";
                    }
                }
                ;

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

    @Override
    public Mono<ResponseEntity<Status>>
    simulateExpiredJwtToken(@PathVariable String enabled) {
        return shortUrlUserService.simulateExpiredJwtToken(Boolean.parseBoolean(enabled))
                .map(shortUrlUserStatus -> {
                    HttpStatus httpStatus;
                    String message;

                    if (Objects.requireNonNull(shortUrlUserStatus) == SUCCESS) {
                        httpStatus = HttpStatus.NO_CONTENT;
                        message = String.format(
                            "Simulated JWT token expiration successfully %s",
                                Boolean.parseBoolean(enabled) ? "enabled" : "disabled");
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
