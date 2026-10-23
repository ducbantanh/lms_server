package lms.quiz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Chốt và chấm bài trắc nghiệm hết giờ mà sinh viên chưa bấm nộp (đóng trình duyệt, mất mạng...).
 * Các API đọc/ghi bài cũng tự chốt khi gặp bài quá hạn; sweeper để điểm có ngay cả khi không ai mở bài.
 */
@Component
public class QuizSweeper {

    private static final Logger log = LoggerFactory.getLogger(QuizSweeper.class);

    private final QuizService service;

    public QuizSweeper(QuizService service) {
        this.service = service;
    }

    @Scheduled(fixedDelay = 30_000, initialDelay = 30_000)
    public void sweep() {
        try {
            int done = service.finalizeExpired();
            if (done > 0) {
                log.info("Finalized {} timed-out quiz submissions", done);
            }
        } catch (RuntimeException e) {
            log.warn("Quiz sweep failed: {}", e.getMessage());
        }
    }
}
