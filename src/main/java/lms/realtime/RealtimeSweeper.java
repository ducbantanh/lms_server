package lms.realtime;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lms.auth.session.SessionService;
import lms.course.Assessment;
import lms.submission.SubmissionRepository;

/**
 * Chạy mỗi 30 giây:
 * - Giữ phiên của người đang thi (bài kiểm tra đang mở, chưa nộp) còn mở trang: không tự đăng xuất khi
 *   không hoạt động, vì sinh viên có thể làm bài ở phần mềm khác ngoài trình duyệt.
 * - Gửi WebSocket ping để giữ kết nối (proxy/idle timeout) và phát hiện client chết.
 * - Đóng socket có session đã hết hạn / bị revoke / user bị khóa (phòng trường hợp bị sót).
 */
@Component
public class RealtimeSweeper {

    private final RealtimeHub hub;
    private final SessionService sessions;
    private final SubmissionRepository submissions;

    public RealtimeSweeper(RealtimeHub hub, SessionService sessions, SubmissionRepository submissions) {
        this.hub = hub;
        this.sessions = sessions;
        this.submissions = submissions;
    }

    @Scheduled(fixedDelay = 30_000, initialDelay = 30_000)
    public void sweep() {
        Collection<RealtimeHub.Connection> conns = hub.snapshot();
        if (conns.isEmpty()) {
            return;
        }
        keepExamSessionsAlive(conns);

        Set<Long> sessionIds = conns.stream()
                .map(c -> c.principal().sessionId())
                .collect(Collectors.toSet());
        Set<Long> valid = sessions.filterValid(sessionIds);

        for (RealtimeHub.Connection c : conns) {
            if (!valid.contains(c.principal().sessionId())) {
                // "idle": client báo người dùng bị đăng xuất vì không hoạt động
                hub.revokeAndClose(c, sessions.expiredReason(c.principal().sessionId()));
            } else {
                hub.ping(c);
            }
        }
    }

    /**
     * Gia hạn như khi có thao tác, mỗi 30 giây (thời gian chờ ngắn nhất 1 phút nên phiên không kịp hết hạn).
     * Nộp bài hoặc hết giờ thì thôi gia hạn, thời gian chờ bình thường tính từ lần gia hạn cuối.
     */
    private void keepExamSessionsAlive(Collection<RealtimeHub.Connection> conns) {
        Set<Long> userIds = conns.stream()
                .map(c -> c.principal().userId())
                .collect(Collectors.toSet());
        Set<Long> takingExam = new HashSet<>(
                submissions.findTakingAssessment(userIds, Assessment.TYPE_EXAM, Instant.now()));
        if (takingExam.isEmpty()) {
            return;
        }
        conns.stream()
                .filter(c -> takingExam.contains(c.principal().userId()))
                .map(c -> c.principal().sessionId())
                .distinct()
                .forEach(sessions::touch);
    }
}
