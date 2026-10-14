package lms.audit;

import java.time.Instant;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lms.auth.Permissions;
import lms.auth.RequirePermission;
import lms.common.ApiResponse;
import lms.common.PageResult;

@RestController
@RequestMapping("/api/v1/admin/audit")
@RequirePermission(Permissions.AUDIT_VIEW)
public class AuditController {

    public record AuditView(long id, Long actorUserId, String action, String target, String detail, String ip,
            Instant createdAt) {
        static AuditView of(AuditLog a) {
            return new AuditView(a.getId(), a.getActorUserId(), a.getAction(), a.getTarget(), a.getDetail(),
                    a.getIp(), a.getCreatedAt());
        }
    }

    private final AuditLogRepository repo;

    public AuditController(AuditLogRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public ApiResponse<PageResult<AuditView>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        PageRequest pr = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 200));
        return ApiResponse.ok(PageResult.of(repo.findAllByOrderByIdDesc(pr), AuditView::of));
    }
}
