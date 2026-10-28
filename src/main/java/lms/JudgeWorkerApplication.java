package lms;

import java.util.Map;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fasterxml.jackson.databind.ObjectMapper;

import lms.code.CodeJudgeQueue;
import lms.code.CodeJudgeWorker;
import lms.config.AppProperties;
import lms.realtime.OutboxRealtimeRelay;
import lms.realtime.RealtimeOutboxRepository;
import lms.realtime.RealtimeRelay;

/**
 * Process máy chấm riêng: cùng jar với server web, chạy bằng APP_JUDGE_MODE=worker (hoặc --app.judge.mode=worker).
 * Không có web/WebSocket, chỉ nạp hàng đợi chấm (CodeJudgeQueue) và vòng chấm (CodeJudgeWorker); dùng chung DB với
 * server web (app.judge.mode=external): lấy bài từ code_attempts, ghi kết quả, gửi code.judged qua realtime_outbox.
 * Bài sinh viên chạy trong sandbox như khi chấm trong server web; process này mới cần Docker và trình biên dịch.
 *
 * Cố ý không dùng @SpringBootApplication / @Configuration: LmsServerApplication quét cả package lms, lớp này mà là
 * component thì server web cũng nạp theo.
 */
@EnableAutoConfiguration
@EntityScan("lms")
@EnableJpaRepositories("lms")
@EnableConfigurationProperties(AppProperties.class)
@EnableScheduling
@Import({ CodeJudgeQueue.class, CodeJudgeWorker.class })
public class JudgeWorkerApplication {

    public static void run(String[] args) {
        // Đặt trên application.yml (properties() của builder chỉ là giá trị mặc định, yml ghi đè): log ghi
        // [lms-judge-worker] để phân biệt với server web khi hai process chạy cùng máy
        StandardEnvironment env = new StandardEnvironment();
        env.getPropertySources().addFirst(new MapPropertySource("judgeWorker",
                Map.of("app.judge.mode", "worker", "spring.application.name", "lms-judge-worker")));
        new SpringApplicationBuilder(JudgeWorkerApplication.class)
                .main(JudgeWorkerApplication.class)
                .environment(env)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    /** Không giữ WebSocket: sự kiện realtime ghi vào outbox cho server web gửi. */
    @Bean
    RealtimeRelay realtimeRelay(RealtimeOutboxRepository outbox, ObjectMapper mapper) {
        return new OutboxRealtimeRelay(outbox, mapper);
    }
}
