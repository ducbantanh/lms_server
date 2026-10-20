package lms.user.importing;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
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
import lms.user.importing.AccountImportParser.LineIssue;
import lms.user.importing.AccountImportParser.Row;
import lms.user.importing.ImportReport.CreatedRow;

/**
 * Tạo hàng loạt tài khoản sinh viên hoặc giảng viên: username = mã, mật khẩu ban đầu = ngày sinh ddmmyyyy.
 * Không ghi đè tài khoản đã có. Tài khoản mới bị bắt đổi mật khẩu ở lần đăng nhập đầu.
 * Giới hạn số dòng/kích thước dùng chung cấu hình app.student-import.
 */
@Service
public class AccountImportService {

    public static final String ALREADY_EXISTS = "ALREADY_EXISTS";

    /** Excel tiếng Việt lưu "CSV" (không phải "CSV UTF-8") bằng code page Windows-1258. */
    private static final Charset WINDOWS_VIETNAMESE = Charset.forName("windows-1258");

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuditService audit;
    private final AppProperties.StudentImport cfg;

    public AccountImportService(UserRepository users, PasswordEncoder encoder, AuditService audit,
            AppProperties props) {
        this.users = users;
        this.encoder = encoder;
        this.audit = audit;
        this.cfg = props.studentImport();
    }

    /** Chỉ tạo được sinh viên hoặc giảng viên; admin tạo bằng cách khác. */
    public static Role importableRole(Integer code) {
        Role role = code == null || code == Role.STUDENT.code() ? Role.STUDENT
                : code == Role.TEACHER.code() ? Role.TEACHER : null;
        if (role == null) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Only student or teacher accounts can be imported");
        }
        return role;
    }

    /** Nội dung file: UTF-8 nếu đúng UTF-8, ngược lại coi là Windows-1258. */
    public static String decode(byte[] data) {
        try {
            return StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(data)).toString();
        } catch (CharacterCodingException e) {
            return new String(data, WINDOWS_VIETNAMESE);
        }
    }

    @Transactional
    public ImportReport importAccounts(String content, Role role, AuthPrincipal actor, String ip) {
        if (content == null || content.isBlank()) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Content is empty");
        }
        if (content.getBytes(StandardCharsets.UTF_8).length > cfg.maxBytes()) {
            throw new AppException(ErrorCode.PAYLOAD_TOO_LARGE);
        }

        AccountImportParser.Result parsed;
        try {
            parsed = new AccountImportParser(cfg.maxLines()).parse(content);
        } catch (AccountImportParser.TooManyLinesException e) {
            throw new AppException(ErrorCode.PAYLOAD_TOO_LARGE, e.getMessage());
        }

        List<LineIssue> duplicates = new ArrayList<>(parsed.duplicates());
        List<Row> toCreate = new ArrayList<>();

        if (!parsed.rows().isEmpty()) {
            Set<String> existing = new HashSet<>();
            for (String u : users.findExistingUsernames(parsed.rows().stream().map(Row::username).toList())) {
                existing.add(u.toUpperCase(Locale.ROOT));
            }
            for (Row r : parsed.rows()) {
                if (existing.contains(r.username().toUpperCase(Locale.ROOT))) {
                    duplicates.add(new LineIssue(r.line(), r.username(), ALREADY_EXISTS));
                } else {
                    toCreate.add(r);
                }
            }
        }

        // BCrypt chậm có chủ đích -> băm song song cho nhanh khi import nhiều dòng
        List<User> newUsers = toCreate.parallelStream().map(r -> {
            User u = new User(r.username(), r.fullName(), encoder.encode(r.initialPassword()), role);
            u.setBirthDate(r.birthDate());
            u.setMustChangePassword(true);
            return u;
        }).toList();
        users.saveAll(newUsers);

        List<CreatedRow> created = toCreate.stream()
                .map(r -> new CreatedRow(r.line(), r.username(), r.fullName()))
                .toList();

        audit.record(actor.userId(), AuditService.USERS_IMPORTED, null,
                "role=" + role.name() + ", created=" + created.size() + ", duplicates=" + duplicates.size()
                        + ", errors=" + parsed.errors().size(),
                ip);

        return new ImportReport(parsed.dataLines(), created, duplicates, parsed.errors());
    }
}
