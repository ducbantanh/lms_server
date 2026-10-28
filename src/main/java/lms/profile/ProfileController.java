package lms.profile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.auth.AuthPrincipal;
import lms.common.ApiResponse;
import lms.common.RequestInfo;
import lms.profile.ProfileService.ProfileView;
import lms.profile.ProfileService.UpdateRequest;

/** Trang hồ sơ (/profile/, /teacher/profile/): mọi tài khoản đã đăng nhập xem và sửa hồ sơ của chính mình. */
@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<ProfileView> get(AuthPrincipal me) {
        return ApiResponse.ok(service.get(me));
    }

    @PutMapping
    public ApiResponse<ProfileView> update(@Valid @RequestBody UpdateRequest body, AuthPrincipal me,
            HttpServletRequest req) {
        return ApiResponse.ok(service.update(body, me, RequestInfo.clientIp(req)));
    }
}
