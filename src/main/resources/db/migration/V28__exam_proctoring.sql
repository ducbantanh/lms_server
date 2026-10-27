-- V28: giám sát bài kiểm tra (chống gian lận). Giảng viên bật cho từng bài kiểm tra (type = 2, mọi hình thức):
-- sinh viên phải vào "phòng thi" (phiên thi gắn với phiên đăng nhập và máy đang dùng) mới xem được đề và làm bài;
-- trình duyệt báo rời trang, thoát toàn màn hình, dán từ ngoài vào; đủ số lần vi phạm thì bài bị khóa chờ giám thị.

ALTER TABLE assessments
    ADD COLUMN proctored              TINYINT(1)        NOT NULL DEFAULT 0 AFTER score_release,
    ADD COLUMN proctor_networks       VARCHAR(500)      NULL AFTER proctored,          -- IP / dải CIDR được vào thi, NULL = mọi mạng
    ADD COLUMN proctor_access_code    TINYINT(1)        NOT NULL DEFAULT 0 AFTER proctor_networks, -- phải nhập mã phòng thi (đổi mỗi phút)
    ADD COLUMN proctor_fullscreen     TINYINT(1)        NOT NULL DEFAULT 1 AFTER proctor_access_code,
    ADD COLUMN proctor_max_violations SMALLINT UNSIGNED NULL AFTER proctor_fullscreen, -- đủ số lần thì khóa bài, NULL = chỉ ghi lại
    ADD COLUMN proctor_secret         CHAR(43)          NULL AFTER proctor_max_violations; -- khóa HMAC của mã phòng thi

-- Phiên thi: một dòng cho mỗi sinh viên ở mỗi bài, tạo khi sinh viên vào phòng thi.
CREATE TABLE proctor_sessions (
    id              BIGINT UNSIGNED   NOT NULL AUTO_INCREMENT,
    public_id       CHAR(22)          NOT NULL,
    assessment_id   BIGINT UNSIGNED   NOT NULL,
    course_id       BIGINT UNSIGNED   NOT NULL,
    user_id         BIGINT UNSIGNED   NOT NULL,
    auth_session_id BIGINT UNSIGNED   NULL,              -- phiên đăng nhập đang làm bài; NULL = giám thị cho đổi máy
    status          TINYINT UNSIGNED  NOT NULL,          -- 0 đang làm, 1 bị khóa, 2 đã kết thúc
    violations      SMALLINT UNSIGNED NOT NULL DEFAULT 0,
    start_ip        VARCHAR(45)       NULL,
    last_ip         VARCHAR(45)       NULL,
    user_agent      VARCHAR(255)      NULL,
    fullscreen      TINYINT(1)        NOT NULL DEFAULT 0, -- trạng thái trình duyệt báo ở nhịp gần nhất
    away_since      DATETIME(3)       NULL,              -- đang rời trang bài thi từ lúc này
    lock_reason     VARCHAR(200)      NULL,
    locked_at       DATETIME(3)       NULL,
    started_at      DATETIME(3)       NOT NULL,
    last_seen_at    DATETIME(3)       NOT NULL,
    finished_at     DATETIME(3)       NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_proctor_sessions_public_id (public_id),
    UNIQUE KEY uk_proctor_sessions_assessment_user (assessment_id, user_id),
    KEY idx_proctor_sessions_user (user_id, status),
    CONSTRAINT fk_proctor_sessions_assessment FOREIGN KEY (assessment_id) REFERENCES assessments (id) ON DELETE CASCADE,
    CONSTRAINT fk_proctor_sessions_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE,
    CONSTRAINT fk_proctor_sessions_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Nhật ký phiên thi: vi phạm trình duyệt báo, đổi IP, vào lại, khóa / mở khóa của giám thị.
CREATE TABLE proctor_events (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    session_id  BIGINT UNSIGNED NOT NULL,
    type        VARCHAR(32)     NOT NULL,
    counted     TINYINT(1)      NOT NULL,                -- tính là một lần vi phạm
    duration_ms INT UNSIGNED    NULL,                    -- rời trang bao lâu
    detail      VARCHAR(300)    NULL,
    ip          VARCHAR(45)     NULL,
    actor_id    BIGINT UNSIGNED NULL,                    -- giám thị khóa / mở khóa / cho đổi máy
    occurred_at DATETIME(3)     NOT NULL,
    PRIMARY KEY (id),
    KEY idx_proctor_events_session (session_id, id),
    CONSTRAINT fk_proctor_events_session FOREIGN KEY (session_id) REFERENCES proctor_sessions (id) ON DELETE CASCADE,
    CONSTRAINT fk_proctor_events_actor FOREIGN KEY (actor_id) REFERENCES users (id) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
