package lms.proctor;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.auth.AuthPrincipal;
import lms.auth.Permissions;
import lms.auth.RequirePermission;
import lms.common.ApiResponse;
import lms.proctor.ProctorViews.AccessCode;
import lms.proctor.ProctorViews.Board;
import lms.proctor.ProctorViews.BoardRow;
import lms.proctor.ProctorViews.LockRequest;
import lms.proctor.ProctorViews.Settings;
import lms.proctor.ProctorViews.SettingsRequest;
import lms.proctor.ProctorViews.StudentLog;

/** Giám thị: bảng theo dõi phòng thi, mã phòng thi, nhật ký từng sinh viên, khóa / mở khóa / cho đổi máy. */
@RestController
@RequestMapping("/api/v1/teacher/courses/{courseId}/assessments/{assessmentId}/proctor")
@RequirePermission(Permissions.COURSES_MANAGE)
public class TeacherProctorController {

    private final ProctorService service;

    public TeacherProctorController(ProctorService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<Board> board(@PathVariable String courseId, @PathVariable String assessmentId,
            AuthPrincipal me) {
        return ApiResponse.ok(service.board(courseId, assessmentId, me));
    }

    @GetMapping("/access-code")
    public ApiResponse<AccessCode> accessCode(@PathVariable String courseId, @PathVariable String assessmentId,
            AuthPrincipal me) {
        return ApiResponse.ok(service.currentCode(courseId, assessmentId, me));
    }

    @PutMapping("/settings")
    public ApiResponse<Settings> settings(@PathVariable String courseId, @PathVariable String assessmentId,
            @Valid @RequestBody SettingsRequest body, AuthPrincipal me) {
        return ApiResponse.ok(service.updateSettings(courseId, assessmentId, body, me));
    }

    @GetMapping("/students/{studentId}")
    public ApiResponse<StudentLog> student(@PathVariable String courseId, @PathVariable String assessmentId,
            @PathVariable String studentId, AuthPrincipal me) {
        return ApiResponse.ok(service.studentLog(courseId, assessmentId, studentId, me));
    }

    @PostMapping("/students/{studentId}/lock")
    public ApiResponse<BoardRow> lock(@PathVariable String courseId, @PathVariable String assessmentId,
            @PathVariable String studentId, @Valid @RequestBody LockRequest body, AuthPrincipal me) {
        return ApiResponse.ok(service.lock(courseId, assessmentId, studentId, body.reason(), me));
    }

    @PostMapping("/students/{studentId}/unlock")
    public ApiResponse<BoardRow> unlock(@PathVariable String courseId, @PathVariable String assessmentId,
            @PathVariable String studentId, @Valid @RequestBody LockRequest body, AuthPrincipal me) {
        return ApiResponse.ok(service.unlock(courseId, assessmentId, studentId, body.reason(), me));
    }

    @PostMapping("/students/{studentId}/reset-device")
    public ApiResponse<BoardRow> resetDevice(@PathVariable String courseId, @PathVariable String assessmentId,
            @PathVariable String studentId, AuthPrincipal me) {
        return ApiResponse.ok(service.resetDevice(courseId, assessmentId, studentId, me));
    }
}
