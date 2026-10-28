package lms.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

class RoomImportParserTest {

    private final RoomImportParser parser = new RoomImportParser(1000);

    @Test
    void parsesAllSeparatorsAndOptionalColumns() {
        RoomImportParser.Result r = parser.parse("""
                # danh mục phòng
                Phòng,Tòa nhà,Sức chứa,Ghi chú
                402-A2|Nhà A2|60|Có máy chiếu
                Lab 1\tNhà A3\t40
                105-A1
                "P. 201 (cũ)";Nhà B;1.000;"Hội trường, 2 tầng"
                """);

        assertEquals(4, r.dataLines());
        assertEquals(List.of(), r.errors());
        assertEquals(new RoomImportParser.Row(3, "402-A2", "Nhà A2", 60, "Có máy chiếu"), r.rows().get(0));
        assertEquals(new RoomImportParser.Row(4, "Lab 1", "Nhà A3", 40, null), r.rows().get(1));
        assertEquals(new RoomImportParser.Row(5, "105-A1", null, null, null), r.rows().get(2));
        assertEquals(new RoomImportParser.Row(6, "P. 201 (cũ)", "Nhà B", 1000, "Hội trường, 2 tầng"),
                r.rows().get(3));
    }

    @Test
    void reportsInvalidLinesWithLineNumbers() {
        RoomImportParser.Result r = parser.parse("""
                -bad|Nhà A
                A101|Nhà A|sáu mươi
                A102|Nhà A|0
                A103|Nhà A|10|ghi chú|thừa cột
                A104,,,
                """);

        assertEquals(List.of(
                new RoomImportParser.LineIssue(1, "-bad", RoomImportParser.INVALID_NAME),
                new RoomImportParser.LineIssue(2, "A101", RoomImportParser.INVALID_CAPACITY),
                new RoomImportParser.LineIssue(3, "A102", RoomImportParser.INVALID_CAPACITY),
                new RoomImportParser.LineIssue(4, null, RoomImportParser.INVALID_FORMAT)), r.errors());
        // Ô trống thừa ở cuối (Excel) vẫn hợp lệ
        assertEquals("A104", r.rows().get(0).name());
        assertNull(r.rows().get(0).building());
    }

    @Test
    void duplicatesIgnoreCaseAccentsAndSpaces() {
        RoomImportParser.Result r = parser.parse("Phòng Lab  1\nphong lab 1\nPHÒNG LAB 1|Nhà A\n");

        assertEquals(1, r.rows().size());
        assertEquals("Phòng Lab 1", r.rows().get(0).name());
        assertEquals(List.of(2, 3), r.duplicates().stream().map(RoomImportParser.LineIssue::line).toList());
    }

    @Test
    void headerOnlySkippedOnFirstLine() {
        assertEquals(1, parser.parse("Room|Building\nRoom|x\n").rows().size());
    }

    @Test
    void rejectsTooManyLines() {
        assertThrows(RoomImportParser.TooManyLinesException.class,
                () -> new RoomImportParser(2).parse("A1\nA2\nA3\n"));
    }
}
