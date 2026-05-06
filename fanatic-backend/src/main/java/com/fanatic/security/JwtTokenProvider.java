package com.fanatic.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey key;
    private final long jwtExpiration;
    private final long refreshExpiration;

    public JwtTokenProvider(
            @Value("${app.jwt.secret:${JWT_SECRET:myUltraSecretKeyForFanaticApplicationThatMustBeAtLeast64CharactersLongForSecurity2024Fanatic}}") String jwtSecret,
            @Value("${app.jwt.expiration:86400000}") long jwtExpiration,
            @Value("${app.jwt.refresh-expiration:604800000}") long refreshExpiration) {

        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtExpiration = jwtExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    // ========== GENERATE ACCESS TOKEN ==========
    public String generateAccessToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return generateToken(userDetails.getId(), userDetails.getUsername(),
                userDetails.getRole(), jwtExpiration);
    }

    // ========== GENERATE ACCESS TOKEN FROM USER DETAILS ==========
    public String generateAccessToken(CustomUserDetails userDetails) {
        return generateToken(userDetails.getId(), userDetails.getUsername(),
                userDetails.getRole(), jwtExpiration);
    }

    // ========== GENERATE REFRESH TOKEN ==========
    public String generateRefreshToken(CustomUserDetails userDetails) {
        return generateToken(userDetails.getId(), userDetails.getUsername(),
                userDetails.getRole(), refreshExpiration);
    }

    // ========== CORE TOKEN GENERATION ==========
    private String generateToken(UUID userId, String username, String role, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    // ========== GET USER ID FROM TOKEN ==========
    public UUID getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return UUID.fromString(claims.getSubject());
    }

    // ========== GET USERNAME FROM TOKEN ==========
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("username", String.class);
    }

    // ========== GET ROLE FROM TOKEN ==========
    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("role", String.class);
    }

    // ========== VALIDATE TOKEN ==========
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        } catch (SecurityException ex) {
            log.error("JWT signature validation failed: {}", ex.getMessage());
        }
        return false;
    }
}