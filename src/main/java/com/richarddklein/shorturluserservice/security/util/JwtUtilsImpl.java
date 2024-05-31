/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.security.util;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

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

        return Jwts.builder()
                .subject(shortUrlUser.getUsername())
                .claim("role", shortUrlUser.getRole())
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getKey())
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(
                parameterStoreReader.getJwtSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
