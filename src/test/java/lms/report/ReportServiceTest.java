package lms.report;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class ReportServiceTest {

    @Test
    void csvQuotesSpecialCharactersAndDefusesFormulas() {
        assertEquals("B21DCCN001,Nguyễn Văn A,8.5\r\n", ReportService.csvLine(List.of("B21DCCN001", "Nguyễn Văn A", "8.5")));
        assertEquals("\"Lê, B\"", ReportService.csvCell("Lê, B"));
        assertEquals("\"Bài \"\"1\"\"\"", ReportService.csvCell("Bài \"1\""));
        assertEquals("'=HYPERLINK(1)", ReportService.csvCell("=HYPERLINK(1)"));
        assertEquals("\"'=1,2\"", ReportService.csvCell("=1,2"));
        assertEquals("", ReportService.csvCell(null));
    }

    @Test
    void fileSafeRemovesCharactersWindowsRejects() {
        assertEquals("B21 - Trần_Thị_C", ReportService.fileSafe("B21 - Trần/Thị:C"));
        assertEquals("bai", ReportService.fileSafe("bai. "));
        assertEquals("_", ReportService.fileSafe("..."));
        assertEquals("_x", ReportService.fileSafe("\u0001x"));
    }

    @Test
    void uniqueNameNumbersDuplicatesCaseInsensitively() {
        Set<String> used = new HashSet<>();
        assertEquals("bai.pdf", ReportService.uniqueName("bai.pdf", used));
        assertEquals("Bai (2).pdf", ReportService.uniqueName("Bai.pdf", used));
        assertEquals("bai (3).pdf", ReportService.uniqueName("bai.pdf", used));
        assertEquals("README", ReportService.uniqueName("README", used));
        assertEquals("README (2)", ReportService.uniqueName("README", used));
    }
}
