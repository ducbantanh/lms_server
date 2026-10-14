package lms.realtime;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import lms.config.AppProperties;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final RealtimeWebSocketHandler handler;
    private final SessionHandshakeInterceptor handshakeInterceptor;
    private final AppProperties.Ws cfg;

    public WebSocketConfig(RealtimeWebSocketHandler handler, SessionHandshakeInterceptor handshakeInterceptor,
            AppProperties props) {
        this.handler = handler;
        this.handshakeInterceptor = handshakeInterceptor;
        this.cfg = props.ws();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        WebSocketHandlerRegistration reg = registry.addHandler(handler, cfg.path())
                .addInterceptors(handshakeInterceptor);
        // Mặc định Spring chỉ cho same-origin. Chỉ mở thêm origin khi cấu hình rõ ràng.
        List<String> origins = cfg.allowedOrigins().stream().filter(o -> o != null && !o.isBlank()).toList();
        if (!origins.isEmpty()) {
            reg.setAllowedOrigins(origins.toArray(String[]::new));
        }
    }

    /** Giới hạn kích thước frame và thời gian rảnh của socket. */
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8 * 1024);
        container.setMaxBinaryMessageBufferSize(1024);
        container.setMaxSessionIdleTimeout(90_000L);
        return container;
    }
}
