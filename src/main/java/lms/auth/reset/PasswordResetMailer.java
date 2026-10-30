package lms.auth.reset;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lms.common.AppException;
import lms.common.ErrorCode;
import lms.config.AppProperties;

/** Gửi OTP bằng SMTP cấu hình trong app.mail; không log mã OTP hoặc địa chỉ người nhận. */
@Service
public class PasswordResetMailer {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetMailer.class);

    private final JavaMailSenderImpl sender;
    private final String from;

    public PasswordResetMailer(AppProperties props) {
        AppProperties.Mail config = props.mail();
        this.from = firstText(config.from(), config.username());
        if (!StringUtils.hasText(config.host()) || !StringUtils.hasText(from)) {
            this.sender = null;
            return;
        }
        JavaMailSenderImpl mail = new JavaMailSenderImpl();
        mail.setHost(config.host().strip());
        mail.setPort(config.port());
        mail.setDefaultEncoding(StandardCharsets.UTF_8.name());
        if (StringUtils.hasText(config.username())) {
            mail.setUsername(config.username().strip());
            mail.setPassword(config.password());
        }
        Properties javaMail = mail.getJavaMailProperties();
        javaMail.put("mail.smtp.auth", Boolean.toString(StringUtils.hasText(config.username())));
        javaMail.put("mail.smtp.starttls.enable", Boolean.toString(config.startTls()));
        javaMail.put("mail.smtp.ssl.enable", Boolean.toString(config.ssl()));
        javaMail.put("mail.smtp.connectiontimeout", "5000");
        javaMail.put("mail.smtp.timeout", "5000");
        javaMail.put("mail.smtp.writetimeout", "5000");
        this.sender = mail;
    }

    public void requireConfigured() {
        if (sender == null) {
            throw new AppException(ErrorCode.EMAIL_UNAVAILABLE);
        }
    }

    public void sendOtp(String recipient, String fullName, String code, Duration ttl) {
        requireConfigured();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(recipient);
        message.setSubject("Mã OTP khôi phục mật khẩu F LMS");
        long minutes = Math.max(1, ttl.toMinutes());
        message.setText("Xin chào " + fullName + ",\n\n"
                + "Mã OTP để đặt lại mật khẩu F LMS của bạn là: " + code + "\n\n"
                + "Mã có hiệu lực trong " + minutes + " phút và chỉ dùng được một lần. "
                + "Không chia sẻ mã này với bất kỳ ai.\n\n"
                + "Nếu bạn không yêu cầu đặt lại mật khẩu, hãy bỏ qua email này.");
        try {
            sender.send(message);
        } catch (MailException ex) {
            log.error("Could not send password reset email: {}", ex.getClass().getSimpleName());
            throw new AppException(ErrorCode.EMAIL_UNAVAILABLE);
        }
    }

    private static String firstText(String first, String second) {
        if (StringUtils.hasText(first)) {
            return first.strip();
        }
        return StringUtils.hasText(second) ? second.strip() : null;
    }
}
