package lms.course;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import lms.common.RandomTokens;
import lms.config.AppProperties;

/**
 * Lưu nội dung tài liệu trên đĩa với tên ngẫu nhiên (storage key), không dùng tên gốc
 * để tránh path traversal và trùng tên. Quyền truy cập do service kiểm tra trước khi đọc.
 */
@Component
public class FileStorage {

    private static final Pattern KEY = Pattern.compile("[A-Za-z0-9_-]{43}");

    private final Path root;

    public FileStorage(AppProperties props) throws IOException {
        this.root = Path.of(props.storage().dir()).toAbsolutePath().normalize();
        Files.createDirectories(root);
    }

    /** Ghi file mới, trả về storage key (43 ký tự base64url). */
    public String store(byte[] data) {
        String key = RandomTokens.urlSafe(32);
        try {
            Files.write(resolve(key), data, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot store file", e);
        }
        return key;
    }

    public Path path(String key) {
        return resolve(key);
    }

    /** Xóa file; lỗi xóa chỉ để lại file rác trên đĩa, không làm hỏng nghiệp vụ. */
    public void deleteQuietly(String key) {
        try {
            Files.deleteIfExists(resolve(key));
        } catch (IOException | IllegalArgumentException e) {
            // bỏ qua
        }
    }

    private Path resolve(String key) {
        if (key == null || !KEY.matcher(key).matches()) {
            throw new IllegalArgumentException("Invalid storage key");
        }
        return root.resolve(key);
    }
}
