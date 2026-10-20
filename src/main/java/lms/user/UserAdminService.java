package lms.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lms.audit.AuditService;
import lms.auth.AuthPrincipal;
import lms.auth.Role;
import lms.auth.session.SessionService;
import lms.common.AppException;
import lms.common.ErrorCode;
import lms.realtime.RealtimeHub;
import lms.user.importing.AccountImportParser;

/**
 * Nghiệp vụ quản lý tài khoản của admin. Mọi thao tác thay đổi đều ghi audit.
 */
@Service
public class UserAdminService {

    /** Sửa thông tin: birthDate null = không có ngày sinh (không đặt lại mật khẩu về ngày sinh được). */
    public record UpdateRequest(
            @NotBlank @Size(max = 32) String username,
            @NotBlank @Size(max = 100) String fullName,
            LocalDate birthDate) {
    }

    private final UserRepository users;
    private final SessionService sessions;
    private final RealtimeHub hub;
    private final AuditService audit;
    private final PasswordEncoder encoder;

    public UserAdminService(UserRepository users, SessionService sessions, RealtimeHub hub, AuditService audit,
            PasswordEncoder encoder) {
        this.users = users;
        this.sessions = sessions;
        this.hub = hub;
        this.audit = audit;
        this.encoder = encoder;
    }

    /**
     * Sửa mã đăng nhập, họ tên, ngày sinh (cùng quy tắc với import). Đổi mã sang mã đã có người dùng:
     * USERNAME_TAKEN. Không đổi vai trò, mật khẩu hay trạng thái ở đây.
     */
    @Transactional
    public User update(String publicId, UpdateRequest req, AuthPrincipal actor, String ip) {
        User u = find(publicId);
        String username = req.username().strip();
        String fullName = AccountImportParser.normalizeName(req.fullName());
        if (!AccountImportParser.isValidUsername(username)) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Invalid username");
        }
        if (!AccountImportParser.isValidName(fullName)) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Invalid full name");
        }
        if (req.birthDate() != null && !AccountImportParser.isValidBirthDate(req.birthDate())) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Invalid birth date");
        }
        // collation _ci: đổi hoa/thường của chính mã mình thì tìm ra chính mình, không tính là trùng
        if (!username.equalsIgnoreCase(u.getUsername()) && users.findByUsername(username).isPresent()) {
            throw new AppException(ErrorCode.USERNAME_TAKEN);
        }
        List<String> changes = new ArrayList<>();
        if (!username.equals(u.getUsername())) {
            changes.add("username " + u.getUsername() + " → " + username);
        }
        if (!fullName.equals(u.getFullName())) {
            changes.add("họ tên " + u.getFullName() + " → " + fullName);
        }
        if (!Objects.equals(req.birthDate(), u.getBirthDate())) {
            changes.add("ngày sinh " + u.getBirthDate() + " → " + req.birthDate());
        }
        u.setUsername(username);
        u.setFullName(fullName);
        u.setBirthDate(req.birthDate());
        users.flush(); // trùng mã do ghi đồng thời: lỗi unique bật ra ở đây, trong transaction
        if (!changes.isEmpty()) {
            audit.record(actor.userId(), AuditService.USER_UPDATED, u.getUsername(), String.join("; ", changes), ip);
        }
        return u;
    }

    /**
     * Đặt lại mật khẩu về ngày sinh (ddmmyyyy) và bắt đổi ở lần đăng nhập tới; đăng xuất mọi thiết bị.
     * Tài khoản chưa có ngày sinh: BIRTH_DATE_REQUIRED.
     */
    @Transactional
    public User resetPassword(String publicId, AuthPrincipal actor, String ip) {
        User u = find(publicId);
        guardNotSelf(u, actor);
        if (u.getBirthDate() == null) {
            throw new AppException(ErrorCode.BIRTH_DATE_REQUIRED);
        }
        u.setPasswordHash(encoder.encode(AccountImportParser.passwordFor(u.getBirthDate())));
        u.setMustChangePassword(true);
        int revoked = sessions.revokeAllForUser(u.getId(), SessionService.REASON_PASSWORD_CHANGED);
        hub.disconnectUser(u.getId(), SessionService.REASON_PASSWORD_CHANGED);
        audit.record(actor.userId(), AuditService.PASSWORD_RESET, u.getUsername(), "sessions=" + revoked, ip);
        return u;
    }

    @Transactional(readOnly = true)
    public Page<User> search(String q, Integer role, Integer status, int page, int size) {
        String query = (q == null || q.isBlank()) ? null : q.strip();
        Role r = role == null ? null : Role.fromCode(role);
        PageRequest pr = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "id"));
        return users.search(query, r, status, pr);
    }

    @Transactional
    public User lock(String publicId, AuthPrincipal actor, String ip) {
        User u = find(publicId);
        guardNotSelf(u, actor);
        guardNotLastActiveAdmin(u);
        u.setStatus(User.STATUS_INACTIVE);
        sessions.revokeAllForUser(u.getId(), SessionService.REASON_LOCKED);
        hub.disconnectUser(u.getId(), SessionService.REASON_LOCKED);
        audit.record(actor.userId(), AuditService.USER_LOCKED, u.getUsername(), null, ip);
        return u;
    }

    @Transactional
    public User unlock(String publicId, AuthPrincipal actor, String ip) {
        User u = find(publicId);
        u.setStatus(User.STATUS_ACTIVE);
        audit.record(actor.userId(), AuditService.USER_UNLOCKED, u.getUsername(), null, ip);
        return u;
    }

    @Transactional
    public void delete(String publicId, AuthPrincipal actor, String ip) {
        User u = find(publicId);
        guardNotSelf(u, actor);
        guardNotLastActiveAdmin(u);
        hub.disconnectUser(u.getId(), SessionService.REASON_DELETED);
        users.delete(u); // sessions bị xóa theo ON DELETE CASCADE
        audit.record(actor.userId(), AuditService.USER_DELETED, u.getUsername(), null, ip);
    }

    /** Ép logout: thu hồi mọi session và đóng mọi WebSocket của user ngay lập tức. */
    @Transactional
    public int forceLogout(String publicId, AuthPrincipal actor, String ip) {
        User u = find(publicId);
        guardNotSelf(u, actor);
        int revoked = sessions.revokeAllForUser(u.getId(), SessionService.REASON_FORCED);
        hub.disconnectUser(u.getId(), SessionService.REASON_FORCED);
        audit.record(actor.userId(), AuditService.FORCE_LOGOUT, u.getUsername(), "sessions=" + revoked, ip);
        return revoked;
    }

    private User find(String publicId) {
        return users.findByPublicId(publicId).orElseThrow(() -> AppException.notFound("User"));
    }

    private static void guardNotSelf(User target, AuthPrincipal actor) {
        if (target.getId() == actor.userId()) {
            throw new AppException(ErrorCode.CONFLICT, "Cannot perform this action on your own account");
        }
    }

    private void guardNotLastActiveAdmin(User target) {
        if (target.getRole() == Role.ADMIN && target.isActive()
                && users.countByRoleAndStatus(Role.ADMIN, User.STATUS_ACTIVE) <= 1) {
            throw new AppException(ErrorCode.CONFLICT, "Cannot remove the last active admin");
        }
    }
}
