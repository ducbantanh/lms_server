-- V21: bài lập trình nhiều câu, không chấm điểm.
-- Một bài (assessment) gồm nhiều câu (code_problems: tiêu đề, đề, giới hạn, bộ test); ngôn ngữ cho phép đặt chung
-- cho cả bài (code_settings). Lần chạy/nộp và test thuộc về một câu. Kết quả chỉ là số test đúng / tổng số test
-- (AC 5/5): bỏ điểm của lần nộp và trọng số test. Bài cũ (một đề) thành bài có một câu cùng tên bài.

RENAME TABLE code_problems TO code_settings;
ALTER TABLE code_settings DROP FOREIGN KEY fk_code_problems_assessment;
ALTER TABLE code_settings
    ADD CONSTRAINT fk_code_settings_assessment FOREIGN KEY (assessment_id) REFERENCES assessments (id) ON DELETE CASCADE;

CREATE TABLE code_problems (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    public_id       CHAR(22)        NOT NULL,
    assessment_id   BIGINT UNSIGNED NOT NULL,
    position        INT             NOT NULL,
    title           VARCHAR(200)    NOT NULL,
    statement       MEDIUMTEXT      NOT NULL,
    time_limit_ms   INT UNSIGNED    NOT NULL,
    memory_limit_mb INT UNSIGNED    NOT NULL,
    max_attempts    INT UNSIGNED    NULL,               -- số lần nộp tối đa của câu; NULL = không giới hạn
    PRIMARY KEY (id),
    UNIQUE KEY uk_code_problems_public_id (public_id),
    KEY idx_code_problems_assessment (assessment_id, position),
    CONSTRAINT fk_code_problems_assessment FOREIGN KEY (assessment_id) REFERENCES assessments (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

INSERT INTO code_problems (public_id, assessment_id, position, title, statement, time_limit_ms, memory_limit_mb,
                           max_attempts)
SELECT SUBSTRING(REPLACE(REPLACE(TO_BASE64(RANDOM_BYTES(16)), '+', '-'), '/', '_'), 1, 22),
       s.assessment_id, 0, a.title, s.statement, s.time_limit_ms, s.memory_limit_mb, s.max_attempts
FROM code_settings s
         JOIN assessments a ON a.id = s.assessment_id;

ALTER TABLE code_settings DROP COLUMN statement;
ALTER TABLE code_settings DROP COLUMN time_limit_ms;
ALTER TABLE code_settings DROP COLUMN memory_limit_mb;
ALTER TABLE code_settings DROP COLUMN max_attempts;

-- Test thuộc về câu
ALTER TABLE code_tests ADD COLUMN problem_id BIGINT UNSIGNED NULL AFTER public_id;
UPDATE code_tests t JOIN code_problems p ON p.assessment_id = t.assessment_id SET t.problem_id = p.id;
ALTER TABLE code_tests DROP FOREIGN KEY fk_code_tests_assessment;
ALTER TABLE code_tests DROP INDEX idx_code_tests_assessment;
ALTER TABLE code_tests DROP COLUMN assessment_id;
ALTER TABLE code_tests DROP COLUMN weight;
ALTER TABLE code_tests MODIFY COLUMN problem_id BIGINT UNSIGNED NOT NULL;
ALTER TABLE code_tests ADD KEY idx_code_tests_problem (problem_id, position);
ALTER TABLE code_tests ADD CONSTRAINT fk_code_tests_problem FOREIGN KEY (problem_id) REFERENCES code_problems (id) ON DELETE CASCADE;

-- Lần chạy/nộp thuộc về câu (giữ assessment_id để liệt kê theo bài); không còn điểm
ALTER TABLE code_attempts ADD COLUMN problem_id BIGINT UNSIGNED NULL AFTER assessment_id;
UPDATE code_attempts x JOIN code_problems p ON p.assessment_id = x.assessment_id SET x.problem_id = p.id;
ALTER TABLE code_attempts DROP COLUMN score;
ALTER TABLE code_attempts MODIFY COLUMN problem_id BIGINT UNSIGNED NOT NULL;
ALTER TABLE code_attempts ADD KEY idx_code_attempts_problem (problem_id, user_id, id);
ALTER TABLE code_attempts ADD CONSTRAINT fk_code_attempts_problem FOREIGN KEY (problem_id) REFERENCES code_problems (id) ON DELETE CASCADE;

-- Điểm máy chấm cũ của bài lập trình (khi còn tính điểm) không còn ý nghĩa
UPDATE submissions s JOIN assessments a ON a.id = s.assessment_id
SET s.score = NULL, s.auto_score = NULL, s.graded_at = NULL, s.graded_by = NULL, s.feedback = NULL
WHERE a.answer_mode = 2;
