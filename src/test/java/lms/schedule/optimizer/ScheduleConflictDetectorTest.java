package lms.schedule.optimizer;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScheduleConflictDetectorTest {

    @Test
    @DisplayName("periodsOverlap phát hiện chính xác tiết học trùng nhau")
    void testPeriodsOverlap() {
        // Tiết 1-3 và Tiết 2-4 -> Trùng (tiết 2, 3)
        assertThat(ScheduleConflictDetector.periodsOverlap(1, 3, 2, 3)).isTrue();

        // Tiết 1-3 và Tiết 4-6 -> Không trùng
        assertThat(ScheduleConflictDetector.periodsOverlap(1, 3, 4, 3)).isFalse();

        // Tiết 4-6 và Tiết 1-3 -> Không trùng
        assertThat(ScheduleConflictDetector.periodsOverlap(4, 3, 1, 3)).isFalse();

        // Tiết 1-5 bao trọn Tiết 2-3 -> Trùng
        assertThat(ScheduleConflictDetector.periodsOverlap(1, 5, 2, 2)).isTrue();
    }
}
