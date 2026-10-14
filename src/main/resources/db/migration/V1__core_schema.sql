-- V1: schema lõi cho giai đoạn 1 (auth, session, user, audit).
-- Quy tắc: KHÔNG sửa file migration đã chạy; muốn đổi schema thì tạo V3, V4...

CREATE TABLE roles (
    id          TINYINT UNSIGNED NOT NULL,
    code        VARCHAR(32)      NOT NULL,
    name        VARCHAR(64)      NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_roles_code (code)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE permissions (
    id          SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
    code        VARCHAR(64)       NOT NULL,
    description VARCHAR(255)      NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_permissions_code (code)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE role_permissions (
    role_id       TINYINT UNSIGNED  NOT NULL,
    permission_id SMALLINT UNSIGNED NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_rp_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
    CONSTRAINT fk_rp_permission FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE users (
    id                   BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT,
    public_id            CHAR(22)         NOT NULL,           -- ID ngẫu nhiên dùng trong URL/API
    username             VARCHAR(32)      NOT NULL,           -- collation _ci => không phân biệt hoa thường
    full_name            VARCHAR(100)     NOT NULL,
    password_hash        VARCHAR(100)     NOT NULL,           -- BCrypt
    role                 TINYINT UNSIGNED NOT NULL,
    status               TINYINT(1)       NOT NULL DEFAULT 1, -- 1 active, 0 inactive
    must_change_password TINYINT(1)       NOT NULL DEFAULT 0,
    birth_date           DATE             NULL,
    created_at           DATETIME(3)      NOT NULL,
    updated_at           DATETIME(3)      NOT NULL,
    last_login_at        DATETIME(3)      NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_public_id (public_id),
    UNIQUE KEY uk_users_username (username),
    KEY idx_users_role (role),
    CONSTRAINT fk_users_role FOREIGN KEY (role) REFERENCES roles (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE sessions (
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    token_hash    CHAR(64)        NOT NULL,  -- SHA-256 hex của token; token gốc chỉ nằm trong cookie
    csrf_token    VARCHAR(64)     NOT NULL,
    user_id       BIGINT UNSIGNED NOT NULL,
    created_at    DATETIME(3)     NOT NULL,
    last_seen_at  DATETIME(3)     NOT NULL,
    expires_at    DATETIME(3)     NOT NULL,
    revoked_at    DATETIME(3)     NULL,
    revoke_reason VARCHAR(32)     NULL,
    ip            VARCHAR(45)     NULL,
    user_agent    VARCHAR(255)    NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_sessions_token_hash (token_hash),
    KEY idx_sessions_user (user_id),
    KEY idx_sessions_expires (expires_at),
    CONSTRAINT fk_sessions_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE audit_logs (
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    actor_user_id BIGINT UNSIGNED NULL,
    action        VARCHAR(64)     NOT NULL,
    target        VARCHAR(128)    NULL,
    detail        VARCHAR(1000)   NULL,
    ip            VARCHAR(45)     NULL,
    created_at    DATETIME(3)     NOT NULL,
    PRIMARY KEY (id),
    KEY idx_audit_actor (actor_user_id),
    KEY idx_audit_created (created_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
