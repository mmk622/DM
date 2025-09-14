package com.dmmate.auth;

import com.dmmate.auth.dto.*;
import com.dmmate.common.MailService;
import com.dmmate.security.JwtTokenProvider;
import com.dmmate.user.*;
import com.dmmate.user.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final OtpService otpService;
  private final MailService mailService;
  private final UserRepository users;
  private final JwtTokenProvider jwt;
  private final PasswordEncoder encoder; // 생성자 주입

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
    User user = users.findByEmail(req.email()).orElseGet(() -> users.save(
        User.builder()
            .email(req.email())
            .name(req.name())
            .status(User.Status.PROFILE_INCOMPLETE)
            .build()));
    otpService.consume(req.email());
    String at = jwt.createAccessToken(user.getId(), user.getEmail());
    String rt = jwt.createRefreshToken(user.getId(), user.getEmail());
    boolean incomplete = user.getStatus() == User.Status.PROFILE_INCOMPLETE;
    return ResponseEntity.ok(new AuthResponse(at, rt, incomplete));
  }

  @GetMapping("/check-nickname")
  public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
    return ResponseEntity.ok(!users.existsByNickname(nickname));
  }

  @PostMapping("/complete-signup")
  public ResponseEntity<?> completeSignup(
      @Valid @RequestBody CompleteSignupRequest req,
      @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
    var u = users.findByEmail(principal.getUsername()).orElseThrow();
    if (users.existsByNickname(req.nickname())) {
      return ResponseEntity.status(409).body("이미 사용 중인 닉네임입니다.");
    }
    u.setNickname(req.nickname());
    u.setPasswordHash(encoder.encode(req.password()));
    u.setStatus(User.Status.ACTIVE);
    users.save(u);
    return ResponseEntity.ok().build();
  }
}