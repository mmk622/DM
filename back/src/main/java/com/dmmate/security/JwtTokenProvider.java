package com.dmmate.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
  private final Key key;
  private final long accessValidityMs;
  private final long refreshValidityMs;

  public JwtTokenProvider(
      @Value("${security.jwt.secret}") String secret,
      @Value("${security.jwt.access-token-validity-seconds}") long accessSec,
      @Value("${security.jwt.refresh-token-validity-seconds}") long refreshSec) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes());
    this.accessValidityMs = accessSec * 1000L;
    this.refreshValidityMs = refreshSec * 1000L;
  }

  public String createAccessToken(Long userId, String email) {
    return createToken(userId, email, accessValidityMs);
  }

  public String createRefreshToken(Long userId, String email) {
    return createToken(userId, email, refreshValidityMs);
  }

  private String createToken(Long userId, String email, long validityMs) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
        .setSubject(String.valueOf(userId))
        .claim("email", email)
        .setIssuedAt(new Date(now))
        .setExpiration(new Date(now + validityMs))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }
}