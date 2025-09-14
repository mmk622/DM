package com.dmmate.security;

import io.jsonwebtoken.*;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter implements Filter {
  private final JwtTokenProvider jwt;

  public JwtAuthFilter(JwtTokenProvider jwt) {
    this.jwt = jwt;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    String header = req.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);
      try {
        Jws<Claims> claims = Jwts.parserBuilder()
            .setSigningKey(jwt.getKey())
            .build()
            .parseClaimsJws(token);

        String email = claims.getBody().get("email", String.class);

        var principal = new User(email, "", List.of());
        var auth = new UsernamePasswordAuthenticationToken(principal, token, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
      } catch (JwtException e) {
        SecurityContextHolder.clearContext();
      }
    }
    chain.doFilter(request, response);
  }
}