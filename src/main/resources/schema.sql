
CREATE TABLE IF NOT EXISTS tokens (
   id INT AUTO_INCREMENT NOT NULL,  -- 기본 키로 사용할 자동 증가 정수
   `token` VARCHAR(1024) NOT NULL,  -- JWT 또는 인증 토큰 문자열 (최대 1024자)
   token_type VARCHAR(50) NOT NULL,  -- 토큰 유형 (예: BEARER, API 등)
   expired BOOLEAN NULL,  -- 만료 여부 (true: 만료됨, false: 유효함)
   revoked BOOLEAN NULL,  -- 철회 여부 (true: 철회됨, false: 사용 가능)
   user_name VARCHAR(255) NOT NULL,  -- 해당 토큰이 연결된 사용자 식별자 (예: username 또는 email)
   CONSTRAINT tokens_pk PRIMARY KEY (id),
   CONSTRAINT tokens_un UNIQUE (`token`) -- 중복 토큰 방지 (token은 유일해야 함)
);

CREATE TABLE IF NOT EXISTS users (
      id INT AUTO_INCREMENT NOT NULL,
      user_id VARCHAR(255),
      email VARCHAR(255),
      name VARCHAR(255),
      password VARCHAR(255),
      PRIMARY KEY (id),
      CONSTRAINT uk_users_email UNIQUE (email)
);

INSERT IGNORE INTO users (user_id, email, name, password)
VALUES ('swlee724@blackholic.com', 'swlee724@blackholic.com', '이승우', '$2a$10$OfxVYABMgei99MDqnkRBMexihMXvR3t1uFZ.9LPM1cDS9pmjTPUHu');
