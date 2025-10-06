package com.dmmate.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret:${JWT_SECRET}}")
    private String secret;

    @Value("${jwt.access-exp-min:1440}")
    private long accessExpMin;

    @Value("${jwt.issuer:dm-app}")
    private String issuer;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Date now() { return new Date(); }

    // ===== Access Token (로그인용) =====
    public String createAccessToken(String email) {
        Date iat = now();
        Date exp = new Date(iat.getTime() + TimeUnit.MINUTES.toMillis(accessExpMin));
        return Jwts.builder()
                .setIssuer(issuer)                 // 0.11.x: setIssuer
                .setSubject(email)
                .claim("type", "access")
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 0.11.x
                .compact();
    }

    // JwtAuthFilter 호환용
    public boolean validate(String token) {
        try {
            Claims c = parse(token);
            return Objects.equals("access", c.get("type")) && !isExpired(c);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getEmail(String token) {
        return parse(token).getSubject();
    }

    // ===== Signup Token (짧은 만료) =====
    public String createSignupToken(String email) {
        Date iat = now();
        Date exp = new Date(iat.getTime() + Duration.ofMinutes(15).toMillis());
        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(email)
                .claim("type", "signup")
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String validateSignupTokenAndGetEmail(String token) {
        Claims c = parse(token);
        Object type = c.get("type");
        if (type == null || !"signup".equals(type.toString())) {
            throw new JwtException("invalid signup token type");
        }
        if (isExpired(c)) throw new ExpiredJwtException(null, c, "signup token expired");
        return c.getSubject();
    }

    // ===== Common =====
    private Claims parse(String token) {
        // 0.11.x: parserBuilder().setSigningKey(...)
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isExpired(Claims claims) {
        Date exp = claims.getExpiration();
        return exp != null && exp.before(now());
    }
}