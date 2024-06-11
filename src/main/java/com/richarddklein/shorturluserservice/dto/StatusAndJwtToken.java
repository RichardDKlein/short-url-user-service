/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.dto;

import com.richarddklein.shorturluserservice.controller.response.ShortUrlUserStatus;

@SuppressWarnings("unused")
public class StatusAndJwtToken {
    private ShortUrlUserStatus status;
    private String jwtToken;

    public StatusAndJwtToken(ShortUrlUserStatus status, String jwtToken) {
        this.status = status;
        this.jwtToken = jwtToken;
    }

    public ShortUrlUserStatus getStatus() {
        return status;
    }

    public void setStatus(ShortUrlUserStatus status) {
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
        return "StatusAndJwtToken{" +
                "status=" + status +
                ", jwtToken='" + jwtToken + '\'' +
                '}';
    }
}
