-- 게시글 평점 테이블 (0~10, 반개=1점)
CREATE TABLE IF NOT EXISTS post_ratings (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  post_id      BIGINT       NOT NULL,
  rater_email  VARCHAR(255) NOT NULL,
  score        TINYINT      NOT NULL CHECK (score BETWEEN 0 AND 10),
  created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   DATETIME     NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  CONSTRAINT fk_rating_post  FOREIGN KEY (post_id)     REFERENCES posts(id)     ON DELETE CASCADE,
  CONSTRAINT fk_rating_user  FOREIGN KEY (rater_email) REFERENCES users(email)  ON DELETE CASCADE,
  CONSTRAINT uq_rating UNIQUE (post_id, rater_email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;