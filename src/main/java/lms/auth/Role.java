package lms.auth;

/**
 * Role dạng số, khớp với bảng roles (V2 migration). Không đổi số đã dùng.
 */
public enum Role {
    ADMIN(0),
    TEACHER(1),
    MENTOR(2),
    SUPERVISOR(3),
    PARENT(4),
    STUDENT(5),
    VISITOR(6);

    private final int code;

    Role(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public static Role fromCode(int code) {
        for (Role r : values()) {
            if (r.code == code) {
                return r;
            }
        }
        throw new IllegalArgumentException("Unknown role code: " + code);
    }
}
