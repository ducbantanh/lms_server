package lms.user.importing;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lms.audit.AuditService;
import lms.auth.AuthPrincipal;
import lms.auth.Role;
import lms.common.AppException;
import lms.common.ErrorCode;
import lms.config.AppProperties;
import lms.user.User;
import lms.user.UserRepository;
import lms.user.importing.ImportReport.CreatedRow;
import lms.user.importing.StudentImportParser.LineIssue;
import lms.user.importing.StudentImportParser.Row;

/**
 * Import sinh viên: username = mã SV, mật khẩu ban đầu = ngày sinh ddmmyyyy.
 * Không ghi đè tài khoản đã có. Tài khoản mới bị bắt đổi mật khẩu ở lần đăng nhập đầu.
 */
@Service
public class StudentImportService {

    public static final String ALREADY_EXISTS = "ALREADY_EXISTS";

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuditService audit;
    private final AppProperties.StudentImport cfg;

    public StudentImportService(UserRepository users, PasswordEncoder encoder, AuditService audit,
            AppProperties props) {
        this.users = users;
        this.encoder = encoder;
        this.audit = audit;
        this.cfg = props.studentImport();
    }

    @Transactional
    public ImportReport importStudents(String content, AuthPrincipal actor, String ip) {
        if (content == null || content.isBlank()) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Content is empty");
        }
        if (content.getBytes(StandardCharsets.UTF_8).length > cfg.maxBytes()) {
            throw new AppException(ErrorCode.PAYLOAD_TOO_LARGE);
        }

        StudentImportParser.Result parsed;
        try {
            parsed = new StudentImportParser(cfg.maxLines()).parse(content);
        } catch (StudentImportParser.TooManyLinesException e) {
            throw new AppException(ErrorCode.PAYLOAD_TOO_LARGE, e.getMessage());
        }

        List<LineIssue> duplicates = new ArrayList<>(parsed.duplicates());
        List<Row> toCreate = new ArrayList<>();

        if (!parsed.rows().isEmpty()) {
            Set<String> existing = new HashSet<>();
            for (String u : users.findExistingUsernames(parsed.rows().stream().map(Row::studentId).toList())) {
                existing.add(u.toUpperCase(Locale.ROOT));
            }
            for (Row r : parsed.rows()) {
                if (existing.contains(r.studentId().toUpperCase(Locale.ROOT))) {
                    duplicates.add(new LineIssue(r.line(), r.studentId(), ALREADY_EXISTS));
                } else {
                    toCreate.add(r);
                }
            }
        }

        // BCrypt chậm có chủ đích -> băm song song cho nhanh khi import nhiều dòng
        List<User> newUsers = toCreate.parallelStream().map(r -> {
            User u = new User(r.studentId(), r.fullName(), encoder.encode(r.rawBirthDate()), Role.STUDENT);
            u.setBirthDate(r.birthDate());
            u.setMustChangePassword(true);
            return u;
        }).toList();
        users.saveAll(newUsers);

        List<CreatedRow> created = toCreate.stream()
                .map(r -> new CreatedRow(r.line(), r.studentId(), r.fullName()))
                .toList();

        audit.record(actor.userId(), AuditService.USERS_IMPORTED, null,
                "created=" + created.size() + ", duplicates=" + duplicates.size()
                        + ", errors=" + parsed.errors().size(),
                ip);

        return new ImportReport(parsed.dataLines(), created, duplicates, parsed.errors());
    }
}
