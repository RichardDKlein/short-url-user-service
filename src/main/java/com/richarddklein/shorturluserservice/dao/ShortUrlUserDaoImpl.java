/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.dao;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import com.richarddklein.shorturlcommonlibrary.aws.ParameterStoreAccessor;
import com.richarddklein.shorturlcommonlibrary.status.ShortUrlUserStatus;
import com.richarddklein.shorturluserservice.dto.*;
import com.richarddklein.shorturluserservice.entity.ShortUrlUser;
import com.richarddklein.shorturluserservice.exception.NoSuchUserException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

/**
 * The production implementation of the Short URL User DAO interface.
 *
 * <p>This implementation uses a DynamoDB table, the Short URL User table, to store
 * each Short URL User item. Each item describes one of the users of the Short URL
 * service.</p>
 *
 * <p>This table must be strongly consistent, since we cannot allow the possibility
 * that two different users might accidentally sign up using the same username.
 * Therefore, the table cannot be replicated across multiple, geographically dispersed,
 * instances; there can be only one instance of the table.</p>
 *
 * <p>However, DynamoDB will automatically shard (horizontally scale) the table into
 * multiple, disjoint partitions as the access frequency increases, thereby ensuring
 * acceptable throughput regardless of the user load.</p>
 *
 * <p>Each Short URL User item in the table consists of several attributes: `username`,
 * `password`, `role`, `name`, `email`, `lastLogin`, `accountCreationDate`, and `version`.
 * </p>
 *
 * <p>The `username` attribute is the user name of the user. This name is chosen by
 * the user when he signs up with the Short URL User service. The service will ensure
 * that no two users can have the same `username` attribute.</p>
 *
 * <p>The `username` attribute is the Partition Key for each Short URL User item.
 * Because it has a uniform hash distribution, it can be used to quickly locate the
 * database partition containing the corresponding Short URL User item.</p>
 *
 * <p>The `password` attribute of each Short URL User item is the user's password.
 * The password is specified by the user in plain text when he signs up with the
 * Short URL User service. The password is then salted and encoded before being
 * stored in the Short URL User table.</p>
 *
 * <p>The `role` attribute of each Short URL User item specifies the user's role,
 * which in turn specifies the operations the user is permitted to perform. A user
 * with the "USER" role is an ordinary user, and can only perform operations that
 * pertain to his own account. A user with the "ADMIN" role can perform any operation
 * on any user account, as well as any database maintenance operation.</p>
 *
 * <p>The `name` attribute of each Short URL User item is an optional attribute that
 * specifies the user's first and last name.</p>
 *
 * <p>The `email` attribute of each Short URL User item is an optional attribute that
 * specifies the user's email address.</p>
 *
 * <p>The `lastLogin` and `accountCreationDate` attributes of each Short URL User item
 * are timestamps that specify, respectively, when the user last logged in, and when
 * the user created his account.</p>
 *
 * <p>The `version` attribute of each Short URL User item is a long integer indicating
 * the version # of the Short URL User entity. This attribute is for the exclusive use
 * of DynamoDB; the developer should not read or write it. DynamoDB uses the `version`
 * attribute for what it calls "optimistic locking".</p>
 *
 * <p>In the optimistic locking scheme, the code proceeds with a read-update-write
 * transaction under the assumption that most of the time the item will not be updated
 * by another user between the `read` and `write` operations. In the (hopefully rare)
 * situations where this is not the case, the `write` operation will fail, allowing the
 * code to retry with a new read-update-write transaction.</p>
 *
 * <p>DynamoDB uses the `version` attribute to detect when another user has updated the
 * same item concurrently. Every time the item is written to the database, DynamoDB first
 * checks whether the `version` attribute in the item is the same as the `version` attribute
 * in the database. If so, DynamoDB lets the `write` proceed, and updates the `version`
 * attribute in the database. If not, DynamoDB throws an exception to indicate that the
 * `write` has failed.</p>
 */
