package lms.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import lms.course.Course;

class ScheduleImportParserTest {

    private final ScheduleImportParser parser = new ScheduleImportParser(1000);

    @Test
    void parsesSeparatorsWeekdayFormsDatesAndKinds() {
        ScheduleImportParser.Result r = parser.parse("""
                Mã học phần,Nhóm,Thứ,Tiết bắt đầu,Số tiết,Phòng,Từ ngày,Đến ngày,Hình thức
                INT1340|01|2|1|3|402-A2|07/09/2026|20/12/2026|LT
                INT1339\t\tThứ 5\t7\t2\tLab 1\t2026-09-07\t2026-12-20\tThực hành
                INT1434;2;CN;4;2;Hội trường A;7.9.2026;20.12.2026
                INT1306,,T7,10,1,"P. 101 (cũ)",1/10/2026,1/11/2026,th
                """);

        assertEquals(4, r.dataLines());
        assertEquals(List.of(), r.errors());
        assertEquals(new ScheduleImportParser.Row(2, "INT1340", "01", 1, 1, 3, "402-A2", LocalDate.of(2026, 9, 7),
                LocalDate.of(2026, 12, 20), 0), r.rows().get(0));
        ScheduleImportParser.Row thursday = r.rows().get(1);
        assertNull(thursday.group());
        assertEquals(4, thursday.weekday());
        assertEquals(1, thursday.kind());
        assertEquals(7, r.rows().get(2).weekday()); // CN
        assertEquals(0, r.rows().get(2).kind()); // bỏ trống = lý thuyết
        assertEquals(6, r.rows().get(3).weekday()); // T7
        assertEquals("P. 101 (cũ)", r.rows().get(3).room());
    }

    @Test
    void reportsEachKindOfInvalidLine() {
        ScheduleImportParser.Result r = parser.parse("""
                INT1340|01|2|1|3|402-A2|07/09/2026
                INT1340|01|1|1|3|402-A2|07/09/2026|20/12/2026
                INT1340|01|2|0|3|402-A2|07/09/2026|20/12/2026
                INT1340|01|2|1|3||07/09/2026|20/12/2026
                INT1340|01|2|1|3|402-A2|31/02/2026|20/12/2026
                INT1340|01|2|1|3|402-A2|20/12/2026|07/09/2026
                INT1340|01|2|1|3|402-A2|07/09/2026|20/12/2027
                INT1340|01|2|1|3|402-A2|07/09/2026|20/12/2026|Seminar
                """);

        assertEquals(List.of(ScheduleImportParser.INVALID_FORMAT, ScheduleImportParser.INVALID_WEEKDAY,
                ScheduleImportParser.INVALID_PERIODS, ScheduleImportParser.INVALID_ROOM,
                ScheduleImportParser.INVALID_DATE, ScheduleImportParser.INVALID_RANGE,
                ScheduleImportParser.INVALID_RANGE, ScheduleImportParser.INVALID_KIND),
                r.errors().stream().map(ScheduleImportParser.LineIssue::reason).toList());
        assertEquals("INT1340 · Nhóm 01", r.errors().get(1).label());
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8),
                r.errors().stream().map(ScheduleImportParser.LineIssue::line).toList());
    }

    @Test
    void weekdayAcceptsVietnameseForms() {
        assertEquals(1, ScheduleImportParser.parseWeekday("2"));
        assertEquals(1, ScheduleImportParser.parseWeekday("Thứ 2"));
        assertEquals(1, ScheduleImportParser.parseWeekday("thu2"));
        assertEquals(6, ScheduleImportParser.parseWeekday("T7"));
        assertEquals(7, ScheduleImportParser.parseWeekday("CN"));
        assertEquals(7, ScheduleImportParser.parseWeekday("Chủ nhật"));
        assertEquals(7, ScheduleImportParser.parseWeekday("8"));
        assertNull(ScheduleImportParser.parseWeekday("1"));
        assertNull(ScheduleImportParser.parseWeekday("Thứ hai"));
    }

    @Test
    void rejectsTooManyLines() {
        assertThrows(ScheduleImportParser.TooManyLinesException.class, () -> new ScheduleImportParser(1)
                .parse("A|1|2|1|1|P|01/01/2026|02/02/2026\nB|1|2|1|1|P|01/01/2026|02/02/2026"));
    }

    private static Course course(String code, String group) {
        Course c = new Course(code, "Môn " + code, null);
        c.setGroupName(group);
        return c;
    }

    @Test
    void groupMatchingIgnoresLeadingZerosAndCase() {
        Course g01 = course("INT1340", "01");
        Course g02 = course("INT1340", "02");
        Course a = course("INT1340", "a");
        List<Course> all = List.of(g01, g02, a);

        assertEquals(List.of(g01), ScheduleImportService.matchGroup(all, "1"));
        assertEquals(List.of(g02), ScheduleImportService.matchGroup(all, "02"));
        assertEquals(List.of(a), ScheduleImportService.matchGroup(all, "A"));
        assertEquals(List.of(), ScheduleImportService.matchGroup(all, "3"));
        // Bỏ trống nhóm: học phần có nhiều nhóm thì trả hết để báo COURSE_AMBIGUOUS, không đoán
        assertEquals(all, ScheduleImportService.matchGroup(all, null));
        // Bỏ trống nhóm, học phần chỉ một khóa (có nhóm) thì lấy khóa đó
        assertEquals(List.of(g01), ScheduleImportService.matchGroup(List.of(g01), null));
        Course ungrouped = course("INT1111", null);
        assertEquals(List.of(ungrouped), ScheduleImportService.matchGroup(List.of(ungrouped), null));
    }
}
