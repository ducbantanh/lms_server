package lms.course;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lms.audit.AuditService;
import lms.auth.AuthPrincipal;
import lms.auth.Role;
import lms.common.AppException;
import lms.common.ErrorCode;
import lms.course.CourseViews.AssessmentView;
import lms.course.CourseViews.CourseDetail;
import lms.course.CourseViews.CourseSummary;
import lms.course.CourseViews.CourseView;
import lms.course.CourseViews.CreateCourseRequest;
import lms.course.CourseViews.EnrollReport;
import lms.course.CourseViews.FileView;
import lms.course.CourseViews.SectionView;
import lms.course.CourseViews.StudentRow;
import lms.course.CourseViews.UserPick;
import lms.user.User;
import lms.user.UserRepository;

/**
 * Khóa học và ghi danh: phía giảng viên (quản lý khóa học của mình; admin quản lý tất cả)
 * và phía sinh viên (xem khóa học đã ghi danh, tham gia bằng mã).
 * Khóa học không thuộc quyền quản lý trả NOT_FOUND để không lộ sự tồn tại.
 */
@Service
public class CourseService {

    /** Bỏ ký tự dễ nhầm (I, L, O, 0, 1) vì sinh viên gõ mã từ máy chiếu. */
    private static final char[] KEY_ALPHABET = "ABCDEFGHJKMNPQRSTUVWXYZ23456789".toCharArray();
    private static final int KEY_LENGTH = 8;
    private static final int MAX_PASTE_TOKENS = 1000;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final CourseRepository courses;
    private final EnrollmentRepository enrollments;
    private final CourseSectionRepository sections;
    private final CourseFileRepository files;
    private final AssessmentRepository assessments;
    private final UserRepository users;
    private final FileStorage storage;
    private final AuditService audit;

    public CourseService(CourseRepository courses, EnrollmentRepository enrollments,
            CourseSectionRepository sections, CourseFileRepository files, AssessmentRepository assessments,
            UserRepository users, FileStorage storage, AuditService audit) {
        this.courses = courses;
        this.enrollments = enrollments;
        this.sections = sections;
        this.files = files;
        this.assessments = assessments;
        this.users = users;
        this.storage = storage;
        this.audit = audit;
    }

    // ================================================================ truy cập

    /** Khóa học mà người gọi được quản lý; ngược lại NOT_FOUND. */
    public Course requireManaged(String publicId, AuthPrincipal me) {
        Course c = courses.findByPublicId(publicId).orElseThrow(() -> AppException.notFound("Course"));
        if (!c.isManagedBy(me.userId(), me.role() == Role.ADMIN)) {
            throw AppException.notFound("Course");
        }
        return c;
    }

    public boolean canView(Course c, AuthPrincipal me) {
        return c.isManagedBy(me.userId(), me.role() == Role.ADMIN)
                || enrollments.existsByCourseIdAndUserId(c.getId(), me.userId());
    }

    // ================================================================ giảng viên

    @Transactional(readOnly = true)
    public List<CourseSummary> listManaged(AuthPrincipal me) {
        List<Course> list = me.role() == Role.ADMIN
                ? courses.findAllNewestFirst()
                : courses.findByTeacherNewestFirst(me.userId());
        Map<Long, Long> counts = studentCounts(list);
        return list.stream().map(c -> summary(c, counts.getOrDefault(c.getId(), 0L))).toList();
    }

    @Transactional
    public CourseView create(CreateCourseRequest req, AuthPrincipal me, String ip) {
        User teacher = users.findById(me.userId()).orElseThrow(() -> AppException.notFound("User"));
        Course c = new Course(req.code().trim(), req.title().trim(), teacher);
        c.setDescription(blankToNull(req.description()));
        c.setFormat(req.format());
        courses.save(c);
        audit.record(me.userId(), AuditService.COURSE_CREATED, c.getCode(), c.getTitle(), ip);
        return view(c, false, 0);
    }

    @Transactional(readOnly = true)
    public CourseDetail managedDetail(String publicId, AuthPrincipal me) {
        return detail(requireManaged(publicId, me), false);
    }

    /** Xóa khóa học; DB xóa dây chuyền mục/file/bài/ghi danh, file trên đĩa xóa sau khi commit. */
    @Transactional
    public void delete(String publicId, AuthPrincipal me, String ip) {
        Course c = requireManaged(publicId, me);
        List<String> keys = files.findByCourseIdOrderByCreatedAt(c.getId()).stream()
                .map(CourseFile::getStorageKey).toList();
        courses.delete(c);
        afterCommit(() -> keys.forEach(storage::deleteQuietly));
        audit.record(me.userId(), AuditService.COURSE_DELETED, c.getCode(), c.getTitle(), ip);
    }

