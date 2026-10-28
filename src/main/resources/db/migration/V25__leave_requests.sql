-- V25: đơn xin nghỉ buổi học. Sinh viên viết lý do, đính kèm minh chứng (ảnh, PDF) cho một buổi học, chậm nhất
-- leave_notice_hours giờ trước giờ bắt đầu buổi (giảng viên phụ trách đặt cho từng khóa học). Giảng viên duyệt thì
-- sinh viên được điểm danh "có phép" ở buổi đó.

ALTER TABLE courses
    ADD COLUMN leave_notice_hours SMALLINT UNSIGNED NOT NULL DEFAULT 24 AFTER enroll_key;

CREATE TABLE leave_requests (
    id           BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT,
    public_id    CHAR(22)         NOT NULL,
    meeting_id   BIGINT UNSIGNED  NOT NULL,
    course_id    BIGINT UNSIGNED  NOT NULL,
    user_id      BIGINT UNSIGNED  NOT NULL,
    reason       VARCHAR(1000)    NOT NULL,
    status       TINYINT UNSIGNED NOT NULL,          -- 0 chờ duyệt, 1 đồng ý, 2 từ chối
    teacher_note VARCHAR(500)     NULL,
    reviewed_by  BIGINT UNSIGNED  NULL,
    reviewed_at  DATETIME(3)      NULL,
    created_at   DATETIME(3)      NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_leave_requests_public_id (public_id),
    UNIQUE KEY uk_leave_requests_meeting_user (meeting_id, user_id),
    KEY idx_leave_requests_course (course_id, status, created_at),
    KEY idx_leave_requests_user (user_id),
    CONSTRAINT fk_leave_requests_meeting FOREIGN KEY (meeting_id) REFERENCES class_meetings (id) ON DELETE CASCADE,
    CONSTRAINT fk_leave_requests_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE,
    CONSTRAINT fk_leave_requests_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_leave_requests_reviewer FOREIGN KEY (reviewed_by) REFERENCES users (id) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- File minh chứng; nội dung lưu trên đĩa như bài nộp (FileStorage), DB chỉ giữ metadata.
CREATE TABLE leave_request_files (
    id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    public_id    CHAR(22)        NOT NULL,
    request_id   BIGINT UNSIGNED NOT NULL,
    name         VARCHAR(255)    NOT NULL,
    content_type VARCHAR(100)    NOT NULL,
    size_bytes   BIGINT UNSIGNED NOT NULL,
    storage_key  CHAR(43)        NOT NULL,
    created_at   DATETIME(3)     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_leave_request_files_public_id (public_id),
    UNIQUE KEY uk_leave_request_files_storage_key (storage_key),
    KEY idx_leave_request_files_request (request_id),
    CONSTRAINT fk_leave_request_files_request FOREIGN KEY (request_id) REFERENCES leave_requests (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
