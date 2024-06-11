/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.config;

import com.richarddklein.shorturlcommonlibrary.aws.ParameterStoreReader;
import com.richarddklein.shorturlcommonlibrary.config.AwsConfig;
import com.richarddklein.shorturlcommonlibrary.config.SecurityConfig;
import com.richarddklein.shorturluserservice.dao.ShortUrlUserDao;
import com.richarddklein.shorturluserservice.entity.ShortUrlUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import com.richarddklein.shorturluserservice.dao.ShortUrlUserDaoImpl;

/**
 * The DAO (Data Access Object) @Configuration class.
 *
 * <p>Tells Spring how to construct instances of classes that are needed
 * to implement the DAO package.</p>
 */
@Configuration
@Import({AwsConfig.class, SecurityConfig.class})
public class DaoConfig {
    @Autowired
    ParameterStoreReader parameterStoreReader;

    @Autowired
    PasswordEncoder passwordEncoder;

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
    public DynamoDbAsyncClient
    dynamoDbAsyncClient() {
        return DynamoDbAsyncClient.builder()
                .build();
    }

    @Bean
    public DynamoDbEnhancedAsyncClient
    dynamoDbEnhancedAsyncClient() {
        return DynamoDbEnhancedAsyncClient.builder()
                .dynamoDbClient(dynamoDbAsyncClient())
                .build();
    }

    @Bean
    public DynamoDbAsyncTable<ShortUrlUser>
    shortUrlUserTable() {
        return dynamoDbEnhancedAsyncClient().table(
                parameterStoreReader.getShortUrlUserTableName(),
                TableSchema.fromBean(ShortUrlUser.class));
    }
}
