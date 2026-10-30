package lms.security.threat;

public enum ThreatType {
    SQL_INJECTION("Tấn công tiêm mã độc SQL Injection", ThreatSeverity.CRITICAL),
    CROSS_SITE_SCRIPTING("Tấn công kịch bản liên trang XSS", ThreatSeverity.HIGH),
    PATH_TRAVERSAL("Tấn công duyệt thư mục trái phép Path Traversal", ThreatSeverity.HIGH),
    COMMAND_INJECTION("Tấn công tiêm lệnh hệ điều hành OS Command Injection", ThreatSeverity.CRITICAL),
    SUSPICIOUS_SCANNER("Công cụ dò quét lỗ hổng tự động", ThreatSeverity.MEDIUM);

    private final String description;
    private final ThreatSeverity defaultSeverity;

    ThreatType(String description, ThreatSeverity defaultSeverity) {
        this.description = description;
        this.defaultSeverity = defaultSeverity;
    }

    public String getDescription() {
        return description;
    }

    public ThreatSeverity getDefaultSeverity() {
        return defaultSeverity;
    }
}
