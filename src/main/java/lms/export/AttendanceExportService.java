package lms.export;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lms.attendance.AttendanceRecord;
import lms.attendance.AttendanceRecordRepository;
import lms.attendance.AttendanceSession;
import lms.attendance.AttendanceSessionRepository;
import lms.common.AppException;
import lms.common.ErrorCode;
import lms.common.util.CsvUtils;
import lms.course.Course;
import lms.course.CourseRepository;
import lms.course.EnrollmentRepository;
import lms.user.User;

/**
 * Dịch vụ xuất bảng tổng hợp điểm danh toàn khóa học ra file CSV chuẩn UTF-8 BOM.
 */
@Service
public class AttendanceExportService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceSessionRepository sessionRepository;
    private final AttendanceRecordRepository recordRepository;

    public AttendanceExportService(
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            AttendanceSessionRepository sessionRepository,
            AttendanceRecordRepository recordRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.sessionRepository = sessionRepository;
        this.recordRepository = recordRepository;
    }

    /**
     * Xuất danh sách điểm danh chi tiết từng buổi và tỷ lệ tham gia của sinh viên.
     */
    @Transactional(readOnly = true)
    public byte[] exportAttendanceCsv(String coursePublicId) {
        Course course = courseRepository.findByPublicId(coursePublicId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy khóa học"));

        List<Object[]> enrollmentUsers = enrollmentRepository.findWithUsers(course.getId());
        List<User> students = enrollmentUsers.stream()
                .map(row -> (User) row[1])
                .toList();

        List<AttendanceSession> sessions = sessionRepository.findByCourseIdOrderByCreatedAtAscIdAsc(course.getId());
        List<Long> sessionIds = sessions.stream().map(AttendanceSession::getId).toList();

        List<AttendanceRecord> records = sessionIds.isEmpty() ?
                Collections.emptyList() :
                recordRepository.findBySessionIdIn(sessionIds);

        // Map (sessionId + "_" + userId) -> status
        Map<String, Integer> recordMap = new HashMap<>();
        for (AttendanceRecord r : records) {
            recordMap.put(r.getSessionId() + "_" + r.getUserId(), r.getStatus());
        }

        StringBuilder csv = new StringBuilder();
        csv.append(CsvUtils.UTF8_BOM);

        csv.append(CsvUtils.buildLine(List.of("BẢNG TỔNG HỢP ĐIỂM DANH:", course.getTitle(), "MÃ:", course.getCode()))).append("\n");
        csv.append("\n");

        List<String> headers = new ArrayList<>();
        headers.add("STT");
        headers.add("Mã sinh viên");
        headers.add("Họ và tên");

        for (int i = 0; i < sessions.size(); i++) {
            headers.add("Buổi " + (i + 1) + " (" + sessions.get(i).getTitle() + ")");
        }
        headers.add("Có mặt");
        headers.add("Đi muộn");
        headers.add("Vắng có phép");
        headers.add("Vắng không phép");
        headers.add("Tỷ lệ tham dự (%)");
        csv.append(CsvUtils.buildLine(headers)).append("\n");

        int index = 1;
        int totalSessions = sessions.size();

        for (User student : students) {
            List<Object> row = new ArrayList<>();
            row.add(index++);
            row.add(student.getUsername());
            row.add(student.getFullName());

            int presentCount = 0;
            int lateCount = 0;
            int excusedCount = 0;
            int absentCount = 0;

            for (AttendanceSession s : sessions) {
                Integer status = recordMap.get(s.getId() + "_" + student.getId());
                if (status == null) {
                    row.add("Vắng");
                    absentCount++;
                } else if (status == AttendanceRecord.STATUS_PRESENT) {
                    row.add("Có mặt");
                    presentCount++;
                } else if (status == AttendanceRecord.STATUS_LATE) {
                    row.add("Đi muộn");
                    lateCount++;
                } else if (status == AttendanceRecord.STATUS_EXCUSED) {
                    row.add("Có phép");
                    excusedCount++;
                } else {
                    row.add("Vắng");
                    absentCount++;
                }
            }

            row.add(presentCount);
            row.add(lateCount);
            row.add(excusedCount);
            row.add(absentCount);

            if (totalSessions > 0) {
                // Điểm danh có mặt tính 100%, đi muộn tính 50%
                double attendedSessions = presentCount + (lateCount * 0.5);
                double rate = Math.round((attendedSessions / totalSessions * 100.0) * 10.0) / 10.0;
                row.add(rate + "%");
            } else {
                row.add("100%");
            }

            csv.append(CsvUtils.buildLine(row)).append("\n");
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }
}
