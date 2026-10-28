package lms.security;

import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor theo dõi thời gian thực thi (Latency & Execution Timing) của các API request.
 * Gắn mã định danh tương quan X-Request-ID và đo lường thời gian xử lý (X-Response-Time-Millis).
 */
@Component
public class RequestExecutionTimingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RequestExecutionTimingInterceptor.class);
    private static final String START_TIME_ATTR = "req_start_time_millis";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());

        String requestId = request.getHeader("X-Request-ID");
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        response.setHeader("X-Request-ID", requestId);

        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {

        Long startTime = (Long) request.getAttribute(START_TIME_ATTR);
        if (startTime != null) {
            long durationMillis = System.currentTimeMillis() - startTime;
            response.setHeader("X-Response-Time-Millis", String.valueOf(durationMillis));

            if (durationMillis > 1000) {
                log.warn("Cảnh báo hiệu năng: API chậm {} {} hoàn thành sau {} ms (Status: {})",
                        request.getMethod(), request.getRequestURI(), durationMillis, response.getStatus());
            } else {
                log.debug("API completed {} {} in {} ms (Status: {})",
                        request.getMethod(), request.getRequestURI(), durationMillis, response.getStatus());
            }
        }
    }
}
