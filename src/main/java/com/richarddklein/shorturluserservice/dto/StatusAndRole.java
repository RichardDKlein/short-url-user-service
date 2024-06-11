/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.dto;

import com.richarddklein.shorturluserservice.controller.response.ShortUrlUserStatus;

@SuppressWarnings("unused")
public class StatusAndRole {
    private ShortUrlUserStatus status;
    private String role;

    public StatusAndRole(ShortUrlUserStatus status, String role) {
        this.status = status;
        this.role = role;
    }

    public ShortUrlUserStatus getStatus() {
        return status;
    }

    public void setStatus(ShortUrlUserStatus status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "StatusAndRole{" +
                "status=" + status +
                ", role='" + role + '\'' +
                '}';
    }
}
