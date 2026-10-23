package lms.notification;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lms.audit.AuditService;
import lms.auth.AuthPrincipal;
import lms.common.AppException;
import lms.common.ErrorCode;
import lms.common.PageResult;
import lms.course.Assessment;
import lms.course.AssessmentRepository;
import lms.course.Course;
import lms.course.CourseRepository;
import lms.course.CourseService;
import lms.course.EnrollmentRepository;
import lms.notification.NotificationViews.AnnouncementRequest;
import lms.notification.NotificationViews.CourseRef;
import lms.notification.NotificationViews.NotificationView;
import lms.notification.NotificationViews.SentView;
import lms.notification.NotificationViews.Summary;
import lms.realtime.RealtimeEvent;
import lms.realtime.RealtimeHub;
import lms.user.User;
import lms.user.UserRepository;

/**
 * Thông báo cho sinh viên đã ghi danh. Tự động khi tạo bài kiểm tra / bài thực hành; tùy chọn khi tạo
 * bài tập, bài học, tài liệu; giảng viên soạn và gửi cả lớp hoặc một số sinh viên. Người nhận được đẩy
 * sự kiện notification.created qua WebSocket sau khi commit.
 */
@Service
public class NotificationService {

    /** Giờ trong nội dung thông báo theo múi giờ của trường (server chạy trong LAN tại Việt Nam). */
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")
            .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));
    private static final int HISTORY_SIZE = 50;

    private final NotificationRepository notifications;
    private final NotificationRecipientRepository recipients;
    private final CourseService courseService;
    private final CourseRepository courses;
    private final EnrollmentRepository enrollments;
    private final AssessmentRepository assessments;
    private final UserRepository users;
    private final RealtimeHub hub;
    private final AuditService audit;

    public NotificationService(NotificationRepository notifications, NotificationRecipientRepository recipients,
            CourseService courseService, CourseRepository courses, EnrollmentRepository enrollments,
            AssessmentRepository assessments, UserRepository users, RealtimeHub hub, AuditService audit) {
        this.audit = audit;
        this.notifications = notifications;
        this.recipients = recipients;
        this.courseService = courseService;
        this.courses = courses;
        this.enrollments = enrollments;
        this.assessments = assessments;
        this.users = users;
        this.hub = hub;
    }

    // ================================================================ gửi khi tạo nội dung

    /** Bài kiểm tra và bài thực hành luôn thông báo; bài tập chỉ khi giảng viên chọn `requested`. */
    public void assessmentCreated(Course c, Assessment a, boolean requested, long senderId) {
        int category = switch (a.getType()) {
            case Assessment.TYPE_EXAM -> Notification.CATEGORY_EXAM;
            case Assessment.TYPE_LAB -> Notification.CATEGORY_LAB;
            default -> Notification.CATEGORY_PRACTICE;
        };
        if (category == Notification.CATEGORY_PRACTICE && !requested) {
            return;
        }
        String prefix = switch (category) {
            case Notification.CATEGORY_EXAM -> "Bài kiểm tra mới";
            case Notification.CATEGORY_LAB -> "Bài thực hành mới";
            default -> "Bài tập mới";
        };
        String opens = a.getOpensAt() == null ? "Mở ngay" : "Mở lúc " + TIME.format(a.getOpensAt());
        String closes = a.getClosesAt() == null ? "không giới hạn thời gian" : "hạn nộp " + TIME.format(a.getClosesAt());
        sendToCourse(c, category, prefix + ": " + a.getTitle(), opens + " · " + closes, a, senderId);
    }

    /** Bài học mới (người gọi đã kiểm tra giảng viên chọn gửi và bài không bị ẩn). */
    public void lessonCreated(Course c, String lessonTitle, String sectionTitle, long senderId) {
        sendToCourse(c, Notification.CATEGORY_CONTENT, "Bài học mới: " + lessonTitle,
                sectionTitle == null ? null : "Trong " + sectionTitle, null, senderId);
    }

    /** Tài liệu mới (người gọi đã kiểm tra giảng viên chọn gửi và sinh viên thấy được file). */
    public void fileUploaded(Course c, String fileName, String where, long senderId) {
        sendToCourse(c, Notification.CATEGORY_CONTENT, "Tài liệu mới: " + fileName,
                where == null ? null : "Trong " + where, null, senderId);
    }

    // ================================================================ giảng viên

    /** Thông báo tùy chỉnh tới cả lớp hoặc các sinh viên được chọn (phải đang ghi danh). */
    @Transactional
    public SentView announce(String courseId, AnnouncementRequest req, AuthPrincipal me) {
        Course c = courseService.requireTeaching(courseId, me);
        Set<Long> enrolled = enrolledUserIds(c);
        Collection<Long> targets = enrolled;
        if (req.studentIds() != null && !req.studentIds().isEmpty()) {
            targets = users.findByPublicIdIn(req.studentIds()).stream().map(User::getId)
                    .filter(enrolled::contains).collect(Collectors.toSet());
        }
        if (targets.isEmpty()) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "No recipients");
        }
        Notification n = send(c, Notification.CATEGORY_ANNOUNCEMENT, req.title().trim(),
                CourseService.blankToNull(req.body()), (Assessment) null, me.userId(), targets);
        audit.record(me.userId(), AuditService.NOTIFICATION_SENT, c.displayCode(),
                n.getTitle() + " → " + targets.size() + " sinh viên");
        return new SentView(n.getPublicId(), NotificationViews.CATEGORIES.get(n.getCategory()), n.getTitle(),
                n.getBody(), me.fullName(), n.getCreatedAt(), targets.size(), 0);
    }

    /** Thông báo gần đây của khóa học (mọi loại) kèm số người đã đọc. */
    @Transactional(readOnly = true)
    public List<SentView> history(String courseId, AuthPrincipal me) {
        Course c = courseService.requireManaged(courseId, me);
        List<Notification> list = notifications.findByCourseIdOrderByCreatedAtDescIdDesc(c.getId(),
                PageRequest.of(0, HISTORY_SIZE));
        if (list.isEmpty()) {
            return List.of();
        }
        Map<Long, long[]> stats = recipients.readStats(list.stream().map(Notification::getId).toList()).stream()
                .collect(Collectors.toMap(r -> (Long) r[0], r -> new long[] { (Long) r[1], (Long) r[2] }));
        Map<Long, String> senders = names(list.stream().map(Notification::getSenderId).toList());
        return list.stream().map(n -> {
            long[] s = stats.getOrDefault(n.getId(), new long[] { 0, 0 });
            return new SentView(n.getPublicId(), NotificationViews.CATEGORIES.get(n.getCategory()), n.getTitle(),
                    n.getBody(), n.getSenderId() == null ? null : senders.get(n.getSenderId()), n.getCreatedAt(),
                    s[0], s[1]);
        }).toList();
    }

    // ================================================================ người nhận

    /** category: exam / lab / practice / content / announcement, null = mọi loại. */
    @Transactional(readOnly = true)
    public PageResult<NotificationView> list(AuthPrincipal me, String category, boolean unreadOnly, int page,
            int size) {
        Integer cat = null;
        if (category != null && !category.isBlank()) {
            int index = NotificationViews.CATEGORIES.indexOf(category);
            if (index < 0) {
                throw new AppException(ErrorCode.VALIDATION_FAILED, "Unknown category");
            }
            cat = index;
        }
        Page<Object[]> rows = recipients.findForUser(me.userId(), cat, unreadOnly,
                PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100)));
        List<Notification> list = rows.getContent().stream().map(r -> (Notification) r[0]).toList();
        Map<Long, Course> courseById = courses.findAllById(list.stream().map(Notification::getCourseId).toList())
                .stream().collect(Collectors.toMap(Course::getId, Function.identity()));
        Map<Long, String> assessmentIds = assessments
                .findAllById(list.stream().map(Notification::getAssessmentId).filter(Objects::nonNull).toList())
                .stream().collect(Collectors.toMap(Assessment::getId, Assessment::getPublicId));
        Map<Long, String> senders = names(list.stream().map(Notification::getSenderId).toList());
        return PageResult.of(rows, r -> {
            Notification n = (Notification) r[0];
            NotificationRecipient rec = (NotificationRecipient) r[1];
            return view(n, courseById.get(n.getCourseId()),
                    n.getAssessmentId() == null ? null : assessmentIds.get(n.getAssessmentId()),
                    n.getSenderId() == null ? null : senders.get(n.getSenderId()), rec.getReadAt() != null);
        });
    }

    @Transactional(readOnly = true)
    public Summary summary(AuthPrincipal me) {
        Map<String, Long> byCategory = new LinkedHashMap<>();
        long total = 0;
        for (Object[] row : recipients.unreadByCategory(me.userId())) {
            long count = (Long) row[1];
            byCategory.put(NotificationViews.CATEGORIES.get(((Number) row[0]).intValue()), count);
            total += count;
        }
        return new Summary(total, byCategory);
    }

    @Transactional
    public void markRead(String notificationId, AuthPrincipal me) {
        Notification n = notifications.findByPublicId(notificationId)
                .filter(x -> recipients.existsByNotificationIdAndUserId(x.getId(), me.userId()))
                .orElseThrow(() -> AppException.notFound("Notification"));
        recipients.markRead(me.userId(), n.getId(), Instant.now());
    }

    @Transactional
    public void markAllRead(AuthPrincipal me) {
        recipients.markAllRead(me.userId(), Instant.now());
    }

    // ================================================================ nội bộ

    private void sendToCourse(Course c, int category, String title, String body, Assessment a, long senderId) {
        Set<Long> targets = enrolledUserIds(c);
        if (!targets.isEmpty()) {
            send(c, category, title, body, a, senderId, targets);
        }
    }

    private Notification send(Course c, int category, String title, String body, Assessment a, Long senderId,
            Collection<Long> targets) {
        String shortTitle = title.length() > 200 ? title.substring(0, 199) + "…" : title;
        Notification n = notifications.save(new Notification(c.getId(), category, shortTitle, body,
                a == null ? null : a.getId(), senderId));
        recipients.saveAll(targets.stream().map(u -> new NotificationRecipient(n.getId(), u)).toList());
        String sender = senderId == null ? null : users.findById(senderId).map(User::getFullName).orElse(null);
        NotificationView pushed = view(n, c, a == null ? null : a.getPublicId(), sender, false);
        List<Long> to = List.copyOf(targets);
        CourseService.afterCommit(
                () -> to.forEach(u -> hub.sendToUser(u, RealtimeEvent.of(RealtimeEvent.NOTIFICATION_CREATED, pushed))));
        return n;
    }

    private Set<Long> enrolledUserIds(Course c) {
        return enrollments.findWithUsers(c.getId()).stream().map(row -> ((User) row[1]).getId())
                .collect(Collectors.toSet());
    }

    private Map<Long, String> names(Collection<Long> ids) {
        List<Long> distinct = ids.stream().filter(Objects::nonNull).distinct().toList();
        return distinct.isEmpty() ? Map.of()
                : users.findAllById(distinct).stream().collect(Collectors.toMap(User::getId, User::getFullName));
    }

    private static NotificationView view(Notification n, Course c, String assessmentPublicId, String sender,
            boolean read) {
        return new NotificationView(n.getPublicId(), NotificationViews.CATEGORIES.get(n.getCategory()), n.getTitle(),
                n.getBody(), c == null ? null : new CourseRef(c.getPublicId(), c.displayCode(), c.getTitle()),
                assessmentPublicId, sender, n.getCreatedAt(), read);
    }
}
