package lms.code;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lms.course.Assessment;
import lms.course.AssessmentRepository;
import lms.course.Course;
import lms.course.CourseRepository;
import lms.realtime.RealtimeEvent;
import lms.realtime.RealtimeRelay;

/**
 * Phía máy chấm của hàng đợi bài lập trình (bảng code_attempts): lấy bài chờ, nhận bài, lưu kết quả, tự chấm lại khi
 * lỗi máy chấm, báo code.judged. Chỉ phụ thuộc repository và RealtimeRelay để process máy chấm riêng
 * (JudgeWorkerApplication) nạp được mà không kéo theo các service của server web.
 */
@Service
public class CodeJudgeQueue {

    private static final Logger log = LoggerFactory.getLogger(CodeJudgeQueue.class);

    /** Số lần tự chấm lại khi lỗi máy chấm (SE); lần thứ n chờ n × RETRY_DELAY. */
    static final int MAX_SYSTEM_RETRIES = 2;
    static final Duration RETRY_DELAY = Duration.ofSeconds(5);

    private final CodeAttemptRepository attempts;
    private final CodeProblemRepository problems;
    private final CodeTestRepository tests;
    private final AssessmentRepository assessments;
    private final CourseRepository courses;
    private final RealtimeRelay relay;

    public CodeJudgeQueue(CodeAttemptRepository attempts, CodeProblemRepository problems, CodeTestRepository tests,
            AssessmentRepository assessments, CourseRepository courses, RealtimeRelay relay) {
        this.attempts = attempts;
        this.problems = problems;
        this.tests = tests;
        this.assessments = assessments;
        this.courses = courses;
        this.relay = relay;
    }

    /** Bài đã nhận để chấm: token để complete() biết kết quả còn dùng được không. */
    public record Claimed(long attemptId, String token, CodeJudge.Task task) {
    }

    /** Dữ liệu của sự kiện code.judged; mọi id là public_id. */
    public record JudgedEvent(String courseId, String assessmentId, String problemId, String attemptId,
            String kind) {
    }

    @Transactional(readOnly = true)
    public List<Long> queued(int limit) {
        return attempts.findQueuedIds(Instant.now(), Limit.of(limit));
    }

    /** Nhận một bài đang chờ (đặt RUNNING); empty nếu bài đã bị worker khác nhận hoặc đã xóa. */
    @Transactional
    public Optional<Claimed> claim(long attemptId) {
        CodeAttempt x = attempts.lockById(attemptId).orElse(null);
        if (x == null || x.getStatus() != CodeAttempt.QUEUED) {
            return Optional.empty();
        }
        CodeProblem p = problems.findById(x.getProblemId()).orElse(null);
        if (p == null) {
            return Optional.empty();
        }
        String token = x.claim();
        // Lần chạy thử chỉ chạy test mẫu; nội dung từng test đọc khi tới lượt (repository tự mở giao dịch đọc)
        List<CodeTestInfo> list = tests.findInfos(x.getProblemId());
        List<CodeJudge.TestCase> cases = list.stream().filter(t -> x.isSubmit() || t.sample())
                .map(t -> new CodeJudge.TestCase(t.sample(), () -> testContent(t.id()))).toList();
        return Optional.of(new Claimed(x.getId(), token, new CodeJudge.Task(x.getLanguage(), x.getSource(), cases,
                p.getTimeLimitMs(), p.getMemoryLimitMb())));
    }

    /**
     * Lưu kết quả; bài đã bị đưa về hàng đợi (chấm lại) hoặc bị xóa trong lúc chạy thì bỏ qua. Lỗi máy chấm (SE)
     * thường chỉ thoáng qua nên bài được đưa về hàng đợi, tối đa MAX_SYSTEM_RETRIES lần, lần sau chờ lâu hơn; hết
     * lượt mới lưu SE. Lưu xong báo code.judged cho sinh viên nộp bài và giảng viên phụ trách.
     */
    @Transactional
    public void complete(long attemptId, String token, CodeJudge.Result result) {
        CodeAttempt x = attempts.lockById(attemptId).orElse(null);
        if (x == null || x.getStatus() != CodeAttempt.RUNNING || !token.equals(x.getClaimToken())) {
            return;
        }
        Instant now = Instant.now();
        if (CodeJudge.SE.equals(result.verdict()) && x.getJudgeRetries() < MAX_SYSTEM_RETRIES) {
            Instant retryAt = now.plus(RETRY_DELAY.multipliedBy(x.getJudgeRetries() + 1L));
            x.retryAfterSystemError(retryAt);
            log.warn("Judge system error on attempt {} (retry {}/{} at {}): {}", x.getPublicId(),
                    x.getJudgeRetries(), MAX_SYSTEM_RETRIES, retryAt, result.compileOutput());
            return;
        }
        x.finish(result, now);
        announceJudged(x);
    }

    /** Server dừng giữa lúc chấm: đưa bài đang chấm về hàng đợi (CodeJudgeWorker gọi khi khởi động). */
    @Transactional
    public int requeueInterrupted() {
        return attempts.requeueRunning();
    }

    public CodeJudge.Content testContent(long testId) {
        List<Object[]> rows = tests.content(testId);
        return rows.isEmpty() ? null : new CodeJudge.Content((String) rows.get(0)[0], (String) rows.get(0)[1]);
    }

    /**
     * Sự kiện code.judged: chỉ báo "có kết quả mới" (không kèm verdict, để không lộ kết quả bài chưa công bố);
     * client tải lại qua API như thường, nên quyền xem kết quả vẫn do API quyết định. Giảng viên chỉ nhận lần nộp
     * (bảng kết quả), lần chạy thử chỉ báo sinh viên.
     */
    private void announceJudged(CodeAttempt x) {
        Assessment a = assessments.findById(x.getAssessmentId()).orElse(null);
        if (a == null) {
            return;
        }
        Course c = courses.findById(a.getCourseId()).orElse(null);
        String problemId = problems.findById(x.getProblemId()).map(CodeProblem::getPublicId).orElse(null);
        JudgedEvent event = new JudgedEvent(c == null ? null : c.getPublicId(), a.getPublicId(), problemId,
                x.getPublicId(), x.isSubmit() ? CodeViews.SUBMIT : CodeViews.RUN);
        List<Long> targets = new ArrayList<>(List.of(x.getUserId()));
        if (x.isSubmit() && c != null && c.getTeacher() != null && !c.getTeacher().getId().equals(x.getUserId())) {
            targets.add(c.getTeacher().getId());
        }
        relay.sendAfterCommit(targets, RealtimeEvent.CODE_JUDGED, event);
    }
}
