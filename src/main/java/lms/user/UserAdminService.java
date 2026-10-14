package lms.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lms.audit.AuditService;
import lms.auth.AuthPrincipal;
import lms.auth.Role;
import lms.auth.session.SessionService;
import lms.common.AppException;
import lms.common.ErrorCode;
import lms.realtime.RealtimeHub;

/**
 * Nghiệp vụ quản lý tài khoản của admin. Mọi thao tác thay đổi đều ghi audit.
 */
@Service
public class UserAdminService {

    private final UserRepository users;
    private final SessionService sessions;
    private final RealtimeHub hub;
    private final AuditService audit;

    public UserAdminService(UserRepository users, SessionService sessions, RealtimeHub hub, AuditService audit) {
        this.users = users;
        this.sessions = sessions;
        this.hub = hub;
        this.audit = audit;
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