    // ---------------------------------------------------------------- ghi danh

    @Transactional(readOnly = true)
    public List<StudentRow> students(String publicId, AuthPrincipal me) {
        Course c = requireManaged(publicId, me);
        return enrollments.findWithUsers(c.getId()).stream().map(row -> {
            Enrollment e = (Enrollment) row[0];
            User u = (User) row[1];
            return new StudentRow(u.getPublicId(), u.getUsername(), u.getFullName(), e.getMethod(),
                    e.getEnrolledAt());
        }).toList();
    }

    /** Dán danh sách MSSV (cách nhau bởi khoảng trắng, dấu phẩy, chấm phẩy, xuống dòng). */
    @Transactional
    public EnrollReport enrollByUsernames(String publicId, String content, AuthPrincipal me) {
        Course c = requireManaged(publicId, me);
        Map<String, String> tokens = new LinkedHashMap<>();
        Arrays.stream(content.split("[\\s,;]+")).filter(t -> !t.isBlank())
                .forEach(t -> tokens.putIfAbsent(t.toUpperCase(Locale.ROOT), t));
        if (tokens.size() > MAX_PASTE_TOKENS) {
            throw new AppException(ErrorCode.PAYLOAD_TOO_LARGE, "Too many usernames");
        }
        List<User> found = new ArrayList<>();
        List<String> notFound = new ArrayList<>();
        tokens.values().forEach(t -> users.findByUsername(t).ifPresentOrElse(found::add, () -> notFound.add(t)));
        return enroll(c, found, Enrollment.METHOD_PASTE, notFound);
    }

    @Transactional
    public EnrollReport enrollByIds(String publicId, List<String> userIds, AuthPrincipal me) {
        Course c = requireManaged(publicId, me);
        List<User> found = new ArrayList<>();
        List<String> notFound = new ArrayList<>();
        userIds.stream().distinct()
                .forEach(id -> users.findByPublicId(id).ifPresentOrElse(found::add, () -> notFound.add(id)));
        return enroll(c, found, Enrollment.METHOD_PICK, notFound);
    }

    @Transactional
    public void unenroll(String publicId, String userPublicId, AuthPrincipal me) {
        Course c = requireManaged(publicId, me);
        User u = users.findByPublicId(userPublicId).orElseThrow(() -> AppException.notFound("Enrollment"));
        if (enrollments.deleteOne(c.getId(), u.getId()) == 0) {
            throw AppException.notFound("Enrollment");
        }
    }

    @Transactional(readOnly = true)
    public List<UserPick> searchStudents(String q) {
        String needle = q == null || q.isBlank() ? null : q.trim();
        // Dùng lại tìm kiếm của trang quản trị: sinh viên đang hoạt động, 50 kết quả đầu theo username
        return users.search(needle, Role.STUDENT, User.STATUS_ACTIVE, PageRequest.of(0, 50, Sort.by("username")))
                .stream()
                .map(u -> new UserPick(u.getPublicId(), u.getUsername(), u.getFullName())).toList();
    }

    @Transactional
    public String newEnrollKey(String publicId, AuthPrincipal me) {
        Course c = requireManaged(publicId, me);
        String key;
        do {
            StringBuilder sb = new StringBuilder(KEY_LENGTH);
            for (int i = 0; i < KEY_LENGTH; i++) {
                sb.append(KEY_ALPHABET[RANDOM.nextInt(KEY_ALPHABET.length)]);
            }
            key = sb.toString();
        } while (courses.existsByEnrollKey(key));
        c.setEnrollKey(key);
        return key;
    }

    @Transactional
    public void disableEnrollKey(String publicId, AuthPrincipal me) {
        requireManaged(publicId, me).setEnrollKey(null);
    }

    // ================================================================ sinh viên

    @Transactional(readOnly = true)
    public CourseDetail studentDetail(String publicId, AuthPrincipal me) {
        Course c = courses.findByPublicId(publicId).orElseThrow(() -> AppException.notFound("Course"));
        if (!canView(c, me)) {
            throw AppException.notFound("Course");
        }
        return detail(c, true);
    }

