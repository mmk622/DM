package com.dmmate.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class OtpService {
  private final StringRedisTemplate redis;
  private final SecureRandom rnd = new SecureRandom();

  private String otpKey(String email) {
    return "otp:" + email.toLowerCase();
  }

  private String rateKey(String email) {
    return "otp:rate:" + email.toLowerCase();
  }

  private String ipRateKey(String ip) {
    return "otp:ip:" + ip;
  }

  public String generateAndStore(String email) {
    String code = String.format("%06d", rnd.nextInt(1_000_000));
    redis.opsForValue().set(otpKey(email), code, Duration.ofMinutes(10));
    return code;
  }

  public boolean verify(String email, String code) {
    String v = redis.opsForValue().get(otpKey(email));
    return v != null && v.equals(code);
  }

  public boolean hitEmailRate(String email) { // 3분에 3회 제한
    String key = rateKey(email);
    Long c = redis.opsForValue().increment(key);
    if (c == 1L)
      redis.expire(key, Duration.ofMinutes(3));
    return c <= 3; // 허용이면 true
  }

  public boolean hitIpRate(String ip) { // 1분 10회 제한
    String key = ipRateKey(ip);
    Long c = redis.opsForValue().increment(key);
    if (c == 1L)
      redis.expire(key, Duration.ofMinutes(1));
    return c <= 10;
  }

  public void consume(String email) {
    redis.delete(otpKey(email));
  }
}