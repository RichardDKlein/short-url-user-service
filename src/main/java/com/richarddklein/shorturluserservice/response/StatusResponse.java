/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.response;

/**
 * Class defining an HTTP Response containing a status
 * code/message only.
 */
public class StatusResponse {
    private ShortUrlUserStatus status;
    private String message;

    /**
     * General constructor.
     *
     * @param status The status code to be embedded in the HTTP Response.
     * @param message The status message to be embedded in the HTTP Response.
     */
    public StatusResponse(ShortUrlUserStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public ShortUrlUserStatus getStatus() {
        return status;
    }

    public void setStatus(ShortUrlUserStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "StatusResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
