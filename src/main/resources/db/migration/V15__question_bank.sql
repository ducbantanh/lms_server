-- V15: ngân hàng câu hỏi trắc nghiệm của giảng viên, không gắn với khóa học nào.
-- Mỗi giảng viên có nhiều ngân hàng (vd. "Java – Chương 1"); bài thi sau này chép câu hỏi sang bài
-- (bản chụp), nên sửa/xóa câu trong ngân hàng không làm đổi bài thi đã tạo.

CREATE TABLE question_banks (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    public_id   CHAR(22)        NOT NULL,
    owner_id    BIGINT UNSIGNED NOT NULL,           -- xóa tài khoản giảng viên thì xóa luôn ngân hàng (muốn giữ: khóa tài khoản)
    name        VARCHAR(200)    NOT NULL,
    description VARCHAR(2000)   NULL,
    created_at  DATETIME(3)     NOT NULL,
    updated_at  DATETIME(3)     NOT NULL,           -- đổi cả khi thêm/sửa/xóa câu hỏi bên trong
    PRIMARY KEY (id),
    UNIQUE KEY uk_question_banks_public_id (public_id),
    KEY idx_question_banks_owner (owner_id, updated_at),
    CONSTRAINT fk_question_banks_owner FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE bank_questions (
    id          BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT,
    public_id   CHAR(22)         NOT NULL,
    bank_id     BIGINT UNSIGNED  NOT NULL,
    body        TEXT             NOT NULL,          -- văn bản thuần, hiển thị giữ xuống dòng
    multiple    TINYINT(1)       NOT NULL,          -- 0 chọn một đáp án, 1 chọn nhiều đáp án
    difficulty  TINYINT UNSIGNED NOT NULL,          -- 0 dễ, 1 trung bình, 2 khó
    explanation VARCHAR(2000)    NULL,              -- giải thích đáp án, hiện cho sinh viên khi bài thi cho xem đáp án
    created_at  DATETIME(3)      NOT NULL,
    updated_at  DATETIME(3)      NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_bank_questions_public_id (public_id),
    KEY idx_bank_questions_bank (bank_id, id),
    CONSTRAINT fk_bank_questions_bank FOREIGN KEY (bank_id) REFERENCES question_banks (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Đáp án luôn sửa cùng câu hỏi (thay cả danh sách) nên không cần public_id; position 0 = A, 1 = B...
CREATE TABLE bank_question_options (
    question_id BIGINT UNSIGNED NOT NULL,
    position    INT             NOT NULL,
    body        VARCHAR(1000)   NOT NULL,
    correct     TINYINT(1)      NOT NULL,
    PRIMARY KEY (question_id, position),
    CONSTRAINT fk_bank_question_options_question FOREIGN KEY (question_id) REFERENCES bank_questions (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
