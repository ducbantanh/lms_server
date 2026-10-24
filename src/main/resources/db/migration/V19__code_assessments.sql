-- V19: bài lập trình chấm tự động (assessments.answer_mode = 2).
-- Giảng viên soạn đề (văn bản) và bộ test: test mẫu (sinh viên thấy input/output) và test ẩn, mỗi test có
-- trọng số. Sinh viên "chạy thử" (chỉ test mẫu, không tính điểm) hoặc "nộp bài" (mọi test) nhiều lần khi bài mở;
-- mỗi lần là một dòng code_attempts, xếp hàng chờ máy chấm (CodeJudgeWorker) biên dịch và chạy trong sandbox.
-- Điểm = tổng trọng số test đúng / tổng trọng số × 10; điểm của bài là lần nộp cao nhất (submissions.auto_score).
-- Sửa bộ test thì mọi lần nộp được chấm lại.

CREATE TABLE code_problems (
    assessment_id   BIGINT UNSIGNED NOT NULL,
    statement       MEDIUMTEXT      NOT NULL,           -- đề bài, văn bản thuần (giữ xuống dòng)
    languages       VARCHAR(100)    NOT NULL,           -- mã ngôn ngữ cho phép, ngăn bởi dấu phẩy: c,cpp,java,python
    time_limit_ms   INT UNSIGNED    NOT NULL,           -- mỗi test; Java, Python được nhân thêm (CodeLanguage.timeFactor)
    memory_limit_mb INT UNSIGNED    NOT NULL,
    max_attempts    INT UNSIGNED    NULL,               -- số lần nộp bài tối đa; NULL = không giới hạn
    PRIMARY KEY (assessment_id),
    CONSTRAINT fk_code_problems_assessment FOREIGN KEY (assessment_id) REFERENCES assessments (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE code_tests (
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    assessment_id BIGINT UNSIGNED NOT NULL,
    position      INT             NOT NULL,
    input         MEDIUMTEXT      NOT NULL,
    expected      MEDIUMTEXT      NOT NULL,
    sample        TINYINT(1)      NOT NULL,             -- test mẫu: hiện cho sinh viên, dùng khi chạy thử
    weight        INT UNSIGNED    NOT NULL,
    PRIMARY KEY (id),
    KEY idx_code_tests_assessment (assessment_id, position),
    CONSTRAINT fk_code_tests_assessment FOREIGN KEY (assessment_id) REFERENCES assessments (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE code_attempts (
    id             BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT,
    public_id      CHAR(22)         NOT NULL,
    assessment_id  BIGINT UNSIGNED  NOT NULL,
    user_id        BIGINT UNSIGNED  NOT NULL,
    kind           TINYINT UNSIGNED NOT NULL,           -- 0 chạy thử (test mẫu), 1 nộp bài (mọi test, tính điểm)
    language       VARCHAR(16)      NOT NULL,
    source         MEDIUMTEXT       NOT NULL,
    status         TINYINT UNSIGNED NOT NULL,           -- 0 chờ chấm, 1 đang chấm, 2 đã chấm
    claim_token    CHAR(22)         NULL,               -- lượt chấm đang giữ bài; chấm lại thì đổi, kết quả cũ bị bỏ
    verdict        VARCHAR(8)       NULL,               -- AC / WA / TLE / RE / OLE / CE / SE (test đầu tiên sai)
    passed         INT UNSIGNED     NULL,
    total          INT UNSIGNED     NULL,
    score          DECIMAL(4, 2)    NULL,               -- chỉ lần nộp bài
    compile_output TEXT             NULL,
    created_at     DATETIME(3)      NOT NULL,
    judged_at      DATETIME(3)      NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_code_attempts_public_id (public_id),
    KEY idx_code_attempts_user (assessment_id, user_id, created_at),
    KEY idx_code_attempts_status (status, id),
    CONSTRAINT fk_code_attempts_assessment FOREIGN KEY (assessment_id) REFERENCES assessments (id) ON DELETE CASCADE,
    CONSTRAINT fk_code_attempts_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Kết quả từng test, position = thứ tự test lúc chấm (chạy thử: thứ tự trong các test mẫu).
-- Sửa bộ test thì xóa hết kết quả cũ của bài.
CREATE TABLE code_attempt_results (
    attempt_id BIGINT UNSIGNED NOT NULL,
    position   INT             NOT NULL,
    verdict    VARCHAR(8)      NOT NULL,
    time_ms    INT UNSIGNED    NOT NULL,
    output     TEXT            NULL,                    -- stdout (cắt ngắn), chỉ giữ khi sai hoặc test mẫu
    error      TEXT            NULL,                    -- stderr (cắt ngắn) khi lỗi lúc chạy
    PRIMARY KEY (attempt_id, position),
    CONSTRAINT fk_code_attempt_results_attempt FOREIGN KEY (attempt_id) REFERENCES code_attempts (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
