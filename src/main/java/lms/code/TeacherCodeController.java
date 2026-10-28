package lms.code;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import lms.code.CodeViews.ArchiveCheck;
import lms.code.CodeViews.AttemptView;
import lms.code.CodeViews.CodeRequest;
import lms.code.CodeViews.CodeResults;
import lms.code.CodeViews.CreateCodeRequest;
import lms.code.CodeViews.StudentCode;
import lms.code.CodeViews.TeacherCode;
import lms.common.ApiResponse;
import lms.course.CourseViews.AssessmentView;
import lms.report.ReportService;

/**
 * API giảng viên: tạo bài lập trình nhiều câu (bộ test soạn tay và/hoặc file ZIP), sửa câu và bộ test, tải bộ test,
 * chấm lại, bảng kết quả sinh viên × câu, xem các lần nộp của sinh viên.
 */
@RestController
@RequestMapping("/api/v1/teacher/courses/{courseId}")
@RequirePermission(Permissions.COURSES_MANAGE)
public class TeacherCodeController {

    private final CodeService service;

    public TeacherCodeController(CodeService service) {
        this.service = service;
    }

    @PostMapping(path = "/code-assessments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<AssessmentView> create(@PathVariable String courseId,
            @Valid @RequestBody CreateCodeRequest body, AuthPrincipal me) {
        return ApiResponse.ok(service.create(courseId, body, Map.of(), me));
    }

    /**
     * Tạo bài kèm file ZIP bộ test: part `data` (JSON như trên) + mỗi file ZIP một part, tên part là
     * ProblemRequest.archive của câu dùng file đó.
     */
    @PostMapping(path = "/code-assessments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<AssessmentView> createWithArchives(@PathVariable String courseId,
            @Valid @RequestPart("data") CreateCodeRequest body, @RequestParam Map<String, MultipartFile> files,
            AuthPrincipal me) {
        return ApiResponse.ok(service.create(courseId, body, files, me));
    }

    /** Đọc thử file ZIP bộ test (part `tests`): số test, tên, lỗi; không lưu gì. */
    @PostMapping(path = "/code-test-archive", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ArchiveCheck> checkArchive(@PathVariable String courseId,
            @RequestPart("tests") MultipartFile tests, AuthPrincipal me) {
        return ApiResponse.ok(service.checkArchive(courseId, tests, me));
    }

    @GetMapping("/assessments/{assessmentId}/code")
    public ApiResponse<TeacherCode> code(@PathVariable String courseId, @PathVariable String assessmentId,
            AuthPrincipal me) {
        return ApiResponse.ok(service.teacherCode(courseId, assessmentId, me));
    }

    @PutMapping(path = "/assessments/{assessmentId}/code", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<TeacherCode> update(@PathVariable String courseId, @PathVariable String assessmentId,
            @Valid @RequestBody CodeRequest body, AuthPrincipal me) {
        return ApiResponse.ok(service.updateCode(courseId, assessmentId, body, Map.of(), me));
    }

    /** Sửa kèm file ZIP: part `data` (JSON như trên) + các part ZIP như khi tạo bài. */
    @PutMapping(path = "/assessments/{assessmentId}/code", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<TeacherCode> updateWithArchives(@PathVariable String courseId,
            @PathVariable String assessmentId, @Valid @RequestPart("data") CodeRequest body,
            @RequestParam Map<String, MultipartFile> files, AuthPrincipal me) {
        return ApiResponse.ok(service.updateCode(courseId, assessmentId, body, files, me));
    }

    @PostMapping("/assessments/{assessmentId}/code/rejudge")
    public ApiResponse<TeacherCode> rejudge(@PathVariable String courseId, @PathVariable String assessmentId,
            AuthPrincipal me) {
        return ApiResponse.ok(service.rejudge(courseId, assessmentId, me));
    }

    @GetMapping("/assessments/{assessmentId}/code/results")
    public ApiResponse<CodeResults> results(@PathVariable String courseId, @PathVariable String assessmentId,
            AuthPrincipal me) {
        return ApiResponse.ok(service.results(courseId, assessmentId, me));
    }

    @GetMapping("/assessments/{assessmentId}/code/results.csv")
    public ResponseEntity<byte[]> resultsCsv(@PathVariable String courseId, @PathVariable String assessmentId,
            AuthPrincipal me) {
        ReportService.Csv csv = service.resultsCsv(courseId, assessmentId, me);
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(csv.fileName(), StandardCharsets.UTF_8).build()
                                .toString())
                .header(HttpHeaders.CACHE_CONTROL, "private, no-store")
                .body(csv.content());
    }

    /**
     * Tải bộ test một câu dạng ZIP (01.in/01.out, ... theo thứ tự test; nạp lại được). Ghi thẳng vào response,
     * đọc từng test một để không giữ cả bộ test trong bộ nhớ.
     */
    @GetMapping("/assessments/{assessmentId}/code/problems/{problemId}/tests.zip")
    public void downloadTests(@PathVariable String courseId, @PathVariable String assessmentId,
            @PathVariable String problemId, AuthPrincipal me, HttpServletResponse response) throws IOException {
        CodeService.TestExport export = service.exportTests(courseId, assessmentId, problemId, me);
        response.setContentType("application/zip");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment().filename(export.fileName(), StandardCharsets.UTF_8).build().toString());
        response.setHeader(HttpHeaders.CACHE_CONTROL, "private, no-store");
        int digits = Math.max(2, String.valueOf(export.tests().size()).length());
        try (ZipOutputStream zip = new ZipOutputStream(response.getOutputStream())) {
            for (int i = 0; i < export.tests().size(); i++) {
                CodeJudge.Content c = service.testContent(export.tests().get(i).id());
                if (c == null) {
                    continue;
                }
                String name = String.format("%0" + digits + "d", i + 1);
                zip.putNextEntry(new ZipEntry(name + ".in"));
                zip.write(c.input().getBytes(StandardCharsets.UTF_8));
                zip.closeEntry();
                zip.putNextEntry(new ZipEntry(name + ".out"));
                zip.write(c.expected().getBytes(StandardCharsets.UTF_8));
                zip.closeEntry();
            }
        }
    }

    @GetMapping("/assessments/{assessmentId}/code/attempts/{attemptId}")
    public ApiResponse<AttemptView> attempt(@PathVariable String courseId, @PathVariable String assessmentId,
            @PathVariable String attemptId, AuthPrincipal me) {
        return ApiResponse.ok(service.teacherAttempt(courseId, assessmentId, attemptId, me));
    }

    @GetMapping("/assessments/{assessmentId}/submissions/{studentId}/code")
    public ApiResponse<StudentCode> studentCode(@PathVariable String courseId, @PathVariable String assessmentId,
            @PathVariable String studentId, AuthPrincipal me) {
        return ApiResponse.ok(service.studentCode(courseId, assessmentId, studentId, me));
    }
}
