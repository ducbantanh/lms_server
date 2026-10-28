package lms.diagnostics;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import lms.common.util.FileUtils;
import lms.config.AppProperties;

/**
 * Dịch vụ thu thập thông số chẩn đoán tài nguyên phần cứng, JVM và các hệ thống con.
 */
@Service
public class SystemDiagnosticsService {

    private final AppProperties appProperties;
    private final DataSource dataSource;

    public SystemDiagnosticsService(AppProperties appProperties, DataSource dataSource) {
        this.appProperties = appProperties;
        this.dataSource = dataSource;
    }

    /**
     * Thu thập thông số vận hành hiện tại của máy chủ.
     */
    public SystemDiagnosticsDto captureDiagnostics() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        ThreadMXBean thread = ManagementFactory.getThreadMXBean();

        long uptimeSec = runtime.getUptime() / 1000;
        String javaVer = System.getProperty("java.version", "Unknown");
        String osName = System.getProperty("os.name", "Unknown");
        int processors = Runtime.getRuntime().availableProcessors();

        // Bộ nhớ Heap & Non-heap
        long heapUsed = memory.getHeapMemoryUsage().getUsed();
        long heapMax = memory.getHeapMemoryUsage().getMax();
        long nonHeapUsed = memory.getNonHeapMemoryUsage().getUsed();
        double heapPct = heapMax > 0 ? Math.round(((double) heapUsed / heapMax * 100.0) * 10.0) / 10.0 : 0.0;

        SystemDiagnosticsDto.MemoryInfo memInfo = new SystemDiagnosticsDto.MemoryInfo(
                heapUsed,
                heapMax,
                nonHeapUsed,
                FileUtils.formatFileSize(heapUsed),
                FileUtils.formatFileSize(heapMax),
                heapPct
        );

        // Luồng thực thi
        SystemDiagnosticsDto.ThreadInfo threadInfo = new SystemDiagnosticsDto.ThreadInfo(
                thread.getThreadCount(),
                thread.getPeakThreadCount(),
                thread.getTotalStartedThreadCount()
        );

        // Dung lượng ổ đĩa lưu trữ tệp
        File storageDir = new File(appProperties.storage().dir());
        long freeDisk = storageDir.exists() ? storageDir.getFreeSpace() : new File(".").getFreeSpace();
        long totalDisk = storageDir.exists() ? storageDir.getTotalSpace() : new File(".").getTotalSpace();
        double freePct = totalDisk > 0 ? Math.round(((double) freeDisk / totalDisk * 100.0) * 10.0) / 10.0 : 0.0;

        SystemDiagnosticsDto.DiskInfo diskInfo = new SystemDiagnosticsDto.DiskInfo(
                freeDisk,
                totalDisk,
                FileUtils.formatFileSize(freeDisk),
                FileUtils.formatFileSize(totalDisk),
                freePct
        );

        // Kiểm tra trạng thái hệ thống con
        Map<String, String> subsystemStatus = new LinkedHashMap<>();
        subsystemStatus.put("database", checkDatabaseHealth());
        subsystemStatus.put("storage", storageDir.canWrite() || storageDir.mkdirs() ? "OK" : "READ_ONLY");
        subsystemStatus.put("judge_sandbox", appProperties.judge().sandbox());

        return new SystemDiagnosticsDto(
                Instant.now(),
                uptimeSec,
                javaVer,
                osName,
                processors,
                memInfo,
                threadInfo,
                diskInfo,
                subsystemStatus
        );
    }

    private String checkDatabaseHealth() {
        try (var conn = dataSource.getConnection()) {
            return conn.isValid(2) ? "HEALTHY" : "DEGRADED";
        } catch (Exception e) {
            return "UNAVAILABLE: " + e.getMessage();
        }
    }
}
