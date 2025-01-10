/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.exception;

import com.richarddklein.shorturlcommonlibrary.service.status.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static com.richarddklein.shorturlcommonlibrary.service.status.ShortUrlStatus.UNKNOWN_ERROR;

/**
 * The global exception handler for the Short URL User Service.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle the `NoResourceFoundException` exception, which is thrown
     * by the Spring framework.
     *
     * @param e The `NoResourceFoundException` exception that was thrown
     *          by Spring.
     * @return An HTTP Response Entity containing an error message as well
     * as the HTTP "Not Found" error code (404).
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Status> handleNoResourceFoundException(
            NoResourceFoundException e) {
        logger.warn("====> ", e);
        Status status = new Status(UNKNOWN_ERROR, e.getMessage());
        return new ResponseEntity<>(status, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle all other exceptions.
     *
     * <p>Handle all exceptions that are not handled by the above exception
     * handlers.</p>
     *
     * @param e The exception to be handled.
     * @return An HTTP Response Entity containing an error message as well
     * as the HTTP "Internal Server Error" error code (500).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Status> handleAllOtherExceptions(Exception e) {
        logger.warn("====> ", e);
        Status status = new Status(UNKNOWN_ERROR, e.getMessage());
        return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
