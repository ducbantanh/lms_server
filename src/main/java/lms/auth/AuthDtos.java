package lms.auth;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** DTO cho các endpoint /api/v1/auth. */
public final class AuthDtos {

    private AuthDtos() {
    }

    public record LoginRequest(
            @NotBlank @Size(max = 32) String username,
            @NotBlank @Size(max = 128) String password) {
    }

    public record ChangePasswordRequest(
            @NotBlank @Size(max = 128) String currentPassword,
            @NotBlank @Size(max = 128) String newPassword) {
    }

    public record PasswordResetCodeRequest(
            @NotBlank @Size(max = 32) String username) {
    }

    public record PasswordResetRequest(
            @NotBlank @Size(max = 32) String username,
            @NotBlank @Pattern(regexp = "\\d{6}") String code,
            @NotBlank @Size(max = 128) String newPassword) {
    }

    public record MeView(
            String id,
            String username,
            String fullName,
            int role,
            String roleName,
            boolean mustChangePassword,
            Set<String> permissions) {
    }

    /**
     * Trả về sau login và GET /me. Frontend giữ csrfToken trong bộ nhớ (biến JS),
     * gửi lại qua header X-CSRF-Token cho mọi POST/PUT/PATCH/DELETE.
     */
    public record SessionView(MeView user, String csrfToken) {
    }
}
