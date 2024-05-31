/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.security.util;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.richarddklein.shorturluserservice.dao.ParameterStoreReader;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import com.richarddklein.shorturluserservice.entity.ShortUrlUser;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public class JwtUtilsImpl implements JwtUtils {
    private ParameterStoreReader parameterStoreReader;

    public JwtUtilsImpl(ParameterStoreReader parameterStoreReader) {
        this.parameterStoreReader = parameterStoreReader;
    }

    public String generateToken(ShortUrlUser shortUrlUser) {
        Date now = new Date();
        long timeToLive = TimeUnit.MINUTES.toMillis(
                parameterStoreReader.getJwtMinutesToLive());
        Date expirationDate = new Date(now.getTime() + timeToLive);

        byte[] keyBytes = Decoders.BASE64.decode(
                parameterStoreReader.getJwtSecretKey());
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .subject(shortUrlUser.getUsername())
                .claim("role", shortUrlUser.getRole())
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(key)
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(
                        parameterStoreReader.getJwtSecretKey().getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
