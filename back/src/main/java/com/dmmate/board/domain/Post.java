package com.dmmate.board.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.*;

@Entity
@Table(name = "posts")
@Getter
@Setter
public class Post {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 255)
  private String authorId;
  @Column(nullable = false, length = 120)
  private String title;
  @Lob
  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  private LocalDate mealDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private GenderPref genderPref;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PartyPref partyPref;

  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
  @Column(nullable = false)
  private LocalDateTime updatedAt = LocalDateTime.now();

  @PreUpdate
  void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }
}