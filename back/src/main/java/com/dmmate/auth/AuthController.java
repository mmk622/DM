package com.dmmate.auth;

import com.dmmate.auth.dto.*;
import com.dmmate.common.MailService;
import com.dmmate.security.JwtTokenProvider;
import com.dmmate.user.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final OtpService otpService;
  private final MailService mailService;
  private final UserRepository users;
  private final JwtTokenProvider jwt;

  @PostMapping("/otp")
  public ResponseEntity<?> sendOtp(@Valid @RequestBody OtpSendRequest req, HttpServletRequest http) {
    String ip = http.getRemoteAddr();
    if (!otpService.hitIpRate(ip) || !otpService.hitEmailRate(req.email())) {
      return ResponseEntity.status(429).body("Too many requests");
    }
    String code = otpService.generateAndStore(req.email());
    mailService.sendOtp(req.email(), code);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/verify")
  public ResponseEntity<AuthResponse> verify(@Valid @RequestBody OtpVerifyRequest req) {
    if (!otpService.verify(req.email(), req.code())) {
      return ResponseEntity.status(401).build();
    }
    // 사용자 생성(없으면)
    User user = users.findByEmail(req.email()).orElseGet(() -> users.save(
        User.builder().email(req.email()).name(req.name()).build()));
    otpService.consume(req.email());
    String at = jwt.createAccessToken(user.getId(), user.getEmail());
    String rt = jwt.createRefreshToken(user.getId(), user.getEmail());
    return ResponseEntity.ok(new AuthResponse(at, rt));
  }
}