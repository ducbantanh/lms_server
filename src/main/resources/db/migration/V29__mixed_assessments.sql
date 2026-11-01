-- V29: bài tổng hợp (assessments.answer_mode = 3) gồm từ hai phần trở lên trong ba phần: trắc nghiệm (câu hỏi ở
-- assessment_questions), lập trình (code_settings, code_problems) và nộp file (đề PDF tùy chọn ở paper_file_id).
-- Giảng viên đặt điểm tối đa của từng phần (trọng số), tổng các phần có trong bài = 10; phần không có thì NULL.
-- Sinh viên bấm "Bắt đầu" một lần (submissions.started_at), đồng hồ (time_limit_minutes) tính chung cho cả bài,
-- bấm nộp hoặc hết giờ thì mọi phần cùng khóa.
-- Điểm: trắc nghiệm = số câu đúng / tổng số câu × điểm phần; lập trình = số câu AC / tổng số câu × điểm phần
-- (submissions.auto_score = hai phần này, tính lại khi máy chấm xong lần nộp); nộp file do giảng viên chấm
-- (submissions.manual_score). submissions.score = auto_score + manual_score (bài không có phần nộp file thì = auto_score).
-- Mỗi câu lệnh ALTER chỉ một mệnh đề (TiDB Cloud).

ALTER TABLE assessments ADD COLUMN quiz_weight DECIMAL(4, 2) NULL AFTER reveal_mode;
ALTER TABLE assessments ADD COLUMN code_weight DECIMAL(4, 2) NULL AFTER quiz_weight;
ALTER TABLE assessments ADD COLUMN file_weight DECIMAL(4, 2) NULL AFTER code_weight;

ALTER TABLE submissions ADD COLUMN manual_score DECIMAL(4, 2) NULL AFTER auto_score; -- bài tổng hợp: điểm phần nộp file
