package lms.audit;

import java.time.Instant;
import java.util.Locale;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lms.auth.Permissions;
import lms.auth.RequirePermission;
import lms.common.ApiResponse;
import lms.common.PageResult;
import lms.user.User;

@RestController
@RequestMapping("/api/v1/admin/audit")
@RequirePermission(Permissions.AUDIT_VIEW)
public class AuditController {

    /** actorUsername/actorName null: hệ thống (script backup/restore) hoặc tài khoản đã bị xóa. */
    public record AuditView(String actorUsername, String actorName, String action, String target, String detail,
            String ip, Instant createdAt) {
        static AuditView of(Object[] row) {
            AuditLog a = (AuditLog) row[0];
            User u = (User) row[1];
            return new AuditView(u == null ? null : u.getUsername(), u == null ? null : u.getFullName(),
                    a.getAction(), a.getTarget(), a.getDetail(), a.getIp(), a.getCreatedAt());
        }
    }

    private final AuditLogRepository repo;

    public AuditController(AuditLogRepository repo) {
        this.repo = repo;
    }

    /**
     * GET /admin/audit?category=course&q=...: category là phần trước dấu chấm của action
     * (auth, user, course, lesson...), q tìm trong đối tượng, mô tả, tài khoản và họ tên người thực hiện.
     */
    @GetMapping
    public ApiResponse<PageResult<AuditView>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        String prefix = category == null || !category.matches("[a-z_]{1,32}") ? null : category + ".";
        String needle = q == null || q.isBlank() ? null
                : "%" + escapeLike(q.trim().toLowerCase(Locale.ROOT)) + "%";
        PageRequest pr = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 200));
        return ApiResponse.ok(PageResult.of(repo.search(prefix, needle, pr), AuditView::of));
    }

    /** % và _ người dùng gõ là ký tự thường, không phải wildcard (MySQL dùng \ làm ký tự escape mặc định). */
    private static String escapeLike(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
