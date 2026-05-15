package com.codeTutor.backend.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration.ms}")
    private long expirationMilliseconds;

    @PostConstruct
    public void validateSecret() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("Missing required env var: JWT_SECRET");
        }
        if (secret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET must be at least 32 characters long");
        }
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(Long userId, String email) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMilliseconds))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long extractUserId(String token) {
        return Long.parseLong(extractClaims(token).getSubject());
    }

    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getValidationError(String token) {
        try {
            extractClaims(token);
            return null;
        } catch (ExpiredJwtException e) {
            return "Token expired";
        } catch (SecurityException | MalformedJwtException e) {
            return "Invalid token";
        } catch (Exception e) {
            return "Invalid token";
        }
    }
}
