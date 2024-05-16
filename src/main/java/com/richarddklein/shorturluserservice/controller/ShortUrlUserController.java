/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;

import com.richarddklein.shorturluserservice.response.StatusResponse;

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
}

/*
Here are some REST endpoints that your ShortUrlUsers microservice could include:

User Registration: Endpoint for users to create new accounts.
Login: Endpoint for users to authenticate and obtain JWT tokens.
Token Validation: Endpoint to validate JWT tokens.
User Profile: Endpoint to retrieve user information.
Password Reset: Endpoint for users to reset their passwords.
Role Assignment: Endpoint to assign roles to users.
Role-Based Access Control (RBAC): Endpoint to check user roles and permissions.

User
What sort of information would the User Profile endpoint return?
ChatGPT
The User Profile endpoint typically returns information about the authenticated user, providing details such as their username, email, user ID, roles, and any other relevant information associated with the user's account. Here's a breakdown of the information the User Profile endpoint could return:

Username: The user's username or unique identifier within the system.
Email Address: The email address associated with the user's account. This can be used for communication or verification purposes.
User ID: A unique identifier for the user, which can be used for internal reference or linking to other data.
Roles: The roles assigned to the user, indicating their permissions or privileges within the system. This information is crucial for role-based access control (RBAC) and determining what actions the user can perform.
Additional User Attributes: Any additional user attributes or metadata that may be relevant, such as name, profile picture, contact information, etc.
Account Status: Information about the status of the user's account, such as whether it's active, disabled, or locked.
Last Login: Timestamp indicating when the user last logged in to the system.
Account Creation Date: Timestamp indicating when the user's account was created.

{
  "username": "john_doe",
  "email": "john@example.com",
  "userId": "123456",
  "roles": ["user", "admin"],
  "name": "John Doe",
  "profilePicture": "https://example.com/profile.jpg",
  "accountStatus": "active",
  "lastLogin": "2024-05-15T08:00:00Z",
  "accountCreationDate": "2023-01-01T12:00:00Z"
}
 */