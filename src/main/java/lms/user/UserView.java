package lms.user;

import java.time.Instant;

/**
 * Dữ liệu user trả về client. Dùng publicId thay cho id nội bộ; không bao giờ có password hash.
 */
public record UserView(
        String id,
        String username,
        String fullName,
        int role,
        String roleName,
        int status,
        boolean mustChangePassword,
        Instant createdAt,
        Instant lastLoginAt) {

    public static UserView of(User u) {
        return new UserView(
                u.getPublicId(),
                u.getUsername(),
                u.getFullName(),
                u.getRole().code(),
                u.getRole().name(),
                u.getStatus(),
                u.isMustChangePassword(),
                u.getCreatedAt(),
                u.getLastLoginAt());
    }
}
