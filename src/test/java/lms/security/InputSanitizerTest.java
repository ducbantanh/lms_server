package lms.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InputSanitizerTest {

    @Test
    @DisplayName("stripHtml loại bỏ các thẻ script và html độc hại")
    void testStripHtml() {
        String dirty = "<script>alert('XSS')</script><b>Hello</b> <a href='javascript:void(0)'>Link</a>";
        String clean = InputSanitizer.stripHtml(dirty);
        assertThat(clean).isEqualTo("Hello Link");
    }

    @Test
    @DisplayName("escapeHtml mã hóa các ký tự đặc biệt thành HTML entities")
    void testEscapeHtml() {
        assertThat(InputSanitizer.escapeHtml("a < b && c > d")).isEqualTo("a &lt; b &amp;&amp; c &gt; d");
        assertThat(InputSanitizer.escapeHtml("\"Hello's\"")).isEqualTo("&quot;Hello&#x27;s&quot;");
    }

    @Test
    @DisplayName("containsSuspiciousContent nhận diện mã độc XSS")
    void testContainsSuspiciousContent() {
        assertThat(InputSanitizer.containsSuspiciousContent("<script src='evil.js'></script>")).isTrue();
        assertThat(InputSanitizer.containsSuspiciousContent("<img src='x' onerror='alert(1)'>")).isTrue();
        assertThat(InputSanitizer.containsSuspiciousContent("This is normal Vietnamese text: Lập trình mạng")).isFalse();
    }
}
