-- V26: lỗi máy chấm (SE: sandbox/Docker lỗi, đĩa đầy…) thường chỉ thoáng qua nên bài được tự đưa về hàng đợi
-- chấm lại, tối đa CodeService.MAX_SYSTEM_RETRIES lần, lần sau chờ lâu hơn (retry_at); hết lượt mới lưu SE.
-- Giảng viên bấm chấm lại / sửa bộ test thì đếm lại từ 0.

ALTER TABLE code_attempts
    ADD COLUMN judge_retries TINYINT UNSIGNED NOT NULL DEFAULT 0 AFTER claim_token,
    ADD COLUMN retry_at      DATETIME(3)      NULL AFTER judge_retries;
