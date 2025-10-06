package com.dmmate.auth;

import com.dmmate.auth.dto.*;
import com.dmmate.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OtpService otpService;
    private final AuthService authService;
    private final JwtTokenProvider jwt;

    /** 1) OTP 발송 */
    @PostMapping("/otp/send")
    public ResponseEntity<Map<String, Object>> sendOtp(@Valid @RequestBody OtpRequest req) {
        otpService.send(req.email());
        return ResponseEntity.ok(Map.of("ok", true));
    }

    /** 2) OTP 검증 -> 유저 조회/필요시 생성 */
    @PostMapping("/otp/verify")
    public ResponseEntity<AuthUserDto> verifyOtp(@Valid @RequestBody OtpVerifyRequest req) {
        boolean ok = otpService.verify(req.email(), req.code());
        if (!ok) return ResponseEntity.status(401).build();

        var userInfo = authService.afterVerifiedLookupOnly(req.email());
        return ResponseEntity.ok(userInfo);
    }

    /** 3) 회원가입 완료 */
    @PostMapping("/signup/complete")
    public ResponseEntity<AuthUserDto> completeSignup(@Valid @RequestBody CompleteSignupRequest req) {
        var dto = authService.completeSignup(req.email(), req.name(), req.nickname(), req.password());
        return ResponseEntity.ok(dto);
    }

    /** 4) 로그인(이메일/비번) */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest req) {
        String token = authService.login(req.email(), req.password());
        return ResponseEntity.ok(Map.of("accessToken", token));
    }
}