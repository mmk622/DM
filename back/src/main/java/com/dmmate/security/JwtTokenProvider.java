package com.dmmate.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

  // 우선순위: application.yml의 security.jwt.secret → 환경변수 JWT_SECRET
  @Value("${security.jwt.secret:${JWT_SECRET}}")
  private String secret;

  @Value("${security.jwt.issuer:${JWT_ISSUER:dm-app}}")
  private String issuer;

  @Value("${security.jwt.access-exp-min:${JWT_ACCESS_EXP_MIN:1440}}")
  private long accessExpMin;

  private Key key;

  @PostConstruct
  public void init() {
    if (secret == null || secret.isBlank()) {
      throw new IllegalStateException("JWT secret not configured");
    }
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public String createAccessToken(String email) {
    long now = System.currentTimeMillis();
    Date issuedAt = new Date(now);
    Date exp = new Date(now + (accessExpMin * 60_000L));

    return Jwts.builder()
        .setSubject(email)
        .setIssuer(issuer)
        .setIssuedAt(issuedAt)
        .setExpiration(exp)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public boolean validate(String token) {
    try {
      Jwts.parser().setSigningKey(key).parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public String getEmail(String token) {
    Claims claims = Jwts.parser()
        .setSigningKey(key)
        .parseClaimsJws(token)
        .getBody();
    return claims.getSubject();
  }
}