-- V7: bài nộp của sinh viên. Mỗi sinh viên một bài nộp cho mỗi bài (assessment), gồm nhiều file.
-- submitted_at NULL = đang làm (còn thêm/xóa file được); khác NULL = đã bấm nộp, khóa bài làm.
-- Hết giờ (assessments.closes_at) mà chưa bấm nộp thì các file đã tải lên được tính là bài nộp.

CREATE TABLE submissions (
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    public_id     CHAR(22)        NOT NULL,
    assessment_id BIGINT UNSIGNED NOT NULL,
    user_id       BIGINT UNSIGNED NOT NULL,
    submitted_at  DATETIME(3)     NULL,
    created_at    DATETIME(3)     NOT NULL,
    updated_at    DATETIME(3)     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_submissions_public_id (public_id),
    UNIQUE KEY uk_submissions_assessment_user (assessment_id, user_id),
    KEY idx_submissions_user (user_id),
    CONSTRAINT fk_submissions_assessment FOREIGN KEY (assessment_id) REFERENCES assessments (id) ON DELETE CASCADE,
    CONSTRAINT fk_submissions_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE submission_files (
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    public_id     CHAR(22)        NOT NULL,
    submission_id BIGINT UNSIGNED NOT NULL,
    name          VARCHAR(255)    NOT NULL,           -- tên gốc, chỉ để hiển thị/tải về
    content_type  VARCHAR(100)    NOT NULL,
    size_bytes    BIGINT UNSIGNED NOT NULL,
    storage_key   CHAR(43)        NOT NULL,           -- tên file ngẫu nhiên trong thư mục lưu trữ
    created_at    DATETIME(3)     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_submission_files_public_id (public_id),
    UNIQUE KEY uk_submission_files_storage_key (storage_key),
    KEY idx_submission_files_submission (submission_id),
    CONSTRAINT fk_submission_files_submission FOREIGN KEY (submission_id) REFERENCES submissions (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
