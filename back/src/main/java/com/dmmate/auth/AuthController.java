package com.dmmate.auth;

import com.dmmate.auth.dto.*;
import com.dmmate.security.JwtTokenProvider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OtpService otpService;
    private final AuthService authService;
    private final JwtTokenProvider jwt;
    private final StringRedisTemplate redis;

    // 예전처럼: 이메일 기준 키 + 토큰 기준 키 둘 다 저장 (양방향)
    private static final String VERIFIED_EMAIL_KEY = "otp:verified:"; // otp:verified:<email> = <signupToken>
    private static final String VERIFIED_TOKEN_KEY = "otp:signup:"; // otp:signup:<token> = <email>
    private static final Duration VERIFIED_TTL = Duration.ofMinutes(10);

    /** 1) OTP 발송 (변경 없음) */
    @PostMapping("/request-otp")
    public ResponseEntity<Map<String, Object>> sendOtp(@Valid @RequestBody OtpRequest req) {
        otpService.send(req.email());
        return ResponseEntity.ok(Map.of("ok", true));
    }

    /** 2) OTP 검증 → signupToken 발급(10분 TTL) */
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@Valid @RequestBody OtpVerifyRequest req) {
        boolean ok = otpService.verify(req.email(), req.code());
        if (!ok)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String signupToken = UUID.randomUUID().toString();
        // 양방향 저장 (토큰만 있어도 이메일 찾을 수 있게)
        redis.opsForValue().set(VERIFIED_EMAIL_KEY + req.email(), signupToken, VERIFIED_TTL);
        redis.opsForValue().set(VERIFIED_TOKEN_KEY + signupToken, req.email(), VERIFIED_TTL);

        // 프론트에서 email도 함께 로컬에 들고 있게 해두면 UX 좋음
        return ResponseEntity.ok(Map.of("signupToken", signupToken, "email", req.email()));
    }

    /** 3) 회원가입 완료: signupToken만 받아도 가입 가능(토큰→이메일 역복원) */
    @PostMapping("/complete-signup")
    public ResponseEntity<Map<String, Object>> completeSignup(@Valid @RequestBody CompleteSignupRequest req) {
        // signupToken -> email 복구
        String email = redis.opsForValue().get(VERIFIED_TOKEN_KEY + req.signupToken());
        if (email == null || email.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "invalid signupToken"));
        }

        // 실제 가입 처리(이름/닉네임/비번)
        AuthUserDto user = authService.completeSignup(email, req.name(), req.nickname(), req.password());

        // 일회성 사용: 양쪽 키 삭제
        redis.delete(VERIFIED_EMAIL_KEY + email);
        redis.delete(VERIFIED_TOKEN_KEY + req.signupToken());

        // 프론트가 기대하는 accessToken을 바로 발급/반환 (완료 후 자동 로그인)
        String accessToken = authService.login(email, req.password());
        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }

    /** 4) 로그인(이메일/비번) */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest req) {
        String token = authService.login(req.email(), req.password());
        return ResponseEntity.ok(Map.of("accessToken", token));
    }
}