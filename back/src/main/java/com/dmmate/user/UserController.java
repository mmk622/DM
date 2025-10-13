// UserController.java
package com.dmmate.user;

import com.dmmate.user.dto.MeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepo;

    public UserController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // /api/users/me 는 SecurityConfig 에서 authenticated()로 보호중이라고 가정
    @GetMapping("/me")
    public MeResponse me(Authentication auth) {
        // JwtAuthFilter 가 이메일을 Principal 로 넣었다고 가정 (없으면 auth.getName()에 email 세팅 로직 점검)
        String email = auth.getName();

        User u = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found: " + email));

        // 권한/롤이 엔티티에 없다면 일단 빈 리스트로
        List<String> roles = List.of(); // 필요하면 u.getRoles().stream().map(Role::getName).toList()

        return new MeResponse(
                u.getEmail(),
                u.getName(), // ✅ 엔티티에 getter 가 있어야 함
                u.getNickname(),
                roles,
                u.getCreatedAt(), // ✅ 엔티티에 getter 가 있어야 함 (LocalDateTime)
                u.getUpdatedAt());
    }

    /** 회원 탈퇴: 자신의 계정 삭제 */
    @DeleteMapping({ "/me", "/me/" })
    public ResponseEntity<Void> deleteMe(Authentication auth) {
        String email = auth.getName(); // JwtAuthFilter 가 principal = email 로 세팅했다고 가정
        userRepo.findByEmail(email).ifPresent(userRepo::delete); // 존재하면 삭제
        return ResponseEntity.noContent().build(); // 204
    }
}