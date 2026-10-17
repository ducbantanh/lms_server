package lms.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lms.auth.AuthInterceptor;
import lms.auth.AuthPrincipalArgumentResolver;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final AuthPrincipalArgumentResolver principalResolver;

    public WebMvcConfig(AuthInterceptor authInterceptor, AuthPrincipalArgumentResolver principalResolver) {
        this.authInterceptor = authInterceptor;
        this.principalResolver = principalResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor).addPathPatterns("/api/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(principalResolver);
    }

    // Spring chỉ tự phục vụ index.html ở "/", nên các thư mục con của frontend cần map tay
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        for (String dir : List.of("admin", "teacher", "student")) {
            registry.addRedirectViewController("/" + dir, "/" + dir + "/");
            registry.addViewController("/" + dir + "/").setViewName("forward:/" + dir + "/index.html");
        }
    }
}
