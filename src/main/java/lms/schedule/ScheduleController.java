package lms.schedule;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lms.auth.AuthPrincipal;
import lms.auth.Permissions;
import lms.auth.RequirePermission;
import lms.common.ApiResponse;
import lms.schedule.ScheduleViews.AttendanceLink;
import lms.schedule.ScheduleViews.ScheduleFeed;

/** Lịch của người đang đăng nhập (sinh viên, giảng viên, admin) và mở điểm danh từ buổi học. */
@RestController
public class ScheduleController {

    private final ScheduleService service;

    public ScheduleController(ScheduleService service) {
        this.service = service;
    }

    @GetMapping("/api/v1/schedule")
    @RequirePermission(Permissions.COURSES_VIEW)
    public ApiResponse<ScheduleFeed> feed(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String course, @RequestParam(required = false) String room,
            AuthPrincipal me) {
        return ApiResponse.ok(service.feed(me, from, to, course, room));
    }

    /** Giờ tiết 1 và số tiết mỗi ngày, cho form xếp lịch. */
    @GetMapping("/api/v1/schedule/periods")
    @RequirePermission(Permissions.COURSES_VIEW)
    public ApiResponse<ScheduleRules> periods() {
        return ApiResponse.ok(service.rules());
    }

    @PostMapping("/api/v1/teacher/courses/{courseId}/meetings/{meetingId}/attendance")
    @RequirePermission(Permissions.COURSES_MANAGE)
    public ApiResponse<AttendanceLink> openAttendance(@PathVariable String courseId, @PathVariable String meetingId,
            AuthPrincipal me) {
        return ApiResponse.ok(service.openAttendance(courseId, meetingId, me));
    }
}
