package com.dmmate.board.repo;

import com.dmmate.board.domain.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
  Optional<Rating> findByPostIdAndRaterEmail(Long postId, String raterEmail);

  @Query("select coalesce(avg(r.score), 0) from Rating r where r.postId = :postId")
  Double getAverageScore(@Param("postId") Long postId);

  @Query("select count(r) from Rating r where r.postId = :postId")
  Long getCount(@Param("postId") Long postId);

  // 특정 사용자가 작성한 모든 게시글에 대한 평균 평점
  @Query("""
      select coalesce(avg(r.score), 0)
      from Rating r
      join Post p on r.postId = p.id
      where p.authorId = :email
      """)
  Double getAverageScoreForAuthor(@Param("email") String email);
}