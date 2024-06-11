/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.controller;

import java.security.Principal;

import com.richarddklein.shorturluserservice.dto.UsernameAndPassword;
import com.richarddklein.shorturluserservice.entity.ShortUrlUser;
import com.richarddklein.shorturluserservice.controller.response.StatusAndJwtTokenResponse;
import com.richarddklein.shorturluserservice.controller.response.StatusAndShortUrlUserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;

import com.richarddklein.shorturluserservice.controller.response.StatusResponse;
import reactor.core.publisher.Mono;

/**
 * The Short URL User Controller interface.
 *
 * <p>Specifies the REST API endpoints for the Short URL User
 * Service.</p>
 */
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
    @PostMapping("/dbinit")
    ResponseEntity<StatusResponse>
    initializeShortUrlUserRepository(ServerHttpRequest request);

    @PostMapping("/signup")
    ResponseEntity<StatusResponse>
    signup(@RequestBody ShortUrlUser shortUrlUser);

    @PostMapping("/login")
    ResponseEntity<StatusAndJwtTokenResponse>
    login(@RequestBody UsernameAndPassword usernameAndPasswordDto);

    @GetMapping("/details")
    Mono<ResponseEntity<StatusAndShortUrlUserResponse>>
    getUserDetails(Mono<Principal> principal);
}
