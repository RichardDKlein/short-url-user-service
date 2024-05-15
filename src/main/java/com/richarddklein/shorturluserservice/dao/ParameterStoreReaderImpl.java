/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.dao;

import org.springframework.stereotype.Component;

import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

/**
 * The production implementation of the Parameter Store Reader interface.
 */
@Component
public class ParameterStoreReaderImpl implements ParameterStoreReader {
    private static final String ADMIN_USERNAME = "/shortUrl/users/adminUsername";
    private static final String ADMIN_PASSWORD = "/shortUrl/users/adminPassword";
    private static final String SHORT_URL_USER_TABLE_NAME = "/shortUrl/users/tableName";

    private final SsmClient ssmClient;

    /**
     * General constructor.
     *
     * @param ssmClient Dependency injection of a class instance that is to play
     *                  the role of an SSM (Simple Systems Manager) Client.
     */
    public ParameterStoreReaderImpl(SsmClient ssmClient) {
        this.ssmClient = ssmClient;
    }

    @Override
    public String getAdminUsername() {
        return getParameter(ADMIN_USERNAME);
    }

    @Override
    public String getAdminPassword() {
        return getParameter(ADMIN_PASSWORD);
    }

    @Override
    public String getShortUrlUserTableName() {
        return getParameter(SHORT_URL_USER_TABLE_NAME);
    }

    /**
     * Get a parameter from the Parameter Store.
     *
     * @param parameterName The name of the parameter of interest.
     * @return The value of the parameter in the Parameter Store.
     */
    private String getParameter(String parameterName) {
        GetParameterResponse parameterResponse =
                ssmClient.getParameter(req -> req.name(parameterName));
        return parameterResponse.parameter().value();
    }
}
