/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.dto;

@SuppressWarnings("unused")
public class StatusAndJwtToken {
    private Status status;
    private String jwtToken;

    public StatusAndJwtToken() {
    }

    public StatusAndJwtToken(Status status, String jwtToken) {
        this.status = status;
        this.jwtToken = jwtToken;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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
