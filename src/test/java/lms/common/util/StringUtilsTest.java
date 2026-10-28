package lms.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StringUtilsTest {

    @Test
    @DisplayName("stripAccents loại bỏ toàn bộ dấu tiếng Việt chuẩn xác")
    void testStripAccents() {
        assertThat(StringUtils.stripAccents("Nguyễn Đức Nghĩa")).isEqualTo("Nguyen Duc Nghia");
        assertThat(StringUtils.stripAccents("Học viện Công nghệ Bưu chính Viễn thông")).isEqualTo("Hoc vien Cong nghe Buu chinh Vien thong");
        assertThat(StringUtils.stripAccents("Đường Đời Đầy Đau Đớn")).isEqualTo("Duong Doi Day Dau Don");
        assertThat(StringUtils.stripAccents("Hello World 123")).isEqualTo("Hello World 123");
        assertThat(StringUtils.stripAccents("")).isEqualTo("");
        assertThat(StringUtils.stripAccents(null)).isEqualTo("");
    }

    @Test
    @DisplayName("toSlug sinh đường dẫn URL thân thiện từ tiêu đề tiếng Việt")
    void testToSlug() {
        assertThat(StringUtils.toSlug("Lập trình Mạng Căn bản - Kỳ 1")).isEqualTo("lap-trinh-mang-can-ban-ky-1");
        assertThat(StringUtils.toSlug("  Học Phần    Đặc Biệt &!@#  ")).isEqualTo("hoc-phan-dac-biet");
        assertThat(StringUtils.toSlug("")).isEqualTo("");
        assertThat(StringUtils.toSlug(null)).isEqualTo("");
    }

    @Test
    @DisplayName("truncate rút gọn văn bản vượt độ dài chỉ định")
    void testTruncate() {
        assertThat(StringUtils.truncate("Short text", 20)).isEqualTo("Short text");
        assertThat(StringUtils.truncate("This is a long description that needs trimming", 15)).isEqualTo("This is a lo...");
        assertThat(StringUtils.truncate("ABCDE", 3)).isEqualTo("ABC");
        assertThat(StringUtils.truncate(null, 10)).isEqualTo("");
        assertThat(StringUtils.truncate("Text", 0)).isEqualTo("");
    }

    @Test
    @DisplayName("maskEmail làm mờ email người dùng để bảo mật thông tin")
    void testMaskEmail() {
        assertThat(StringUtils.maskEmail("ducnghia@ptit.edu.vn")).isEqualTo("d***a@ptit.edu.vn");
        assertThat(StringUtils.maskEmail("ab@xyz.com")).isEqualTo("**@xyz.com");
        assertThat(StringUtils.maskEmail("invalid-email")).isEqualTo("invalid-email");
        assertThat(StringUtils.maskEmail(null)).isEqualTo("");
    }

    @Test
    @DisplayName("maskPhone làm mờ số điện thoại bảo mật")
    void testMaskPhone() {
        assertThat(StringUtils.maskPhone("0912345678")).isEqualTo("091***5678");
        assertThat(StringUtils.maskPhone("12345")).isEqualTo("12345");
        assertThat(StringUtils.maskPhone(null)).isEqualTo("");
    }

    @Test
    @DisplayName("escapeSqlWildcards thoát các ký tự % và _ an toàn")
    void testEscapeSqlWildcards() {
        assertThat(StringUtils.escapeSqlWildcards("100%_success\\test")).isEqualTo("100\\%\\_success\\\\test");
        assertThat(StringUtils.escapeSqlWildcards(null)).isEqualTo("");
    }

    @Test
    @DisplayName("levenshteinDistance tính đúng khoảng cách khác biệt giữa hai chuỗi")
    void testLevenshteinDistance() {
        assertThat(StringUtils.levenshteinDistance("kitten", "sitting")).isEqualTo(3);
        assertThat(StringUtils.levenshteinDistance("abc", "abc")).isEqualTo(0);
        assertThat(StringUtils.levenshteinDistance("", "abc")).isEqualTo(3);
        assertThat(StringUtils.levenshteinDistance("abc", "")).isEqualTo(3);
        assertThat(StringUtils.levenshteinDistance(null, null)).isEqualTo(0);
    }

    @Test
    @DisplayName("similarityScore tính tỷ lệ tương đồng chuẩn xác từ 0.0 đến 1.0")
    void testSimilarityScore() {
        assertThat(StringUtils.similarityScore("identical", "identical")).isEqualTo(1.0);
        assertThat(StringUtils.similarityScore("", "")).isEqualTo(1.0);
        assertThat(StringUtils.similarityScore("abc", "xyz")).isLessThan(0.1);
        assertThat(StringUtils.similarityScore("functionA()", "functionB()")).isGreaterThan(0.7);
    }
}
