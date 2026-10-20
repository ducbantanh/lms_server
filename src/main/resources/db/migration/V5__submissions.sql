-- V5: giai đoạn 2 — nộp bài kiểm tra và chấm điểm thủ công.

CREATE TABLE submissions (
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    public_id     CHAR(22)        NOT NULL,
    assessment_id BIGINT UNSIGNED NOT NULL,
    user_id       BIGINT UNSIGNED NOT NULL,
    file_name     VARCHAR(255)    NOT NULL,
    storage_key   CHAR(43)        NOT NULL,
    content_type  VARCHAR(100)    NOT NULL,
    size_bytes    BIGINT UNSIGNED NOT NULL,
    score         DECIMAL(5, 2)   NULL,
    feedback      VARCHAR(2000)   NULL,
    submitted_at  DATETIME(3)     NOT NULL,
    graded_at     DATETIME(3)     NULL,
    graded_by     BIGINT UNSIGNED NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_submissions_public_id (public_id),
    UNIQUE KEY uk_submissions_assessment_user (assessment_id, user_id),
    KEY idx_submissions_assessment (assessment_id),
    KEY idx_submissions_user (user_id),
    CONSTRAINT fk_submissions_assessment FOREIGN KEY (assessment_id) REFERENCES assessments (id) ON DELETE CASCADE,
    CONSTRAINT fk_submissions_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_submissions_grader FOREIGN KEY (graded_by) REFERENCES users (id) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
