-- V29: mã OTP khôi phục mật khẩu. Chỉ lưu BCrypt của mã, không lưu OTP rõ trong DB/log.
-- Mỗi tài khoản chỉ có một mã còn hiệu lực; gửi mã mới ghi đè mã cũ.

CREATE TABLE password_reset_otps (
    user_id       BIGINT UNSIGNED  NOT NULL,
    code_hash     VARCHAR(100)     NOT NULL,
    sent_at       DATETIME(3)      NOT NULL,
    expires_at    DATETIME(3)      NOT NULL,
    attempts      TINYINT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY (user_id),
    KEY idx_password_reset_expires (expires_at),
    CONSTRAINT fk_password_reset_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
