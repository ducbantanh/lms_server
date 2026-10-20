-- V11: chỉ admin tạo khóa học, phân công/đổi giảng viên phụ trách và xóa khóa học.
-- Giảng viên (courses.manage) vẫn quản lý nội dung, sinh viên, bài kiểm tra của khóa được phân công.

INSERT INTO permissions (code, description) VALUES
    ('courses.assign', 'Create and delete courses, assign the responsible teacher');

INSERT INTO role_permissions (role_id, permission_id)
SELECT 0, id FROM permissions WHERE code = 'courses.assign';
