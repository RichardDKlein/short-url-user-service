/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import software.amazon.awssdk.enhanced.dynamodb.extensions.annotations.DynamoDbVersionAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

/**
 * The Entity corresponding to an item in the Short URL User
 * table in AWS DynamoDB.
 */
@DynamoDbBean
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShortUrlUser {
    /**
     * The Short URL User item attributes. See the `ShortUrlUserDaoImpl`
     * Javadoc for a detailed description of these attributes.
     */
    private String username;
    private String password;
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
     * Call this constructor when you just want to specify the
     * `username` and `password` fields.
     *
     * @param username The username.
     * @param plaintextPassword The password, in plaintext.
     */
    public ShortUrlUser(
            String username,
            String plaintextPassword) {

        this.username = username;
        this.password = plaintextPassword;
    }
    /**
     * General constructor.
     *
     * Call this constructor to create an account for a new user. The
     * `lastLogin` and `accountCreationDate` fields will be generated
     * automatically.
     *
     * @param username The username.
     * @param plaintextPassword The password, in plaintext.
     * @param role The user's role.
     * @param name The user's name.
     * @param email The user's email address.
     * @param passwordEncoder Dependency injection of the class instance that
     *                        should be used to encode the plaintext password.
     */
    public ShortUrlUser(
            String username,
            String plaintextPassword,
            String role,
            String name,
            String email,
            PasswordEncoder passwordEncoder) {

        this(
                username,
                plaintextPassword,
                role,
                name,
                email,
                "hasn't logged in yet",
                LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss")),
                passwordEncoder);
    }

    /**
     * General constructor.
     *
     * Call this constructor if you want to set all fields explicitly.
     *
     * @param username The username.
     * @param plaintextPassword The password, in plaintext.
     * @param role The user's role.
     * @param name The user's name.
     * @param email The user's email address.
     * @param lastLogin The date/time the user last logged in.
     * @param accountCreationDate The date/time the user's account was created.
     * @param passwordEncoder Dependency injection of the class instance that
     *                        should be used to encode the plaintext password.
     */
    public ShortUrlUser(String username,
                        String plaintextPassword,
                        String role,
                        String name,
                        String email,
                        String lastLogin,
                        String accountCreationDate,
                        PasswordEncoder passwordEncoder) {

        this.username = username;
        this.password = passwordEncoder.encode(plaintextPassword);
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

    @DynamoDbAttribute("password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", lastLogin='" + lastLogin + '\'' +
                ", accountCreationDate='" + accountCreationDate + '\'' +
                ", version=" + version +
                '}';
    }
}
