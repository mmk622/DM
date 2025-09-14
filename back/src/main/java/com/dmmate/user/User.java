package com.dmmate.user;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String name;

  @Column(unique = true)
  private String nickname;

  @Column(name = "password_hash")
  private String passwordHash;

  public enum Status {
    ACTIVE, BANNED, PROFILE_INCOMPLETE
  }

  @Enumerated(EnumType.STRING)
  @Builder.Default
  private Status status = Status.PROFILE_INCOMPLETE;

  private LocalDateTime createdAt;

  @PrePersist
  void prePersist() {
    if (createdAt == null)
      createdAt = LocalDateTime.now();
  }
}