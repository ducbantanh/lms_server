package lms.user.importing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class StudentImportParserTest {

    private final StudentImportParser parser = new StudentImportParser(100);

    @Test
    void parsesValidLinesAndSkipsCommentsAndBlankLines() {
        var r = parser.parse("""
                # header comment

                B21DCCN001|01012003|Nguyễn Văn   A
                B21DCCN002|15022003|Trần Thị B
                """);
        assertEquals(2, r.rows().size());
        assertEquals(2, r.dataLines());
        assertEquals("Nguyễn Văn A", r.rows().get(0).fullName());
        assertEquals(LocalDate.of(2003, 1, 1), r.rows().get(0).birthDate());
        assertEquals(3, r.rows().get(0).line());
    }

    @Test
    void reportsErrorsPerLine() {
        var r = parser.parse("""
                bad line
                !!|01012003|Name
                B21X|31022003|Name
                B21Y|01012003|N4me
                """);
        assertEquals(0, r.rows().size());
        assertEquals(4, r.errors().size());
        assertEquals(StudentImportParser.INVALID_FORMAT, r.errors().get(0).reason());
        assertEquals(StudentImportParser.INVALID_STUDENT_ID, r.errors().get(1).reason());
        assertEquals(StudentImportParser.INVALID_DATE, r.errors().get(2).reason());
        assertEquals(StudentImportParser.INVALID_NAME, r.errors().get(3).reason());
    }

    @Test
    void detectsCaseInsensitiveDuplicatesInInput() {
        var r = parser.parse("ABC001|01012003|An\nabc001|01012003|An");
        assertEquals(1, r.rows().size());
        assertEquals(1, r.duplicates().size());
        assertEquals(2, r.duplicates().get(0).line());
    }

    @Test
    void handlesBomAndWindowsLineEndings() {
        var r = parser.parse("﻿ABC001|01012003|An\r\nABC002|02012003|Binh\r\n");
        assertEquals(2, r.rows().size());
        assertEquals("ABC001", r.rows().get(0).studentId());
    }

    @Test
    void rejectsTooManyLines() {
        var small = new StudentImportParser(1);
        assertThrows(StudentImportParser.TooManyLinesException.class,
                () -> small.parse("ABC001|01012003|An\nABC002|01012003|Binh"));
    }
}
