package lms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
public class LmsServerApplication {

    /** Cùng một jar: APP_JUDGE_MODE=worker (hoặc --app.judge.mode=worker) chạy process máy chấm riêng. */
    public static void main(String[] args) {
        if ("worker".equalsIgnoreCase(judgeMode(args, System.getProperty("app.judge.mode"),
                System.getenv("APP_JUDGE_MODE")))) {
            JudgeWorkerApplication.run(args);
            return;
        }
        SpringApplication.run(LmsServerApplication.class, args);
    }

    /**
     * Chế độ máy chấm, phải biết trước khi dựng Spring (chọn lớp khởi động): tham số dòng lệnh, rồi system property,
     * rồi biến môi trường. Đặt trong .env hoặc application.yml thì không tính (JudgeModeGuard báo lỗi).
     */
    static String judgeMode(String[] args, String systemProperty, String env) {
        for (String arg : args) {
            if (arg.startsWith("--app.judge.mode=")) {
                return arg.substring("--app.judge.mode=".length());
            }
        }
        return systemProperty != null ? systemProperty : env;
    }
}
