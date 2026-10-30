package lms.auth.reset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lms.audit.AuditService;
import lms.auth.Role;
import lms.auth.session.SessionService;
import lms.config.AppProperties;
import lms.realtime.RealtimeHub;
import lms.user.User;
import lms.user.UserRepository;

class PasswordResetServiceTest {

    private final UserRepository users = mock(UserRepository.class);
    private final PasswordResetOtpRepository otps = mock(PasswordResetOtpRepository.class);
    private final PasswordResetMailer mailer = mock(PasswordResetMailer.class);
    private final PasswordEncoder encoder = new BCryptPasswordEncoder(4);
    private final SessionService sessions = mock(SessionService.class);
    private final RealtimeHub hub = mock(RealtimeHub.class);
    private final AuditService audit = mock(AuditService.class);
    private PasswordResetService service;

    @BeforeEach
    void setup() {
        AppProperties props = mock(AppProperties.class);
        AppProperties.PasswordReset reset = mock(AppProperties.PasswordReset.class);
        AppProperties.Security security = mock(AppProperties.Security.class);
        when(props.passwordReset()).thenReturn(reset);
        when(props.security()).thenReturn(security);
        when(reset.otpTtl()).thenReturn(Duration.ofMinutes(10));
        when(reset.resendCooldown()).thenReturn(Duration.ofSeconds(60));
        when(reset.maxAttempts()).thenReturn(5);
        when(security.passwordMinLength()).thenReturn(8);
        service = new PasswordResetService(users, otps, mailer, encoder, sessions, hub, audit, props);
    }

    @Test
    void requestStoresHashAndSendsPlainCodeOnlyByEmail() {
        User user = user(7L, "student01", "old-password", "student@example.com");
        when(users.findByUsername("student01")).thenReturn(Optional.of(user));
        when(otps.findForUpdate(7L)).thenReturn(Optional.empty());

        PasswordResetService.Requested result = service.requestCode(" student01 ", "127.0.0.1");

        assertEquals(600, result.expiresInSeconds());
        assertEquals(60, result.resendAfterSeconds());
        ArgumentCaptor<PasswordResetOtp> saved = ArgumentCaptor.forClass(PasswordResetOtp.class);
        ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);
        verify(otps).save(saved.capture());
        verify(mailer).sendOtp(eq("student@example.com"), eq("Student One"), code.capture(),
                eq(Duration.ofMinutes(10)));
        assertTrue(code.getValue().matches("\\d{6}"));
        assertTrue(encoder.matches(code.getValue(), saved.getValue().getCodeHash()));
    }

    @Test
    void correctCodeChangesPasswordAndRevokesSessions() {
        User user = user(9L, "student02", "old-password", "student2@example.com");
        user.setMustChangePassword(true);
        PasswordResetOtp otp = new PasswordResetOtp(9L, encoder.encode("123456"), Instant.now(),
                Instant.now().plusSeconds(600));
        when(users.findByUsername("student02")).thenReturn(Optional.of(user));
        when(otps.findForUpdate(9L)).thenReturn(Optional.of(otp));
        when(sessions.revokeAllForUser(9L, SessionService.REASON_PASSWORD_CHANGED)).thenReturn(2);

        PasswordResetService.ResetOutcome result = service.resetPassword("student02", "123456", "new-password",
                "127.0.0.1");

        assertEquals(PasswordResetService.ResetOutcome.SUCCESS, result);
        assertTrue(encoder.matches("new-password", user.getPasswordHash()));
        assertFalse(user.isMustChangePassword());
        verify(otps).delete(otp);
        verify(sessions).revokeAllForUser(9L, SessionService.REASON_PASSWORD_CHANGED);
        verify(hub).disconnectUser(9L, SessionService.REASON_PASSWORD_CHANGED);
    }

    @Test
    void wrongCodeCountsAttempts() {
        User user = user(11L, "student03", "old-password", "student3@example.com");
        PasswordResetOtp otp = new PasswordResetOtp(11L, encoder.encode("123456"), Instant.now(),
                Instant.now().plusSeconds(600));
        when(users.findByUsername("student03")).thenReturn(Optional.of(user));
        when(otps.findForUpdate(11L)).thenReturn(Optional.of(otp));

        PasswordResetService.ResetOutcome result = service.resetPassword("student03", "654321", "new-password",
                "127.0.0.1");

        assertEquals(PasswordResetService.ResetOutcome.INVALID_CODE, result);
        assertEquals(1, otp.getAttempts());
    }

    private User user(long id, String username, String password, String email) {
        User user = new User(username, "Student One", encoder.encode(password), Role.STUDENT);
        user.setEmail(email);
        try {
            Field field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, id);
            return user;
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
