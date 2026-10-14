package lms.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Yêu cầu user có TẤT CẢ permission liệt kê. Đặt trên class hoặc method
 * (method được kiểm tra thêm, không thay thế class).
 *
 * <pre>@RequirePermission(Permissions.USERS_MANAGE)</pre>
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    String[] value();
}