@Repository
public class ShortUrlUserDaoImpl implements ShortUrlUserDao {
    private static final String ADMIN_ROLE = "ADMIN";
    private static final String ADMIN_NAME = "Richard Klein";
    private static final String ADMIN_EMAIL = "RichardDKlein@gmail.com";
    private static final String USER_ROLE = "USER";

    private final ParameterStoreAccessor parameterStoreAccessor;
    private final PasswordEncoder passwordEncoder;
    private final DynamoDbClient dynamoDbClient;
    private final DynamoDbAsyncTable<ShortUrlUser> shortUrlUserTable;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    /**
     * General constructor.
     *
     * @param parameterStoreAccessor Dependency injection of a class instance that
     *                               is to play the role of getting and setting
     *                               parameters residing in the Parameter Store
     *                               component of the AWS Simple System Manager
     *                               (SSM).
     * @param passwordEncoder Dependency injection of a class instance that is
     *                        able to encode (encrypt) passwords.
     * @param dynamoDbClient Dependency injection of a class instance that is to
     *                       play the role of a DynamoDB Client.
     * @param shortUrlUserTable Dependency injection of a class instance that is
     *                          to model the Short URL User table in DynamoDB.
     */
    public ShortUrlUserDaoImpl(
            ParameterStoreAccessor parameterStoreAccessor,
            PasswordEncoder passwordEncoder,
            DynamoDbClient dynamoDbClient,
            DynamoDbAsyncTable<ShortUrlUser> shortUrlUserTable) {

        this.parameterStoreAccessor = parameterStoreAccessor;
        this.passwordEncoder = passwordEncoder;
        this.dynamoDbClient = dynamoDbClient;
        this.shortUrlUserTable = shortUrlUserTable;
    }

    // Initialization of the Short URL User repository is performed rarely,
    // and then only by the Admin from a local machine. Therefore, we do not
    // need to use reactive (asynchronous) programming techniques here. Simple
    // synchronous logic will work just fine.
    @Override
    public void initializeShortUrlUserRepository() {
        if (doesTableExist()) {
            deleteShortUrlUserTable();
        }
        createShortUrlUserTable();
        addAdminToShortUrlUserTable();
    }

    @Override
    public Mono<ShortUrlUser>
    getSpecificUser(String username) {
        ShortUrlUser key = new ShortUrlUser();
        key.setUsername(username);
        return Mono.fromFuture(shortUrlUserTable.getItem(key))
        .flatMap(user -> user != null ? Mono.just(user) : Mono.empty())
        .switchIfEmpty(Mono.error(new NoSuchUserException()));
    }

    @Override
    public Mono<StatusAndShortUrlUserArray>
    getAllUsers() {
        return Flux.from(shortUrlUserTable.scan().items())
        .collectList()
        .map(shortUrlUsers -> new StatusAndShortUrlUserArray(
                new Status(ShortUrlUserStatus.SUCCESS),
                shortUrlUsers))
        .onErrorResume(e -> {
            System.out.println("====> " + e.getMessage());
            return Mono.just(new StatusAndShortUrlUserArray(
                    new Status(ShortUrlUserStatus.UNKNOWN_ERROR),
                    Collections.emptyList()));
        });
    }

    @Override
    public Mono<ShortUrlUserStatus>
    signup(@RequestBody ShortUrlUser shortUrlUser) {
        ShortUrlUser shortUrlUserCopy = new ShortUrlUser(
                shortUrlUser.getUsername(),
                shortUrlUser.getPassword(),
                USER_ROLE,
                shortUrlUser.getName(),
                shortUrlUser.getEmail(),
                passwordEncoder
        );

        return Mono.fromFuture(
        shortUrlUserTable.putItem(req -> req
            .item(shortUrlUserCopy)
            .conditionExpression(Expression.builder()
                    .expression("attribute_not_exists(username)")
                    .build())
        ))
        .then(Mono.just(ShortUrlUserStatus.SUCCESS))
        .onErrorResume(ConditionalCheckFailedException.class, e ->
                Mono.just(ShortUrlUserStatus.USER_ALREADY_EXISTS));
    }

