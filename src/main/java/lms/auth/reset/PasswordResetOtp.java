package lms.auth.reset;

import java.time.Duration;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Một OTP đang hiệu lực của tài khoản; codeHash là BCrypt, không bao giờ lưu mã rõ. */
@Entity
@Table(name = "password_reset_otps")
public class PasswordResetOtp {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "code_hash", nullable = false, length = 100)
    private String codeHash;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private int attempts;

    protected PasswordResetOtp() {
        // JPA
    }

    public PasswordResetOtp(long userId, String codeHash, Instant sentAt, Instant expiresAt) {
        this.userId = userId;
        replace(codeHash, sentAt, expiresAt);
    }

    public void replace(String nextHash, Instant sent, Instant expires) {
        this.codeHash = nextHash;
        this.sentAt = sent;
        this.expiresAt = expires;
        this.attempts = 0;
    }

    public boolean canResend(Instant now, Duration cooldown) {
        return !sentAt.plus(cooldown).isAfter(now);
    }

    public boolean isExpired(Instant now) {
        return !expiresAt.isAfter(now);
    }

    public int recordFailure() {
        return ++attempts;
    }

    public Long getUserId() {
        return userId;
    }

    public String getCodeHash() {
        return codeHash;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public int getAttempts() {
        return attempts;
    }
}
