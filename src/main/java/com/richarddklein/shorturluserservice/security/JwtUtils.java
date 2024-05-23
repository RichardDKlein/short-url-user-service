/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.security;

import com.richarddklein.shorturluserservice.entity.ShortUrlUser;
import io.jsonwebtoken.Claims;

public interface JwtUtils {
    String generateToken(ShortUrlUser shortUrlUser);
    Claims getClaimsFromToken(String jwtToken);
}
