/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.response;

import com.richarddklein.shorturluserservice.entity.ShortUrlUser;

/**
 * Class defining an HTTP Response containing a status
 * code/message as well as a ShortUrlUser entity.
 */
public class StatusAndShortUrlUserResponse {
    private StatusResponse status;
    private ShortUrlUser shortUrlUser;

    /**
     * General constructor.
     *
     * @param status The status code/message to be embedded
     *               in the HTTP Response.
     * @param shortUrlUser The ShortUrlUser entity to be
     *                     embedded in the HTTP Response.
     */
    public StatusAndShortUrlUserResponse(
            StatusResponse status,
            ShortUrlUser shortUrlUser) {

        this.status = status;
        this.shortUrlUser = shortUrlUser;
    }

    public StatusResponse getStatus() {
        return status;
    }

    public void setStatus(StatusResponse status) {
        this.status = status;
    }

    public ShortUrlUser getShortUrlUser() {
        return shortUrlUser;
    }

    public void setShortUrlUser(ShortUrlUser shortUrlUser) {
        this.shortUrlUser = shortUrlUser;
    }

    @Override
    public String toString() {
        return "StatusAndJwtTokenResponse{" +
                "status=" + status +
                ", shortUrlUser='" + shortUrlUser + '\'' +
                '}';
    }
}
