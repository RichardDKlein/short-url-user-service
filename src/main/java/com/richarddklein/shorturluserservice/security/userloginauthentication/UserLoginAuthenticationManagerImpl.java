/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.security.userloginauthentication;

import com.richarddklein.shorturluserservice.entity.ShortUrlUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

public class UserLoginAuthenticationManagerImpl implements UserLoginAuthenticationManager {
    @Autowired
    DynamoDbTable<ShortUrlUser> shortUrlUserTable;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        System.out.println("====> Entering UserLoginAuthenticationManagerImpl ...");
        String username = (String)authentication.getPrincipal();
        System.out.printf("====> username = %s\n", username);
        String plaintextPassword = (String)authentication.getCredentials();
        System.out.printf("====> plaintextPassword = %s\n", plaintextPassword);
        ShortUrlUser shortUrlUser = new ShortUrlUser(username, plaintextPassword);
        ShortUrlUser item = shortUrlUserTable.getItem(shortUrlUser);
        System.out.printf("====> item = %s\n", item);
        if (item == null) {
            return Mono.error(new BadCredentialsException(
                    String.format("User '%s' does not exist", username)));
        }
        if (!passwordEncoder.matches(plaintextPassword, item.getPassword())) {
            return Mono.error(new BadCredentialsException(
                    "The supplied password is not correct"));
        }
        System.out.println("====> Authentication successful, calling Mono.just() ...");
        Mono<Authentication> result = Mono.just(authentication);
        System.out.println("====> Returning result");
        return result;
    }
}
