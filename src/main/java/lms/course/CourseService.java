package lms.course;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import lms.course.CourseViews.LessonView;
import lms.course.CourseViews.SectionView;
import lms.course.CourseViews.StudentRow;
import lms.course.CourseViews.UpdateCourseRequest;
import lms.course.CourseViews.UserPick;
import lms.leave.LeaveRequestFileRepository;
import lms.quiz.QuizQuestionRepository;
import lms.submission.SubmissionFileRepository;
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
    private final LessonRepository lessons;
    private final CourseFileRepository files;
    private final AssessmentRepository assessments;
    private final UserRepository users;
    private final SubmissionFileRepository submissionFiles;
    private final QuizQuestionRepository quizQuestions;
    private final LeaveRequestFileRepository leaveFiles;
    private final FileStorage storage;
    private final AuditService audit;

    public CourseService(CourseRepository courses, EnrollmentRepository enrollments,
            CourseSectionRepository sections, LessonRepository lessons, CourseFileRepository files,
            AssessmentRepository assessments, UserRepository users, SubmissionFileRepository submissionFiles,
            FileStorage storage, AuditService audit, QuizQuestionRepository quizQuestions,
            LeaveRequestFileRepository leaveFiles) {
        this.leaveFiles = leaveFiles;
        this.submissionFiles = submissionFiles;
        this.quizQuestions = quizQuestions;
        this.courses = courses;
        this.enrollments = enrollments;
        this.sections = sections;
        this.lessons = lessons;
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

    /**
     * Chỉ giảng viên phụ trách (nội dung, bài tập/thực hành/kiểm tra, chấm điểm, thông báo).
     * Admin vẫn xem được qua requireManaged nhưng ở đây nhận TEACHER_ONLY: tách người quản trị khỏi người dạy,
     * điểm và đề chỉ do giảng viên phụ trách làm. Cần thay người thì admin đổi giảng viên phụ trách.
     */
    public Course requireTeaching(String publicId, AuthPrincipal me) {
        Course c = requireManaged(publicId, me);
        if (!isTeacherOf(c, me)) {
            throw new AppException(ErrorCode.TEACHER_ONLY);
        }
        return c;
    }

    public static boolean isTeacherOf(Course c, AuthPrincipal me) {
        return c.getTeacher() != null && c.getTeacher().getId() == me.userId();
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

    /** Chỉ admin (courses.assign): tạo khóa học và phân công giảng viên phụ trách. */
    @Transactional
    public CourseView create(CreateCourseRequest req, AuthPrincipal me, String ip) {
        User teacher = requireTeacher(req.teacherId());
        String code = req.code().trim();
        String group = blankToNull(req.group());
        if (group != null && courses.existsByCodeAndGroupName(code, group)) {
            throw new AppException(ErrorCode.COURSE_GROUP_TAKEN);
        }
        Course c = new Course(code, req.title().trim(), teacher);
        c.setGroupName(group);
        c.setDescription(blankToNull(req.description()));
        c.setFormat(req.format());
        courses.save(c);
        audit.record(me.userId(), AuditService.COURSE_CREATED, c.displayCode(),
                c.getTitle() + " · giảng viên " + teacher.getUsername(), ip);
        return view(c, false, 0);
    }

    /** Chỉ admin (courses.assign): đổi giảng viên phụ trách; giảng viên cũ mất quyền quản lý khóa học. */
    @Transactional
    public CourseView assignTeacher(String publicId, String teacherId, AuthPrincipal me) {
        Course c = requireManaged(publicId, me);
        User teacher = requireTeacher(teacherId);
        String before = c.getTeacher() == null ? "(chưa có)" : c.getTeacher().getUsername();
        c.setTeacher(teacher);
        audit.record(me.userId(), AuditService.COURSE_ASSIGNED, c.displayCode(), before + " → " + teacher.getUsername());
        return view(c, false, enrollments.countByCourseId(c.getId()));
    }

    /** Giảng viên đang hoạt động để chọn khi tạo/phân công khóa học (50 kết quả đầu theo mã). */
    @Transactional(readOnly = true)
    public List<UserPick> searchTeachers(String q) {
        String needle = q == null || q.isBlank() ? null : q.trim();
        return users.search(needle, Role.TEACHER, User.STATUS_ACTIVE, PageRequest.of(0, 50, Sort.by("username")))
                .stream()
                .map(u -> new UserPick(u.getPublicId(), u.getUsername(), u.getFullName())).toList();
    }

    private User requireTeacher(String publicId) {
        return users.findByPublicId(publicId)
                .filter(u -> u.getRole() == Role.TEACHER && u.isActive())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_TEACHER));
    }

    /** Sửa mã, nhóm, tên, mô tả; đổi dạng chỉ giữa "mục tự đặt tên" và "theo tuần" (dạng chỉ tài liệu không có mục/bài). */
    @Transactional
    public CourseView update(String publicId, UpdateCourseRequest req, AuthPrincipal me) {
        Course c = requireManaged(publicId, me);
        if (req.format() != c.getFormat()
                && (req.format() == Course.FORMAT_FILES_ONLY || c.getFormat() == Course.FORMAT_FILES_ONLY)) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Files-only format cannot be changed");
        }
        String code = req.code().trim();
        String group = blankToNull(req.group());
        if (group != null && courses.existsByCodeAndGroupNameAndIdNot(code, group, c.getId())) {
            throw new AppException(ErrorCode.COURSE_GROUP_TAKEN);
        }
        String before = c.displayCode() + " – " + c.getTitle();
        c.setCode(code);
        c.setGroupName(group);
        c.setTitle(req.title().trim());
        c.setDescription(blankToNull(req.description()));
        c.setFormat(req.format());
        audit.record(me.userId(), AuditService.COURSE_UPDATED, c.displayCode(), "was: " + before);
        return view(c, false, enrollments.countByCourseId(c.getId()));
    }

    @Transactional(readOnly = true)
    public CourseDetail managedDetail(String publicId, AuthPrincipal me) {
        return detail(requireManaged(publicId, me), false);
    }

    /** Xóa khóa học; DB xóa dây chuyền mục/file/bài/bài nộp/ghi danh, file trên đĩa xóa sau khi commit. */
    @Transactional
    public void delete(String publicId, AuthPrincipal me, String ip) {
        Course c = requireManaged(publicId, me);
        List<String> keys = new ArrayList<>(files.findByCourseIdOrderByCreatedAt(c.getId()).stream()
                .map(CourseFile::getStorageKey).toList());
        keys.addAll(submissionFiles.storageKeysByCourse(c.getId()));
        keys.addAll(leaveFiles.storageKeysByCourse(c.getId()));
        courses.delete(c);
        afterCommit(() -> keys.forEach(storage::deleteQuietly));
        audit.record(me.userId(), AuditService.COURSE_DELETED, c.displayCode(), c.getTitle(), ip);
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
        return enroll(c, found, Enrollment.METHOD_PASTE, notFound, me);
    }

    @Transactional
    public EnrollReport enrollByIds(String publicId, List<String> userIds, AuthPrincipal me) {
        Course c = requireManaged(publicId, me);
        List<User> found = new ArrayList<>();
        List<String> notFound = new ArrayList<>();
        userIds.stream().distinct()
                .forEach(id -> users.findByPublicId(id).ifPresentOrElse(found::add, () -> notFound.add(id)));
        return enroll(c, found, Enrollment.METHOD_PICK, notFound, me);
    }

    @Transactional
    public void unenroll(String publicId, String userPublicId, AuthPrincipal me) {
        Course c = requireManaged(publicId, me);
        User u = users.findByPublicId(userPublicId).orElseThrow(() -> AppException.notFound("Enrollment"));
        if (enrollments.deleteOne(c.getId(), u.getId()) == 0) {
            throw AppException.notFound("Enrollment");
        }
        audit.record(me.userId(), AuditService.STUDENT_UNENROLLED, c.displayCode(), u.getUsername());
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
        // Không ghi mã vào audit: ai đọc được audit cũng không tự vào được khóa học
        audit.record(me.userId(), AuditService.ENROLL_KEY_CREATED, c.displayCode(), null);
        return key;
    }

    @Transactional
    public void disableEnrollKey(String publicId, AuthPrincipal me) {
        Course c = requireManaged(publicId, me);
        c.setEnrollKey(null);
        audit.record(me.userId(), AuditService.ENROLL_KEY_DISABLED, c.displayCode(), null);
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
            audit.record(me.userId(), AuditService.COURSE_JOINED, c.displayCode(), me.username());
        }
        return summary(c, enrollments.countByCourseId(c.getId()));
    }

    // ================================================================ dựng view

    CourseDetail detail(Course c, boolean studentView) {
        List<CourseSection> secs = sections.findByCourseIdOrderByPosition(c.getId());
        Map<Long, String> sectionIds = secs.stream()
                .collect(Collectors.toMap(CourseSection::getId, CourseSection::getPublicId));
        List<Lesson> ls = lessons.findByCourseIdOrderByPositionAscIdAsc(c.getId());
        Map<Long, Lesson> lessonById = ls.stream().collect(Collectors.toMap(Lesson::getId, Function.identity()));
        List<CourseFile> fs = files.findByCourseIdOrderByCreatedAt(c.getId());
        Map<Long, CourseFile> fileById = fs.stream().collect(Collectors.toMap(CourseFile::getId, Function.identity()));
        Instant now = Instant.now();

        List<LessonView> lessonViews = ls.stream()
                .filter(l -> !studentView || !l.isHidden())
                .map(l -> lessonView(l, sectionIds.get(l.getSectionId())))
                .toList();
        // Đề riêng của bài không phải tài liệu: chỉ hiện qua bài. Sinh viên không thấy file bị ẩn,
        // cũng không thấy file đính kèm của bài bị ẩn
        List<FileView> fileViews = fs.stream()
                .filter(f -> !f.isPaper())
                .filter(f -> !studentView || (!f.isHidden() && !inHiddenLesson(f, lessonById)))
                .map(f -> fileView(f, sectionIds.get(f.getSectionId()),
                        Optional.ofNullable(f.getLessonId()).map(lessonById::get).map(Lesson::getPublicId)
                                .orElse(null)))
                .toList();
        List<Assessment> assessmentList = assessments.findByCourseIdOrderByCreatedAt(c.getId());
        // Số câu: bài trắc nghiệm đếm câu hỏi, bài lập trình đếm câu (đề)
        Map<Long, Long> questionCounts = new HashMap<>();
        List<Long> ids = assessmentList.stream().map(Assessment::getId).toList();
        // Bài tổng hợp: số câu trắc nghiệm (thẻ bài hiện các phần theo điểm tối đa)
        if (assessmentList.stream().anyMatch(Assessment::usesQuiz)) {
            quizQuestions.countByAssessmentIds(ids).forEach(r -> questionCounts.put((Long) r[0], (Long) r[1]));
        }
        List<Long> codeIds = assessmentList.stream().filter(Assessment::isCode).map(Assessment::getId).toList();
        if (!codeIds.isEmpty()) {
            assessments.countCodeProblems(codeIds).forEach(r -> questionCounts.put((Long) r[0], (Long) r[1]));
        }
        List<AssessmentView> assessmentViews = assessmentList.stream()
                .map(a -> {
                    CourseFile paper = Optional.ofNullable(a.getPaperFileId()).map(fileById::get).orElse(null);
                    // Sinh viên chỉ thấy đề khi bài đã mở; bài có giám sát thì chỉ trong phòng thi (trang làm bài)
                    boolean show = paper != null && (!studentView || (a.isOpened(now) && !a.isProctoredAt(now)));
                    return CourseViews.assessmentView(a, show ? paper : null,
                            questionCounts.getOrDefault(a.getId(), 0L), now);
                }).toList();
        return new CourseDetail(view(c, studentView, enrollments.countByCourseId(c.getId())),
                secs.stream().map(s -> new SectionView(s.getPublicId(), s.getPosition(), s.getTitle())).toList(),
                lessonViews, fileViews, assessmentViews);
    }

    static boolean inHiddenLesson(CourseFile f, Map<Long, Lesson> lessonById) {
        Lesson l = f.getLessonId() == null ? null : lessonById.get(f.getLessonId());
        return l != null && l.isHidden();
    }

    static FileView fileView(CourseFile f, String sectionPublicId, String lessonPublicId) {
        return new FileView(f.getPublicId(), sectionPublicId, lessonPublicId, f.getName(), f.getContentType(),
                f.getSize(), f.isHidden(), f.getCreatedAt());
    }

    static LessonView lessonView(Lesson l, String sectionPublicId) {
        return new LessonView(l.getPublicId(), sectionPublicId, l.getPosition(), l.getTitle(), l.getBody(),
                l.isHidden(), l.getCreatedAt(), l.getUpdatedAt());
    }

    private EnrollReport enroll(Course c, List<User> candidates, int method, List<String> notFound,
            AuthPrincipal me) {
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
        if (!added.isEmpty()) {
            audit.record(me.userId(), AuditService.STUDENTS_ENROLLED, c.displayCode(), added.size() + ": "
                    + String.join(", ", added.stream().map(UserPick::username).toList()));
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
        return new CourseSummary(c.getPublicId(), c.getCode(), c.getGroupName(), c.getTitle(), c.getFormat(),
                studentCount, teacherName(c), c.getCreatedAt());
    }

    private static CourseView view(Course c, boolean studentView, long studentCount) {
        return new CourseView(c.getPublicId(), c.getCode(), c.getGroupName(), c.getTitle(), c.getDescription(),
                c.getFormat(), studentView ? null : c.getEnrollKey(), studentCount,
                studentView || c.getTeacher() == null ? null : c.getTeacher().getPublicId(), teacherName(c),
                c.getCreatedAt());
    }

    private static String teacherName(Course c) {
        return c.getTeacher() == null ? null : c.getTeacher().getFullName();
    }

    public static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    public static void afterCommit(Runnable action) {
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
