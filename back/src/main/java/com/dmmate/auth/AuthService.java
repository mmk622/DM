package com.dmmate.auth;

import com.dmmate.security.JwtTokenProvider;
import com.dmmate.user.User;
import com.dmmate.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository users;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwt;
  private final OtpService otpService;

  /**
   * 이메일/OTP 검증에 성공했을 때 호출.
   * 프론트에서 name을 보내오지만 현재 User 엔티티엔 name 필드가 없으므로 사용하지 않습니다.
   */
  public AuthResponse handleEmailVerifiedAndSignIn(String email, String name) {
    String normalized = email.trim().toLowerCase();

    User u = users.findByEmail(normalized).orElse(null);
    if (u == null) {
      // 최초 로그인인 경우: 임시 패스워드로 계정 생성(프로필 미완성 상태)
      User nu = new User();
      nu.setEmail(normalized);
      nu.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));
      nu.setNickname(null); // 아직 미설정
      nu.setEmailVerifiedAt(LocalDateTime.now());
      nu.setCreatedAt(LocalDateTime.now());
      nu.setUpdatedAt(LocalDateTime.now());
      u = users.save(nu);
    } else {
      // 기존 계정: 이메일 미검증 상태였다면 검증 표시
      if (u.getEmailVerifiedAt() == null) {
        u.setEmailVerifiedAt(LocalDateTime.now());
        u.setUpdatedAt(LocalDateTime.now());
        users.save(u);
      }
    }

    // OTP 1회용 소모
    otpService.consumeVerification(normalized);

    boolean profileIncomplete = (u.getNickname() == null || u.getNickname().isBlank());
    return issueTokens(u, profileIncomplete);
  }

  /**
   * 회원가입 추가 정보 완료(닉네임/패스워드 설정).
   */
  public AuthResponse completeSignupFor(String email, String nickname, String rawPassword) {
    String normalized = email.trim().toLowerCase();

    User u = users.findByEmail(normalized)
        .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다: " + normalized));

    u.setNickname(nickname);
    u.setPasswordHash(passwordEncoder.encode(rawPassword));
    u.setUpdatedAt(LocalDateTime.now());
    users.save(u);

    return issueTokens(u, false);
  }

  /* ---------- 내부 공통 메서드 ---------- */

  private AuthResponse issueTokens(User u, boolean profileIncomplete) {
    String access = jwt.createAccessToken(u.getEmail());
    String refresh = jwt.createRefreshToken(u.getEmail());
    return new AuthResponse(access, refresh, profileIncomplete);
  }
}