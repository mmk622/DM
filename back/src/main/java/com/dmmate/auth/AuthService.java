package com.dmmate.auth;

import com.dmmate.auth.dto.AuthUserDto;
import com.dmmate.user.User;
import com.dmmate.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * OTP 인증 직후(이메일 검증 완료 시) 사용자 조회/생성 + 인증시각 기록.
     * 컨트롤러에서 afterVerifiedLookupOnly(...)를 호출하므로 시그니처를 맞춰둔다.
     */
    public AuthUserDto afterVerifiedLookupOnly(String email) {
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(new User(email)));

        // 이메일 인증 시각 갱신
        user.setEmailVerifiedAt(LocalDateTime.now());
        userRepository.save(user);

        return AuthUserDto.from(user);
    }

    /**
     * 과거/다른 코드 호환: 이름이 다른 동일 목적 메서드
     */
    public AuthUserDto handleEmailVerifiedAndSignIn(String email) {
        return afterVerifiedLookupOnly(email);
    }

    /**
     * 프로필 정보 완성(이름/닉네임/비밀번호 설정)
     * 컨트롤러에서 completeSignup(...)을 호출하므로 시그니처를 맞춰둔다.
     */
    public AuthUserDto completeSignup(String email, String name, String nickname, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found: " + email));

        user.setName(name);
        user.setNickname(nickname);
        if (rawPassword != null && !rawPassword.isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(rawPassword));
        }
        userRepository.save(user);

        return AuthUserDto.from(user);
    }

    /**
     * 과거/다른 코드 호환: 이름만 다른 동일 목적 메서드
     */
    public AuthUserDto completeProfile(String email, String name, String nickname, String rawPassword) {
        return completeSignup(email, name, nickname, rawPassword);
    }
}