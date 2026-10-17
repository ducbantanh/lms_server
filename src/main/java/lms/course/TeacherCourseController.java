package lms.course;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import lms.common.RequestInfo;
import lms.course.CourseViews.AssessmentRequest;
import lms.course.CourseViews.AssessmentView;
import lms.course.CourseViews.CourseDetail;
import lms.course.CourseViews.CourseSummary;
import lms.course.CourseViews.CourseView;
import lms.course.CourseViews.CreateCourseRequest;
import lms.course.CourseViews.EnrollKey;
import lms.course.CourseViews.EnrollReport;
import lms.course.CourseViews.FileView;
import lms.course.CourseViews.HiddenRequest;
import lms.course.CourseViews.PasteRequest;
import lms.course.CourseViews.PickRequest;
import lms.course.CourseViews.SectionRequest;
import lms.course.CourseViews.SectionView;
import lms.course.CourseViews.StudentRow;
import lms.course.CourseViews.UserPick;

/** API giảng viên: khóa học mình phụ trách (admin: mọi khóa học), nội dung, ghi danh, bài kiểm tra. */
@RestController
@RequestMapping("/api/v1/teacher")
@RequirePermission(Permissions.COURSES_MANAGE)
public class TeacherCourseController {

    private final CourseService courses;
    private final CourseContentService content;

    public TeacherCourseController(CourseService courses, CourseContentService content) {
        this.courses = courses;
        this.content = content;
    }

    @GetMapping("/courses")
    public ApiResponse<List<CourseSummary>> list(AuthPrincipal me) {
        return ApiResponse.ok(courses.listManaged(me));
    }

    @PostMapping("/courses")
    public ApiResponse<CourseView> create(@Valid @RequestBody CreateCourseRequest body, AuthPrincipal me,
            HttpServletRequest req) {
        return ApiResponse.ok(courses.create(body, me, RequestInfo.clientIp(req)));
    }

    @GetMapping("/courses/{id}")
    public ApiResponse<CourseDetail> detail(@PathVariable String id, AuthPrincipal me) {
        return ApiResponse.ok(courses.managedDetail(id, me));
    }

    @DeleteMapping("/courses/{id}")
    public ApiResponse<Void> delete(@PathVariable String id, AuthPrincipal me, HttpServletRequest req) {
        courses.delete(id, me, RequestInfo.clientIp(req));
        return ApiResponse.ok();
    }

    // ---------------------------------------------------------------- mục

    @PostMapping("/courses/{id}/sections")
    public ApiResponse<SectionView> addSection(@PathVariable String id, @Valid @RequestBody SectionRequest body,
            AuthPrincipal me) {
        return ApiResponse.ok(content.addSection(id, body.title(), me));
    }

    @DeleteMapping("/courses/{id}/sections/{sectionId}")
    public ApiResponse<Void> deleteSection(@PathVariable String id, @PathVariable String sectionId,
            AuthPrincipal me) {
        content.deleteSection(id, sectionId, me);
        return ApiResponse.ok();
    }

    // ---------------------------------------------------------------- tài liệu

    @PostMapping(path = "/courses/{id}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FileView> upload(@PathVariable String id, @RequestPart("file") MultipartFile file,
            @RequestParam(required = false) String sectionId,
            @RequestParam(defaultValue = "false") boolean hidden, AuthPrincipal me) {
        return ApiResponse.ok(content.upload(id, file, sectionId, hidden, me));
    }

    @PatchMapping("/courses/{id}/files/{fileId}")
    public ApiResponse<Void> setHidden(@PathVariable String id, @PathVariable String fileId,
            @RequestBody HiddenRequest body, AuthPrincipal me) {
        content.setHidden(id, fileId, body.hidden(), me);
        return ApiResponse.ok();
    }

    @DeleteMapping("/courses/{id}/files/{fileId}")
    public ApiResponse<Void> deleteFile(@PathVariable String id, @PathVariable String fileId, AuthPrincipal me) {
        content.deleteFile(id, fileId, me);
        return ApiResponse.ok();
    }

    // ---------------------------------------------------------------- sinh viên

    @GetMapping("/students")
    public ApiResponse<List<UserPick>> searchStudents(@RequestParam(required = false) String q) {
        return ApiResponse.ok(courses.searchStudents(q));
    }

    @GetMapping("/courses/{id}/students")
    public ApiResponse<List<StudentRow>> students(@PathVariable String id, AuthPrincipal me) {
        return ApiResponse.ok(courses.students(id, me));
    }

    @PostMapping("/courses/{id}/students/paste")
    public ApiResponse<EnrollReport> enrollPaste(@PathVariable String id, @Valid @RequestBody PasteRequest body,
            AuthPrincipal me) {
        return ApiResponse.ok(courses.enrollByUsernames(id, body.content(), me));
    }

    @PostMapping("/courses/{id}/students")
    public ApiResponse<EnrollReport> enrollPick(@PathVariable String id, @Valid @RequestBody PickRequest body,
            AuthPrincipal me) {
        return ApiResponse.ok(courses.enrollByIds(id, body.userIds(), me));
    }

    @DeleteMapping("/courses/{id}/students/{userId}")
    public ApiResponse<Void> unenroll(@PathVariable String id, @PathVariable String userId, AuthPrincipal me) {
        courses.unenroll(id, userId, me);
        return ApiResponse.ok();
    }

    @PostMapping("/courses/{id}/enroll-key")
    public ApiResponse<EnrollKey> newKey(@PathVariable String id, AuthPrincipal me) {
        return ApiResponse.ok(new EnrollKey(courses.newEnrollKey(id, me)));
    }

    @DeleteMapping("/courses/{id}/enroll-key")
    public ApiResponse<Void> disableKey(@PathVariable String id, AuthPrincipal me) {
        courses.disableEnrollKey(id, me);
        return ApiResponse.ok();
    }

    // ---------------------------------------------------------------- bài kiểm tra

    @PostMapping("/courses/{id}/assessments")
    public ApiResponse<AssessmentView> createAssessment(@PathVariable String id,
            @Valid @RequestBody AssessmentRequest body, AuthPrincipal me) {
        return ApiResponse.ok(content.createAssessment(id, body, me));
    }

    @DeleteMapping("/courses/{id}/assessments/{assessmentId}")
    public ApiResponse<Void> deleteAssessment(@PathVariable String id, @PathVariable String assessmentId,
            AuthPrincipal me) {
        content.deleteAssessment(id, assessmentId, me);
        return ApiResponse.ok();
    }
}
