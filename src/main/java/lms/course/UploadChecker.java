package lms.course;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lms.common.AppException;
import lms.common.ErrorCode;
import lms.config.AppProperties;

/**
 * Bộ kiểm tra và chuẩn hóa tệp tải lên (File Upload Security Validator).
 * - Giới hạn dung lượng theo cấu hình hệ thống.
 * - Xác thực định dạng dựa trên danh sách phần mở rộng hợp lệ.
 * - Xác thực tính xác thực của tệp dựa trên chữ ký Magic Bytes để chống giả mạo phần mở rộng (MIME Spoofing).
 * - Phòng chống các tấn công Path Traversal, Null Byte Injection và tệp thực thi độc hại.
 */
@Component
public class UploadChecker {

    private static final Logger log = LoggerFactory.getLogger(UploadChecker.class);

    /** Tài liệu khóa học và đề thi: PDF, DOCX, PPTX. */
    public static final Set<String> MATERIAL = Set.of("pdf", "docx", "pptx");

    /** Bài nộp bài tập / thực hành của sinh viên: PDF, DOCX, PPTX, ZIP. */
    public static final Set<String> SUBMISSION = Set.of("pdf", "docx", "pptx", "zip");

    /** Minh chứng xin nghỉ học (giấy khám bệnh, quyết định triệu tập): ảnh chụp hoặc PDF. */
    public static final Set<String> EVIDENCE = Set.of("pdf", "jpg", "jpeg", "png");

    /** Danh sách phần mở rộng bị cấm tuyệt đối vì lý do an toàn mã nguồn. */
    private static final Set<String> DANGEROUS_EXTENSIONS = Set.of(
            "exe", "dll", "bat", "cmd", "sh", "bin", "elf",
            "jsp", "jspx", "php", "asp", "aspx", "cgi", "pl",
            "vbs", "ps1", "jar", "war", "pyc", "class"
    );

    private static final Map<String, String> MIME_TYPES = Map.of(
            "pdf", CourseContentService.PDF,
            "docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "zip", "application/zip",
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "png", "image/png"
    );

    private static final byte[] PDF_MAGIC = "%PDF-".getBytes();
    private static final byte[] ZIP_MAGIC = { 0x50, 0x4b, 0x03, 0x04 };
    private static final byte[] JPEG_MAGIC = { (byte) 0xff, (byte) 0xd8, (byte) 0xff };
    private static final byte[] PNG_MAGIC = { (byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a };

    /**
     * DTO lưu trữ tệp đã qua kiểm duyệt an toàn.
     */
    public record Checked(
            String name,
            String contentType,
            byte[] data,
            String sha256Checksum,
            long sizeBytes) {

        /** Constructor tương thích ngược 3 tham số */
        public Checked(String name, String contentType, byte[] data) {
            this(name, contentType, data, calculateSha256(data), data.length);
        }
    }

    private final long maxFileBytes;

    public UploadChecker(AppProperties props) {
        this.maxFileBytes = props.storage().maxFileBytes();
    }

    /**
     * Kiểm tra toàn diện tệp upload: kích thước, phần mở rộng, magic bytes và tính toàn vẹn.
     */
    public Checked check(MultipartFile upload, Set<String> allowedExtensions) {
        if (upload == null || upload.isEmpty()) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Tập tin rỗng hoặc không tồn tại");
        }
        if (upload.getSize() > maxFileBytes) {
            log.warn("Tập tin tải lên vượt quá giới hạn {} bytes: kích thước hiện tại = {} bytes",
                    maxFileBytes, upload.getSize());
            throw new AppException(ErrorCode.PAYLOAD_TOO_LARGE);
        }

        String rawName = upload.getOriginalFilename();
        String sanitizedName = sanitizeFilename(rawName);
        String extension = extractExtension(sanitizedName);

        if (DANGEROUS_EXTENSIONS.contains(extension)) {
            log.error("Cảnh báo an ninh: Phát hiện tập tin có đuôi nguy hại '{}' từ IP hoặc người dùng", extension);
            throw new AppException(ErrorCode.UNSUPPORTED_FILE_TYPE, "Định dạng tập tin bị hệ thống từ chối vì lý do bảo mật");
        }

        if (!allowedExtensions.contains(extension)) {
            throw new AppException(ErrorCode.UNSUPPORTED_FILE_TYPE, "Phần mở rộng ." + extension + " không thuộc danh mục cho phép");
        }

        String contentType = MIME_TYPES.get(extension);
        if (contentType == null) {
            throw new AppException(ErrorCode.UNSUPPORTED_FILE_TYPE);
        }

        byte[] fileBytes;
        try {
            fileBytes = upload.getBytes();
        } catch (IOException e) {
            log.error("Lỗi khi đọc dữ liệu tập tin tải lên: {}", e.getMessage(), e);
            throw new UncheckedIOException(e);
        }

        if (!matchesMagicBytes(fileBytes, contentType)) {
            log.warn("Tệp '{}' có phần mở rộng '{}' nhưng chữ ký byte (Magic Header) không khớp", sanitizedName, extension);
            throw new AppException(ErrorCode.UNSUPPORTED_FILE_TYPE, "Nội dung tệp không khớp với định dạng công bố");
        }

        String checksum = calculateSha256(fileBytes);
        return new Checked(sanitizedName, contentType, fileBytes, checksum, fileBytes.length);
    }

    /**
     * Làm sạch tên tệp: chống Path Traversal, ký tự điều khiển và null bytes.
     */
    public static String sanitizeFilename(String original) {
        if (original == null) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Tên tệp không được để trống");
        }

        // Chuẩn hóa dấu phân cách thư mục
        String name = original.replace('\\', '/');
        name = name.substring(name.lastIndexOf('/') + 1).strip();

        // Loại bỏ ký tự Null byte và control characters
        name = name.replaceAll("[\\x00-\\x1F\\x7F]", "");

        // Chống directory traversal sequences
        while (name.contains("..")) {
            name = name.replace("..", ".");
        }

        if (name.isEmpty() || name.equals(".")) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Tên tệp không hợp lệ sau khi chuẩn hóa");
        }

        return name.length() > 255 ? name.substring(name.length() - 255) : name;
    }

    /**
     * Tách phần mở rộng của tên tệp chữ thường.
     */
    public static String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }

    /**
     * So sánh Magic Bytes của mảng byte với chuẩn MIME type.
     */
    private static boolean matchesMagicBytes(byte[] data, String contentType) {
        byte[] expected = switch (contentType) {
            case CourseContentService.PDF -> PDF_MAGIC;
            case "image/jpeg" -> JPEG_MAGIC;
            case "image/png" -> PNG_MAGIC;
            default -> ZIP_MAGIC;
        };

        if (data.length < expected.length) {
            return false;
        }
        return Arrays.equals(data, 0, expected.length, expected, 0, expected.length);
    }

    /**
     * Tính toán mã băm SHA-256 để kiểm tra tính toàn vẹn và chống trùng lặp.
     */
    public static String calculateSha256(byte[] data) {
        if (data == null) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Thuật toán SHA-256 không khả dụng trên JVM này", e);
        }
    }
}
