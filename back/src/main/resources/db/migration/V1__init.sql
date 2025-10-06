CREATE TABLE IF NOT EXISTS users (
  id               BIGINT PRIMARY KEY AUTO_INCREMENT,
  email            VARCHAR(255) NOT NULL UNIQUE,
  -- 프로필 미완성 상태 허용
  name             VARCHAR(100)     NULL,
  nickname         VARCHAR(100)     NULL,
  password_hash    VARCHAR(255)     NULL,

  created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       DATETIME     NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  email_verified_at DATETIME    NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- (선택) 아직 권한/역할 안 쓰면 이 테이블은 만들지 않아도 됨
-- CREATE TABLE IF NOT EXISTS user_roles (
--   id        BIGINT PRIMARY KEY AUTO_INCREMENT,
--   user_id   BIGINT NOT NULL,
--   role      VARCHAR(50) NOT NULL,
--   CONSTRAINT fk_user_roles_user
--     FOREIGN KEY (user_id) REFERENCES users(id)
--     ON DELETE CASCADE
-- );
