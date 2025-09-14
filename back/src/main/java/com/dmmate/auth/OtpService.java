package com.dmmate.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

  private final StringRedisTemplate redis;

  private static final String OTP_KEY = "otp:";
  private static final String VERIFIED_KEY = "otp:verified:";
  private static final Duration OTP_TTL = Duration.ofMinutes(5);
  private static final Duration VERIFIED_TTL = Duration.ofMinutes(10);
  private final SecureRandom random = new SecureRandom();

  public String generateOtp(String email) {
    String code = String.format("%06d", random.nextInt(1_000_000));
    redis.opsForValue().set(OTP_KEY + email, code, OTP_TTL);
    log.info("Generated OTP for {} -> {}", email, code);
    return code;
  }

  public boolean verifyOtp(String email, String code) {
    String saved = redis.opsForValue().get(OTP_KEY + email);
    boolean ok = saved != null && saved.equals(code);
    log.info("verify OTP -> email={}, ok={}", email, ok);
    if (ok) {
      // 검증 성공 흔적 남김 (이메일/회원 생성/로그인 등에서 신뢰 근거로 사용)
      redis.opsForValue().set(VERIFIED_KEY + email, "true", VERIFIED_TTL);
      // 원본 OTP 제거
      redis.delete(OTP_KEY + email);
    }
    return ok;
  }

  /** 이메일 검증 완료 후 호출(선택). 여기선 깔끔히 흔적 삭제. */
  public void consumeVerification(String email) {
    redis.delete(VERIFIED_KEY + email);
    log.info("consumeVerification -> {}", email);
  }

  /** “방금 검증됨” 여부가 필요하면 이런 헬퍼도 유용합니다. */
  public boolean isVerifiedRecently(String email) {
    return Boolean.parseBoolean(redis.opsForValue().get(VERIFIED_KEY + email));
  }
}