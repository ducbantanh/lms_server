-- V16: gắn ngân hàng câu hỏi với môn học (mã học phần, như courses.code). Các nhóm của cùng học phần dùng
-- chung ngân hàng; tạo bài thi cho khóa học thì gợi ý ngân hàng cùng mã. Tên môn chép lại lúc chọn để vẫn
-- hiện được khi giảng viên không còn dạy học phần đó. NULL = chưa gắn môn học.

ALTER TABLE question_banks ADD COLUMN subject_code VARCHAR(32)  NULL AFTER owner_id;
ALTER TABLE question_banks ADD COLUMN subject_name VARCHAR(200) NULL AFTER subject_code;
ALTER TABLE question_banks ADD KEY idx_question_banks_subject (owner_id, subject_code);
