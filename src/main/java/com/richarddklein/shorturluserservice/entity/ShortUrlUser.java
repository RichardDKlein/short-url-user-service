/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.entity;

import software.amazon.awssdk.enhanced.dynamodb.extensions.annotations.DynamoDbVersionAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

/**
 * The Entity corresponding to an item in the Short URL User
 * table in AWS DynamoDB.
 */
@DynamoDbBean
public class ShortUrlUser {
    /**
     * The Short URL User item attributes. See the `ShortUrlUserDaoImpl`
     * Javadoc for a detailed description of these attributes.
     */
    private String username;
    private String encodedPassword;
    private String role;
    private String name;
    private String email;
    private String lastLogin;
    private String accountCreationDate;
    private Long version;

    /**
     * Default constructor.
     *
     * This is not used by our code, but Spring requires it.
     */
    public ShortUrlUser() {
    }

    /**
     * General constructor.
     *
     * @param username The username.
     * @param encodedPassword The encoded password.
     * @param role The user's role.
     * @param name The user's name.
     * @param email The user's email address.
     * @param lastLogin The date/time the user last logged in.
     * @param accountCreationDate The date/time the user's account was created.
     */
    public ShortUrlUser(String username,
                        String encodedPassword,
                        String role,
                        String name,
                        String email,
                        String lastLogin,
                        String accountCreationDate) {

        this.username = username;
        this.encodedPassword = encodedPassword;
        this.role = role;
        this.name = name;
        this.email = email;
        this.lastLogin = lastLogin;
        this.accountCreationDate = accountCreationDate;
    }

    @DynamoDbPartitionKey
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @DynamoDbAttribute("encodedPassword")
    public String getEncodedPassword() {
        return encodedPassword;
    }

    public void setEncodedPassword(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }

    @DynamoDbAttribute("role")
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @DynamoDbAttribute("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DynamoDbAttribute("email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @DynamoDbAttribute("lastLogin")
    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    @DynamoDbAttribute("accountCreationDate")
    public String getAccountCreationDate() {
        return accountCreationDate;
    }

    public void setAccountCreationDate(String accountCreationDate) {
        this.accountCreationDate = accountCreationDate;
    }

    @DynamoDbVersionAttribute
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "ShortUrlUser{" +
                "username='" + username + '\'' +
                ", encodedPassword='" + encodedPassword + '\'' +
                ", role='" + role + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", lastLogin='" + lastLogin + '\'' +
                ", accountCreationDate='" + accountCreationDate + '\'' +
                ", version=" + version +
                '}';
    }
}
