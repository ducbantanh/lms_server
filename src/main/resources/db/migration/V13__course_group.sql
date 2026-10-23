-- V13: một học phần (mã) mở nhiều nhóm; mỗi khóa học là một nhóm của học phần, vd. INT1340 nhóm 01.
-- NULL = không chia nhóm (khóa học tạo trước V13). Mỗi mã + nhóm chỉ có một khóa học
-- (UNIQUE của MySQL không coi các dòng NULL là trùng nhau).

ALTER TABLE courses
    ADD COLUMN group_name VARCHAR(16) NULL AFTER code,
    ADD UNIQUE KEY uk_courses_code_group (code, group_name);
