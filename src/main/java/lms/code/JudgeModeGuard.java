package lms.code;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import lms.config.AppProperties;

/**
 * Server web chỉ nhận app.judge.mode embedded / external. "worker" đặt trong .env hoặc application.yml thì
 * LmsServerApplication không thấy kịp (chọn lớp khởi động trước khi đọc cấu hình) và process sẽ chạy thành server
 * web: dừng ngay với hướng dẫn thay vì âm thầm chạy sai. JudgeWorkerApplication không nạp lớp này.
 */
@Component
public class JudgeModeGuard {

    private static final Logger log = LoggerFactory.getLogger(JudgeModeGuard.class);
    private static final Set<String> WEB_MODES = Set.of("embedded", "external");

    public JudgeModeGuard(AppProperties props) {
        String mode = props.judge().mode();
        if (!WEB_MODES.contains(mode)) {
            throw new IllegalStateException("app.judge.mode=" + mode + " is not valid for the web server (embedded |"
                    + " external). To run the judge worker set the environment variable APP_JUDGE_MODE=worker or pass"
                    + " --app.judge.mode=worker; .env and application.yml are read too late for that.");
        }
        if ("external".equals(mode)) {
            log.info("Code judge mode: external (this server only queues attempts; run a worker process)");
        }
    }
}
