package lms.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lms.auth.AuthInterceptor;
import lms.auth.AuthPrincipalArgumentResolver;
import lms.proctor.ProctorInterceptor;
import lms.security.RequestExecutionTimingInterceptor;

/**
 * Cấu hình Spring Web MVC, đăng ký các bộ giải mã tham số (Argument Resolvers),
 * Interceptors kiểm soát bảo mật, giám sát phòng thi và hiệu năng hệ thống.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final ProctorInterceptor proctorInterceptor;
    private final AuthPrincipalArgumentResolver principalResolver;
    private final RequestExecutionTimingInterceptor timingInterceptor;

    public WebMvcConfig(
            AuthInterceptor authInterceptor,
            ProctorInterceptor proctorInterceptor,
            AuthPrincipalArgumentResolver principalResolver,
            RequestExecutionTimingInterceptor timingInterceptor) {
        this.authInterceptor = authInterceptor;
        this.proctorInterceptor = proctorInterceptor;
        this.principalResolver = principalResolver;
        this.timingInterceptor = timingInterceptor;
    }

    /**
     * Thứ tự đăng ký là thứ tự chạy:
     * 1. timingInterceptor: Ghi nhận thời điểm bắt đầu và sinh X-Request-ID.
     * 2. authInterceptor: Xác thực phiên đăng nhập và gắn AuthPrincipal.
     * 3. proctorInterceptor: Kiểm tra giám sát phòng thi dựa trên AuthPrincipal.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(timingInterceptor).addPathPatterns("/api/**");
        registry.addInterceptor(authInterceptor).addPathPatterns("/api/**");
        registry.addInterceptor(proctorInterceptor).addPathPatterns("/api/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(principalResolver);
    }

    /**
     * Spring chỉ tự phục vụ index.html ở "/", nên các thư mục con của frontend tĩnh cần map điều hướng thủ công.
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        for (String dir : List.of("admin", "teacher", "student")) {
            registry.addRedirectViewController("/" + dir, "/" + dir + "/");
            registry.addViewController("/" + dir + "/").setViewName("forward:/" + dir + "/index.html");
        }
    }
}
