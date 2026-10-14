package lms.auth;

import java.time.Instant;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lms.audit.AuditService;
import lms.auth.AuthDtos.MeView;
import lms.auth.AuthDtos.SessionView;
import lms.auth.session.SessionService;
import lms.common.AppException;
import lms.common.ErrorCode;
import lms.config.AppProperties;
import lms.realtime.RealtimeHub;
import lms.user.User;
import lms.user.UserRepository;

@Service
public class AuthService {

    /** Hash giả để khi username không tồn tại vẫn tốn thời gian như khi sai mật khẩu. */
    private final String dummyHash;

    private final UserRepository users;
    private final SessionService sessions;
    private final PasswordEncoder encoder;
    private final LoginThrottle throttle;
    private final PermissionService permissions;
    private final AuditService audit;
    private final RealtimeHub hub;
    private final int passwordMinLength;

    public AuthService(UserRepository users, SessionService sessions, PasswordEncoder encoder,
            LoginThrottle throttle, PermissionService permissions, AuditService audit, RealtimeHub hub,
            AppProperties props) {
        this.users = users;
        this.sessions = sessions;
        this.encoder = encoder;
        this.throttle = throttle;
        this.permissions = permissions;
        this.audit = audit;
        this.hub = hub;
        this.passwordMinLength = props.security().passwordMinLength();
        this.dummyHash = encoder.encode("timing-equalizer-not-a-real-password");
    }

    public record LoginResult(String rawToken, SessionView session) {
    }

    @Transactional
    public LoginResult login(String username, String password, String ip, String userAgent) {
        String name = username.trim();
        if (throttle.isBlocked(name, ip)) {
            throw new AppException(ErrorCode.TOO_MANY_ATTEMPTS);
        }

        User user = users.findByUsername(name).orElse(null);
        boolean passwordOk = encoder.matches(password, user != null ? user.getPasswordHash() : dummyHash);

        // Không tiết lộ username có tồn tại hay không
        if (user == null || !passwordOk) {
            throttle.recordFailure(name, ip);
            audit.record(user != null ? user.getId() : null, AuditService.LOGIN_FAILED, name, null, ip);
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
        // Chỉ báo "bị khóa" khi đã đúng mật khẩu
        if (!user.isActive()) {
            throw new AppException(ErrorCode.ACCOUNT_DISABLED);
        }

        throttle.recordSuccess(name, ip);
        user.setLastLoginAt(Instant.now());
        SessionService.Created created = sessions.create(user, ip, userAgent);
        audit.record(user.getId(), AuditService.LOGIN_SUCCESS, user.getUsername(), null, ip);
        return new LoginResult(created.rawToken(), toSessionView(created.principal()));
    }

    public void logout(AuthPrincipal me, String ip) {
        sessions.revoke(me.sessionId(), SessionService.REASON_LOGOUT);
        hub.disconnectSession(me.sessionId(), SessionService.REASON_LOGOUT);
        audit.record(me.userId(), AuditService.LOGOUT, me.username(), null, ip);
    }

    /** Đổi mật khẩu: giữ phiên hiện tại, thu hồi mọi phiên khác của user. */
    @Transactional
    public void changePassword(AuthPrincipal me, String currentPassword, String newPassword, String ip) {
        User user = users.findById(me.userId()).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        if (!encoder.matches(currentPassword, user.getPasswordHash())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS, "Current password is incorrect");
        }
        if (newPassword.length() < passwordMinLength) {
            throw new AppException(ErrorCode.VALIDATION_FAILED,
                    "Password must be at least " + passwordMinLength + " characters");
        }
        if (encoder.matches(newPassword, user.getPasswordHash())) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "New password must differ from the current one");
        }
        user.setPasswordHash(encoder.encode(newPassword));
        user.setMustChangePassword(false);
        sessions.revokeOthersForUser(user.getId(), me.sessionId(), SessionService.REASON_PASSWORD_CHANGED);
        hub.disconnectUserExcept(user.getId(), me.sessionId(), SessionService.REASON_PASSWORD_CHANGED);
        audit.record(user.getId(), AuditService.PASSWORD_CHANGED, user.getUsername(), null, ip);
    }

    public SessionView toSessionView(AuthPrincipal p) {
        MeView me = new MeView(
                p.publicId(),
                p.username(),
                p.fullName(),
                p.role().code(),
                p.role().name(),
                p.mustChangePassword(),
                permissions.permissionsOf(p.role()));
        return new SessionView(me, p.csrfToken());
    }
}
