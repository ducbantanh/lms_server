package lms.auth.reset;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lms.audit.AuditService;
import lms.auth.session.SessionService;
import lms.common.AppException;
import lms.common.ErrorCode;
import lms.common.RandomTokens;
import lms.config.AppProperties;
import lms.realtime.RealtimeHub;
import lms.user.User;
import lms.user.UserRepository;

/** Khôi phục mật khẩu bằng OTP 6 số gửi tới email đã đăng ký trong hồ sơ. */
@Service
public class PasswordResetService {

    public record Requested(int expiresInSeconds, int resendAfterSeconds) {
    }

    public enum ResetOutcome {
        SUCCESS,
        INVALID_CODE,
        TOO_MANY_ATTEMPTS
    }

    private static final int OTP_DIGITS = 6;

    private final UserRepository users;
    private final PasswordResetOtpRepository otps;
    private final PasswordResetMailer mailer;
    private final PasswordEncoder encoder;
    private final SessionService sessions;
    private final RealtimeHub hub;
    private final AuditService audit;
    private final Duration ttl;
    private final Duration cooldown;
    private final int maxAttempts;
    private final int passwordMinLength;
    private final String dummyOtpHash;

    public PasswordResetService(UserRepository users, PasswordResetOtpRepository otps, PasswordResetMailer mailer,
            PasswordEncoder encoder, SessionService sessions, RealtimeHub hub, AuditService audit,
            AppProperties props) {
        this.users = users;
        this.otps = otps;
        this.mailer = mailer;
        this.encoder = encoder;
        this.sessions = sessions;
        this.hub = hub;
        this.audit = audit;
        this.ttl = props.passwordReset().otpTtl();
        this.cooldown = props.passwordReset().resendCooldown();
        this.maxAttempts = props.passwordReset().maxAttempts();
        this.passwordMinLength = props.security().passwordMinLength();
        this.dummyOtpHash = encoder.encode("000000");
    }

    /** Luôn trả cùng kiểu phản hồi để không tiết lộ username có tồn tại hoặc đã có email hay chưa. */
    @Transactional
    public Requested requestCode(String username, String ip) {
        mailer.requireConfigured();
        String name = username.strip();
        User user = users.findByUsername(name).filter(User::isActive).orElse(null);
        Requested response = policyView();
        if (user == null || user.getEmail() == null || user.getEmail().isBlank()) {
            encoder.matches("000000", dummyOtpHash);
            return response;
        }

        Instant now = now();
        PasswordResetOtp otp = otps.findForUpdate(user.getId()).orElse(null);
        if (otp != null && !otp.canResend(now, cooldown)) {
            return response;
        }

        String code = RandomTokens.numericCode(OTP_DIGITS);
        String hash = encoder.encode(code);
        Instant expiresAt = now.plus(ttl);
        if (otp == null) {
            otp = new PasswordResetOtp(user.getId(), hash, now, expiresAt);
        } else {
            otp.replace(hash, now, expiresAt);
        }
        otps.save(otp);
        mailer.sendOtp(user.getEmail(), user.getFullName(), code, ttl);
        audit.record(null, AuditService.PASSWORD_RESET_REQUESTED, user.getUsername(), null, ip);
        return response;
    }

    /** Không ném lỗi khi OTP sai để transaction vẫn ghi số lần thử. */
    @Transactional
    public ResetOutcome resetPassword(String username, String code, String newPassword, String ip) {
        if (newPassword.length() < passwordMinLength) {
            throw new AppException(ErrorCode.VALIDATION_FAILED,
                    "Password must be at least " + passwordMinLength + " characters");
        }
        String name = username.strip();
        User user = users.findByUsername(name).orElse(null);
        if (user == null || !user.isActive()) {
            encoder.matches(code, dummyOtpHash);
            return ResetOutcome.INVALID_CODE;
        }

        PasswordResetOtp otp = otps.findForUpdate(user.getId()).orElse(null);
        Instant now = now();
        if (otp == null) {
            encoder.matches(code, dummyOtpHash);
            return ResetOutcome.INVALID_CODE;
        }
        if (otp.isExpired(now)) {
            otps.delete(otp);
            return ResetOutcome.INVALID_CODE;
        }
        if (otp.getAttempts() >= maxAttempts) {
            otps.delete(otp);
            return ResetOutcome.TOO_MANY_ATTEMPTS;
        }
        if (!encoder.matches(code, otp.getCodeHash())) {
            if (otp.recordFailure() >= maxAttempts) {
                otps.delete(otp);
                return ResetOutcome.TOO_MANY_ATTEMPTS;
            }
            return ResetOutcome.INVALID_CODE;
        }
        if (encoder.matches(newPassword, user.getPasswordHash())) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "New password must differ from the current one");
        }

        user.setPasswordHash(encoder.encode(newPassword));
        user.setMustChangePassword(false);
        otps.delete(otp);
        int revoked = sessions.revokeAllForUser(user.getId(), SessionService.REASON_PASSWORD_CHANGED);
        hub.disconnectUser(user.getId(), SessionService.REASON_PASSWORD_CHANGED);
        audit.record(user.getId(), AuditService.PASSWORD_RESET_COMPLETED, user.getUsername(),
                "sessions=" + revoked, ip);
        return ResetOutcome.SUCCESS;
    }

    @Scheduled(fixedDelay = 60 * 60 * 1000)
    @Transactional
    public void purgeExpired() {
        otps.deleteExpiredBefore(now());
    }

    private Requested policyView() {
        return new Requested(toSeconds(ttl), toSeconds(cooldown));
    }

    private static int toSeconds(Duration duration) {
        return Math.toIntExact(Math.min(Integer.MAX_VALUE, Math.max(1, duration.toSeconds())));
    }

    private static Instant now() {
        return Instant.now().truncatedTo(ChronoUnit.MILLIS);
    }
}
