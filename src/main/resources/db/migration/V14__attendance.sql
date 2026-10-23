-- V14: điểm danh theo buổi học. Sinh viên quét mã QR (đổi mỗi 10 giây) trên màn chiếu của giảng viên,
-- hoặc giảng viên điểm danh thủ công (quên điện thoại, máy lỗi...). Không có dòng = vắng.

CREATE TABLE attendance_sessions (
    id          BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT,
    public_id   CHAR(22)         NOT NULL,
    course_id   BIGINT UNSIGNED  NOT NULL,
    title       VARCHAR(200)     NOT NULL,
    qr_secret   CHAR(43)         NOT NULL,          -- khóa HMAC ký mã QR của buổi, không bao giờ trả ra API
    qr_open     BOOLEAN          NOT NULL,          -- đang nhận điểm danh bằng QR
    created_by  BIGINT UNSIGNED  NULL,
    created_at  DATETIME(3)      NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_attendance_sessions_public_id (public_id),
    KEY idx_attendance_sessions_course (course_id, created_at),
    CONSTRAINT fk_attendance_sessions_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE,
    CONSTRAINT fk_attendance_sessions_creator FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE attendance_records (
    session_id  BIGINT UNSIGNED  NOT NULL,
    user_id     BIGINT UNSIGNED  NOT NULL,
    status      TINYINT UNSIGNED NOT NULL,          -- 0 có mặt, 1 đi muộn, 2 vắng có phép
    method      TINYINT UNSIGNED NOT NULL,          -- 0 quét QR, 1 giảng viên điểm danh thủ công
    checked_at  DATETIME(3)      NOT NULL,
    marked_by   BIGINT UNSIGNED  NULL,              -- giảng viên điểm danh thủ công; NULL khi quét QR
    ip          VARCHAR(45)      NULL,              -- IP lúc quét QR, để giảng viên đối chiếu khi nghi điểm danh hộ
    PRIMARY KEY (session_id, user_id),
    KEY idx_attendance_records_user (user_id),
    CONSTRAINT fk_attendance_records_session FOREIGN KEY (session_id) REFERENCES attendance_sessions (id) ON DELETE CASCADE,
    CONSTRAINT fk_attendance_records_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_attendance_records_marker FOREIGN KEY (marked_by) REFERENCES users (id) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
