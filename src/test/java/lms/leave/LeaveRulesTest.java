package lms.leave;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import lms.common.AppException;
import lms.common.ErrorCode;
import lms.config.AppProperties;
import lms.course.Course;
import lms.course.UploadChecker;
import lms.schedule.ClassMeeting;
import lms.schedule.ScheduleRules;

class LeaveRulesTest {

    private final ScheduleRules rules = new ScheduleRules(7, 14);

    @Test
    void deadlineIsClassStartMinusNoticeHours() {
        Course c = new Course("INT1340", "Nhập môn", null);
        // Thứ 3 27/10/2026, tiết 3 = 09:00 giờ Việt Nam = 02:00 UTC
        ClassMeeting m = new ClassMeeting(1L, null, 1L, LocalDate.of(2026, 10, 27), 3, 2, 0);

        assertEquals(Instant.parse("2026-10-26T02:00:00Z"), LeaveService.deadline(rules, c, m)); // mặc định 24 giờ
        c.setLeaveNoticeHours(0);
        assertEquals(Instant.parse("2026-10-27T02:00:00Z"), LeaveService.deadline(rules, c, m));
        c.setLeaveNoticeHours(72);
        assertEquals(Instant.parse("2026-10-24T02:00:00Z"), LeaveService.deadline(rules, c, m));
    }

    private static UploadChecker checker() {
        AppProperties props = new AppProperties(null, null, null, null, null,
                new AppProperties.Storage("data/files", 1024), null, null);
        return new UploadChecker(props);
    }

    @Test
    void evidenceAcceptsImagesAndPdfByContentNotJustExtension() {
        UploadChecker checker = checker();
        byte[] jpeg = { (byte) 0xff, (byte) 0xd8, (byte) 0xff, (byte) 0xe0, 0 };
        byte[] png = { (byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a, 0 };

        assertEquals("image/jpeg",
                checker.check(new MockMultipartFile("files", "giay-kham.JPG", null, jpeg), UploadChecker.EVIDENCE)
                        .contentType());
        assertEquals("image/png",
                checker.check(new MockMultipartFile("files", "anh.png", null, png), UploadChecker.EVIDENCE)
                        .contentType());
        // Đổi đuôi .exe thành .png không qua được kiểm tra nội dung
        AppException fake = assertThrows(AppException.class, () -> checker
                .check(new MockMultipartFile("files", "virus.png", null, "MZ...".getBytes()), UploadChecker.EVIDENCE));
        assertEquals(ErrorCode.UNSUPPORTED_FILE_TYPE, fake.code());
        // Ảnh không nhận ở bài nộp
        assertThrows(AppException.class, () -> checker
                .check(new MockMultipartFile("files", "anh.png", null, png), UploadChecker.SUBMISSION));
    }
}
