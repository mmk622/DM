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