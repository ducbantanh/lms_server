-- V18: khi nào sinh viên được xem điểm (và nhận xét) của bài. Mặc định như trước: thấy ngay khi có điểm.
-- "Sau giờ đóng bài" dùng cho bài kiểm tra: sinh viên nộp xong chưa biết điểm cho tới closes_at
-- (bài không có giờ đóng thì coi như công bố ngay). Giảng viên luôn thấy điểm.

ALTER TABLE assessments
    ADD COLUMN score_release TINYINT UNSIGNED NOT NULL DEFAULT 0 AFTER reveal_mode; -- 0 ngay khi có điểm, 1 sau giờ đóng bài
