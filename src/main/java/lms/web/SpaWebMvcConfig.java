package lms.web;

import java.io.IOException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * Cấu hình phục vụ giao diện xuất tĩnh của Next.js từ classpath:/static/.
 * Hỗ trợ tự động:
 * 1. File tĩnh trực tiếp (_next/..., assets, favicon, fonts).
 * 2. Đường dẫn trang có hoặc không có dấu gạch chéo cuối (/login -> static/login/index.html).
 * 3. Fallback về index.html cho các route phía client.
 */
@Configuration
public class SpaWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        if (resourcePath.startsWith("api") || resourcePath.startsWith("ws")) {
                            return null;
                        }

                        // 1. Tìm file chính xác (CSS, JS, font, ảnh, html)
                        Resource requested = location.createRelative(resourcePath);
                        if (requested.exists() && requested.isReadable()) {
                            return requested;
                        }

                        // 2. Thử tìm index.html trong thư mục con (/login -> login/index.html)
                        String trimmed = resourcePath.endsWith("/")
                                ? resourcePath.substring(0, resourcePath.length() - 1)
                                : resourcePath;
                        if (!trimmed.isEmpty()) {
                            Resource subIndex = location.createRelative(trimmed + "/index.html");
                            if (subIndex.exists() && subIndex.isReadable()) {
                                return subIndex;
                            }
                        }

                        // 3. Fallback về root index.html nếu không có đuôi file (không phải .js, .css, .png...)
                        if (!resourcePath.contains(".")) {
                            Resource rootIndex = location.createRelative("index.html");
                            if (rootIndex.exists() && rootIndex.isReadable()) {
                                return rootIndex;
                            }
                        }

                        return null;
                    }
                });
    }
}
