-- V10: tiến độ học. Sinh viên tự đánh dấu bài học đã học; bài kiểm tra tính theo bài nộp (không cần bảng mới).
-- Tiến độ = (bài học đã học + bài kiểm tra đã nộp) / (bài học đang hiện + bài kiểm tra), tính lúc đọc.

CREATE TABLE lesson_completions (
    lesson_id    BIGINT UNSIGNED NOT NULL,
    user_id      BIGINT UNSIGNED NOT NULL,
    completed_at DATETIME(3)     NOT NULL,
    PRIMARY KEY (lesson_id, user_id),
    KEY idx_completions_user (user_id),
    CONSTRAINT fk_completions_lesson FOREIGN KEY (lesson_id) REFERENCES lessons (id) ON DELETE CASCADE,
    CONSTRAINT fk_completions_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
