package com.dmmate.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "users",
       indexes = {
         @Index(name = "idx_users_email", columnList = "email", unique = true),
         @Index(name = "idx_users_nickname", columnList = "nickname")
       })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이메일만 필수
    @Column(name = "email", nullable = false, unique = true, length = 190)
    private String email;

    // 이름/닉네임/패스워드는 OTP 이후 프로필 완성 단계에서 채울 수 있도록 nullable
    @Column(name = "name", nullable = true, length = 100)
    private String name;

    @Column(name = "nickname", nullable = true, length = 100)
    private String nickname;

    @Column(name = "password_hash", nullable = true, length = 255)
    private String passwordHash;

    // 이메일 인증 시각(OTP 검증 완료 시 기록)
    @Column(name = "email_verified_at", nullable = true)
    private LocalDateTime emailVerifiedAt;

    // 생성/수정 시각
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // === 라이프사이클 ===
    @PrePersist
    protected void onCreate() {
        final var now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // === 도메인 편의 메서드 ===
    public boolean isProfileIncomplete() {
        return (this.name == null || this.name.isBlank())
            || (this.nickname == null || this.nickname.isBlank())
            || (this.passwordHash == null || this.passwordHash.isBlank());
    }

    public void markEmailVerifiedNow() {
        this.emailVerifiedAt = LocalDateTime.now();
    }

    // === 기본 생성자 ===
    protected User() {
    }

    public User(String email) {
      this.email = email;
      this.createdAt = LocalDateTime.now();
      this.updatedAt = LocalDateTime.now();
    }

    // === Getter/Setter ===
    public Long getId() { return id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public LocalDateTime getEmailVerifiedAt() { return emailVerifiedAt; }
    public void setEmailVerifiedAt(LocalDateTime emailVerifiedAt) { this.emailVerifiedAt = emailVerifiedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // === equals/hashCode ===
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        // 영속 전/후 모두 안전하게 비교: 이메일 고유
        return Objects.equals(email, other.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    // === toString (민감정보 제외) ===
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                ", emailVerifiedAt=" + emailVerifiedAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}