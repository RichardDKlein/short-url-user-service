/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.controller.response;

import java.util.List;

import com.richarddklein.shorturluserservice.entity.ShortUrlUser;

/**
 * Class defining an HTTP Response containing a status
 * code/message as well as an ArrayList of ShortUrlUser
 * entities.
 */
public class StatusAndShortUrlUserArrayResponse {
    private StatusResponse status;
    private List<ShortUrlUser> shortUrlUsers;

    /**
     * General constructor.
     *
     * @param status The status code/message to be embedded
     *               in the HTTP Response.
     * @param shortUrlUsers The ShortUrlUser entities to be
     *                      embedded in the HTTP Response.
     */
    public StatusAndShortUrlUserArrayResponse(
            StatusResponse status,
            List<ShortUrlUser> shortUrlUsers) {

        this.status = status;
        this.shortUrlUsers = shortUrlUsers;
    }

    public StatusResponse getStatus() {
        return status;
    }

    public void setStatus(StatusResponse status) {
        this.status = status;
    }

    public List<ShortUrlUser> getShortUrlUsers() {
        return shortUrlUsers;
    }

    public void setShortUrlUsers(List<ShortUrlUser> shortUrlUsers) {
        this.shortUrlUsers = shortUrlUsers;
    }

    @Override
    public String toString() {
        return "StatusAndShortUrlUserArrayResponse{" +
                "status=" + status +
                ", shortUrlUsers=" + shortUrlUsers +
                '}';
    }
}
