package lms.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorCodeTest {

    @Test
    @DisplayName("ErrorCode có đầy đủ thông điệp tiếng Việt và phân loại hợp lệ")
    void testErrorCodeProperties() {
        for (ErrorCode code : ErrorCode.values()) {
            assertThat(code.defaultMessage()).isNotBlank();
            assertThat(code.vietnameseMessage()).isNotBlank();
            assertThat(code.status()).isNotNull();
            assertThat(code.category()).isNotNull();
        }
    }

    @Test
    @DisplayName("fromName tìm kiếm chính xác theo mã lỗi không phân biệt hoa thường")
    void testFromName() {
        assertThat(ErrorCode.fromName("UNAUTHENTICATED")).contains(ErrorCode.UNAUTHENTICATED);
        assertThat(ErrorCode.fromName("unauthenticated")).contains(ErrorCode.UNAUTHENTICATED);
        assertThat(ErrorCode.fromName("NON_EXISTING_CODE")).isEmpty();
        assertThat(ErrorCode.fromName("")).isEmpty();
        assertThat(ErrorCode.fromName(null)).isEmpty();
    }
}
