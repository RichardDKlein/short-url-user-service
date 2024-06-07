/**
 * The Short URL Mapping Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.dto;

/**
 * Class defining a DTO (Data Transfer Object) containing
 * `username` and `password` fields.
 */
@SuppressWarnings("unused")
public class UsernameAndPassword {
    private String username;
    private String password;

    /**
     * General constructor.
     *
     * @param username The username.
     * @param password The user's password, in plaintext.
     */
    @SuppressWarnings("unused")
    public UsernameAndPassword(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UsernameAndRole{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
