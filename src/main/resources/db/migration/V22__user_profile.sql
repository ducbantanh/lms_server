-- V22: hồ sơ cá nhân. Sinh viên và giảng viên tự sửa thông tin liên hệ và lời giới thiệu;
-- họ tên, mã đăng nhập, ngày sinh vẫn do quản trị viên quản lý. NULL = chưa điền.

ALTER TABLE users ADD COLUMN email VARCHAR(100) NULL AFTER birth_date;
ALTER TABLE users ADD COLUMN phone VARCHAR(20)  NULL AFTER email;
ALTER TABLE users ADD COLUMN bio   VARCHAR(500) NULL AFTER phone;
