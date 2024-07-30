/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.exception;

public class NoSuchUserException extends Exception {
    public NoSuchUserException() {
        super("No such user");
    }
}
