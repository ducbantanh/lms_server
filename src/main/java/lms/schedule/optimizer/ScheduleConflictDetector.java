package lms.schedule.optimizer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lms.schedule.ClassMeeting;

/**
 * Thuật toán phát hiện xung đột lịch học thông minh (Schedule Collision Detection Matrix).
 * Kiểm tra va chạm phòng học, tiết học và lịch giảng dạy.
 */
public final class ScheduleConflictDetector {

    public enum ConflictType {
        ROOM_COLLISION,
        TEACHER_COLLISION,
        COURSE_SELF_COLLISION
    }

    public record ConflictEntry(
            ConflictType type,
            LocalDate date,
            int startPeriod,
            int periods,
            Long entityId,
            String description) {
    }

    private ScheduleConflictDetector() {
    }

    /**
     * Kiểm tra hai khoảng tiết học có bị giao nhau (overlap) trong cùng một ngày hay không.
     */
    public static boolean periodsOverlap(int start1, int count1, int start2, int count2) {
        int end1 = start1 + count1 - 1;
        int end2 = start2 + count2 - 1;
        return start1 <= end2 && end1 >= start2;
    }

    /**
     * Quét và đối chiếu xung đột phòng học giữa một buổi học mới và danh sách các buổi đã xếp.
     */
    public static List<ConflictEntry> checkRoomConflicts(
            LocalDate date,
            int startPeriod,
            int periods,
            Long roomId,
            List<ClassMeeting> existingMeetings) {

        List<ConflictEntry> conflicts = new ArrayList<>();
        if (date == null || roomId == null || existingMeetings == null) {
            return conflicts;
        }

        for (ClassMeeting m : existingMeetings) {
            if (m.isCancelled()) {
                continue;
            }
            if (date.equals(m.getDate()) && Objects.equals(roomId, m.getRoomId())) {
                if (periodsOverlap(startPeriod, periods, m.getStartPeriod(), m.getPeriods())) {
                    conflicts.add(new ConflictEntry(
                            ConflictType.ROOM_COLLISION,
                            date,
                            m.getStartPeriod(),
                            m.getPeriods(),
                            roomId,
                            "Trùng phòng học ID " + roomId + " vào ngày " + date + " (tiết " + m.getStartPeriod() + "-" + (m.getStartPeriod() + m.getPeriods() - 1) + ")"
                    ));
                }
            }
        }
        return conflicts;
    }

    /**
     * Tính toán tỷ lệ sử dụng phòng học (Room Utilization Rate) trong tuần (tổng số tiết sử dụng / tổng số tiết tối đa).
     * Giả định 1 tuần học từ Thứ 2 đến Thứ 7 (6 ngày), mỗi ngày có 12 tiết học (tối đa 72 tiết/tuần).
     */
    public static double calculateWeeklyUtilization(List<ClassMeeting> weekMeetingsForRoom) {
        if (weekMeetingsForRoom == null || weekMeetingsForRoom.isEmpty()) {
            return 0.0;
        }

        int totalUsedPeriods = 0;
        for (ClassMeeting m : weekMeetingsForRoom) {
            if (!m.isCancelled()) {
                totalUsedPeriods += m.getPeriods();
            }
        }

        // 6 ngày x 12 tiết = 72 tiết tối đa
        double maxCapacity = 72.0;
        double rate = (totalUsedPeriods / maxCapacity) * 100.0;
        return Math.round(Math.min(100.0, rate) * 10.0) / 10.0;
    }
}
