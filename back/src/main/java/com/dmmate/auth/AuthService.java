package com.dmmate.auth;

import com.dmmate.auth.dto.AuthUserDto;
import com.dmmate.user.User;
import com.dmmate.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.dmmate.security.JwtTokenProvider jwtTokenProvider;

    /**
     * OTP 검증 통과 직후: 유저가 없으면 이메일만으로 생성, 있으면 그대로 반환
     * 프론트는 반환값의 incomplete 여부를 보고 추가 정보 입력 화면으로 보냄
     */
    public AuthUserDto afterVerifiedLookupOnly(String email) {
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.createForEmailOnly(email)));
        return AuthUserDto.from(user);
    }

    /** 회원가입 완료(이름/닉네임/비번 설정) */
    @Transactional
    public AuthUserDto completeSignup(String email, String name, String nickname, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("email already exists");
        }

        LocalDateTime now = LocalDateTime.now();

        User user = userRepository.findByEmail(email).orElseGet(() -> User.builder()
                .email(email)
                .name(name)
                .nickname(nickname)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .emailVerifiedAt(now)
                .createdAt(now) // ✅ 반드시 채우기
                .updatedAt(now) // ✅ 반드시 채우기
                .build());

        // 혹시 findByEmail(...)로 기존 스텁을 가져왔는데 createdAt이 비어 있으면 보정
        if (user.getCreatedAt() == null)
            user.setCreatedAt(now);
        user.setUpdatedAt(now);

        userRepository.save(user);
        return AuthUserDto.from(user);
    }

    /** 이메일/비밀번호 로그인 -> 액세스 토큰 발급 */
    public String login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (user.getPasswordHash() == null || !passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        return jwtTokenProvider.createAccessToken(email);
    }
}