package lms.security;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Tiện ích trích xuất và phân tích địa chỉ IP thật của người dùng (Real Client IP Resolver).
 * Hỗ trợ các proxy trung gian như Nginx, Cloudflare, Traefik, Docker Gateway.
 */
public final class IpAddressResolver {

    private static final List<String> IP_HEADERS = List.of(
            "CF-Connecting-IP",
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
    );

    private IpAddressResolver() {
    }

    /**
     * Lấy địa chỉ IP thật của client từ HttpServletRequest.
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "127.0.0.1";
        }

        for (String header : IP_HEADERS) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip.trim())) {
                // X-Forwarded-For có thể chứa nhiều IP cách nhau bởi dấu phẩy, IP đầu tiên là của client
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return normalizeIp(ip);
            }
        }

        return normalizeIp(request.getRemoteAddr());
    }

    /**
     * Chuẩn hóa địa chỉ IP: IPv6 localhost ::1 -> 127.0.0.1.
     */
    public static String normalizeIp(String ip) {
        if (ip == null || ip.isBlank() || "0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            return "127.0.0.1";
        }
        return ip.trim();
    }

    /**
     * Kiểm tra địa chỉ IP có thuộc mạng nội bộ (LAN / Private IP) hay không.
     * RFC 1918: 10.0.0.0/8, 172.16.0.0/12, 192.168.0.0/16, cùng với 127.0.0.1.
     */
    public static boolean isPrivateOrLocalIp(String ip) {
        String normalized = normalizeIp(ip);
        if ("127.0.0.1".equals(normalized) || "localhost".equalsIgnoreCase(normalized)) {
            return true;
        }

        try {
            InetAddress address = InetAddress.getByName(normalized);
            return address.isSiteLocalAddress() || address.isLoopbackAddress() || address.isLinkLocalAddress();
        } catch (UnknownHostException e) {
            return false;
        }
    }
}
