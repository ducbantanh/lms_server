package lms.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationUtilsTest {

    @Test
    @DisplayName("isValidStudentCode kiểm tra mã sinh viên chuẩn")
    void testStudentCode() {
        assertThat(ValidationUtils.isValidStudentCode("B21DCCN123")).isTrue();
        assertThat(ValidationUtils.isValidStudentCode("SV_2026")).isTrue();
        assertThat(ValidationUtils.isValidStudentCode("")).isFalse();
        assertThat(ValidationUtils.isValidStudentCode("abc")).isFalse(); // Quá ngắn (< 4)
        assertThat(ValidationUtils.isValidStudentCode(null)).isFalse();
    }

    @Test
    @DisplayName("isValidEmail kiểm tra định dạng email chuẩn")
    void testEmail() {
        assertThat(ValidationUtils.isValidEmail("ducnghia@ptit.edu.vn")).isTrue();
        assertThat(ValidationUtils.isValidEmail("user.name+tag@example.com")).isTrue();
        assertThat(ValidationUtils.isValidEmail("invalid-email")).isFalse();
        assertThat(ValidationUtils.isValidEmail("@missing-user.com")).isFalse();
        assertThat(ValidationUtils.isValidEmail(null)).isFalse();
    }

    @Test
    @DisplayName("isValidVietnamesePhone kiểm tra số điện thoại di động Việt Nam")
    void testPhone() {
        assertThat(ValidationUtils.isValidVietnamesePhone("0912345678")).isTrue();
        assertThat(ValidationUtils.isValidVietnamesePhone("+84987654321")).isTrue();
        assertThat(ValidationUtils.isValidVietnamesePhone("0323456789")).isTrue();
        assertThat(ValidationUtils.isValidVietnamesePhone("0123456789")).isFalse(); // Đầu 01 cũ
        assertThat(ValidationUtils.isValidVietnamesePhone("091234567")).isFalse(); // Thiếu số
        assertThat(ValidationUtils.isValidVietnamesePhone(null)).isFalse();
    }

    @Test
    @DisplayName("isValidIpv4OrCidr kiểm tra địa chỉ IP và dải mạng CIDR")
    void testIpv4OrCidr() {
        assertThat(ValidationUtils.isValidIpv4OrCidr("192.168.1.1")).isTrue();
        assertThat(ValidationUtils.isValidIpv4OrCidr("10.0.0.0/8")).isTrue();
        assertThat(ValidationUtils.isValidIpv4OrCidr("172.16.0.0/16")).isTrue();
        assertThat(ValidationUtils.isValidIpv4OrCidr("999.999.999.999")).isFalse();
        assertThat(ValidationUtils.isValidIpv4OrCidr("invalid-ip")).isFalse();
        assertThat(ValidationUtils.isValidIpv4OrCidr(null)).isFalse();
    }

    @Test
    @DisplayName("calculatePasswordStrength tính điểm độ mạnh mật khẩu")
    void testPasswordStrength() {
        assertThat(ValidationUtils.calculatePasswordStrength("123456")).isLessThan(30);
        assertThat(ValidationUtils.calculatePasswordStrength("Password123!")).isGreaterThanOrEqualTo(80);
        assertThat(ValidationUtils.calculatePasswordStrength("")).isEqualTo(0);
        assertThat(ValidationUtils.calculatePasswordStrength(null)).isEqualTo(0);
    }
}
