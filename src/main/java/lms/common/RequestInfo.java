package lms.common;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Lấy thông tin client từ request. Khi chạy sau proxy, bật
 * server.forward-headers-strategy=native để getRemoteAddr() trả đúng IP thật.
 */
public final class RequestInfo {

    private RequestInfo() {
    }

    public static String clientIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    public static String userAgent(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        if (ua == null) {
            return null;
        }
        return ua.length() > 255 ? ua.substring(0, 255) : ua;
    }
}
