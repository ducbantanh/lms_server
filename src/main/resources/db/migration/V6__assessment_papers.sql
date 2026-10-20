-- V6: đề tải lên riêng cho một bài (bài tập / thực hành / kiểm tra).
-- File loại này không nằm trong tài liệu khóa học: không thuộc mục/bài học nào, không hiện trong
-- danh sách tài liệu, chỉ tải được qua bài đã mở; xóa bài thì xóa luôn file.

ALTER TABLE course_files
    ADD COLUMN kind TINYINT UNSIGNED NOT NULL DEFAULT 0 AFTER lesson_id; -- 0 tài liệu, 1 đề riêng của bài
