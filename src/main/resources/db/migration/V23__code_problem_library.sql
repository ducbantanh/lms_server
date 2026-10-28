-- V23: kho bài lập trình của giảng viên (kiểu danh sách bài trên các trang OJ), không gắn với bài hay khóa học.
-- Dùng chung bảng code_problems / code_tests với câu của bài: câu trong kho có owner_id, assessment_id NULL.
-- Tạo bài từ kho thì chép câu và bộ test sang bài (bản chụp), nên sửa/xóa câu trong kho không đổi bài đã tạo.
-- Môn học (mã học phần) giống ngân hàng câu hỏi: để lọc, tên môn chép lại lúc chọn.

ALTER TABLE code_problems
    MODIFY COLUMN assessment_id BIGINT UNSIGNED NULL,
    ADD COLUMN owner_id     BIGINT UNSIGNED NULL AFTER assessment_id,
    ADD COLUMN subject_code VARCHAR(32)     NULL AFTER owner_id,
    ADD COLUMN subject_name VARCHAR(200)    NULL AFTER subject_code,
    ADD COLUMN created_at   DATETIME(3)     NULL,
    ADD COLUMN updated_at   DATETIME(3)     NULL,
    ADD KEY idx_code_problems_owner (owner_id, updated_at),
    ADD CONSTRAINT fk_code_problems_owner FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE;
