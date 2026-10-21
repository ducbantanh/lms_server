package lms.submission;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lms.auth.AuthPrincipal;
import lms.auth.Permissions;
import lms.auth.RequirePermission;
import lms.common.ApiResponse;
import lms.common.RequestInfo;
import lms.submission.SubmissionViews.MyResult;
import lms.submission.SubmissionViews.StudentAssessment;
import lms.submission.SubmissionViews.SubmissionView;

/** API sinh viên làm bài: xem bài, tải/xóa file bài làm, nộp bài. */
@RestController
@RequestMapping("/api/v1/student/assessments")
@RequirePermission(Permissions.SUBMISSIONS_CREATE)
public class StudentSubmissionController {

    private final SubmissionService service;

    public StudentSubmissionController(SubmissionService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ApiResponse<StudentAssessment> view(@PathVariable String id, AuthPrincipal me) {
        return ApiResponse.ok(service.view(id, me));
    }

    /** GET /student/assessments?course=...: trạng thái và điểm của mình ở mọi bài của khóa học. */
    @GetMapping
    public ApiResponse<List<MyResult>> myResults(@RequestParam("course") String courseId, AuthPrincipal me) {
        return ApiResponse.ok(service.myResults(courseId, me));
    }

    @PostMapping(path = "/{id}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<SubmissionView> upload(@PathVariable String id, @RequestPart("file") MultipartFile file,
            AuthPrincipal me) {
        return ApiResponse.ok(service.upload(id, file, me));
    }

    @DeleteMapping("/{id}/files/{fileId}")
    public ApiResponse<SubmissionView> deleteFile(@PathVariable String id, @PathVariable String fileId,
            AuthPrincipal me) {
        return ApiResponse.ok(service.deleteFile(id, fileId, me));
    }

    @PostMapping("/{id}/submit")
    public ApiResponse<SubmissionView> submit(@PathVariable String id, AuthPrincipal me, HttpServletRequest req) {
        return ApiResponse.ok(service.submit(id, me, RequestInfo.clientIp(req)));
    }
}
