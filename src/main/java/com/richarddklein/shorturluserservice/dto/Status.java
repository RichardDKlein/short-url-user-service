/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.dto;

import com.richarddklein.shorturlcommonlibrary.config.AwsConfig;
import com.richarddklein.shorturlcommonlibrary.config.SecurityConfig;
import com.richarddklein.shorturlcommonlibrary.status.ShortUrlUserStatus;
import org.springframework.context.annotation.Import;

public class Status {
    private ShortUrlUserStatus status;
    private String message;

    public Status(ShortUrlUserStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public Status(ShortUrlUserStatus status) {
        this.status = status;
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
        return "Status{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
