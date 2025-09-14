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

  @Value("${security.jwt.secret}")
  private String secret;

  @Value("${security.jwt.access-token-validity-seconds}")
  private long accessValiditySec;

  @Value("${security.jwt.refresh-token-validity-seconds}")
  private long refreshValiditySec;

  private Key key;

  @PostConstruct
  void init() {
    // HS256 키 생성
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public String createAccessToken(String email) {
    long now = System.currentTimeMillis();
    Date iat = new Date(now);
    Date exp = new Date(now + accessValiditySec * 1000);

    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(iat)
        .setExpiration(exp)
        .signWith(SignatureAlgorithm.HS256, key) // 구버전 스타일
        .compact();
  }

  public String createRefreshToken(String email) {
    long now = System.currentTimeMillis();
    Date iat = new Date(now);
    Date exp = new Date(now + refreshValiditySec * 1000);

    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(iat)
        .setExpiration(exp)
        .signWith(SignatureAlgorithm.HS256, key)
        .compact();
  }

  public boolean validate(String token) {
    try {
      // 구버전 파서 스타일 (경고 가능)
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