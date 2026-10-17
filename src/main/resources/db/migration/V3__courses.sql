-- V3: module khóa học tối thiểu (đầu giai đoạn 2): khóa học + ghi danh.
-- Mới đủ cho dashboard sinh viên; nội dung theo tuần, bài nộp... sẽ thêm ở migration sau.

CREATE TABLE courses (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    public_id   CHAR(22)        NOT NULL,           -- ID ngẫu nhiên dùng trong URL/API
    code        VARCHAR(32)     NOT NULL,           -- mã học phần, ví dụ INT1340
    title       VARCHAR(200)    NOT NULL,
    teacher_id  BIGINT UNSIGNED NULL,               -- giảng viên phụ trách; NULL nếu chưa phân công
    created_at  DATETIME(3)     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_courses_public_id (public_id),
    KEY idx_courses_teacher (teacher_id),
    CONSTRAINT fk_courses_teacher FOREIGN KEY (teacher_id) REFERENCES users (id) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE enrollments (
    course_id   BIGINT UNSIGNED NOT NULL,
    user_id     BIGINT UNSIGNED NOT NULL,
    enrolled_at DATETIME(3)     NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (course_id, user_id),
    KEY idx_enrollments_user (user_id),
    CONSTRAINT fk_enrollments_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE,
    CONSTRAINT fk_enrollments_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
