/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.dto;

import com.richarddklein.shorturluserservice.controller.response.ShortUrlUserStatus;
import com.richarddklein.shorturluserservice.entity.ShortUrlUser;

@SuppressWarnings("unused")
public class StatusAndShortUrlUser {
    private ShortUrlUserStatus status;
    private ShortUrlUser shortUrlUser;

    public StatusAndShortUrlUser() {
    }

    public StatusAndShortUrlUser(ShortUrlUserStatus status, ShortUrlUser shortUrlUser) {
        this.status = status;
        this.shortUrlUser = shortUrlUser;
    }

    public ShortUrlUserStatus getStatus() {
        return status;
    }

    public void setStatus(ShortUrlUserStatus status) {
        this.status = status;
    }

    public ShortUrlUser getShortUrlUser() {
        return shortUrlUser;
    }

    public void setShortUrlUser(ShortUrlUser shortUrlUser) {
        this.shortUrlUser = shortUrlUser;
    }
}
