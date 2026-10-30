package lms.security.threat;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ThreatDetectionServiceTest {

    private final ThreatDetectionService threatService = new ThreatDetectionService();

    @Test
    @DisplayName("Dữ liệu an toàn không kích hoạt cảnh báo đe dọa")
    void testCleanInput() {
        ThreatDetectionResult result = threatService.inspectText("username", "nguyenducnghia_2026");
        assertThat(result.isThreatDetected()).isFalse();
        assertThat(result.incidents()).isEmpty();
    }

    @Test
    @DisplayName("Phát hiện tấn công SQL Injection kinh điển")
    void testSqlInjectionDetection() {
        String sqliPayload = "admin' OR '1'='1";
        ThreatDetectionResult result = threatService.inspectText("password", sqliPayload);

        assertThat(result.isThreatDetected()).isTrue();
        assertThat(result.maxSeverity()).isEqualTo(ThreatSeverity.CRITICAL);
        assertThat(result.incidents()).anyMatch(i -> i.threatType() == ThreatType.SQL_INJECTION);
    }

    @Test
    @DisplayName("Phát hiện tấn công Cross-Site Scripting (XSS)")
    void testXssDetection() {
        String xssPayload = "<script>alert('pwned')</script>";
        ThreatDetectionResult result = threatService.inspectText("comment", xssPayload);

        assertThat(result.isThreatDetected()).isTrue();
        assertThat(result.incidents()).anyMatch(i -> i.threatType() == ThreatType.CROSS_SITE_SCRIPTING);
    }

    @Test
    @DisplayName("Phát hiện tấn công duyệt thư mục Path Traversal")
    void testPathTraversalDetection() {
        String traversalPayload = "../../etc/passwd";
        ThreatDetectionResult result = threatService.inspectText("filePath", traversalPayload);

        assertThat(result.isThreatDetected()).isTrue();
        assertThat(result.incidents()).anyMatch(i -> i.threatType() == ThreatType.PATH_TRAVERSAL);
    }

    @Test
    @DisplayName("Phát hiện công cụ quét bảo mật tự động thông qua User-Agent")
    void testScannerUserAgentDetection() {
        String scannerAgent = "sqlmap/1.5.2#stable (http://sqlmap.org)";
        ThreatDetectionResult result = threatService.inspectUserAgent(scannerAgent);

        assertThat(result.isThreatDetected()).isTrue();
        assertThat(result.incidents()).anyMatch(i -> i.threatType() == ThreatType.SUSPICIOUS_SCANNER);
    }

    @Test
    @DisplayName("Kiểm tra toàn bộ bản đồ tham số Request Parameter Map")
    void testParameterMapInspection() {
        Map<String, String[]> params = Map.of(
                "page", new String[]{"1"},
                "sort", new String[]{"name; DROP TABLE users;--"}
        );

        ThreatDetectionResult result = threatService.inspectParameters(params);
        assertThat(result.isThreatDetected()).isTrue();
        assertThat(result.incidents()).anyMatch(i -> i.parameterName().equals("sort"));
    }
}
