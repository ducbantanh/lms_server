package lms.auth;

/**
 * Mã permission, khớp bảng permissions. Dùng hằng số để tránh gõ sai chuỗi trong @RequirePermission.
 */
public final class Permissions {

    public static final String USERS_MANAGE = "users.manage";
    public static final String SESSIONS_MANAGE = "sessions.manage";
    public static final String PRESENCE_VIEW = "presence.view";
    public static final String AUDIT_VIEW = "audit.view";
    public static final String COURSES_VIEW = "courses.view";
    public static final String COURSES_MANAGE = "courses.manage";
    public static final String SUBMISSIONS_CREATE = "submissions.create";
    public static final String SUBMISSIONS_MANAGE = "submissions.manage";

    private Permissions() {
    }
}
