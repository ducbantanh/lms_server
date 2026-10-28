package lms.course;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import lms.common.AppException;
import lms.common.ErrorCode;
import lms.config.AppProperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UploadCheckerTest {

    private final AppProperties.Storage storageProps = new AppProperties.Storage("data/files", 5_000_000);
    private final AppProperties appProperties = new AppProperties(null, null, null, null, null, storageProps, null, null);
    private final UploadChecker checker = new UploadChecker(appProperties);

    @Test
    @DisplayName("check chấp nhận file PDF hợp lệ với magic bytes %PDF-")
    void testValidPdf() {
        byte[] pdfContent = "%PDF-1.7 valid pdf content".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "test_exam.pdf", "application/pdf", pdfContent);

        UploadChecker.Checked checked = checker.check(file, UploadChecker.MATERIAL);
        assertThat(checked.name()).isEqualTo("test_exam.pdf");
        assertThat(checked.contentType()).isEqualTo(CourseContentService.PDF);
        assertThat(checked.sha256Checksum()).isNotBlank();
        assertThat(checked.sizeBytes()).isEqualTo(pdfContent.length);
    }

    @Test
    @DisplayName("check từ chối file có chữ ký byte không khớp phần mở rộng")
    void testSpoofedMime() {
        byte[] fakeContent = "This is not a pdf file".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "fake.pdf", "application/pdf", fakeContent);

        assertThatThrownBy(() -> checker.check(file, UploadChecker.MATERIAL))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).code()).isEqualTo(ErrorCode.UNSUPPORTED_FILE_TYPE));
    }

    @Test
    @DisplayName("check từ chối file vượt quá kích thước tối đa")
    void testFileTooLarge() {
        byte[] largeContent = new byte[6_000_000]; // 6MB > 5MB limit
        MockMultipartFile file = new MockMultipartFile("file", "large.pdf", "application/pdf", largeContent);

        assertThatThrownBy(() -> checker.check(file, UploadChecker.MATERIAL))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).code()).isEqualTo(ErrorCode.PAYLOAD_TOO_LARGE));
    }

    @Test
    @DisplayName("sanitizeFilename làm sạch tên tệp ngăn chặn Path Traversal và Null Bytes")
    void testSanitizeFilename() {
        assertThat(UploadChecker.sanitizeFilename("../../etc/passwd.pdf")).isEqualTo("passwd.pdf");
        assertThat(UploadChecker.sanitizeFilename("folder\\subfolder\\report.docx")).isEqualTo("report.docx");
        assertThat(UploadChecker.sanitizeFilename("normal_file.pdf")).isEqualTo("normal_file.pdf");
    }

    @Test
    @DisplayName("calculateSha256 tính toán mã băm nhất quán")
    void testCalculateSha256() {
        byte[] data1 = "Hello LMS".getBytes();
        byte[] data2 = "Hello LMS".getBytes();
        byte[] data3 = "Different".getBytes();

        assertThat(UploadChecker.calculateSha256(data1)).isEqualTo(UploadChecker.calculateSha256(data2));
        assertThat(UploadChecker.calculateSha256(data1)).isNotEqualTo(UploadChecker.calculateSha256(data3));
    }
}