    /** Tham gia bằng mã: chỉ tài khoản sinh viên/khách; mã không phân biệt hoa thường, bỏ gạch và khoảng trắng. */
    @Transactional
    public CourseSummary join(String rawKey, AuthPrincipal me) {
        if (me.role() != Role.STUDENT && me.role() != Role.VISITOR) {
            throw new AppException(ErrorCode.FORBIDDEN, "Only students can join with a key");
        }
        String key = rawKey.trim().toUpperCase(Locale.ROOT).replaceAll("[-\\s]", "");
        Course c = courses.findByEnrollKey(key).orElseThrow(() -> new AppException(ErrorCode.INVALID_ENROLL_KEY));
        if (!enrollments.existsByCourseIdAndUserId(c.getId(), me.userId())) {
            enrollments.save(new Enrollment(c.getId(), me.userId(), Enrollment.METHOD_KEY));
        }
        return summary(c, enrollments.countByCourseId(c.getId()));
    }

    // ================================================================ dựng view

    CourseDetail detail(Course c, boolean studentView) {
        List<CourseSection> secs = sections.findByCourseIdOrderByPosition(c.getId());
        Map<Long, String> sectionIds = secs.stream()
                .collect(Collectors.toMap(CourseSection::getId, CourseSection::getPublicId));
        List<CourseFile> fs = files.findByCourseIdOrderByCreatedAt(c.getId());
        Map<Long, CourseFile> fileById = fs.stream().collect(Collectors.toMap(CourseFile::getId, Function.identity()));
        Instant now = Instant.now();

        List<FileView> fileViews = fs.stream()
                .filter(f -> !studentView || !f.isHidden())
                .map(f -> fileView(f, sectionIds.get(f.getSectionId())))
                .toList();
        List<AssessmentView> assessmentViews = assessments.findByCourseIdOrderByCreatedAt(c.getId()).stream()
                .map(a -> {
                    CourseFile paper = Optional.ofNullable(a.getPaperFileId()).map(fileById::get).orElse(null);
                    // Sinh viên chỉ thấy đề khi bài đã mở
                    boolean show = paper != null && (!studentView || a.isOpened(now));
                    return new AssessmentView(a.getPublicId(), a.getType(), a.getTitle(), a.getDescription(),
                            show ? paper.getPublicId() : null, show ? paper.getName() : null,
                            a.getOpensAt(), a.getClosesAt(), a.statusAt(now));
                }).toList();
        return new CourseDetail(view(c, studentView, enrollments.countByCourseId(c.getId())),
                secs.stream().map(s -> new SectionView(s.getPublicId(), s.getPosition(), s.getTitle())).toList(),
                fileViews, assessmentViews);
    }

    static FileView fileView(CourseFile f, String sectionPublicId) {
        return new FileView(f.getPublicId(), sectionPublicId, f.getName(), f.getContentType(), f.getSize(),
                f.isHidden(), f.getCreatedAt());
    }

    private EnrollReport enroll(Course c, List<User> candidates, int method, List<String> notFound) {
        List<UserPick> added = new ArrayList<>();
        List<String> already = new ArrayList<>();
        List<String> notStudent = new ArrayList<>();
        for (User u : candidates) {
            if (u.getRole() != Role.STUDENT) {
                notStudent.add(u.getUsername());
            } else if (enrollments.existsByCourseIdAndUserId(c.getId(), u.getId())) {
                already.add(u.getUsername());
            } else {
                enrollments.save(new Enrollment(c.getId(), u.getId(), method));
                added.add(new UserPick(u.getPublicId(), u.getUsername(), u.getFullName()));
            }
        }
        return new EnrollReport(added, already, notFound, notStudent);
    }

    private Map<Long, Long> studentCounts(List<Course> list) {
        if (list.isEmpty()) {
            return Map.of();
        }
        return enrollments.countByCourseIds(list.stream().map(Course::getId).toList()).stream()
                .collect(Collectors.toMap(r -> (Long) r[0], r -> (Long) r[1]));
    }

    private static CourseSummary summary(Course c, long studentCount) {
        return new CourseSummary(c.getPublicId(), c.getCode(), c.getTitle(), c.getFormat(), studentCount,
                teacherName(c), c.getCreatedAt());
    }

    private static CourseView view(Course c, boolean studentView, long studentCount) {
        return new CourseView(c.getPublicId(), c.getCode(), c.getTitle(), c.getDescription(), c.getFormat(),
                studentView ? null : c.getEnrollKey(), studentCount, teacherName(c), c.getCreatedAt());
    }

    private static String teacherName(Course c) {
        return c.getTeacher() == null ? null : c.getTeacher().getFullName();
    }

    static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    static void afterCommit(Runnable action) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    action.run();
                }
            });
        } else {
            action.run();
        }
    }
}
