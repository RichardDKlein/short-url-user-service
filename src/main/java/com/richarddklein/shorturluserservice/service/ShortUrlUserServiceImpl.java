/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.service;

import java.security.Principal;

import com.richarddklein.shorturlcommonlibrary.security.dto.UsernameAndRole;
import com.richarddklein.shorturlcommonlibrary.security.util.JwtUtils;
import com.richarddklein.shorturluserservice.dto.StatusAndRoleDto;
import com.richarddklein.shorturluserservice.dto.UsernameAndPasswordDto;
import com.richarddklein.shorturluserservice.entity.ShortUrlUser;
import com.richarddklein.shorturluserservice.controller.response.ShortUrlUserStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.richarddklein.shorturluserservice.dao.ShortUrlUserDao;
import reactor.core.publisher.Mono;

/**
 * The production implementation of the Short URL User Service interface.
 */
@Service
public class ShortUrlUserServiceImpl implements ShortUrlUserService {
    private final ShortUrlUserDao shortUrlUserDao;
    private final JwtUtils jwtUtils;

    // ------------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------------

    /**
     * General constructor.
     *
     * @param shortUrlUserDao Dependency injection of a class instance that is
     *                        to play the role of the Short URL User Data Access
     *                        Object (DAO).
     * @param jwtUtils Dependency injection of a class instance that is to able
     *                 to provide utilities to handle JWT authentication tokens.
     */
    public ShortUrlUserServiceImpl(
            ShortUrlUserDao shortUrlUserDao,
            JwtUtils jwtUtils) {

        this.shortUrlUserDao = shortUrlUserDao;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void initializeShortUrlUserRepository() {
        shortUrlUserDao.initializeShortUrlUserRepository();
    }

    @Override
    public ShortUrlUserStatus signup(ShortUrlUser shortUrlUser) {
        return shortUrlUserDao.signup(shortUrlUser);
    }

    @Override
    public Object[] login(UsernameAndPasswordDto usernameAndPasswordDto) {
        StatusAndRoleDto statusAndRoleDto = shortUrlUserDao.login(usernameAndPasswordDto);
        if (statusAndRoleDto.getStatus() != ShortUrlUserStatus.SUCCESS) {
            return new Object[] {statusAndRoleDto.getStatus(), null};
        }
        UsernameAndRole usernameAndRole = new UsernameAndRole(
                usernameAndPasswordDto.getUsername(), statusAndRoleDto.getRole());
        String jwtToken = jwtUtils.generateToken(usernameAndRole);
        return new Object[] {ShortUrlUserStatus.SUCCESS, jwtToken};
    }

    @Override
    public Object[] validate(Mono<Principal> principal) {
        Mono<ShortUrlUser> shortUrlUserMono = principal.map(auth -> {
            Authentication authentication = (Authentication)auth;
            ShortUrlUser shortUrlUser = new ShortUrlUser();
            shortUrlUser.setUsername(authentication.getName());
            shortUrlUser.setRole(authentication.getAuthorities()
                    .iterator().next().getAuthority());
            return shortUrlUser;
        });

        return new Object[] {ShortUrlUserStatus.SUCCESS, shortUrlUserMono};
    }

    @Override
    public Object[] getUserDetails(Mono<Principal> principal) {
        Mono<ShortUrlUser> shortUrlUserMono = principal.map(auth -> {
            Authentication authentication = (Authentication)auth;
            String username = authentication.getName();
            ShortUrlUser shortUrlUser = shortUrlUserDao.getUserDetails(username);
            shortUrlUser.setPassword(null);
            return shortUrlUser;
        });

        return new Object[] {ShortUrlUserStatus.SUCCESS, shortUrlUserMono};
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------
}
