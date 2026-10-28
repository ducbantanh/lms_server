-- V4: giai đoạn 2 — giảng viên quản lý khóa học: quyền courses.manage, thông tin khóa học,
-- cách ghi danh, mục/tuần, tài liệu (file lưu trên đĩa, DB chỉ giữ metadata) và bài kiểm tra.

INSERT INTO permissions (code, description) VALUES
    ('courses.manage', 'Create and manage own courses, content and enrollments');

-- Admin: toàn quyền (quản lý mọi khóa học); Teacher: khóa học mình phụ trách
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p ON p.code = 'courses.manage' WHERE r.id IN (0, 1);

ALTER TABLE courses ADD COLUMN description VARCHAR(2000)    NULL AFTER title;
ALTER TABLE courses ADD COLUMN format      TINYINT UNSIGNED NOT NULL DEFAULT 0 AFTER description; -- 0 mục tự đặt tên, 1 theo tuần, 2 chỉ danh sách file
ALTER TABLE courses ADD COLUMN enroll_key  VARCHAR(16)      NULL AFTER format;                    -- NULL = tắt tham gia bằng mã
ALTER TABLE courses ADD UNIQUE KEY uk_courses_enroll_key (enroll_key);

-- 0 dán MSSV, 1 chọn từ danh sách, 2 sinh viên tự nhập mã tham gia
ALTER TABLE enrollments
    ADD COLUMN method TINYINT UNSIGNED NOT NULL DEFAULT 0 AFTER user_id;

CREATE TABLE course_sections (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    public_id   CHAR(22)        NOT NULL,
    course_id   BIGINT UNSIGNED NOT NULL,
    position    INT             NOT NULL,
    title       VARCHAR(200)    NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_sections_public_id (public_id),
    KEY idx_sections_course (course_id, position),
    CONSTRAINT fk_sections_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE course_files (
    id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    public_id    CHAR(22)        NOT NULL,
    course_id    BIGINT UNSIGNED NOT NULL,
    section_id   BIGINT UNSIGNED NULL,               -- NULL = chưa phân mục
    name         VARCHAR(255)    NOT NULL,           -- tên gốc, chỉ để hiển thị/tải về
    content_type VARCHAR(100)    NOT NULL,
    size_bytes   BIGINT UNSIGNED NOT NULL,
    hidden       TINYINT(1)      NOT NULL DEFAULT 0, -- 1 = sinh viên không thấy
    storage_key  CHAR(43)        NOT NULL,           -- tên file ngẫu nhiên trong thư mục lưu trữ
    created_at   DATETIME(3)     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_files_public_id (public_id),
    UNIQUE KEY uk_files_storage_key (storage_key),
    KEY idx_files_course (course_id),
    CONSTRAINT fk_files_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE,
    CONSTRAINT fk_files_section FOREIGN KEY (section_id) REFERENCES course_sections (id) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE assessments (
    id            BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT,
    public_id     CHAR(22)         NOT NULL,
    course_id     BIGINT UNSIGNED  NOT NULL,
    type          TINYINT UNSIGNED NOT NULL,          -- 0 luyện tập, 1 thực hành, 2 thi
    title         VARCHAR(200)     NOT NULL,
    description   VARCHAR(2000)    NULL,
    paper_file_id BIGINT UNSIGNED  NULL,              -- file đề (PDF) trong course_files
    opens_at      DATETIME(3)      NULL,              -- NULL = mở ngay
    closes_at     DATETIME(3)      NULL,              -- NULL = không hạn
    created_at    DATETIME(3)      NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_assessments_public_id (public_id),
    KEY idx_assessments_course (course_id),
    CONSTRAINT fk_assessments_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE,
    CONSTRAINT fk_assessments_paper FOREIGN KEY (paper_file_id) REFERENCES course_files (id) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
