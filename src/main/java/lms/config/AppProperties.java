package lms.config;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Toàn bộ cấu hình riêng của app, prefix "app." trong application.yml.
 */
@ConfigurationProperties(prefix = "app")
public record AppProperties(
        @DefaultValue Session session,
        @DefaultValue Security security,
        @DefaultValue StudentImport studentImport,
        @DefaultValue Ws ws,
        @DefaultValue Bootstrap bootstrap,
        @DefaultValue Storage storage) {

    public record Session(
            @DefaultValue("SID") String cookieName,
            @DefaultValue("2h") Duration idleTimeout,
            @DefaultValue("12h") Duration absoluteTimeout,
            @DefaultValue("false") boolean secureCookie,
            @DefaultValue("Lax") String sameSite) {
    }

    public record Security(
            @DefaultValue("10") int bcryptStrength,
            @DefaultValue("5") int loginMaxAttempts,
            @DefaultValue("5m") Duration loginLockDuration,
            @DefaultValue("8") int passwordMinLength) {
    }

    public record StudentImport(
            @DefaultValue("1000") int maxLines,
            @DefaultValue("262144") int maxBytes) {
    }

    public record Ws(
            @DefaultValue("/ws") String path,
            @DefaultValue List<String> allowedOrigins) {
    }

    /** Tài liệu khóa học lưu trên đĩa; DB chỉ giữ metadata. */
    public record Storage(
            @DefaultValue("data/files") String dir,
            @DefaultValue("20971520") long maxFileBytes) {
    }

    public record Bootstrap(
            @DefaultValue("admin") String adminUsername,
            String adminPassword,
            @DefaultValue("System Administrator") String adminFullName,
            @DefaultValue("false") boolean createDemoStudent,
            @DefaultValue("false") boolean createDemoCourses) {
    }
}
