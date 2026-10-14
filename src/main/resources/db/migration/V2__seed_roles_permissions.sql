-- V2: role dạng số (bắt đầu từ 0) và permission ban đầu.
-- Phải khớp với enum lms.auth.Role và class Permissions.

INSERT INTO roles (id, code, name) VALUES
    (0, 'ADMIN',      'Admin'),
    (1, 'TEACHER',    'Teacher'),
    (2, 'MENTOR',     'Mentor'),
    (3, 'SUPERVISOR', 'Supervisor'),
    (4, 'PARENT',     'Parent'),
    (5, 'STUDENT',    'Student'),
    (6, 'VISITOR',    'Visitor');

INSERT INTO permissions (code, description) VALUES
    ('users.manage',       'Create, import, lock, unlock and delete accounts'),
    ('sessions.manage',    'Force logout and revoke sessions'),
    ('presence.view',      'See who is online in realtime'),
    ('audit.view',         'Read audit logs'),
    ('courses.view',       'View enrolled courses and content'),
    ('submissions.create', 'Submit work'),
    ('submissions.manage', 'Review and grade submissions');

-- Admin: toàn quyền
INSERT INTO role_permissions (role_id, permission_id)
SELECT 0, id FROM permissions;

-- Teacher
INSERT INTO role_permissions (role_id, permission_id)
SELECT 1, id FROM permissions WHERE code IN ('courses.view', 'submissions.manage', 'presence.view');

-- Mentor
INSERT INTO role_permissions (role_id, permission_id)
SELECT 2, id FROM permissions WHERE code IN ('courses.view', 'submissions.manage');

-- Student
INSERT INTO role_permissions (role_id, permission_id)
SELECT 5, id FROM permissions WHERE code IN ('courses.view', 'submissions.create');

-- Visitor
INSERT INTO role_permissions (role_id, permission_id)
SELECT 6, id FROM permissions WHERE code IN ('courses.view', 'submissions.create');
