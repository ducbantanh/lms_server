package lms.common.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Tiện ích quản lý tập tin và đường dẫn lưu trữ an toàn (Safe File Operations).
 */
public final class FileUtils {

    private static final String[] UNITS = new String[] { "B", "KB", "MB", "GB", "TB" };
    private static final DecimalFormat DF = new DecimalFormat("#,##0.#");

    private FileUtils() {
    }

    /**
     * Định dạng kích thước tệp theo đơn vị dễ đọc (B, KB, MB, GB).
     * Ví dụ: 1048576 bytes -> "1 MB".
     */
    public static String formatFileSize(long bytes) {
        if (bytes <= 0) {
            return "0 B";
        }
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        digitGroups = Math.min(digitGroups, UNITS.length - 1);
        double size = bytes / Math.pow(1024, digitGroups);
        return DF.format(size) + " " + UNITS[digitGroups];
    }

    /**
     * Tách phần mở rộng tệp dạng chữ thường (không bao gồm dấu chấm).
     */
    public static String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }

    /**
     * Tách tên cơ sở của tệp (loại bỏ phần mở rộng).
     */
    public static String getBaseName(String filename) {
        if (filename == null) {
            return "";
        }
        int dot = filename.lastIndexOf('.');
        if (dot == -1) {
            return filename;
        }
        return filename.substring(0, dot);
    }

    /**
     * Ghép đường dẫn an toàn và xác minh không vượt ra ngoài thư mục gốc (Chống Path Traversal).
     */
    public static Path resolveSafePath(Path rootDir, String relativePath) {
        if (rootDir == null) {
            throw new IllegalArgumentException("rootDir không được phép null");
        }
        if (relativePath == null || relativePath.isBlank()) {
            return rootDir.normalize();
        }

        Path resolved = rootDir.resolve(relativePath).normalize();
        if (!resolved.startsWith(rootDir.normalize())) {
            throw new SecurityException("Phát hiện nguy cơ Path Traversal: " + relativePath);
        }
        return resolved;
    }
}
