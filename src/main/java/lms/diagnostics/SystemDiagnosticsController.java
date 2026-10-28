package lms.diagnostics;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.auth.Permissions;
import lms.auth.RequirePermission;
import lms.common.ApiResponse;

/**
 * Endpoint quản trị xem thông tin tài nguyên và chẩn đoán hệ thống máy chủ LMS.
 */
@RestController
@RequestMapping("/api/v1/admin/diagnostics")
public class SystemDiagnosticsController {

    private final SystemDiagnosticsService diagnosticsService;

    public SystemDiagnosticsController(SystemDiagnosticsService diagnosticsService) {
        this.diagnosticsService = diagnosticsService;
    }

    /**
     * Lấy các thông số vận hành của hệ thống máy chủ (Bộ nhớ, CPU, Disk, Database).
     */
    @GetMapping
    @RequirePermission(Permissions.AUDIT_VIEW)
    public ApiResponse<SystemDiagnosticsDto> getDiagnostics() {
        return ApiResponse.ok(diagnosticsService.captureDiagnostics());
    }
}
