-- V12: thời gian tự đăng xuất khi không hoạt động, đặt riêng theo vai trò từ trang admin.
-- NULL = dùng app.session.idle-timeout trong application.yml (admin và các vai trò khác).

ALTER TABLE roles
    ADD COLUMN idle_timeout_minutes SMALLINT UNSIGNED NULL AFTER name;

UPDATE roles SET idle_timeout_minutes = 5 WHERE id = 5;   -- STUDENT
UPDATE roles SET idle_timeout_minutes = 10 WHERE id = 1;  -- TEACHER
