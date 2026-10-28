-- V20: bộ test số lượng lớn cho bài lập trình (tải lên bằng file ZIP).
-- Mỗi test có public_id để sửa bộ test mà không phải gửi lại nội dung test lớn, tên (lấy từ tên file trong ZIP)
-- và kích thước input/output (byte UTF-8): giao diện chỉ nhận bản xem trước của test lớn, máy chấm đọc từng test
-- khi chạy, không nạp cả bộ test vào bộ nhớ.

ALTER TABLE code_tests
    ADD COLUMN public_id     CHAR(22)        NULL AFTER id,
    ADD COLUMN name          VARCHAR(100)    NULL AFTER position,      -- vd. "test05" (từ ZIP); NULL = test soạn tay
    ADD COLUMN input_size    BIGINT UNSIGNED NOT NULL DEFAULT 0 AFTER expected,
    ADD COLUMN expected_size BIGINT UNSIGNED NOT NULL DEFAULT 0 AFTER input_size;

UPDATE code_tests
SET public_id     = SUBSTRING(REPLACE(REPLACE(TO_BASE64(RANDOM_BYTES(16)), '+', '-'), '/', '_'), 1, 22),
    input_size    = LENGTH(input),
    expected_size = LENGTH(expected);

ALTER TABLE code_tests
    MODIFY COLUMN public_id CHAR(22) NOT NULL,
    ADD UNIQUE KEY uk_code_tests_public_id (public_id);
