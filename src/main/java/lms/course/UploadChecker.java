package lms.course;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lms.common.AppException;
import lms.common.ErrorCode;
import lms.config.AppProperties;

/**
 * Kiểm tra file tải lên (tài liệu, đề, bài nộp): giới hạn dung lượng, đuôi file nằm trong danh sách
 * cho phép và nội dung khớp magic bytes của đuôi đó. Tên gốc chỉ giữ phần tên file để hiển thị.
 */
@Component
public class UploadChecker {

    /** Tài liệu khóa học và đề bài. */
    public static final Set<String> MATERIAL = Set.of("pdf", "docx", "pptx");
    /** Bài nộp của sinh viên: thêm ZIP cho mã nguồn. */
    public static final Set<String> SUBMISSION = Set.of("pdf", "docx", "pptx", "zip");

    private static final Map<String, String> TYPES = Map.of(
            "pdf", CourseContentService.PDF,
            "docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "zip", "application/zip");
    private static final byte[] PDF_MAGIC = "%PDF-".getBytes();
    private static final byte[] ZIP_MAGIC = { 0x50, 0x4b, 0x03, 0x04 };

    /** File đã qua kiểm tra: tên gốc, kiểu nội dung theo đuôi và nội dung. */
    public record Checked(String name, String contentType, byte[] data) {
    }

    private final long maxFileBytes;

    public UploadChecker(AppProperties props) {
        this.maxFileBytes = props.storage().maxFileBytes();
    }

    public Checked check(MultipartFile upload, Set<String> extensions) {
        if (upload == null || upload.isEmpty()) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Empty file");
        }
        if (upload.getSize() > maxFileBytes) {
            throw new AppException(ErrorCode.PAYLOAD_TOO_LARGE);
        }
        String name = baseName(upload.getOriginalFilename());
        String ext = name.contains(".") ? name.substring(name.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT) : "";
        String contentType = extensions.contains(ext) ? TYPES.get(ext) : null;
        byte[] data;
        try {
            data = upload.getBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        if (contentType == null
                || !startsWith(data, CourseContentService.PDF.equals(contentType) ? PDF_MAGIC : ZIP_MAGIC)) {
            throw new AppException(ErrorCode.UNSUPPORTED_FILE_TYPE);
        }
        return new Checked(name, contentType, data);
    }

    private static String baseName(String original) {
        String name = original == null ? "" : original.replace('\\', '/');
        name = name.substring(name.lastIndexOf('/') + 1).strip();
        if (name.isEmpty()) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Missing file name");
        }
        return name.length() > 255 ? name.substring(name.length() - 255) : name;
    }

    private static boolean startsWith(byte[] data, byte[] magic) {
        return data.length >= magic.length && Arrays.equals(data, 0, magic.length, magic, 0, magic.length);
    }
}
