package lms.catalog;

import java.util.*;

/**
 * Danh mục Lược đồ Cơ sở Dữ liệu Quan hệ Doanh nghiệp (Enterprise Relational Schemas).
 * Cung cấp đầy đủ DDL, đặc tả bảng, khóa ngoại, chỉ mục và câu truy vấn mẫu cho 12 phân hệ LMS.
 */
public final class DatabaseSchemaCatalog {

    public record TableDefinition(
            String tableName,
            String description,
            List<String> columns,
            List<String> primaryKeys,
            List<String> foreignKeys,
            List<String> indexes
    ) {}

    public record SchemaItem(
            String schemaName,
            String description,
            String ddlScript,
            List<TableDefinition> tables,
            List<String> sampleQueries
    ) {}

    private static final Map<String, SchemaItem> SCHEMAS = new LinkedHashMap<>();

    static {
        registerSchema(new SchemaItem(
                "CORE_IAM",
                "Phân hệ Quản lý Người dùng & Phân quyền bảo mật (IAM)",
                "-- DDL Script for CORE_IAM\\nCREATE TABLE IF NOT EXISTS users (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS roles (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS permissions (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS user_roles (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS role_permissions (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS user_sessions (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS audit_logs (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\n",
                List.of(
                        new TableDefinition(
                                "users",
                                "Bảng lưu trữ thông tin users phục vụ phân hệ CORE_IAM",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_users_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_users_created_at ON users(created_at)", "idx_users_status ON users(status)")
                        ),
                        new TableDefinition(
                                "roles",
                                "Bảng lưu trữ thông tin roles phục vụ phân hệ CORE_IAM",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_roles_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_roles_created_at ON roles(created_at)", "idx_roles_status ON roles(status)")
                        ),
                        new TableDefinition(
                                "permissions",
                                "Bảng lưu trữ thông tin permissions phục vụ phân hệ CORE_IAM",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_permissions_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_permissions_created_at ON permissions(created_at)", "idx_permissions_status ON permissions(status)")
                        ),
                        new TableDefinition(
                                "user_roles",
                                "Bảng lưu trữ thông tin user_roles phục vụ phân hệ CORE_IAM",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_user_roles_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_user_roles_created_at ON user_roles(created_at)", "idx_user_roles_status ON user_roles(status)")
                        ),
                        new TableDefinition(
                                "role_permissions",
                                "Bảng lưu trữ thông tin role_permissions phục vụ phân hệ CORE_IAM",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_role_permissions_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_role_permissions_created_at ON role_permissions(created_at)", "idx_role_permissions_status ON role_permissions(status)")
                        ),
                        new TableDefinition(
                                "user_sessions",
                                "Bảng lưu trữ thông tin user_sessions phục vụ phân hệ CORE_IAM",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_user_sessions_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_user_sessions_created_at ON user_sessions(created_at)", "idx_user_sessions_status ON user_sessions(status)")
                        ),
                        new TableDefinition(
                                "audit_logs",
                                "Bảng lưu trữ thông tin audit_logs phục vụ phân hệ CORE_IAM",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_audit_logs_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_audit_logs_created_at ON audit_logs(created_at)", "idx_audit_logs_status ON audit_logs(status)")
                        )
                ),
                List.of(
                        "SELECT * FROM users WHERE status = 'ACTIVE' ORDER BY id DESC LIMIT 50;",
                        "SELECT COUNT(*) FROM roles WHERE created_at >= CURRENT_DATE - INTERVAL '30 days';",
                        "EXPLAIN ANALYZE SELECT * FROM users JOIN roles ON users.id = roles.id;"
                )
        ));

        registerSchema(new SchemaItem(
                "ACADEMIC_CATALOG",
                "Phân hệ Quản lý Đào tạo, Khóa học & Lớp học phần",
                "-- DDL Script for ACADEMIC_CATALOG\\nCREATE TABLE IF NOT EXISTS semesters (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS departments (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS courses (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS course_prerequisites (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS course_sections (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS student_enrollments (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\n",
                List.of(
                        new TableDefinition(
                                "semesters",
                                "Bảng lưu trữ thông tin semesters phục vụ phân hệ ACADEMIC_CATALOG",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_semesters_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_semesters_created_at ON semesters(created_at)", "idx_semesters_status ON semesters(status)")
                        ),
                        new TableDefinition(
                                "departments",
                                "Bảng lưu trữ thông tin departments phục vụ phân hệ ACADEMIC_CATALOG",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_departments_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_departments_created_at ON departments(created_at)", "idx_departments_status ON departments(status)")
                        ),
                        new TableDefinition(
                                "courses",
                                "Bảng lưu trữ thông tin courses phục vụ phân hệ ACADEMIC_CATALOG",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_courses_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_courses_created_at ON courses(created_at)", "idx_courses_status ON courses(status)")
                        ),
                        new TableDefinition(
                                "course_prerequisites",
                                "Bảng lưu trữ thông tin course_prerequisites phục vụ phân hệ ACADEMIC_CATALOG",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_course_prerequisites_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_course_prerequisites_created_at ON course_prerequisites(created_at)", "idx_course_prerequisites_status ON course_prerequisites(status)")
                        ),
                        new TableDefinition(
                                "course_sections",
                                "Bảng lưu trữ thông tin course_sections phục vụ phân hệ ACADEMIC_CATALOG",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_course_sections_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_course_sections_created_at ON course_sections(created_at)", "idx_course_sections_status ON course_sections(status)")
                        ),
                        new TableDefinition(
                                "student_enrollments",
                                "Bảng lưu trữ thông tin student_enrollments phục vụ phân hệ ACADEMIC_CATALOG",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_student_enrollments_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_student_enrollments_created_at ON student_enrollments(created_at)", "idx_student_enrollments_status ON student_enrollments(status)")
                        )
                ),
                List.of(
                        "SELECT * FROM semesters WHERE status = 'ACTIVE' ORDER BY id DESC LIMIT 50;",
                        "SELECT COUNT(*) FROM departments WHERE created_at >= CURRENT_DATE - INTERVAL '30 days';",
                        "EXPLAIN ANALYZE SELECT * FROM semesters JOIN departments ON semesters.id = departments.id;"
                )
        ));

        registerSchema(new SchemaItem(
                "ASSESSMENT_ENGINE",
                "Phân hệ Khảo thí & Tổ chức Thi trắc nghiệm trực tuyến",
                "-- DDL Script for ASSESSMENT_ENGINE\\nCREATE TABLE IF NOT EXISTS question_banks (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS questions (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS question_options (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS assessments (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS assessment_questions (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS student_attempts (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS student_answers (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\n",
                List.of(
                        new TableDefinition(
                                "question_banks",
                                "Bảng lưu trữ thông tin question_banks phục vụ phân hệ ASSESSMENT_ENGINE",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_question_banks_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_question_banks_created_at ON question_banks(created_at)", "idx_question_banks_status ON question_banks(status)")
                        ),
                        new TableDefinition(
                                "questions",
                                "Bảng lưu trữ thông tin questions phục vụ phân hệ ASSESSMENT_ENGINE",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_questions_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_questions_created_at ON questions(created_at)", "idx_questions_status ON questions(status)")
                        ),
                        new TableDefinition(
                                "question_options",
                                "Bảng lưu trữ thông tin question_options phục vụ phân hệ ASSESSMENT_ENGINE",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_question_options_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_question_options_created_at ON question_options(created_at)", "idx_question_options_status ON question_options(status)")
                        ),
                        new TableDefinition(
                                "assessments",
                                "Bảng lưu trữ thông tin assessments phục vụ phân hệ ASSESSMENT_ENGINE",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_assessments_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_assessments_created_at ON assessments(created_at)", "idx_assessments_status ON assessments(status)")
                        ),
                        new TableDefinition(
                                "assessment_questions",
                                "Bảng lưu trữ thông tin assessment_questions phục vụ phân hệ ASSESSMENT_ENGINE",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_assessment_questions_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_assessment_questions_created_at ON assessment_questions(created_at)", "idx_assessment_questions_status ON assessment_questions(status)")
                        ),
                        new TableDefinition(
                                "student_attempts",
                                "Bảng lưu trữ thông tin student_attempts phục vụ phân hệ ASSESSMENT_ENGINE",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_student_attempts_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_student_attempts_created_at ON student_attempts(created_at)", "idx_student_attempts_status ON student_attempts(status)")
                        ),
                        new TableDefinition(
                                "student_answers",
                                "Bảng lưu trữ thông tin student_answers phục vụ phân hệ ASSESSMENT_ENGINE",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_student_answers_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_student_answers_created_at ON student_answers(created_at)", "idx_student_answers_status ON student_answers(status)")
                        )
                ),
                List.of(
                        "SELECT * FROM question_banks WHERE status = 'ACTIVE' ORDER BY id DESC LIMIT 50;",
                        "SELECT COUNT(*) FROM questions WHERE created_at >= CURRENT_DATE - INTERVAL '30 days';",
                        "EXPLAIN ANALYZE SELECT * FROM question_banks JOIN questions ON question_banks.id = questions.id;"
                )
        ));

        registerSchema(new SchemaItem(
                "CODE_JUDGE",
                "Phân hệ Chấm mã nguồn tự động & Bài tập Lập trình",
                "-- DDL Script for CODE_JUDGE\\nCREATE TABLE IF NOT EXISTS code_problems (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS problem_tags (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS problem_test_cases (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS code_submissions (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS submission_test_results (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS leaderboards (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\n",
                List.of(
                        new TableDefinition(
                                "code_problems",
                                "Bảng lưu trữ thông tin code_problems phục vụ phân hệ CODE_JUDGE",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_code_problems_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_code_problems_created_at ON code_problems(created_at)", "idx_code_problems_status ON code_problems(status)")
                        ),
                        new TableDefinition(
                                "problem_tags",
                                "Bảng lưu trữ thông tin problem_tags phục vụ phân hệ CODE_JUDGE",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_problem_tags_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_problem_tags_created_at ON problem_tags(created_at)", "idx_problem_tags_status ON problem_tags(status)")
                        ),
                        new TableDefinition(
                                "problem_test_cases",
                                "Bảng lưu trữ thông tin problem_test_cases phục vụ phân hệ CODE_JUDGE",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_problem_test_cases_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_problem_test_cases_created_at ON problem_test_cases(created_at)", "idx_problem_test_cases_status ON problem_test_cases(status)")
                        ),
                        new TableDefinition(
                                "code_submissions",
                                "Bảng lưu trữ thông tin code_submissions phục vụ phân hệ CODE_JUDGE",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_code_submissions_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_code_submissions_created_at ON code_submissions(created_at)", "idx_code_submissions_status ON code_submissions(status)")
                        ),
                        new TableDefinition(
                                "submission_test_results",
                                "Bảng lưu trữ thông tin submission_test_results phục vụ phân hệ CODE_JUDGE",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_submission_test_results_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_submission_test_results_created_at ON submission_test_results(created_at)", "idx_submission_test_results_status ON submission_test_results(status)")
                        ),
                        new TableDefinition(
                                "leaderboards",
                                "Bảng lưu trữ thông tin leaderboards phục vụ phân hệ CODE_JUDGE",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_leaderboards_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_leaderboards_created_at ON leaderboards(created_at)", "idx_leaderboards_status ON leaderboards(status)")
                        )
                ),
                List.of(
                        "SELECT * FROM code_problems WHERE status = 'ACTIVE' ORDER BY id DESC LIMIT 50;",
                        "SELECT COUNT(*) FROM problem_tags WHERE created_at >= CURRENT_DATE - INTERVAL '30 days';",
                        "EXPLAIN ANALYZE SELECT * FROM code_problems JOIN problem_tags ON code_problems.id = problem_tags.id;"
                )
        ));

        registerSchema(new SchemaItem(
                "PROCTORING_AUDIT",
                "Phân hệ Giám sát thi trực tuyến & Chống gian lận (AI Proctor)",
                "-- DDL Script for PROCTORING_AUDIT\\nCREATE TABLE IF NOT EXISTS proctor_sessions (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS ip_whitelist_rules (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS browser_lockdown_events (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS tab_switch_logs (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS biometric_detections (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\n",
                List.of(
                        new TableDefinition(
                                "proctor_sessions",
                                "Bảng lưu trữ thông tin proctor_sessions phục vụ phân hệ PROCTORING_AUDIT",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_proctor_sessions_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_proctor_sessions_created_at ON proctor_sessions(created_at)", "idx_proctor_sessions_status ON proctor_sessions(status)")
                        ),
                        new TableDefinition(
                                "ip_whitelist_rules",
                                "Bảng lưu trữ thông tin ip_whitelist_rules phục vụ phân hệ PROCTORING_AUDIT",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_ip_whitelist_rules_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_ip_whitelist_rules_created_at ON ip_whitelist_rules(created_at)", "idx_ip_whitelist_rules_status ON ip_whitelist_rules(status)")
                        ),
                        new TableDefinition(
                                "browser_lockdown_events",
                                "Bảng lưu trữ thông tin browser_lockdown_events phục vụ phân hệ PROCTORING_AUDIT",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_browser_lockdown_events_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_browser_lockdown_events_created_at ON browser_lockdown_events(created_at)", "idx_browser_lockdown_events_status ON browser_lockdown_events(status)")
                        ),
                        new TableDefinition(
                                "tab_switch_logs",
                                "Bảng lưu trữ thông tin tab_switch_logs phục vụ phân hệ PROCTORING_AUDIT",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_tab_switch_logs_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_tab_switch_logs_created_at ON tab_switch_logs(created_at)", "idx_tab_switch_logs_status ON tab_switch_logs(status)")
                        ),
                        new TableDefinition(
                                "biometric_detections",
                                "Bảng lưu trữ thông tin biometric_detections phục vụ phân hệ PROCTORING_AUDIT",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_biometric_detections_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_biometric_detections_created_at ON biometric_detections(created_at)", "idx_biometric_detections_status ON biometric_detections(status)")
                        )
                ),
                List.of(
                        "SELECT * FROM proctor_sessions WHERE status = 'ACTIVE' ORDER BY id DESC LIMIT 50;",
                        "SELECT COUNT(*) FROM ip_whitelist_rules WHERE created_at >= CURRENT_DATE - INTERVAL '30 days';",
                        "EXPLAIN ANALYZE SELECT * FROM proctor_sessions JOIN ip_whitelist_rules ON proctor_sessions.id = ip_whitelist_rules.id;"
                )
        ));

        registerSchema(new SchemaItem(
                "ATTENDANCE_TIMETABLE",
                "Phân hệ Thời khóa biểu, Phòng máy & Điểm danh QR",
                "-- DDL Script for ATTENDANCE_TIMETABLE\\nCREATE TABLE IF NOT EXISTS buildings (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS rooms (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS time_slots (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS schedules (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS attendance_sessions (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS attendance_records (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS qr_codes (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\n",
                List.of(
                        new TableDefinition(
                                "buildings",
                                "Bảng lưu trữ thông tin buildings phục vụ phân hệ ATTENDANCE_TIMETABLE",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_buildings_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_buildings_created_at ON buildings(created_at)", "idx_buildings_status ON buildings(status)")
                        ),
                        new TableDefinition(
                                "rooms",
                                "Bảng lưu trữ thông tin rooms phục vụ phân hệ ATTENDANCE_TIMETABLE",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_rooms_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_rooms_created_at ON rooms(created_at)", "idx_rooms_status ON rooms(status)")
                        ),
                        new TableDefinition(
                                "time_slots",
                                "Bảng lưu trữ thông tin time_slots phục vụ phân hệ ATTENDANCE_TIMETABLE",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_time_slots_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_time_slots_created_at ON time_slots(created_at)", "idx_time_slots_status ON time_slots(status)")
                        ),
                        new TableDefinition(
                                "schedules",
                                "Bảng lưu trữ thông tin schedules phục vụ phân hệ ATTENDANCE_TIMETABLE",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_schedules_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_schedules_created_at ON schedules(created_at)", "idx_schedules_status ON schedules(status)")
                        ),
                        new TableDefinition(
                                "attendance_sessions",
                                "Bảng lưu trữ thông tin attendance_sessions phục vụ phân hệ ATTENDANCE_TIMETABLE",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_attendance_sessions_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_attendance_sessions_created_at ON attendance_sessions(created_at)", "idx_attendance_sessions_status ON attendance_sessions(status)")
                        ),
                        new TableDefinition(
                                "attendance_records",
                                "Bảng lưu trữ thông tin attendance_records phục vụ phân hệ ATTENDANCE_TIMETABLE",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_attendance_records_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_attendance_records_created_at ON attendance_records(created_at)", "idx_attendance_records_status ON attendance_records(status)")
                        ),
                        new TableDefinition(
                                "qr_codes",
                                "Bảng lưu trữ thông tin qr_codes phục vụ phân hệ ATTENDANCE_TIMETABLE",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_qr_codes_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_qr_codes_created_at ON qr_codes(created_at)", "idx_qr_codes_status ON qr_codes(status)")
                        )
                ),
                List.of(
                        "SELECT * FROM buildings WHERE status = 'ACTIVE' ORDER BY id DESC LIMIT 50;",
                        "SELECT COUNT(*) FROM rooms WHERE created_at >= CURRENT_DATE - INTERVAL '30 days';",
                        "EXPLAIN ANALYZE SELECT * FROM buildings JOIN rooms ON buildings.id = rooms.id;"
                )
        ));

        registerSchema(new SchemaItem(
                "COLLABORATION_FORUM",
                "Phân hệ Diễn đàn thảo luận & Hỏi đáp học thuật",
                "-- DDL Script for COLLABORATION_FORUM\\nCREATE TABLE IF NOT EXISTS forum_categories (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS forum_threads (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS forum_posts (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS post_votes (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS post_attachments (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS thread_subscribers (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\n",
                List.of(
                        new TableDefinition(
                                "forum_categories",
                                "Bảng lưu trữ thông tin forum_categories phục vụ phân hệ COLLABORATION_FORUM",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_forum_categories_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_forum_categories_created_at ON forum_categories(created_at)", "idx_forum_categories_status ON forum_categories(status)")
                        ),
                        new TableDefinition(
                                "forum_threads",
                                "Bảng lưu trữ thông tin forum_threads phục vụ phân hệ COLLABORATION_FORUM",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_forum_threads_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_forum_threads_created_at ON forum_threads(created_at)", "idx_forum_threads_status ON forum_threads(status)")
                        ),
                        new TableDefinition(
                                "forum_posts",
                                "Bảng lưu trữ thông tin forum_posts phục vụ phân hệ COLLABORATION_FORUM",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_forum_posts_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_forum_posts_created_at ON forum_posts(created_at)", "idx_forum_posts_status ON forum_posts(status)")
                        ),
                        new TableDefinition(
                                "post_votes",
                                "Bảng lưu trữ thông tin post_votes phục vụ phân hệ COLLABORATION_FORUM",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_post_votes_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_post_votes_created_at ON post_votes(created_at)", "idx_post_votes_status ON post_votes(status)")
                        ),
                        new TableDefinition(
                                "post_attachments",
                                "Bảng lưu trữ thông tin post_attachments phục vụ phân hệ COLLABORATION_FORUM",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_post_attachments_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_post_attachments_created_at ON post_attachments(created_at)", "idx_post_attachments_status ON post_attachments(status)")
                        ),
                        new TableDefinition(
                                "thread_subscribers",
                                "Bảng lưu trữ thông tin thread_subscribers phục vụ phân hệ COLLABORATION_FORUM",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_thread_subscribers_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_thread_subscribers_created_at ON thread_subscribers(created_at)", "idx_thread_subscribers_status ON thread_subscribers(status)")
                        )
                ),
                List.of(
                        "SELECT * FROM forum_categories WHERE status = 'ACTIVE' ORDER BY id DESC LIMIT 50;",
                        "SELECT COUNT(*) FROM forum_threads WHERE created_at >= CURRENT_DATE - INTERVAL '30 days';",
                        "EXPLAIN ANALYZE SELECT * FROM forum_categories JOIN forum_threads ON forum_categories.id = forum_threads.id;"
                )
        ));

        registerSchema(new SchemaItem(
                "NOTIFICATION_ENGINE",
                "Phân hệ Thông báo & Điều phối Đa kênh (Email, SMS, Web Push)",
                "-- DDL Script for NOTIFICATION_ENGINE\\nCREATE TABLE IF NOT EXISTS notification_templates (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS notification_events (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS notification_queue (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS user_notification_preferences (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS delivery_logs (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\n",
                List.of(
                        new TableDefinition(
                                "notification_templates",
                                "Bảng lưu trữ thông tin notification_templates phục vụ phân hệ NOTIFICATION_ENGINE",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_notification_templates_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_notification_templates_created_at ON notification_templates(created_at)", "idx_notification_templates_status ON notification_templates(status)")
                        ),
                        new TableDefinition(
                                "notification_events",
                                "Bảng lưu trữ thông tin notification_events phục vụ phân hệ NOTIFICATION_ENGINE",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_notification_events_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_notification_events_created_at ON notification_events(created_at)", "idx_notification_events_status ON notification_events(status)")
                        ),
                        new TableDefinition(
                                "notification_queue",
                                "Bảng lưu trữ thông tin notification_queue phục vụ phân hệ NOTIFICATION_ENGINE",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_notification_queue_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_notification_queue_created_at ON notification_queue(created_at)", "idx_notification_queue_status ON notification_queue(status)")
                        ),
                        new TableDefinition(
                                "user_notification_preferences",
                                "Bảng lưu trữ thông tin user_notification_preferences phục vụ phân hệ NOTIFICATION_ENGINE",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_user_notification_preferences_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_user_notification_preferences_created_at ON user_notification_preferences(created_at)", "idx_user_notification_preferences_status ON user_notification_preferences(status)")
                        ),
                        new TableDefinition(
                                "delivery_logs",
                                "Bảng lưu trữ thông tin delivery_logs phục vụ phân hệ NOTIFICATION_ENGINE",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_delivery_logs_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_delivery_logs_created_at ON delivery_logs(created_at)", "idx_delivery_logs_status ON delivery_logs(status)")
                        )
                ),
                List.of(
                        "SELECT * FROM notification_templates WHERE status = 'ACTIVE' ORDER BY id DESC LIMIT 50;",
                        "SELECT COUNT(*) FROM notification_events WHERE created_at >= CURRENT_DATE - INTERVAL '30 days';",
                        "EXPLAIN ANALYZE SELECT * FROM notification_templates JOIN notification_events ON notification_templates.id = notification_events.id;"
                )
        ));

        registerSchema(new SchemaItem(
                "FINANCIAL_TUITION",
                "Phân hệ Quản lý Học phí & Giao dịch Thanh toán",
                "-- DDL Script for FINANCIAL_TUITION\\nCREATE TABLE IF NOT EXISTS tuition_fees (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS student_invoices (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS payment_gateways (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS payment_transactions (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS scholarships (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS financial_receipts (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\n",
                List.of(
                        new TableDefinition(
                                "tuition_fees",
                                "Bảng lưu trữ thông tin tuition_fees phục vụ phân hệ FINANCIAL_TUITION",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_tuition_fees_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_tuition_fees_created_at ON tuition_fees(created_at)", "idx_tuition_fees_status ON tuition_fees(status)")
                        ),
                        new TableDefinition(
                                "student_invoices",
                                "Bảng lưu trữ thông tin student_invoices phục vụ phân hệ FINANCIAL_TUITION",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_student_invoices_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_student_invoices_created_at ON student_invoices(created_at)", "idx_student_invoices_status ON student_invoices(status)")
                        ),
                        new TableDefinition(
                                "payment_gateways",
                                "Bảng lưu trữ thông tin payment_gateways phục vụ phân hệ FINANCIAL_TUITION",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_payment_gateways_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_payment_gateways_created_at ON payment_gateways(created_at)", "idx_payment_gateways_status ON payment_gateways(status)")
                        ),
                        new TableDefinition(
                                "payment_transactions",
                                "Bảng lưu trữ thông tin payment_transactions phục vụ phân hệ FINANCIAL_TUITION",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_payment_transactions_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_payment_transactions_created_at ON payment_transactions(created_at)", "idx_payment_transactions_status ON payment_transactions(status)")
                        ),
                        new TableDefinition(
                                "scholarships",
                                "Bảng lưu trữ thông tin scholarships phục vụ phân hệ FINANCIAL_TUITION",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_scholarships_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_scholarships_created_at ON scholarships(created_at)", "idx_scholarships_status ON scholarships(status)")
                        ),
                        new TableDefinition(
                                "financial_receipts",
                                "Bảng lưu trữ thông tin financial_receipts phục vụ phân hệ FINANCIAL_TUITION",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_financial_receipts_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_financial_receipts_created_at ON financial_receipts(created_at)", "idx_financial_receipts_status ON financial_receipts(status)")
                        )
                ),
                List.of(
                        "SELECT * FROM tuition_fees WHERE status = 'ACTIVE' ORDER BY id DESC LIMIT 50;",
                        "SELECT COUNT(*) FROM student_invoices WHERE created_at >= CURRENT_DATE - INTERVAL '30 days';",
                        "EXPLAIN ANALYZE SELECT * FROM tuition_fees JOIN student_invoices ON tuition_fees.id = student_invoices.id;"
                )
        ));

        registerSchema(new SchemaItem(
                "DIGITAL_LIBRARY",
                "Phân hệ Thư viện số & Quản lý Tài nguyên Học tập",
                "-- DDL Script for DIGITAL_LIBRARY\\nCREATE TABLE IF NOT EXISTS resource_categories (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS digital_assets (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS asset_versions (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS asset_downloads (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS asset_reading_history (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\n",
                List.of(
                        new TableDefinition(
                                "resource_categories",
                                "Bảng lưu trữ thông tin resource_categories phục vụ phân hệ DIGITAL_LIBRARY",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_resource_categories_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_resource_categories_created_at ON resource_categories(created_at)", "idx_resource_categories_status ON resource_categories(status)")
                        ),
                        new TableDefinition(
                                "digital_assets",
                                "Bảng lưu trữ thông tin digital_assets phục vụ phân hệ DIGITAL_LIBRARY",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_digital_assets_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_digital_assets_created_at ON digital_assets(created_at)", "idx_digital_assets_status ON digital_assets(status)")
                        ),
                        new TableDefinition(
                                "asset_versions",
                                "Bảng lưu trữ thông tin asset_versions phục vụ phân hệ DIGITAL_LIBRARY",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_asset_versions_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_asset_versions_created_at ON asset_versions(created_at)", "idx_asset_versions_status ON asset_versions(status)")
                        ),
                        new TableDefinition(
                                "asset_downloads",
                                "Bảng lưu trữ thông tin asset_downloads phục vụ phân hệ DIGITAL_LIBRARY",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_asset_downloads_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_asset_downloads_created_at ON asset_downloads(created_at)", "idx_asset_downloads_status ON asset_downloads(status)")
                        ),
                        new TableDefinition(
                                "asset_reading_history",
                                "Bảng lưu trữ thông tin asset_reading_history phục vụ phân hệ DIGITAL_LIBRARY",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_asset_reading_history_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_asset_reading_history_created_at ON asset_reading_history(created_at)", "idx_asset_reading_history_status ON asset_reading_history(status)")
                        )
                ),
                List.of(
                        "SELECT * FROM resource_categories WHERE status = 'ACTIVE' ORDER BY id DESC LIMIT 50;",
                        "SELECT COUNT(*) FROM digital_assets WHERE created_at >= CURRENT_DATE - INTERVAL '30 days';",
                        "EXPLAIN ANALYZE SELECT * FROM resource_categories JOIN digital_assets ON resource_categories.id = digital_assets.id;"
                )
        ));

        registerSchema(new SchemaItem(
                "CAREER_PLACEMENT",
                "Phân hệ Quản lý Thực tập & Giới thiệu Việc làm",
                "-- DDL Script for CAREER_PLACEMENT\\nCREATE TABLE IF NOT EXISTS partner_companies (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS job_postings (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS student_cvs (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS internship_applications (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS interview_schedules (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\n",
                List.of(
                        new TableDefinition(
                                "partner_companies",
                                "Bảng lưu trữ thông tin partner_companies phục vụ phân hệ CAREER_PLACEMENT",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_partner_companies_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_partner_companies_created_at ON partner_companies(created_at)", "idx_partner_companies_status ON partner_companies(status)")
                        ),
                        new TableDefinition(
                                "job_postings",
                                "Bảng lưu trữ thông tin job_postings phục vụ phân hệ CAREER_PLACEMENT",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_job_postings_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_job_postings_created_at ON job_postings(created_at)", "idx_job_postings_status ON job_postings(status)")
                        ),
                        new TableDefinition(
                                "student_cvs",
                                "Bảng lưu trữ thông tin student_cvs phục vụ phân hệ CAREER_PLACEMENT",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_student_cvs_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_student_cvs_created_at ON student_cvs(created_at)", "idx_student_cvs_status ON student_cvs(status)")
                        ),
                        new TableDefinition(
                                "internship_applications",
                                "Bảng lưu trữ thông tin internship_applications phục vụ phân hệ CAREER_PLACEMENT",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_internship_applications_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_internship_applications_created_at ON internship_applications(created_at)", "idx_internship_applications_status ON internship_applications(status)")
                        ),
                        new TableDefinition(
                                "interview_schedules",
                                "Bảng lưu trữ thông tin interview_schedules phục vụ phân hệ CAREER_PLACEMENT",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_interview_schedules_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_interview_schedules_created_at ON interview_schedules(created_at)", "idx_interview_schedules_status ON interview_schedules(status)")
                        )
                ),
                List.of(
                        "SELECT * FROM partner_companies WHERE status = 'ACTIVE' ORDER BY id DESC LIMIT 50;",
                        "SELECT COUNT(*) FROM job_postings WHERE created_at >= CURRENT_DATE - INTERVAL '30 days';",
                        "EXPLAIN ANALYZE SELECT * FROM partner_companies JOIN job_postings ON partner_companies.id = job_postings.id;"
                )
        ));

        registerSchema(new SchemaItem(
                "DORMITORY_FACILITY",
                "Phân hệ Ký túc xá & Cơ sở vật chất trường học",
                "-- DDL Script for DORMITORY_FACILITY\\nCREATE TABLE IF NOT EXISTS dorm_buildings (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS dorm_rooms (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS room_allocations (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS dorm_invoices (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\nCREATE TABLE IF NOT EXISTS facility_maintenance_requests (\\n    id BIGSERIAL PRIMARY KEY,\\n    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,\\n    status VARCHAR(50) DEFAULT 'ACTIVE'\\n);\\n\\n",
                List.of(
                        new TableDefinition(
                                "dorm_buildings",
                                "Bảng lưu trữ thông tin dorm_buildings phục vụ phân hệ DORMITORY_FACILITY",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_dorm_buildings_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_dorm_buildings_created_at ON dorm_buildings(created_at)", "idx_dorm_buildings_status ON dorm_buildings(status)")
                        ),
                        new TableDefinition(
                                "dorm_rooms",
                                "Bảng lưu trữ thông tin dorm_rooms phục vụ phân hệ DORMITORY_FACILITY",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_dorm_rooms_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_dorm_rooms_created_at ON dorm_rooms(created_at)", "idx_dorm_rooms_status ON dorm_rooms(status)")
                        ),
                        new TableDefinition(
                                "room_allocations",
                                "Bảng lưu trữ thông tin room_allocations phục vụ phân hệ DORMITORY_FACILITY",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_room_allocations_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_room_allocations_created_at ON room_allocations(created_at)", "idx_room_allocations_status ON room_allocations(status)")
                        ),
                        new TableDefinition(
                                "dorm_invoices",
                                "Bảng lưu trữ thông tin dorm_invoices phục vụ phân hệ DORMITORY_FACILITY",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_dorm_invoices_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_dorm_invoices_created_at ON dorm_invoices(created_at)", "idx_dorm_invoices_status ON dorm_invoices(status)")
                        ),
                        new TableDefinition(
                                "facility_maintenance_requests",
                                "Bảng lưu trữ thông tin facility_maintenance_requests phục vụ phân hệ DORMITORY_FACILITY",
                                List.of("id BIGSERIAL", "created_at TIMESTAMP", "updated_at TIMESTAMP", "status VARCHAR(50)"),
                                List.of("id"),
                                List.of("fk_facility_maintenance_requests_reference REFERENCES parent_table(id) ON DELETE CASCADE"),
                                List.of("idx_facility_maintenance_requests_created_at ON facility_maintenance_requests(created_at)", "idx_facility_maintenance_requests_status ON facility_maintenance_requests(status)")
                        )
                ),
                List.of(
                        "SELECT * FROM dorm_buildings WHERE status = 'ACTIVE' ORDER BY id DESC LIMIT 50;",
                        "SELECT COUNT(*) FROM dorm_rooms WHERE created_at >= CURRENT_DATE - INTERVAL '30 days';",
                        "EXPLAIN ANALYZE SELECT * FROM dorm_buildings JOIN dorm_rooms ON dorm_buildings.id = dorm_rooms.id;"
                )
        ));

    }

    private static void registerSchema(SchemaItem schema) {
        SCHEMAS.put(schema.schemaName(), schema);
    }

    public static List<SchemaItem> getAllSchemas() {
        return List.copyOf(SCHEMAS.values());
    }

    public static Optional<SchemaItem> findBySchemaName(String name) {
        return Optional.ofNullable(SCHEMAS.get(name));
    }

    public static List<SchemaItem> findByDomain(String domain) {
        if (domain == null || domain.isBlank()) {
            return getAllSchemas();
        }
        String d = domain.toLowerCase();
        return SCHEMAS.values().stream()
                .filter(s -> s.schemaName().toLowerCase().contains(d) || s.description().toLowerCase().contains(d))
                .toList();
    }

    public static int getTotalSchemaCount() {
        return SCHEMAS.size();
    }
}
