package lms.user.importing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

class AccountImportParserTest {

    private final AccountImportParser parser = new AccountImportParser(100);

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
        assertEquals("01012003", r.rows().get(0).initialPassword());
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
        assertEquals(AccountImportParser.INVALID_FORMAT, r.errors().get(0).reason());
        assertEquals(AccountImportParser.INVALID_USERNAME, r.errors().get(1).reason());
        assertEquals(AccountImportParser.INVALID_DATE, r.errors().get(2).reason());
        assertEquals(AccountImportParser.INVALID_NAME, r.errors().get(3).reason());
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
        assertEquals("ABC001", r.rows().get(0).username());
    }

    @Test
    void rejectsTooManyLines() {
        var small = new AccountImportParser(1);
        assertThrows(AccountImportParser.TooManyLinesException.class,
                () -> small.parse("ABC001|01012003|An\nABC002|01012003|Binh"));
    }

    @Test
    void parsesCsvWithHeaderQuotesAndExcelDates() {
        var r = parser.parse("""
                MSSV,Ngày sinh,Họ và tên
                GV001,1012003,"Lê Văn Cường"
                GV002,"15/2/1985",Phạm Thu
                GV003,1985-12-31,Hoàng Minh
                """);
        assertEquals(0, r.errors().size(), r.errors().toString());
        assertEquals(3, r.dataLines()); // dòng tiêu đề không tính
        assertEquals(List.of("01012003", "15021985", "31121985"),
                r.rows().stream().map(AccountImportParser.Row::initialPassword).toList());
        assertEquals("Lê Văn Cường", r.rows().get(0).fullName());
    }

    @Test
    void headerOnlySkippedOnFirstDataLine() {
        var r = parser.parse("SV01,01012003,An\nMSSV,Ngày sinh,Họ tên");
        assertEquals(1, r.rows().size());
        assertEquals(AccountImportParser.INVALID_DATE, r.errors().get(0).reason());
    }

    @Test
    void detectsDelimiterPerLineIgnoringQuotedText() {
        assertEquals("Ngô Thị Lan", parser.parse("SV01;02.03.2004;Ngô Thị Lan").rows().get(0).fullName());
        assertEquals(LocalDate.of(2004, 3, 2), parser.parse("SV01\t02032004\tNgô Thị Lan").rows().get(0).birthDate());
        // ; trong ô có nháy không phải dấu phân cách: dòng vẫn tách theo dấu phẩy (họ tên có ; nên bị loại)
        var quoted = parser.parse("SV01,02032004,\"Ngô; Lan\"");
        assertEquals(AccountImportParser.INVALID_NAME, quoted.errors().get(0).reason());
        // dòng hỏng ở đầu không làm sai cách tách các dòng sau
        assertEquals(1, parser.parse("bad line\nSV02|02032004|Lan").rows().size());
    }

    @Test
    void rejectsImpossibleOrFutureDates() {
        assertNull(AccountImportParser.parseDate("30/02/2004"));
        assertNull(AccountImportParser.parseDate("01011899"));
        assertNull(AccountImportParser.parseDate("01/01/" + (LocalDate.now().getYear() + 1)));
        assertNull(AccountImportParser.parseDate("2004/03/02"));
    }

    @Test
    void normalizesCombiningMarksFromWindows1258() {
        // Excel lưu "Nguyễn" bằng CP1258 = "ê" dựng sẵn (0xEA) + dấu ngã kết hợp (0xDE);
        // file đó không phải UTF-8 hợp lệ, sau decode + NFC phải trùng chữ dựng sẵn
        byte[] cp1258 = "SV01,01012003,Nguyễn An".getBytes(Charset.forName("windows-1258"));
        var r = parser.parse(AccountImportService.decode(cp1258));
        assertEquals("Nguyễn An", r.rows().get(0).fullName());
        assertEquals("Nguyễn An", AccountImportService.decode("Nguyễn An".getBytes(StandardCharsets.UTF_8)));
    }
}
