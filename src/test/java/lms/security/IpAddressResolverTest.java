package lms.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IpAddressResolverTest {

    @Test
    @DisplayName("normalizeIp chuẩn hóa IPv6 localhost về 127.0.0.1")
    void testNormalizeIp() {
        assertThat(IpAddressResolver.normalizeIp("0:0:0:0:0:0:0:1")).isEqualTo("127.0.0.1");
        assertThat(IpAddressResolver.normalizeIp("::1")).isEqualTo("127.0.0.1");
        assertThat(IpAddressResolver.normalizeIp("192.168.1.5")).isEqualTo("192.168.1.5");
        assertThat(IpAddressResolver.normalizeIp(null)).isEqualTo("127.0.0.1");
    }

    @Test
    @DisplayName("isPrivateOrLocalIp phát hiện đúng các dải IP nội bộ và IP public")
    void testPrivateIps() {
        assertThat(IpAddressResolver.isPrivateOrLocalIp("127.0.0.1")).isTrue();
        assertThat(IpAddressResolver.isPrivateOrLocalIp("192.168.1.1")).isTrue();
        assertThat(IpAddressResolver.isPrivateOrLocalIp("10.0.0.1")).isTrue();
        assertThat(IpAddressResolver.isPrivateOrLocalIp("172.16.0.1")).isTrue();

        assertThat(IpAddressResolver.isPrivateOrLocalIp("8.8.8.8")).isFalse();
        assertThat(IpAddressResolver.isPrivateOrLocalIp("1.1.1.1")).isFalse();
    }
}
