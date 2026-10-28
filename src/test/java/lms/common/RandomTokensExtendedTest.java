package lms.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RandomTokensExtendedTest {

    @Test
    @DisplayName("numericPin sinh mã PIN chỉ chứa chữ số với độ dài yêu cầu")
    void testNumericPin() {
        String pin6 = RandomTokens.numericPin(6);
        assertThat(pin6).hasSize(6);
        assertThat(pin6).matches("^[0-9]{6}$");

        String pin4 = RandomTokens.numericPin(4);
        assertThat(pin4).hasSize(4);
        assertThat(pin4).matches("^[0-9]{4}$");
    }

    @Test
    @DisplayName("alphanumeric và alphanumericUpper sinh chuỗi ngẫu nhiên chuẩn")
    void testAlphanumeric() {
        String code = RandomTokens.alphanumeric(10);
        assertThat(code).hasSize(10);
        assertThat(code).matches("^[a-zA-Z0-9]{10}$");

        String upperCode = RandomTokens.alphanumericUpper(8);
        assertThat(upperCode).hasSize(8);
        assertThat(upperCode).matches("^[A-Z0-9]{8}$");
    }

    @Test
    @DisplayName("uuidString sinh UUID V4 hợp lệ")
    void testUuidString() {
        String uuid = RandomTokens.uuidString();
        assertThat(uuid).matches("^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$");
    }

    @Test
    @DisplayName("hmacSha256Hex tính toán chữ ký HMAC xác định")
    void testHmacSha256() {
        String hmac1 = RandomTokens.hmacSha256Hex("test data", "secret_key_123");
        String hmac2 = RandomTokens.hmacSha256Hex("test data", "secret_key_123");
        String hmacDiff = RandomTokens.hmacSha256Hex("other data", "secret_key_123");

        assertThat(hmac1).isEqualTo(hmac2);
        assertThat(hmac1).hasSize(64);
        assertThat(hmac1).isNotEqualTo(hmacDiff);
    }
}
