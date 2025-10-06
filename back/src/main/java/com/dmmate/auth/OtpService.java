package com.dmmate.auth;

import java.security.SecureRandom;
import java.time.Duration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final StringRedisTemplate redisTemplate;
    private static final SecureRandom RND = new SecureRandom();

    /** 클라이언트가 이메일만 주면 6자리 OTP 생성해서 5분간 저장 */
    public void send(String email) {
        String code = String.format("%06d", RND.nextInt(1_000_000));
        String key = key(email);
        redisTemplate.opsForValue().set(key, code, Duration.ofMinutes(5));
        // TODO: 실제 이메일 전송로직 연동
        log.info("send OTP -> email={}, code={}", email, code);
    }

    /** 클라이언트가 이메일 + 코드 주면 검증, 맞으면 true */
    public boolean verify(String email, String code) {
        String key = key(email);
        String saved = redisTemplate.opsForValue().get(key);
        boolean ok = (saved != null && saved.equals(code));
        log.info("verify OTP -> email={}, ok={}", email, ok);
        return ok;
    }

    private String key(String email) {
        return "otp:" + email;
    }
}