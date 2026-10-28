package lms.attendance;

import java.nio.charset.StandardCharsets;

import jakarta.validation.Valid;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.attendance.AttendanceViews.AttendanceOverview;
import lms.attendance.AttendanceViews.CreateSessionRequest;
import lms.attendance.AttendanceViews.MarkRequest;
import lms.attendance.AttendanceViews.QrCode;
import lms.attendance.AttendanceViews.RenameRequest;
import lms.attendance.AttendanceViews.SessionDetail;
import lms.auth.AuthPrincipal;
import lms.auth.Permissions;
import lms.auth.RequirePermission;
import lms.common.ApiResponse;
import lms.report.ReportService;

/** API giảng viên: buổi điểm danh, mã QR cho màn chiếu, điểm danh thủ công, xuất CSV. */
@RestController
@RequestMapping("/api/v1/teacher/courses/{courseId}")
@RequirePermission(Permissions.COURSES_MANAGE)
public class TeacherAttendanceController {

    private final AttendanceService service;

    public TeacherAttendanceController(AttendanceService service) {
        this.service = service;
    }

    @GetMapping("/attendance")
    public ApiResponse<AttendanceOverview> overview(@PathVariable String courseId, AuthPrincipal me) {
        return ApiResponse.ok(service.overview(courseId, me));
    }

    @PostMapping("/attendance")
    public ApiResponse<SessionDetail> create(@PathVariable String courseId,
            @Valid @RequestBody CreateSessionRequest body, AuthPrincipal me) {
        return ApiResponse.ok(service.create(courseId, body, me));
    }

    @GetMapping("/attendance/{sessionId}")
    public ApiResponse<SessionDetail> detail(@PathVariable String courseId, @PathVariable String sessionId,
            AuthPrincipal me) {
        return ApiResponse.ok(service.detail(courseId, sessionId, me));
    }

    @PutMapping("/attendance/{sessionId}")
    public ApiResponse<SessionDetail> rename(@PathVariable String courseId, @PathVariable String sessionId,
            @Valid @RequestBody RenameRequest body, AuthPrincipal me) {
        return ApiResponse.ok(service.rename(courseId, sessionId, body.title(), me));
    }

    @DeleteMapping("/attendance/{sessionId}")
    public ApiResponse<Void> delete(@PathVariable String courseId, @PathVariable String sessionId,
            AuthPrincipal me) {
        service.delete(courseId, sessionId, me);
        return ApiResponse.ok();
    }

    @PostMapping("/attendance/{sessionId}/qr/open")
    public ApiResponse<SessionDetail> openQr(@PathVariable String courseId, @PathVariable String sessionId,
            AuthPrincipal me) {
        return ApiResponse.ok(service.setQrOpen(courseId, sessionId, true, me));
    }

    @PostMapping("/attendance/{sessionId}/qr/close")
    public ApiResponse<SessionDetail> closeQr(@PathVariable String courseId, @PathVariable String sessionId,
            AuthPrincipal me) {
        return ApiResponse.ok(service.setQrOpen(courseId, sessionId, false, me));
    }

    /** Màn chiếu gọi lại mỗi khi mã hết lượt (expiresAt), tức khoảng 10 giây một lần. */
    @GetMapping("/attendance/{sessionId}/qr")
    public ApiResponse<QrCode> qr(@PathVariable String courseId, @PathVariable String sessionId, AuthPrincipal me) {
        return ApiResponse.ok(service.qr(courseId, sessionId, me));
    }

    @PutMapping("/attendance/{sessionId}/records")
    public ApiResponse<SessionDetail> mark(@PathVariable String courseId, @PathVariable String sessionId,
            @Valid @RequestBody MarkRequest body, AuthPrincipal me) {
        return ApiResponse.ok(service.mark(courseId, sessionId, body, me));
    }

    @GetMapping("/attendance.csv")
    public ResponseEntity<byte[]> csv(@PathVariable String courseId, AuthPrincipal me) {
        ReportService.Csv csv = service.csv(courseId, me);
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(csv.fileName(), StandardCharsets.UTF_8).build()
                                .toString())
                .header(HttpHeaders.CACHE_CONTROL, "private, no-store")
                .body(csv.content());
    }
}
