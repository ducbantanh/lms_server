package lms.diagnostics;

import java.time.Instant;
import java.util.Map;

/**
 * DTO dữ liệu chẩn đoán sức khỏe hệ thống và tài nguyên máy chủ.
 */
public record SystemDiagnosticsDto(
        Instant timestamp,
        long uptimeSeconds,
        String javaVersion,
        String osName,
        int availableProcessors,
        MemoryInfo memory,
        ThreadInfo threads,
        DiskInfo disk,
        Map<String, String> subsystemStatus) {

    public record MemoryInfo(
            long heapUsedBytes,
            long heapMaxBytes,
            long nonHeapUsedBytes,
            String heapUsedFormatted,
            String heapMaxFormatted,
            double heapUsagePercentage) {
    }

    public record ThreadInfo(
            int activeThreadCount,
            int peakThreadCount,
            long totalStartedThreadCount) {
    }

    public record DiskInfo(
            long freeBytes,
            long totalBytes,
            String freeFormatted,
            String totalFormatted,
            double freePercentage) {
    }
}
