package org.example.demo.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private Long expiration;

  private SecretKey getSigningKey() {
    // ✅ Ensure secret is at least 32 bytes for HS256
    byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    if (keyBytes.length < 32) {
      throw new IllegalStateException("JWT secret must be at least 32 bytes for HS256");
    }
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateToken(String username) {
    return Jwts.builder()
        .subject(username)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSigningKey())
        .compact();
  }

  public String extractUsername(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser()
          .verifyWith(getSigningKey())
          .build()
          .parseSignedClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      System.err.println("❌ JWT validation failed: " + e.getMessage());
      return false;
    }
  }

  @PostConstruct
  public void debugInit() {
    System.out.println("✅ JwtUtil initialized");
    System.out.println("🔑 Secret loaded: " + (secret != null && !secret.isEmpty()));
    System.out.println("🔑 Secret length: " + (secret != null ? secret.length() : 0));
    System.out.println("⏰ Expiration: " + expiration + "ms");
  }
}
