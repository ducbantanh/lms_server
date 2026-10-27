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
    /** Tạo/xóa khóa học và phân công giảng viên phụ trách (chỉ admin). */
    public static final String COURSES_ASSIGN = "courses.assign";
    public static final String SUBMISSIONS_CREATE = "submissions.create";
    public static final String SUBMISSIONS_MANAGE = "submissions.manage";
    /** Danh mục phòng học và lịch học của mọi khóa học (chỉ admin). */
    public static final String SCHEDULES_MANAGE = "schedules.manage";

    private Permissions() {
    }
}
