/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.response;

/**
 * The Short URL User Status.
 *
 * An enumerated type describing the various possible statuses
 * that can be returned in response to a client request.
 */
public enum ShortUrlUserStatus {
    SUCCESS,
    NOT_ON_LOCAL_MACHINE,
}
