-- V24: lịch học do admin lập. Phòng học chọn từ danh mục phòng; mỗi khóa học có các quy tắc
-- lặp hằng tuần ("Thứ 3, tiết 1–3, phòng 402-A2, từ 08/09 đến 15/12"), server sinh sẵn từng buổi cụ thể để hủy,
-- dời, thêm buổi bù và gắn buổi điểm danh. 1 tiết = 1 giờ; giờ của tiết 1 và số tiết mỗi ngày đặt ở app.schedule.

INSERT INTO permissions (code, description) VALUES
    ('schedules.manage', 'Manage rooms and class schedules');

INSERT INTO role_permissions (role_id, permission_id)
SELECT 0, id FROM permissions WHERE code = 'schedules.manage';

CREATE TABLE rooms (
    id          BIGINT UNSIGNED   NOT NULL AUTO_INCREMENT,
    public_id   CHAR(22)          NOT NULL,
    name        VARCHAR(32)       NOT NULL,          -- tên/số phòng, vd. 402-A2
    building    VARCHAR(64)       NULL,
    capacity    SMALLINT UNSIGNED NULL,
    note        VARCHAR(200)      NULL,
    active      BOOLEAN           NOT NULL,          -- ngừng dùng: không chọn được cho lịch mới, lịch cũ giữ nguyên
    created_at  DATETIME(3)       NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_rooms_public_id (public_id),
    UNIQUE KEY uk_rooms_name (name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE course_schedules (
    id            BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT,
    public_id     CHAR(22)         NOT NULL,
    course_id     BIGINT UNSIGNED  NOT NULL,
    room_id       BIGINT UNSIGNED  NOT NULL,
    weekday       TINYINT UNSIGNED NOT NULL,         -- 1 thứ 2 … 7 chủ nhật (ISO)
    start_period  TINYINT UNSIGNED NOT NULL,         -- tiết bắt đầu, từ 1
    periods       TINYINT UNSIGNED NOT NULL,         -- số tiết liên tiếp
    kind          TINYINT UNSIGNED NOT NULL,         -- 0 lý thuyết, 1 thực hành
    starts_on     DATE             NOT NULL,
    ends_on       DATE             NOT NULL,
    created_by    BIGINT UNSIGNED  NULL,
    created_at    DATETIME(3)      NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_course_schedules_public_id (public_id),
    KEY idx_course_schedules_course (course_id),
    CONSTRAINT fk_course_schedules_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE,
    CONSTRAINT fk_course_schedules_room FOREIGN KEY (room_id) REFERENCES rooms (id),
    CONSTRAINT fk_course_schedules_creator FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Từng buổi học. schedule_id NULL = buổi bù/buổi lẻ, hoặc quy tắc đã xóa (buổi đã qua được giữ lại).
CREATE TABLE class_meetings (
    id                     BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT,
    public_id              CHAR(22)         NOT NULL,
    course_id              BIGINT UNSIGNED  NOT NULL,
    schedule_id            BIGINT UNSIGNED  NULL,
    room_id                BIGINT UNSIGNED  NOT NULL,
    meeting_date           DATE             NOT NULL,
    start_period           TINYINT UNSIGNED NOT NULL,
    periods                TINYINT UNSIGNED NOT NULL,
    kind                   TINYINT UNSIGNED NOT NULL,
    cancelled              BOOLEAN          NOT NULL,
    original_date          DATE             NULL,     -- buổi đã dời: ngày, tiết ban đầu (lần dời đầu tiên)
    original_start_period  TINYINT UNSIGNED NULL,
    note                   VARCHAR(200)     NULL,
    attendance_session_id  BIGINT UNSIGNED  NULL,     -- buổi điểm danh mở từ buổi học này
    created_at             DATETIME(3)      NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_class_meetings_public_id (public_id),
    KEY idx_class_meetings_date (meeting_date, start_period),
    KEY idx_class_meetings_course (course_id, meeting_date),
    KEY idx_class_meetings_room (room_id, meeting_date),
    KEY idx_class_meetings_schedule (schedule_id),
    CONSTRAINT fk_class_meetings_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE,
    CONSTRAINT fk_class_meetings_schedule FOREIGN KEY (schedule_id) REFERENCES course_schedules (id) ON DELETE SET NULL,
    CONSTRAINT fk_class_meetings_room FOREIGN KEY (room_id) REFERENCES rooms (id),
    CONSTRAINT fk_class_meetings_attendance FOREIGN KEY (attendance_session_id) REFERENCES attendance_sessions (id)
        ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
