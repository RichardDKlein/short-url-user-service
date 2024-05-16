/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.config;

import com.richarddklein.shorturluserservice.dao.ShortUrlUserDao;
import com.richarddklein.shorturluserservice.entity.ShortUrlUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import com.richarddklein.shorturluserservice.dao.ShortUrlUserDaoImpl;
import com.richarddklein.shorturluserservice.dao.ParameterStoreReader;

/**
 * The DAO (Data Access Object) @Configuration class.
 *
 * <p>Tells Spring how to construct instances of classes that are needed
 * to implement the DAO package.</p>
 */
@Configuration
public class DaoConfig {
    @Autowired
    ParameterStoreReader parameterStoreReader;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Bean
    public ShortUrlUserDao
    shortUrlUserDao() {
        return new ShortUrlUserDaoImpl(
                parameterStoreReader,
                passwordEncoder,
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
                parameterStoreReader.getShortUrlUserTableName(),
                TableSchema.fromBean(ShortUrlUser.class));
    }
}
