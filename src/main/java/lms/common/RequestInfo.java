package lms.common;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Lấy thông tin client từ request. server.forward-headers-strategy=native (mặc định) cho getRemoteAddr()
 * trả IP máy người dùng từ X-Forwarded-For khi request đi qua gateway/proxy chạy cùng máy
 * (server.tomcat.remoteip.internal-proxies); request từ máy khác thì header này bị bỏ qua.
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
