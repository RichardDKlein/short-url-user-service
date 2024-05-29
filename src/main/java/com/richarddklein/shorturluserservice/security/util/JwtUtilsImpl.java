/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.security.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.crypto.spec.SecretKeySpec;

import com.richarddklein.shorturluserservice.dao.ParameterStoreReader;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import com.richarddklein.shorturluserservice.entity.ShortUrlUser;
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
        Date expiryDate = new Date(now.getTime() + timeToLive);

        return Jwts.builder()
                .subject(shortUrlUser.getUsername())
                .claim("role", shortUrlUser.getRole())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getKeyFromString(parameterStoreReader.getJwtSecretKey()))
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

    private Key getKeyFromString(String keyString) {
        byte[] keyBytes = keyString.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, Jwts.SIG.HS256.toString());
    }
}
