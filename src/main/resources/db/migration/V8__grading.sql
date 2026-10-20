-- V8: giảng viên chấm điểm thủ công bài nộp: điểm thang 10 (2 chữ số thập phân) + nhận xét.
-- Sinh viên không nộp vẫn chấm được (ví dụ 0 điểm): khi đó tạo dòng submissions không có file.

ALTER TABLE submissions
    ADD COLUMN score      DECIMAL(4, 2)   NULL AFTER submitted_at, -- NULL = chưa chấm
    ADD COLUMN feedback   VARCHAR(2000)   NULL AFTER score,
    ADD COLUMN graded_at  DATETIME(3)     NULL AFTER feedback,
    ADD COLUMN graded_by  BIGINT UNSIGNED NULL AFTER graded_at,    -- người chấm; NULL nếu tài khoản đã bị xóa
    ADD CONSTRAINT fk_submissions_grader FOREIGN KEY (graded_by) REFERENCES users (id) ON DELETE SET NULL;
