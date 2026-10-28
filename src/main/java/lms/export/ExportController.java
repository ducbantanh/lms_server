package lms.export;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.auth.Permissions;
import lms.auth.RequirePermission;

/**
 * Controller cung cấp API tải về tập tin bảng điểm và điểm danh học phần.
 */
@RestController
@RequestMapping("/api/v1/teacher/export")
public class ExportController {

    private final GradebookExportService gradebookExportService;
    private final AttendanceExportService attendanceExportService;

    public ExportController(
            GradebookExportService gradebookExportService,
            AttendanceExportService attendanceExportService) {
        this.gradebookExportService = gradebookExportService;
        this.attendanceExportService = attendanceExportService;
    }

    /**
     * Tải về bảng điểm học phần dưới dạng file CSV UTF-8.
     */
    @GetMapping("/courses/{coursePublicId}/gradebook.csv")
    @RequirePermission(Permissions.COURSES_MANAGE)
    public ResponseEntity<byte[]> downloadGradebook(@PathVariable("coursePublicId") String coursePublicId) {
        byte[] csvData = gradebookExportService.exportGradebookCsv(coursePublicId);
        String filename = "bang-diem-" + coursePublicId + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"; filename*=UTF-8''" + URLEncoder.encode(filename, StandardCharsets.UTF_8))
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvData);
    }

    /**
     * Tải về bảng tổng hợp điểm danh học phần dưới dạng file CSV UTF-8.
     */
    @GetMapping("/courses/{coursePublicId}/attendance.csv")
    @RequirePermission(Permissions.COURSES_MANAGE)
    public ResponseEntity<byte[]> downloadAttendance(@PathVariable("coursePublicId") String coursePublicId) {
        byte[] csvData = attendanceExportService.exportAttendanceCsv(coursePublicId);
        String filename = "diem-danh-" + coursePublicId + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"; filename*=UTF-8''" + URLEncoder.encode(filename, StandardCharsets.UTF_8))
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvData);
    }
}
