package lms.mixed;

import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
import lms.course.CourseViews.AssessmentView;
import lms.mixed.MixedViews.CreateMixedRequest;

/**
 * API giảng viên tạo bài tổng hợp. Xem, sửa từng phần dùng API sẵn có: /assessments/{id}/quiz (đề trắc nghiệm),
 * /assessments/{id}/code (câu lập trình, bộ test, kết quả), /assessments/{id}/submissions (bài nộp, chấm phần nộp
 * file).
 */
@RestController
@RequestMapping("/api/v1/teacher/courses/{courseId}/mixed-assessments")
@RequirePermission(Permissions.COURSES_MANAGE)
public class TeacherMixedController {

    private final MixedService service;

    public TeacherMixedController(MixedService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<AssessmentView> create(@PathVariable String courseId,
            @Valid @RequestBody CreateMixedRequest body, AuthPrincipal me) {
        return ApiResponse.ok(service.create(courseId, body, Map.of(), me));
    }

    /** data: CreateMixedRequest (JSON); "paper": đề PDF phần nộp file; part khác: file ZIP bộ test. */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<AssessmentView> createMultipart(@PathVariable String courseId,
            @Valid @RequestPart("data") CreateMixedRequest body, @RequestParam Map<String, MultipartFile> parts,
            AuthPrincipal me) {
        return ApiResponse.ok(service.create(courseId, body, parts, me));
    }
}
