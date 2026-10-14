package lms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lms.auth.Role;
import lms.common.RandomTokens;
import lms.user.User;
import lms.user.UserRepository;

/**
 * Lần chạy đầu: tạo admin nếu DB chưa có admin nào.
 * Profile dev: tạo thêm 1 sinh viên demo (student01 / student12345).
 */
@Component
public class BootstrapDataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BootstrapDataInitializer.class);

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AppProperties.Bootstrap cfg;

    public BootstrapDataInitializer(UserRepository users, PasswordEncoder encoder, AppProperties props) {
        this.users = users;
        this.encoder = encoder;
        this.cfg = props.bootstrap();
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!users.existsByRole(Role.ADMIN)) {
            String password = cfg.adminPassword();
            boolean generated = password == null || password.isBlank();
            if (generated) {
                password = RandomTokens.urlSafe(12);
            }
            users.save(new User(cfg.adminUsername(), cfg.adminFullName(), encoder.encode(password), Role.ADMIN));
            if (generated) {
                log.warn("Created admin '{}' with generated password: {}  (shown once, change it after login)",
                        cfg.adminUsername(), password);
            } else {
                log.info("Created admin '{}' from configuration", cfg.adminUsername());
            }
        }

        if (cfg.createDemoStudent() && users.findByUsername("student01").isEmpty()) {
            users.save(new User("student01", "Demo Student", encoder.encode("student12345"), Role.STUDENT));
            log.info("Created demo student 'student01' (dev profile only)");
        }
    }
}
