package lms.common.util;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileUtilsTest {

    @Test
    @DisplayName("formatFileSize định dạng kích thước tệp dễ đọc")
    void testFormatFileSize() {
        assertThat(FileUtils.formatFileSize(0)).isEqualTo("0 B");
        assertThat(FileUtils.formatFileSize(512)).isEqualTo("512 B");
        assertThat(FileUtils.formatFileSize(1024)).isEqualTo("1 KB");
        assertThat(FileUtils.formatFileSize(1048576)).isEqualTo("1 MB");
        assertThat(FileUtils.formatFileSize(1073741824)).isEqualTo("1 GB");
    }

    @Test
    @DisplayName("getExtension và getBaseName tách đúng tên tệp và phần mở rộng")
    void testFileNames() {
        assertThat(FileUtils.getExtension("document.pdf")).isEqualTo("pdf");
        assertThat(FileUtils.getExtension("archive.tar.gz")).isEqualTo("gz");
        assertThat(FileUtils.getExtension("noextension")).isEqualTo("");
        assertThat(FileUtils.getExtension(null)).isEqualTo("");

        assertThat(FileUtils.getBaseName("document.pdf")).isEqualTo("document");
        assertThat(FileUtils.getBaseName("image.png")).isEqualTo("image");
        assertThat(FileUtils.getBaseName("test")).isEqualTo("test");
    }

    @Test
    @DisplayName("resolveSafePath ngăn chặn tấn công Path Traversal")
    void testResolveSafePath() {
        Path root = Paths.get("data", "files");

        Path safe = FileUtils.resolveSafePath(root, "course-1/lecture.pdf");
        assertThat(safe.startsWith(root.normalize())).isTrue();

        assertThatThrownBy(() -> FileUtils.resolveSafePath(root, "../../../etc/passwd"))
                .isInstanceOf(SecurityException.class);
    }
}
