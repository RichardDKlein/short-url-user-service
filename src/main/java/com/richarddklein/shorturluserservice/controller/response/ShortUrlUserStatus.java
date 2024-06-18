/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.controller.response;

/**
 * The Short URL User Status.
 *
 * An enumerated type describing the various possible statuses
 * that can be returned in response to a client request.
 */
public enum ShortUrlUserStatus {
    SUCCESS,
    MISSING_PASSWORD,
    MUST_BE_ADMIN,
    NO_SUCH_USER,
    NOT_ON_LOCAL_MACHINE,
    UNKNOWN_ERROR,
    USER_ALREADY_EXISTS,
    USER_CONFIRMATION_MISMATCH,
    WRONG_PASSWORD,
}
