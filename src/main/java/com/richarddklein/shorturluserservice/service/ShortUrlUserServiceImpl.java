/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.service;

import com.richarddklein.shorturluserservice.entity.ShortUrlUser;
import com.richarddklein.shorturluserservice.response.ShortUrlUserStatus;
import com.richarddklein.shorturluserservice.security.JwtUtils;
import org.springframework.stereotype.Service;

import com.richarddklein.shorturluserservice.dao.ShortUrlUserDao;

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
    public Object[] signup(ShortUrlUser shortUrlUser) {
        ShortUrlUserStatus status = shortUrlUserDao.signup(shortUrlUser);
        if (status != ShortUrlUserStatus.SUCCESS) {
            return new Object[] {status, null};
        }
        String jwtToken = jwtUtils.generateToken(shortUrlUser);
        return new Object[] {ShortUrlUserStatus.SUCCESS, jwtToken};
    }

    // ------------------------------------------------------------------------
    // PRIVATE METHODS
    // ------------------------------------------------------------------------
}
