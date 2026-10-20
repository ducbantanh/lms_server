-- V9: thông báo. Một thông báo (nội dung chung) gửi tới nhiều người nhận, mỗi người có trạng thái đã đọc riêng.
-- Tự động: tạo bài kiểm tra / bài thực hành. Tùy chọn: bài tập, bài học, tài liệu. Thủ công: giảng viên soạn.

CREATE TABLE notifications (
    id            BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT,
    public_id     CHAR(22)         NOT NULL,
    course_id     BIGINT UNSIGNED  NOT NULL,
    category      TINYINT UNSIGNED NOT NULL,          -- 0 bài kiểm tra, 1 bài thực hành, 2 bài tập, 3 nội dung, 4 giảng viên gửi
    title         VARCHAR(200)     NOT NULL,
    body          VARCHAR(2000)    NULL,
    assessment_id BIGINT UNSIGNED  NULL,              -- thông báo về một bài: xóa bài thì xóa thông báo
    sender_id     BIGINT UNSIGNED  NULL,              -- NULL nếu tài khoản người gửi đã bị xóa
    created_at    DATETIME(3)      NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_notifications_public_id (public_id),
    KEY idx_notifications_course (course_id, created_at),
    CONSTRAINT fk_notifications_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE,
    CONSTRAINT fk_notifications_assessment FOREIGN KEY (assessment_id) REFERENCES assessments (id) ON DELETE CASCADE,
    CONSTRAINT fk_notifications_sender FOREIGN KEY (sender_id) REFERENCES users (id) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE notification_recipients (
    notification_id BIGINT UNSIGNED NOT NULL,
    user_id         BIGINT UNSIGNED NOT NULL,
    read_at         DATETIME(3)     NULL,             -- NULL = chưa đọc
    PRIMARY KEY (notification_id, user_id),
    KEY idx_recipients_user (user_id, read_at),
    CONSTRAINT fk_recipients_notification FOREIGN KEY (notification_id) REFERENCES notifications (id) ON DELETE CASCADE,
    CONSTRAINT fk_recipients_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
