package lms.user;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lms.auth.AuthPrincipal;
import lms.auth.Permissions;
import lms.auth.RequirePermission;
import lms.common.ApiResponse;
import lms.common.AppException;
import lms.common.ErrorCode;
import lms.common.PageResult;
import lms.common.RequestInfo;
import lms.user.importing.AccountImportService;
import lms.user.importing.ImportReport;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequirePermission(Permissions.USERS_MANAGE)
public class AdminUserController {

    /** role: 5 sinh viên (mặc định), 1 giảng viên. */
    public record ImportTextRequest(@NotBlank String content, Integer role) {
    }

    private final UserAdminService service;
    private final AccountImportService importService;

    public AdminUserController(UserAdminService service, AccountImportService importService) {
        this.service = service;
        this.importService = importService;
    }

    /** GET /api/v1/admin/users?q=&role=5&status=1&page=0&size=20 */
    @GetMapping
    public ApiResponse<PageResult<UserView>> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer role,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (role != null && (role < 0 || role > 6)) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Unknown role");
        }
        return ApiResponse.ok(PageResult.of(service.search(q, role, status, page, size), UserView::of));
    }

    @PutMapping("/{id}")
    public ApiResponse<UserView> update(@PathVariable String id, @Valid @RequestBody UserAdminService.UpdateRequest body,
            AuthPrincipal me, HttpServletRequest req) {
        return ApiResponse.ok(UserView.of(service.update(id, body, me, RequestInfo.clientIp(req))));
    }

    /** Đặt lại mật khẩu về ngày sinh ddmmyyyy, bắt đổi ở lần đăng nhập tới. */
    @PostMapping("/{id}/reset-password")
    public ApiResponse<UserView> resetPassword(@PathVariable String id, AuthPrincipal me, HttpServletRequest req) {
        return ApiResponse.ok(UserView.of(service.resetPassword(id, me, RequestInfo.clientIp(req))));
    }

    @PostMapping("/{id}/lock")
    public ApiResponse<UserView> lock(@PathVariable String id, AuthPrincipal me, HttpServletRequest req) {
        return ApiResponse.ok(UserView.of(service.lock(id, me, RequestInfo.clientIp(req))));
    }

    @PostMapping("/{id}/unlock")
    public ApiResponse<UserView> unlock(@PathVariable String id, AuthPrincipal me, HttpServletRequest req) {
        return ApiResponse.ok(UserView.of(service.unlock(id, me, RequestInfo.clientIp(req))));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id, AuthPrincipal me, HttpServletRequest req) {
        service.delete(id, me, RequestInfo.clientIp(req));
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/force-logout")
    @RequirePermission(Permissions.SESSIONS_MANAGE)
    public ApiResponse<Map<String, Integer>> forceLogout(@PathVariable String id, AuthPrincipal me,
            HttpServletRequest req) {
        int revoked = service.forceLogout(id, me, RequestInfo.clientIp(req));
        return ApiResponse.ok(Map.of("revokedSessions", revoked));
    }

    /** Import từ text dán vào ô textarea (dạng | , ; hoặc tab như AccountImportParser). */
    @PostMapping(path = "/import", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<ImportReport> importText(@Valid @RequestBody ImportTextRequest body, AuthPrincipal me,
            HttpServletRequest req) {
        return ApiResponse.ok(importService.importAccounts(body.content(),
                AccountImportService.importableRole(body.role()), me, RequestInfo.clientIp(req)));
    }

    /** Import từ file .txt hoặc .csv (UTF-8, hoặc Windows-1258 khi Excel lưu "CSV" thường), field "file". */
    @PostMapping(path = "/import-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ImportReport> importFile(@RequestPart("file") MultipartFile file,
            @RequestParam(required = false) Integer role, AuthPrincipal me, HttpServletRequest req)
            throws IOException {
        String name = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase(Locale.ROOT);
        if (file.isEmpty() || !(name.endsWith(".txt") || name.endsWith(".csv"))) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Only non-empty .txt or .csv files are accepted");
        }
        return ApiResponse.ok(importService.importAccounts(AccountImportService.decode(file.getBytes()),
                AccountImportService.importableRole(role), me, RequestInfo.clientIp(req)));
    }
}
