/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.dao;

/**
 * The Parameter Store Reader interface.
 *
 * <p>Specifies the methods that must be implemented by any class that
 * reads parameters from the Parameter Store component of the AWS Systems
 * Manager.</p>
 */
public interface ParameterStoreReader {
    /**
     * Get the name of the Short URL User table in the DynamoDB
     * database.
     *
     * @return The name of the Short URL User table in the DynamoDB
     * database.
     */
    String getShortUrlUserTableName();
}
