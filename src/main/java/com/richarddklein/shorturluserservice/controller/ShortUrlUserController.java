/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.controller;

import com.richarddklein.shorturluserservice.dto.*;
import com.richarddklein.shorturluserservice.entity.ShortUrlUser;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

/**
 * The Short URL User Controller interface.
 *
 * <p>Specifies the REST API endpoints for the Short URL User
 * Service.</p>
 */
@SuppressWarnings("unused")
public interface ShortUrlUserController {
    /**
     * Initialize the Short URL User repository.
     *
     * <p>This is a synchronous operation. It will return a response
     * to the client only when the database initialization has
     * completed successfully, or has failed.</p>
     *
     * <p>Because database initialization is a long-running operation
     * that exceeds the AWS API Gateway maximum response timeout of
     * 30 seconds, this REST endpoint is available only when the Short
     * URL User Service is running on localhost, not on AWS.</p>
     *
     * @param request The HTTP Request.
     * @return An HTTP Response Entity containing the status (success
     * or failure) of the database initialization operation.
     */
    @PostMapping("/initialize-repository")
    ResponseEntity<Status>
    initializeShortUrlUserRepository(ServerHttpRequest request);

    @GetMapping("/admin-jwt")
    Mono<ResponseEntity<StatusAndJwtToken>>
    getAdminJwtToken();

    @GetMapping("/specific/{username}")
    Mono<ResponseEntity<StatusAndShortUrlUser>>
    getSpecificUser(@PathVariable String username);

    @GetMapping("/all")
    Mono<ResponseEntity<StatusAndShortUrlUserArray>>
    getAllUsers();

    @PostMapping("/signup")
    Mono<ResponseEntity<Status>>
    signup(@RequestBody ShortUrlUser shortUrlUser);

    @PostMapping("/login")
    Mono<ResponseEntity<StatusAndJwtToken>>
    login(@RequestBody UsernameAndPassword usernameAndPasswordDto);

    @PatchMapping("/change-password")
    Mono<ResponseEntity<Status>>
    changePassword(@RequestBody UsernameOldPasswordAndNewPassword
                                usernameOldPasswordAndNewPassword);

    @DeleteMapping("/specific/{username}")
    Mono<ResponseEntity<Status>>
    deleteSpecificUser(@PathVariable String username);

    @DeleteMapping("/all")
    Mono<ResponseEntity<Status>>
    deleteAllUsers();
}
