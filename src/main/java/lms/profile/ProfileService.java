package lms.profile;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lms.audit.AuditService;
import lms.auth.AuthPrincipal;
import lms.common.AppException;
import lms.common.ErrorCode;
import lms.user.User;
import lms.user.UserRepository;

/**
 * Hồ sơ cá nhân của người đang đăng nhập (sinh viên, giảng viên, admin).
 * Chỉ tự sửa được email, số điện thoại, lời giới thiệu; họ tên, mã đăng nhập, ngày sinh do admin quản lý.
 */
@Service
public class ProfileService {

    /** Khớp lms_client/lib/profile.ts; id là public_id. */
    public record ProfileView(String id, String username, String fullName, int role, String roleName,
            LocalDate birthDate, String email, String phone, String bio, Instant createdAt, Instant lastLoginAt) {

        static ProfileView of(User u) {
            return new ProfileView(u.getPublicId(), u.getUsername(), u.getFullName(), u.getRole().code(),
                    u.getRole().name(), u.getBirthDate(), u.getEmail(), u.getPhone(), u.getBio(), u.getCreatedAt(),
                    u.getLastLoginAt());
        }
    }

    /** Chuỗi trống (hoặc chỉ khoảng trắng) = xóa thông tin đó. */
    public record UpdateRequest(
            @Email @Size(max = 100) String email,
            @Pattern(regexp = "^\\s*$|^\\+?[0-9][0-9 .\\-]{6,18}$") @Size(max = 20) String phone,
            @Size(max = 500) String bio) {
    }

    private final UserRepository users;
    private final AuditService audit;

    public ProfileService(UserRepository users, AuditService audit) {
        this.users = users;
        this.audit = audit;
    }

    @Transactional(readOnly = true)
    public ProfileView get(AuthPrincipal me) {
        return ProfileView.of(load(me));
    }

    @Transactional
    public ProfileView update(UpdateRequest body, AuthPrincipal me, String ip) {
        User user = load(me);
        String email = clean(body.email());
        String phone = clean(body.phone());
        String bio = clean(body.bio());
        // Chỉ ghi tên trường đã đổi vào audit, không ghi nội dung liên hệ
        List<String> changed = new ArrayList<>();
        if (!Objects.equals(email, user.getEmail())) {
            changed.add("email");
        }
        if (!Objects.equals(phone, user.getPhone())) {
            changed.add("phone");
        }
        if (!Objects.equals(bio, user.getBio())) {
            changed.add("bio");
        }
        if (changed.isEmpty()) {
            return ProfileView.of(user);
        }
        user.setEmail(email);
        user.setPhone(phone);
        user.setBio(bio);
        audit.record(user.getId(), AuditService.PROFILE_UPDATED, user.getUsername(), String.join(", ", changed), ip);
        return ProfileView.of(user);
    }

    private User load(AuthPrincipal me) {
        return users.findById(me.userId()).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
    }

    static String clean(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.strip();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