    @Override
    public Mono<StatusAndRole>
    login(UsernameAndPassword usernameAndPassword) {
        String username = usernameAndPassword.getUsername();
        String password = usernameAndPassword.getPassword();

        return getSpecificUser(username)
        .flatMap(shortUrlUser -> {
            if (!passwordEncoder.matches(password, shortUrlUser.getPassword())) {
                return Mono.just(new StatusAndRole(
                        new Status(ShortUrlUserStatus.WRONG_PASSWORD), null));
            }
            shortUrlUser.setLastLogin(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss")));

            return updateShortUrlUser(shortUrlUser)
            .map(updatedShortUrlUser -> new StatusAndRole(
                    new Status(ShortUrlUserStatus.SUCCESS), updatedShortUrlUser.getRole()));
        })
        .retryWhen(Retry.backoff(5, Duration.ofSeconds(1))
                .filter(e -> e instanceof ConditionalCheckFailedException)
                .doAfterRetry(retrySignal -> System.out.println(
                        "====> Retrying after error: " + retrySignal.failure().getMessage()))
        )
        .onErrorResume(e -> {
            System.out.println("====> login() failed: " + e.getMessage());
            return (e instanceof NoSuchUserException)
                ? Mono.just(new StatusAndRole(new Status(ShortUrlUserStatus.NO_SUCH_USER), null))
                : Mono.just(new StatusAndRole(new Status(ShortUrlUserStatus.UNKNOWN_ERROR), null));
        });
    }

    @Override
    public Mono<ShortUrlUserStatus>
    changePassword(UsernameOldPasswordAndNewPassword
            usernameOldPasswordAndNewPassword) {

        String username = usernameOldPasswordAndNewPassword.getUsername();
        String oldPassword = usernameOldPasswordAndNewPassword.getOldPassword();
        String newPassword = usernameOldPasswordAndNewPassword.getNewPassword();

        return getSpecificUser(username)
        .flatMap(shortUrlUser -> {
            if (!passwordEncoder.matches(oldPassword, shortUrlUser.getPassword())) {
                return Mono.just(ShortUrlUserStatus.WRONG_PASSWORD);
            }
            shortUrlUser.setPassword(passwordEncoder.encode(newPassword));

            return updateShortUrlUser(shortUrlUser)
            .flatMap(updatedShortUrlUser -> {
                // If the user is Admin, save his new password in the
                // AWS Parameter Store.
                if (updatedShortUrlUser.getRole().equals("ADMIN")) {
                    return parameterStoreAccessor.setAdminPassword(newPassword)
                    .thenReturn(ShortUrlUserStatus.SUCCESS);
                }
                return Mono.just(ShortUrlUserStatus.SUCCESS);
            });
        })
        .retryWhen(Retry.backoff(5, Duration.ofSeconds(1))
            .filter(e -> e instanceof ConditionalCheckFailedException)
            .doAfterRetry(retrySignal -> System.out.println(
                    "====> Retrying after error: " + retrySignal.failure().getMessage()))
        )
        .onErrorResume(e -> {
            System.out.println("====> changePassword() failed: " + e.getMessage());
            return (e instanceof NoSuchUserException)
                ? Mono.just(ShortUrlUserStatus.NO_SUCH_USER)
                : Mono.just(ShortUrlUserStatus.UNKNOWN_ERROR);
        });
    }

    @Override
    public Mono<ShortUrlUserStatus>
    deleteSpecificUser(String username) {
        return getSpecificUser(username)
        .flatMap(shortUrlUser ->
            deleteShortUrlUser(shortUrlUser)
            .map(deletedShortUrlUser -> ShortUrlUserStatus.SUCCESS))
            .onErrorResume(e -> {
                System.out.println("====> deleteSpecificUser() failed: " + e.getMessage());
                return (e instanceof NoSuchUserException)
                    ? Mono.just(ShortUrlUserStatus.NO_SUCH_USER)
                    : Mono.just(ShortUrlUserStatus.UNKNOWN_ERROR);
            });
    }

    @Override
    public Mono<ShortUrlUserStatus> deleteAllUsers() {
        return Flux.from(shortUrlUserTable.scan().items())
        .filter(user -> !ADMIN_ROLE.equals(user.getRole()))
        .flatMap(this::deleteShortUrlUser)
        .then(Mono.just(ShortUrlUserStatus.SUCCESS))
        .onErrorResume(e -> {
            System.out.println("====> deleteAllUsers() failed: " + e.getMessage());
            return Mono.just(ShortUrlUserStatus.UNKNOWN_ERROR);
        });
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------

    /**
     * Determine whether the Short URL User table currently exists in DynamoDB.
     *
     * @return `true` if the table currently exists, or `false` otherwise.
     */
    private boolean doesTableExist() {
        try {
            shortUrlUserTable.describeTable();
        } catch (ResourceNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * Delete the Short URL User table from DynamoDB.
     */
    private void deleteShortUrlUserTable() {
        System.out.print("====> Deleting the Short URL User table ...");

        shortUrlUserTable.deleteTable();

        DynamoDbWaiter waiter = DynamoDbWaiter.builder().client(dynamoDbClient).build();
        waiter.waitUntilTableNotExists(builder -> builder
                // synchronous logic ok here
                .tableName(parameterStoreAccessor.getShortUrlUserTableName().block())
                .build());
        waiter.close();

        System.out.println(" done!");
    }

    /**
     * Create the Short URL User table in DynamoDB.
     */
    private void createShortUrlUserTable() {
        System.out.print("====> Creating the Short URL User table ...");

        CreateTableEnhancedRequest createTableRequest =
                CreateTableEnhancedRequest.builder().build();
        shortUrlUserTable.createTable(createTableRequest);

        DynamoDbWaiter waiter = DynamoDbWaiter.builder().client(dynamoDbClient).build();
        waiter.waitUntilTableExists(builder -> builder
                // synchronous logic ok here
                .tableName(parameterStoreAccessor.getShortUrlUserTableName().block()).build());
        waiter.close();

        System.out.println(" done!");
    }

    /**
     * Add the Admin to the Short URL User table in DynamoDB.
     */
    private void addAdminToShortUrlUserTable() {
        System.out.print("====> Adding the Admin to the Short URL User table ...");

        ShortUrlUser admin = new ShortUrlUser(
                // synchronous logic ok here
                parameterStoreAccessor.getAdminUsername().block(),
                parameterStoreAccessor.getAdminPassword().block(),
                ADMIN_ROLE,
                ADMIN_NAME,
                ADMIN_EMAIL,
                passwordEncoder
        );
        Mono.fromFuture(shortUrlUserTable.putItem(admin)).block();

        System.out.println(" done!");
    }

    private Mono<ShortUrlUser>
    updateShortUrlUser(ShortUrlUser shortUrlUser) {
        return Mono.fromFuture(shortUrlUserTable.updateItem(shortUrlUser))
        .onErrorResume(ConditionalCheckFailedException.class, e -> {
            // Version check failed. Someone updated the ShortUrlUser item in the
            // database after we read the item, so the item we just tried to update
            // contains stale data.
            System.out.println("====> Version check failed: " + e.getMessage());
            return Mono.error(e);
        })
        .onErrorResume(e -> {
            // Some other exception occurred.
            System.out.println("====> Unexpected exception: " + e.getMessage());
            return Mono.error(e);
        });
    }

    private Mono<ShortUrlUser>
    deleteShortUrlUser(ShortUrlUser shortUrlUser) {
        return Mono.fromFuture(shortUrlUserTable.deleteItem(shortUrlUser))
        .onErrorResume(e -> {
            System.out.println("====> deleteShortUrlUser() failed: " + e.getMessage());
            return Mono.error(e);
        });
    }
}
