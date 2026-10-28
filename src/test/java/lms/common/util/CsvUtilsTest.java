package lms.common.util;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CsvUtilsTest {

    @Test
    @DisplayName("escapeField bao đóng dấu nháy kép cho các ký tự đặc biệt")
    void testEscapeField() {
        assertThat(CsvUtils.escapeField("Simple")).isEqualTo("Simple");
        assertThat(CsvUtils.escapeField("Hello, World")).isEqualTo("\"Hello, World\"");
        assertThat(CsvUtils.escapeField("He said \"Hi\"")).isEqualTo("\"He said \"\"Hi\"\"\"");
        assertThat(CsvUtils.escapeField(null)).isEqualTo("");
    }

    @Test
    @DisplayName("buildLine dựng dòng CSV từ danh sách trường")
    void testBuildLine() {
        List<Object> fields = List.of("1", "B21DCCN123", "Nguyễn Đức Nghĩa", "9.5");
        assertThat(CsvUtils.buildLine(fields)).isEqualTo("1,B21DCCN123,Nguyễn Đức Nghĩa,9.5");

        List<Object> withComma = List.of("1", "Nghĩa, Nguyễn", "10");
        assertThat(CsvUtils.buildLine(withComma)).isEqualTo("1,\"Nghĩa, Nguyễn\",10");
    }

    @Test
    @DisplayName("parseLine phân tích chuỗi CSV chuẩn xác")
    void testParseLine() {
        String line = "1,\"Nghĩa, Nguyễn\",10";
        List<String> parsed = CsvUtils.parseLine(line);
        assertThat(parsed).containsExactly("1", "Nghĩa, Nguyễn", "10");
    }
}
