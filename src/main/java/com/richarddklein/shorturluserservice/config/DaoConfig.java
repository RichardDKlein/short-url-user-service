/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.config;

import com.richarddklein.shorturluserservice.dao.ShortUrlUserDao;
import com.richarddklein.shorturluserservice.entity.ShortUrlUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.ssm.SsmClient;

import com.richarddklein.shorturluserservice.dao.ShortUrlUserDaoImpl;
import com.richarddklein.shorturluserservice.dao.ParameterStoreReader;
import com.richarddklein.shorturluserservice.dao.ParameterStoreReaderImpl;

/**
 * The DAO (Data Access Object) @Configuration class.
 *
 * <p>Tells Spring how to construct instances of classes that are needed
 * to implement the DAO package.</p>
 */
@Configuration
public class DaoConfig {
    @Bean
    public ShortUrlUserDao
    shortUrlUserDao() {
        return new ShortUrlUserDaoImpl(
                parameterStoreReader(),
                dynamoDbClient(),
                shortUrlUserTable()
        );
    }

    @Bean
    public DynamoDbClient
    dynamoDbClient() {
        return DynamoDbClient.builder()
                .credentialsProvider(DefaultCredentialsProvider
                        .create())
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient
    dynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient())
                .build();
    }

    @Bean
    public DynamoDbTable<ShortUrlUser>
    shortUrlUserTable() {
        return dynamoDbEnhancedClient().table(
                parameterStoreReader().getShortUrlUserTableName(),
                TableSchema.fromBean(ShortUrlUser.class));
    }
    @Bean
    public SsmClient
    ssmClient() {
        return SsmClient.builder().build();
    }

    @Bean
    public ParameterStoreReader
    parameterStoreReader() {
        return new ParameterStoreReaderImpl(ssmClient());
    }
}
