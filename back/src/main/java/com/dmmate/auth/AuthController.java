package com.dmmate.auth;

import com.dmmate.auth.dto.OtpSendRequest;
import com.dmmate.auth.dto.OtpVerifyRequest;
import com.dmmate.auth.dto.CompleteSignupRequest;
import com.dmmate.common.MailService;
import com.dmmate.security.JwtTokenProvider; // ★ 추가
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final OtpService otpService;
  private final MailService mailService;
  private final AuthService authService;
  private final JwtTokenProvider jwt; // ★ 타입 인식되게 됨

  public AuthController(
      OtpService otpService,
      MailService mailService,
      AuthService authService,
      JwtTokenProvider jwt) {
    this.otpService = otpService;
    this.mailService = mailService;
    this.authService = authService;
    this.jwt = jwt;
  }

  @PostMapping("/otp")
  public ResponseEntity<?> sendOtp(@Valid @RequestBody OtpSendRequest req) {
    var email = req.getEmail().trim().toLowerCase();
    var code = otpService.generateOtp(email); // 기존 구현 사용
    mailService.sendOtp(email, code);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/verify")
  public ResponseEntity<?> verify(@Valid @RequestBody OtpVerifyRequest req) {
    var email = req.getEmail().trim().toLowerCase();
    var code = req.getCode().trim();
    var name = req.getName().trim();

    if (!otpService.verifyOtp(email, code)) {
      return ResponseEntity.status(400).body("Invalid or expired code");
    }
    var res = authService.handleEmailVerifiedAndSignIn(email, name);
    return ResponseEntity.ok(res); // AuthResponse 반환
  }

  @PostMapping("/complete-signup")
  public ResponseEntity<?> completeSignup(@Valid @RequestBody CompleteSignupRequest req) {
    // 토큰 필터(JwtAuthFilter)에서 Authentication이 세팅되어 있어야 함
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getName() == null) {
      return ResponseEntity.status(401).build();
    }
    String email = auth.getName(); // JwtTokenProvider가 setSubject(email)로 넣었으니 여기로 옴

    var res = authService.completeSignupFor(
        email,
        req.getNickname().trim(),
        req.getPassword().trim());
    return ResponseEntity.ok(res);
  }
}