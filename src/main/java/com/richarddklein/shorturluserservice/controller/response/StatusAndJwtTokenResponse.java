/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.controller.response;

/**
 * Class defining an HTTP Response containing a status
 * code/message as well as a JWT authentication token.
 */
public class StatusAndJwtTokenResponse {
    private StatusResponse status;
    private String jwtToken;

    /**
     * General constructor.
     *
     * @param status The status code/message to be embedded
     *               in the HTTP Response.
     * @param jwtToken The JWT authentication token to be
     *                 embedded in the HTTP Response.
     */
    public StatusAndJwtTokenResponse(
            StatusResponse status,
            String jwtToken) {

        this.status = status;
        this.jwtToken = jwtToken;
    }

    public StatusResponse getStatus() {
        return status;
    }

    public void setStatus(StatusResponse status) {
        this.status = status;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    @Override
    public String toString() {
        return "StatusAndJwtTokenResponse{" +
                "status=" + status +
                ", jwtToken='" + jwtToken + '\'' +
                '}';
    }
}
