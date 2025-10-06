package com.dmmate.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 320)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash; // nullable (이메일 인증만 끝난 상태일 수 있음)

    @Column
    private String nickname;     // nullable

    @Column
    private String name;         // nullable

    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt; // nullable -> 인증되면 세팅

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** 최초 이메일 인증 통과 시점에, 이메일만으로 유저를 생성하는 팩토리 */
    public static User createForEmailOnly(String email) {
        User u = new User();
        u.setEmail(email);
        u.setEmailVerifiedAt(LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now();
        u.setCreatedAt(now);
        u.setUpdatedAt(now);
        return u;
    }
}