/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.dto;

import java.util.List;

import com.richarddklein.shorturluserservice.entity.ShortUrlUser;

@SuppressWarnings("unused")
public class StatusAndShortUrlUserArray {
    private Status status;
    private List<ShortUrlUser> shortUrlUsers;

    public StatusAndShortUrlUserArray() {
    }

    public StatusAndShortUrlUserArray
            (Status status,
             List<ShortUrlUser> shortUrlUsers) {

        this.status = status;
        this.shortUrlUsers = shortUrlUsers;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<ShortUrlUser> getShortUrlUsers() {
        return shortUrlUsers;
    }

    public void setShortUrlUsers(List<ShortUrlUser> shortUrlUsers) {
        this.shortUrlUsers = shortUrlUsers;
    }
}
