package lms.auth;

/**
 * Người dùng đã xác thực của request hiện tại (hoặc của một kết nối WebSocket).
 * Controller nhận object này bằng cách khai báo tham số kiểu AuthPrincipal.
 */
public record AuthPrincipal(
        long userId,
        String publicId,
        String username,
        String fullName,
        Role role,
        long sessionId,
        String csrfToken,
        boolean mustChangePassword) {

    public static final String REQUEST_ATTRIBUTE = AuthPrincipal.class.getName();
}
