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
        @DefaultValue Storage storage,
        @DefaultValue Judge judge,
        @DefaultValue Schedule schedule) {

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

    /**
     * Máy chấm bài lập trình (lms.code). sandbox "docker": mỗi lần chấm một container không mạng, giới hạn
     * RAM/CPU/số tiến trình (image dựng từ judge/Dockerfile) — dùng khi triển khai thật. "local": chạy thẳng
     * trên máy server bằng các trình biên dịch dưới đây, chỉ giới hạn thời gian và dung lượng output — chương
     * trình của sinh viên đọc/ghi được file mà tài khoản chạy server đọc/ghi được, chỉ dùng khi phát triển.
     * python để trống: "python" trên Windows, "python3" trên hệ khác.
     *
     * mode: "embedded" (mặc định) máy chấm chạy trong server web; "external" server web chỉ đưa bài vào hàng đợi,
     * máy chấm là process riêng chạy cùng jar với mode "worker" (JudgeWorkerApplication), dùng chung DB.
     */
    public record Judge(
            @DefaultValue("embedded") String mode,
            @DefaultValue("local") String sandbox,
            @DefaultValue("2") int workers,
            @DefaultValue("data/judge") String workDir,
            @DefaultValue("docker") String docker,
            @DefaultValue("lms-judge:1") String dockerImage,
            @DefaultValue("gcc") String gcc,
            @DefaultValue("g++") String gpp,
            @DefaultValue("javac") String javac,
            @DefaultValue("java") String java,
            String python) {
    }

    /**
     * Lịch học (lms.schedule): 1 tiết = 1 giờ, tiết 1 bắt đầu lúc first-period-hour giờ, mỗi ngày periods-per-day
     * tiết. DB lưu ngày + tiết nên đổi giá trị này thì giờ của mọi buổi (cả buổi cũ) đổi theo.
     */
    public record Schedule(
            @DefaultValue("7") int firstPeriodHour,
            @DefaultValue("14") int periodsPerDay) {
    }

    public record Bootstrap(
            @DefaultValue("admin") String adminUsername,
            String adminPassword,
            @DefaultValue("System Administrator") String adminFullName,
            @DefaultValue("false") boolean createDemoStudent,
            @DefaultValue("false") boolean createDemoCourses) {
    }
}
