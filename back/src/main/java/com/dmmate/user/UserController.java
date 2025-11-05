package com.dmmate.user;

import com.dmmate.board.repo.PostRepository;
import com.dmmate.board.repo.RatingRepository;
import com.dmmate.user.dto.MeResponse;
import com.dmmate.user.dto.PublicUserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepo;
    private final PostRepository postRepo;
    private final RatingRepository ratingRepo;

    public UserController(UserRepository userRepo, PostRepository postRepo, RatingRepository ratingRepo) {
        this.userRepo = userRepo;
        this.postRepo = postRepo;
        this.ratingRepo = ratingRepo;
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
                u.getName(), // 엔티티에 getter 가 있어야 함
                u.getNickname(),
                roles,
                u.getCreatedAt(), // 엔티티에 getter 가 있어야 함 (LocalDateTime)
                u.getUpdatedAt());
    }

    /** 회원 탈퇴: 자신의 계정 삭제 */
    @DeleteMapping({ "/me", "/me/" })
    public ResponseEntity<Void> deleteMe(Authentication auth) {
        String email = auth.getName(); // JwtAuthFilter 가 principal = email 로 세팅했다고 가정
        userRepo.findByEmail(email).ifPresent(userRepo::delete); // 존재하면 삭제
        return ResponseEntity.noContent().build(); // 204
    }

    /** 공개 프로필 조회 (닉네임 + 평균 평점 + 게시글 수 포함) */
    @GetMapping("/{email}")
    public PublicUserResponse getPublic(@PathVariable String email) {
        User u = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + email));

        // 사용자가 쓴 게시글 수
        long postCount = postRepo.countByAuthorId(email);

        // 해당 사용자의 모든 게시글에 대한 평균 평점
        Double avgRating = ratingRepo.getAverageScoreForAuthor(email);

        return PublicUserResponse.of(u, avgRating, postCount);
    }
}