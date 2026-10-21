package lms.report;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.auth.AuthPrincipal;
import lms.auth.Permissions;
import lms.auth.RequirePermission;

/**
 * API giảng viên tải file về (không bọc ApiResponse): bảng điểm CSV và ZIP bài nộp.
 * Lỗi quyền/không tìm thấy xảy ra trước khi ghi byte nào nên vẫn trả JSON lỗi như mọi API khác.
 */
@RestController
@RequestMapping("/api/v1/teacher/courses/{courseId}")
@RequirePermission({ Permissions.COURSES_MANAGE, Permissions.SUBMISSIONS_MANAGE })
public class TeacherReportController {

    private static final Logger log = LoggerFactory.getLogger(TeacherReportController.class);

    private final ReportService reports;

    public TeacherReportController(ReportService reports) {
        this.reports = reports;
    }

    @GetMapping("/gradebook.csv")
    public ResponseEntity<byte[]> gradebook(@PathVariable String courseId, AuthPrincipal me) {
        ReportService.Csv csv = reports.gradebook(courseId, me);
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .header(HttpHeaders.CONTENT_DISPOSITION, attachment(csv.fileName()))
                .header(HttpHeaders.CACHE_CONTROL, "private, no-store")
                .body(csv.content());
    }

    /**
     * Ghi ZIP thẳng vào response (đồng bộ, không dùng StreamingResponseBody để khỏi bị cắt bởi timeout async
     * khi tải nhiều file qua LAN chậm). File đã mất trên đĩa thì bỏ qua và ghi log.
     */
    @GetMapping("/assessments/{assessmentId}/submissions.zip")
    public void submissions(@PathVariable String courseId, @PathVariable String assessmentId, AuthPrincipal me,
            HttpServletResponse response) throws IOException {
        ReportService.Archive archive = reports.submissionArchive(courseId, assessmentId, me);
        response.setContentType("application/zip");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, attachment(archive.fileName()));
        response.setHeader(HttpHeaders.CACHE_CONTROL, "private, no-store");
        // ZipOutputStream mặc định UTF-8 và bật cờ EFS: tên tiếng Việt hiển thị đúng trong Windows Explorer
        try (ZipOutputStream zip = new ZipOutputStream(response.getOutputStream())) {
            for (ReportService.ZipItem item : archive.items()) {
                if (!Files.isRegularFile(item.path())) {
                    log.warn("Submission file missing on disk, skipped: {}", item.name());
                    continue;
                }
                zip.putNextEntry(new ZipEntry(item.name()));
                Files.copy(item.path(), zip);
                zip.closeEntry();
            }
        }
    }

    private static String attachment(String fileName) {
        return ContentDisposition.attachment().filename(fileName, StandardCharsets.UTF_8).build().toString();
    }
}
