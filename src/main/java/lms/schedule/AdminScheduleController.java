package lms.schedule;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lms.auth.AuthPrincipal;
import lms.auth.Permissions;
import lms.auth.RequirePermission;
import lms.common.ApiResponse;
import lms.schedule.ScheduleViews.CancelRequest;
import lms.schedule.ScheduleViews.MeetingRequest;
import lms.schedule.ScheduleViews.MeetingView;
import lms.schedule.ScheduleViews.RoomImportReport;
import lms.schedule.ScheduleViews.RoomImportRequest;
import lms.schedule.ScheduleViews.RoomRequest;
import lms.schedule.ScheduleViews.RoomView;
import lms.schedule.ScheduleViews.ScheduleImportReport;
import lms.schedule.ScheduleViews.ScheduleImportRequest;
import lms.schedule.ScheduleViews.ScheduleRequest;
import lms.schedule.ScheduleViews.ScheduleResult;
import lms.schedule.ScheduleViews.ScheduleView;

/** Trang Lịch học của admin: danh mục phòng học, quy tắc lặp của từng khóa học, thêm/dời/hủy từng buổi. */
@RestController
@RequestMapping("/api/v1/admin")
@RequirePermission(Permissions.SCHEDULES_MANAGE)
public class AdminScheduleController {

    private final ScheduleService service;
    private final ScheduleImportService importService;

    public AdminScheduleController(ScheduleService service, ScheduleImportService importService) {
        this.service = service;
        this.importService = importService;
    }

    // ================================================================ phòng học

    @GetMapping("/rooms")
    public ApiResponse<List<RoomView>> rooms() {
        return ApiResponse.ok(service.listRooms());
    }

    @PostMapping("/rooms")
    public ApiResponse<RoomView> createRoom(@Valid @RequestBody RoomRequest body, AuthPrincipal me) {
        return ApiResponse.ok(service.createRoom(body, me));
    }

    @PostMapping("/rooms/import")
    public ApiResponse<RoomImportReport> importRooms(@Valid @RequestBody RoomImportRequest body, AuthPrincipal me) {
        return ApiResponse.ok(service.importRooms(body.content(), me));
    }

    @PutMapping("/rooms/{roomId}")
    public ApiResponse<RoomView> updateRoom(@PathVariable String roomId, @Valid @RequestBody RoomRequest body,
            AuthPrincipal me) {
        return ApiResponse.ok(service.updateRoom(roomId, body, me));
    }

    @DeleteMapping("/rooms/{roomId}")
    public ApiResponse<Void> deleteRoom(@PathVariable String roomId, AuthPrincipal me) {
        service.deleteRoom(roomId, me);
        return ApiResponse.ok();
    }

    // ================================================================ quy tắc lặp

    @GetMapping("/schedules")
    public ApiResponse<List<ScheduleView>> schedules(@RequestParam(required = false) String course,
            @RequestParam(required = false) String room) {
        return ApiResponse.ok(service.listSchedules(course, room));
    }

    @PostMapping("/schedules")
    public ApiResponse<ScheduleResult> createSchedule(@Valid @RequestBody ScheduleRequest body, AuthPrincipal me) {
        return ApiResponse.ok(service.createSchedule(body, me));
    }

    /** Nhập thời khóa biểu hàng loạt; dryRun = chỉ kiểm tra. Có dòng lỗi thì không lưu dòng nào. */
    @PostMapping("/schedules/import")
    public ApiResponse<ScheduleImportReport> importSchedules(@Valid @RequestBody ScheduleImportRequest body,
            AuthPrincipal me) {
        return ApiResponse.ok(
                importService.importSchedules(body.content(), body.dryRun(), body.notifyStudents(), me));
    }

    @PutMapping("/schedules/{scheduleId}")
    public ApiResponse<ScheduleResult> updateSchedule(@PathVariable String scheduleId,
            @Valid @RequestBody ScheduleRequest body, AuthPrincipal me) {
        return ApiResponse.ok(service.updateSchedule(scheduleId, body, me));
    }

    @DeleteMapping("/schedules/{scheduleId}")
    public ApiResponse<Void> deleteSchedule(@PathVariable String scheduleId, AuthPrincipal me) {
        service.deleteSchedule(scheduleId, me);
        return ApiResponse.ok();
    }

    // ================================================================ từng buổi

    @PostMapping("/meetings")
    public ApiResponse<MeetingView> createMeeting(@Valid @RequestBody MeetingRequest body, AuthPrincipal me) {
        return ApiResponse.ok(service.createMeeting(body, me));
    }

    @PatchMapping("/meetings/{meetingId}")
    public ApiResponse<MeetingView> moveMeeting(@PathVariable String meetingId,
            @Valid @RequestBody MeetingRequest body, AuthPrincipal me) {
        return ApiResponse.ok(service.moveMeeting(meetingId, body, me));
    }

    @PostMapping("/meetings/{meetingId}/cancel")
    public ApiResponse<MeetingView> cancelMeeting(@PathVariable String meetingId,
            @Valid @RequestBody CancelRequest body, AuthPrincipal me) {
        return ApiResponse.ok(service.cancelMeeting(meetingId, body, me));
    }

    @PostMapping("/meetings/{meetingId}/restore")
    public ApiResponse<MeetingView> restoreMeeting(@PathVariable String meetingId,
            @Valid @RequestBody CancelRequest body, AuthPrincipal me) {
        return ApiResponse.ok(service.restoreMeeting(meetingId, body, me));
    }

    @DeleteMapping("/meetings/{meetingId}")
    public ApiResponse<Void> deleteMeeting(@PathVariable String meetingId, AuthPrincipal me) {
        service.deleteMeeting(meetingId, me);
        return ApiResponse.ok();
    }
}
