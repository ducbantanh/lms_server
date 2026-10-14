package lms.student;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.auth.AuthPrincipal;
import lms.auth.Permissions;
import lms.auth.RequirePermission;
import lms.common.ApiResponse;

/**
 * API cho dashboard sinh viên. Giai đoạn 1 mới có profile; courses để rỗng,
 * giai đoạn 2 (module courses) sẽ điền dữ liệu thật.
 */
@RestController
@RequestMapping("/api/v1/student")
@RequirePermission(Permissions.COURSES_VIEW)
public class StudentController {

    public record Profile(String id, String username, String fullName) {
    }

    public record Dashboard(Profile profile, List<Object> courses) {
    }

    @GetMapping("/dashboard")
    public ApiResponse<Dashboard> dashboard(AuthPrincipal me) {
        Profile profile = new Profile(me.publicId(), me.username(), me.fullName());
        return ApiResponse.ok(new Dashboard(profile, List.of()));
    }
}
