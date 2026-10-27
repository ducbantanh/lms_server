package lms.leave;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lms.auth.AuthPrincipal;
import lms.auth.Permissions;
import lms.auth.RequirePermission;
import lms.common.ApiResponse;
import lms.leave.LeaveViews.LeaveView;

/** Sinh viên gửi / rút đơn xin nghỉ; tải file minh chứng (người viết đơn hoặc người quản lý khóa học). */
@RestController
@RequirePermission(Permissions.COURSES_VIEW)
public class StudentLeaveController {

    private final LeaveService service;

    public StudentLeaveController(LeaveService service) {
        this.service = service;
    }

    /** Multipart: field "reason" + tối đa 3 part "files" (PDF, JPG, PNG). */
    @PostMapping(path = "/api/v1/student/meetings/{meetingId}/leave-request",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<LeaveView> submit(@PathVariable String meetingId, @RequestParam("reason") String reason,
            @RequestPart(name = "files", required = false) List<MultipartFile> files, AuthPrincipal me) {
        return ApiResponse.ok(service.submit(meetingId, reason, files, me));
    }

    @DeleteMapping("/api/v1/student/leave-requests/{requestId}")
    public ApiResponse<Void> withdraw(@PathVariable String requestId, AuthPrincipal me) {
        service.withdraw(requestId, me);
        return ApiResponse.ok();
    }

    @GetMapping("/api/v1/leave-files/{fileId}")
    public ResponseEntity<Resource> download(@PathVariable String fileId, AuthPrincipal me) {
        LeaveService.Download d = service.download(fileId, me);
        LeaveRequestFile f = d.file();
        // Ảnh, PDF do sinh viên tải lên: luôn tải về, không mở trong trình duyệt
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(f.getName(), StandardCharsets.UTF_8).build();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(f.getContentType()))
                .contentLength(f.getSize())
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .header("X-Content-Type-Options", "nosniff")
                .header(HttpHeaders.CACHE_CONTROL, "private, no-store")
                .body(new FileSystemResource(d.path()));
    }
}
