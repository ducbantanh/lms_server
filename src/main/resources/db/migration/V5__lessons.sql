-- V5: bài học (lesson) nằm giữa mục/tuần và tài liệu.
-- Course → Section/Tuần → Lesson → tài liệu đính kèm. Khóa học "chỉ danh sách file" không dùng lesson.

CREATE TABLE lessons (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    public_id   CHAR(22)        NOT NULL,
    course_id   BIGINT UNSIGNED NOT NULL,           -- giữ trực tiếp để kiểm tra quyền không cần join
    section_id  BIGINT UNSIGNED NULL,               -- NULL = chưa phân mục
    position    INT             NOT NULL,           -- thứ tự trong mục
    title       VARCHAR(200)    NOT NULL,
    body        MEDIUMTEXT      NULL,               -- nội dung văn bản thuần, hiển thị giữ xuống dòng
    hidden      TINYINT(1)      NOT NULL DEFAULT 0, -- 1 = sinh viên không thấy bài và file đính kèm
    created_at  DATETIME(3)     NOT NULL,
    updated_at  DATETIME(3)     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_lessons_public_id (public_id),
    KEY idx_lessons_course (course_id, section_id, position),
    CONSTRAINT fk_lessons_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE,
    CONSTRAINT fk_lessons_section FOREIGN KEY (section_id) REFERENCES course_sections (id) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- File đính kèm bài học; section_id của file luôn bằng section_id của bài để khi xóa bài
-- (lesson_id → NULL) file vẫn nằm đúng mục.
ALTER TABLE course_files
    ADD COLUMN lesson_id BIGINT UNSIGNED NULL AFTER section_id,
    ADD KEY idx_files_lesson (lesson_id),
    ADD CONSTRAINT fk_files_lesson FOREIGN KEY (lesson_id) REFERENCES lessons (id) ON DELETE SET NULL;
