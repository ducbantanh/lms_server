-- V17: bài trắc nghiệm (thực hành / kiểm tra) tạo từ ngân hàng câu hỏi.
-- Câu hỏi được CHÉP sang bài lúc tạo (bản chụp): sửa/xóa ngân hàng sau đó không làm đổi bài đã tạo.
-- Mỗi sinh viên làm một lần: bấm "Bắt đầu" (submissions.started_at), chọn đáp án (lưu ngay từng câu),
-- bấm nộp hoặc hết giờ thì server chấm tự động (điểm = số câu đúng / tổng số câu × 10).

ALTER TABLE assessments
    ADD COLUMN answer_mode        TINYINT UNSIGNED NOT NULL DEFAULT 0 AFTER type, -- 0 nộp file, 1 trắc nghiệm
    ADD COLUMN time_limit_minutes INT UNSIGNED     NULL AFTER closes_at,          -- NULL = làm tới giờ đóng
    ADD COLUMN shuffle            TINYINT(1)       NOT NULL DEFAULT 0 AFTER time_limit_minutes, -- xáo câu và đáp án theo từng sinh viên
    ADD COLUMN reveal_mode        TINYINT UNSIGNED NOT NULL DEFAULT 0 AFTER shuffle; -- 0 không cho xem đáp án, 1 sau khi nộp, 2 sau giờ đóng

CREATE TABLE assessment_questions (
    id                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    public_id          CHAR(22)        NOT NULL,
    assessment_id      BIGINT UNSIGNED NOT NULL,
    position           INT             NOT NULL,
    body               TEXT            NOT NULL,
    multiple           TINYINT(1)      NOT NULL,
    explanation        VARCHAR(2000)   NULL,
    source_question_id BIGINT UNSIGNED NULL,       -- câu gốc trong ngân hàng (chỉ để tham khảo; câu gốc bị xóa thì NULL)
    PRIMARY KEY (id),
    UNIQUE KEY uk_assessment_questions_public_id (public_id),
    KEY idx_assessment_questions_assessment (assessment_id, position),
    CONSTRAINT fk_assessment_questions_assessment FOREIGN KEY (assessment_id) REFERENCES assessments (id) ON DELETE CASCADE,
    CONSTRAINT fk_assessment_questions_source FOREIGN KEY (source_question_id) REFERENCES bank_questions (id) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Đáp án là bản chụp, không sửa sau khi tạo bài: câu trả lời trỏ tới (câu hỏi, position)
CREATE TABLE assessment_question_options (
    question_id BIGINT UNSIGNED NOT NULL,
    position    INT             NOT NULL,
    body        VARCHAR(1000)   NOT NULL,
    correct     TINYINT(1)      NOT NULL,
    PRIMARY KEY (question_id, position),
    CONSTRAINT fk_assessment_question_options_question FOREIGN KEY (question_id) REFERENCES assessment_questions (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Mỗi đáp án sinh viên chọn là một dòng; câu chưa trả lời thì không có dòng
CREATE TABLE submission_answers (
    submission_id   BIGINT UNSIGNED NOT NULL,
    question_id     BIGINT UNSIGNED NOT NULL,
    option_position INT             NOT NULL,
    PRIMARY KEY (submission_id, question_id, option_position),
    KEY idx_submission_answers_question (question_id),
    CONSTRAINT fk_submission_answers_submission FOREIGN KEY (submission_id) REFERENCES submissions (id) ON DELETE CASCADE,
    CONSTRAINT fk_submission_answers_question FOREIGN KEY (question_id) REFERENCES assessment_questions (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

ALTER TABLE submissions
    ADD COLUMN started_at DATETIME(3)  NULL AFTER user_id,     -- bài trắc nghiệm: lúc bấm "Bắt đầu"
    ADD COLUMN auto_score DECIMAL(4, 2) NULL AFTER score;      -- điểm máy chấm (score là điểm chính thức, giảng viên sửa được)
