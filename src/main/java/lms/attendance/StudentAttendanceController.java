package lms.attendance;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.attendance.AttendanceViews.CheckInRequest;
import lms.attendance.AttendanceViews.CheckInResult;
import lms.attendance.AttendanceViews.MyAttendance;
import lms.auth.AuthPrincipal;
import lms.auth.Permissions;
import lms.auth.RequirePermission;
import lms.common.ApiResponse;
import lms.common.RequestInfo;

/** API sinh viên: điểm danh bằng mã QR quét được, xem lịch sử điểm danh của mình. */
@RestController
@RequestMapping("/api/v1/student")
@RequirePermission(Permissions.COURSES_VIEW)
public class StudentAttendanceController {

    private final AttendanceService service;

    public StudentAttendanceController(AttendanceService service) {
        this.service = service;
    }

    /** token: phần ?t=... của link trong mã QR. */
    @PostMapping("/attendance/check-in")
    public ApiResponse<CheckInResult> checkIn(@Valid @RequestBody CheckInRequest body, AuthPrincipal me,
            HttpServletRequest req) {
        return ApiResponse.ok(service.checkIn(body.token(), me, RequestInfo.clientIp(req)));
    }

    @GetMapping("/courses/{courseId}/attendance")
    public ApiResponse<List<MyAttendance>> mine(@PathVariable String courseId, AuthPrincipal me) {
        return ApiResponse.ok(service.mine(courseId, me));
    }
}
