package lms.submission;

import java.nio.charset.StandardCharsets;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lms.auth.AuthPrincipal;
import lms.auth.Permissions;
import lms.auth.RequirePermission;
import lms.common.ApiResponse;
import lms.submission.SubmissionViews.GradeRequest;
import lms.submission.SubmissionViews.StudentSubmissionView;
import lms.submission.SubmissionViews.TeacherSubmissionRow;

@RestController
@RequestMapping("/api/v1")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    // ---------------------------------------------------------------- Sinh viên

    @PostMapping(path = "/student/assessments/{assessmentId}/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequirePermission(Permissions.SUBMISSIONS_CREATE)
    public ApiResponse<StudentSubmissionView> submit(
            @PathVariable String assessmentId,
            @RequestPart("file") MultipartFile file,
            AuthPrincipal me) {
        return ApiResponse.ok(submissionService.submit(assessmentId, file, me));
    }

    @GetMapping("/student/assessments/{assessmentId}/submission")
    @RequirePermission(Permissions.COURSES_VIEW)
    public ApiResponse<StudentSubmissionView> getMySubmission(
            @PathVariable String assessmentId,
            AuthPrincipal me) {
        return ApiResponse.ok(submissionService.getMySubmission(assessmentId, me));
    }

    // ---------------------------------------------------------------- Giảng viên

    @GetMapping("/teacher/assessments/{assessmentId}/submissions")
    @RequirePermission(Permissions.SUBMISSIONS_MANAGE)
    public ApiResponse<List<TeacherSubmissionRow>> listSubmissions(
            @PathVariable String assessmentId,
            AuthPrincipal me) {
        return ApiResponse.ok(submissionService.listSubmissions(assessmentId, me));
    }

    @PostMapping("/teacher/submissions/{submissionId}/grade")
    @RequirePermission(Permissions.SUBMISSIONS_MANAGE)
    public ApiResponse<TeacherSubmissionRow> grade(
            @PathVariable String submissionId,
            @Valid @RequestBody GradeRequest body,
            AuthPrincipal me) {
        return ApiResponse.ok(submissionService.grade(submissionId, body, me));
    }

    // ---------------------------------------------------------------- Tải file nộp chung

    @GetMapping("/submissions/{submissionId}/download")
    public ResponseEntity<Resource> download(
            @PathVariable String submissionId,
            AuthPrincipal me) {
        SubmissionService.Download d = submissionService.download(submissionId, me);
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(d.fileName(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(d.contentType()))
                .contentLength(d.size())
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .header("X-Content-Type-Options", "nosniff")
                .header(HttpHeaders.CACHE_CONTROL, "private, no-store")
                .body(new FileSystemResource(d.path()));
    }
}
