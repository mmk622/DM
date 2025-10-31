-- posts (게시글)
CREATE TABLE IF NOT EXISTS posts (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  author_id     VARCHAR(255) NOT NULL,        -- users.id FK
  title         VARCHAR(120) NOT NULL,
  content       TEXT         NOT NULL,
  meal_date     DATE         NOT NULL,       -- 밥 먹을 날짜
  gender_pref   VARCHAR(20)  NOT NULL,       -- ANY, MALE, FEMALE
  party_pref    VARCHAR(20)  NOT NULL,       -- ANY, TWO, THREE, FOUR_PLUS
  created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_posts_author FOREIGN KEY (author_id) REFERENCES users(email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_posts_meal_date   ON posts(meal_date);
CREATE INDEX idx_posts_created_at  ON posts(created_at);
CREATE INDEX idx_posts_title       ON posts(title);

-- comments (댓글)
CREATE TABLE IF NOT EXISTS comments (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  post_id     BIGINT       NOT NULL,
  author_id   VARCHAR(255) NOT NULL,           -- users.id FK
  content     TEXT         NOT NULL,
  created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_comments_post   FOREIGN KEY (post_id)  REFERENCES posts(id) ON DELETE CASCADE,
  CONSTRAINT fk_comments_author FOREIGN KEY (author_id) REFERENCES users(email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_comments_post_created ON comments(post_id, created_at);