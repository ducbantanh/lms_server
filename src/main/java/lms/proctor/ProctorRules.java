package lms.proctor;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

/**
 * Loại sự kiện của phiên thi và cách tính vi phạm. Trình duyệt chỉ báo sự việc (rời trang bao lâu, thoát toàn màn
 * hình...), server quyết định việc nào tính là vi phạm. Java thuần để test không cần Spring.
 */
public final class ProctorRules {

    // ---------------------------------------------------------------- trình duyệt báo (ProctorService.report)
    /**
     * Chuyển tab / cửa sổ khác rồi quay lại; durationMs = thời gian rời bài, detail = tab / window / file_picker
     * (hộp chọn file của hệ điều hành khi tải bài làm lên: ghi lại, không tính vi phạm).
     */
    public static final String LEFT_PAGE = "left_page";
    public static final String FILE_PICKER = "file_picker";
    /** Thoát chế độ toàn màn hình mà không rời trang (Esc, F11). */
    public static final String FULLSCREEN_EXIT = "fullscreen_exit";
    /** Dán nội dung không chép từ trong bài thi (bị chặn); detail = số ký tự. */
    public static final String PASTE_BLOCKED = "paste_blocked";
    /** Chép nội dung đề ra ngoài (bị chặn). */
    public static final String COPY_BLOCKED = "copy_blocked";
    /** Phím tắt bị chặn (in, lưu trang, xem mã nguồn, công cụ dành cho nhà phát triển); detail = tổ hợp phím. */
    public static final String SHORTCUT_BLOCKED = "shortcut_blocked";
    /** Máy đang nối thêm màn hình phụ. */
    public static final String MULTI_SCREEN = "multi_screen";
    /** Tải lại trang bài thi. */
    public static final String RELOADED = "reloaded";

    // ---------------------------------------------------------------- server ghi
    public static final String STARTED = "started";
    /** Vào lại phòng thi sau khi giám thị cho đổi máy. */
    public static final String DEVICE_CHANGED = "device_changed";
    /** Đổi IP giữa giờ thi. */
    public static final String IP_CHANGED = "ip_changed";
    /**
     * Có người vào thi bằng tài khoản này từ máy (phiên đăng nhập) khác: bị chặn nhưng không tính vi phạm (vd. trình
     * duyệt hỏng phải đăng nhập lại); giám thị xem nhật ký rồi cho đổi máy.
     */
    public static final String OTHER_DEVICE = "other_device";
    /** Nhập sai mã phòng thi. */
    public static final String CODE_REJECTED = "code_rejected";
    public static final String LOCKED = "locked";
    public static final String UNLOCKED = "unlocked";
    public static final String DEVICE_RESET = "device_reset";
    /** Kết thúc phiên thi; detail = submitted / timed_out / finished. */
    public static final String FINISHED = "finished";

    public static final Set<String> CLIENT_TYPES = Set.of(LEFT_PAGE, FULLSCREEN_EXIT, PASTE_BLOCKED, COPY_BLOCKED,
            SHORTCUT_BLOCKED, MULTI_SCREEN, RELOADED);

    /** Rời trang ngắn hơn (thông báo của hệ điều hành chiếm focus chốc lát) chỉ ghi lại, không tính vi phạm. */
    public static final long MIN_AWAY_MS = 2_000;
    /** Một máy coi như mất kết nối khi quá lâu không có nhịp (trình duyệt gửi mỗi 15 giây). */
    public static final Duration OFFLINE_AFTER = Duration.ofSeconds(45);
    /** Giới hạn số vi phạm giảng viên đặt. */
    public static final int MAX_VIOLATION_LIMIT = 100;
    /** Giữ tối đa bấy nhiêu sự kiện mỗi phiên thi; quá thì vẫn đếm vi phạm nhưng không ghi nhật ký nữa. */
    public static final int MAX_EVENTS = 2_000;

    private ProctorRules() {
    }

    /** Sự kiện có tính là một lần vi phạm không. */
    public static boolean counted(String type, Long durationMs, String detail, boolean fullscreenRequired) {
        return switch (type) {
            case LEFT_PAGE -> durationMs != null && durationMs >= MIN_AWAY_MS && !FILE_PICKER.equals(detail);
            case FULLSCREEN_EXIT -> fullscreenRequired;
            case PASTE_BLOCKED, MULTI_SCREEN, IP_CHANGED -> true;
            default -> false;
        };
    }

    /** Đủ số lần vi phạm để khóa bài (max null = chỉ ghi lại, không khóa). */
    public static boolean reachedLimit(int violations, Integer max) {
        return max != null && violations >= max;
    }

    /**
     * Giờ xảy ra do trình duyệt báo (đã quy về giờ server), giữ trong [from, now]: sự kiện bị giữ lại khi mất mạng
     * gửi sau vẫn đúng giờ, giờ sai lệch quá xa thì lấy giờ nhận.
     */
    public static Instant occurredAt(Instant reported, Instant from, Instant now) {
        if (reported == null || reported.isAfter(now) || reported.isBefore(from)) {
            return now;
        }
        return reported;
    }

    public static boolean isOnline(Instant lastSeenAt, Instant now) {
        return lastSeenAt != null && lastSeenAt.isAfter(now.minus(OFFLINE_AFTER));
    }
}
