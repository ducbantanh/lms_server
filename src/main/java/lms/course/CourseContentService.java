package lms.course;

import java.nio.file.Path;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lms.audit.AuditService;
import lms.auth.AuthPrincipal;
import lms.common.AppException;
import lms.common.ErrorCode;
import lms.course.CourseViews.AssessmentRequest;
import lms.course.CourseViews.AssessmentUpdateRequest;
import lms.course.CourseViews.AssessmentView;
import lms.course.CourseViews.FileView;
import lms.course.CourseViews.LessonRequest;
import lms.course.CourseViews.LessonView;
import lms.course.CourseViews.SectionView;
import lms.course.UploadChecker.Checked;
import lms.notification.NotificationService;
import lms.quiz.QuizQuestionRepository;
import lms.submission.SubmissionFileRepository;

/**
 * Nội dung khóa học: mục/tuần, bài học, tài liệu, bài kiểm tra; và tải tài liệu có kiểm tra quyền.
 * Mọi thay đổi chỉ giảng viên phụ trách làm được (requireTeaching; admin chỉ xem, tải file vẫn được)
 * và ghi audit với target là mã khóa học.
 */
@Service
public class CourseContentService {

    public static final String PDF = "application/pdf";

    /** File để controller trả về: metadata + đường dẫn trên đĩa. */
    public record Download(CourseFile file, Path path) {
    }

    private final CourseService courses;
    private final CourseRepository courseRepo;
    private final CourseSectionRepository sections;
    private final LessonRepository lessons;
    private final CourseFileRepository files;
    private final AssessmentRepository assessments;
    private final EnrollmentRepository enrollments;
    private final SubmissionFileRepository submissionFiles;
    private final QuizQuestionRepository quizQuestions;
    private final FileStorage storage;
    private final UploadChecker uploads;
    private final NotificationService notifications;
    private final AuditService audit;

    public CourseContentService(CourseService courses, CourseRepository courseRepo,
            CourseSectionRepository sections, LessonRepository lessons, CourseFileRepository files,
            AssessmentRepository assessments, EnrollmentRepository enrollments,
            SubmissionFileRepository submissionFiles, FileStorage storage, UploadChecker uploads,
            NotificationService notifications, AuditService audit, QuizQuestionRepository quizQuestions) {
        this.notifications = notifications;
        this.quizQuestions = quizQuestions;
        this.courses = courses;
        this.courseRepo = courseRepo;
        this.sections = sections;
        this.lessons = lessons;
        this.files = files;
        this.assessments = assessments;
        this.enrollments = enrollments;
        this.submissionFiles = submissionFiles;
        this.storage = storage;
        this.uploads = uploads;
        this.audit = audit;
    }

    // ---------------------------------------------------------------- mục

    @Transactional
    public SectionView addSection(String courseId, String title, AuthPrincipal me) {
        Course c = courses.requireTeaching(courseId, me);
        if (c.getFormat() == Course.FORMAT_FILES_ONLY) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "This course has no sections");
        }
        CourseSection s = sections.save(new CourseSection(c.getId(), sections.maxPosition(c.getId()) + 1,
                title.trim()));
        audit.record(me.userId(), AuditService.SECTION_CREATED, c.displayCode(), s.getTitle());
        return sectionView(s);
    }

    @Transactional
    public SectionView renameSection(String courseId, String sectionId, String title, AuthPrincipal me) {
        Course c = courses.requireTeaching(courseId, me);
        CourseSection s = requireSection(c, sectionId);
        String before = s.getTitle();
        s.setTitle(title.trim());
        audit.record(me.userId(), AuditService.SECTION_UPDATED, c.displayCode(), before + " → " + s.getTitle());
        return sectionView(s);
    }

    /** ids phải là đúng tập mục hiện có của khóa học; vị trí mới là 1..n theo thứ tự gửi lên. */
    @Transactional
    public List<SectionView> reorderSections(String courseId, List<String> ids, AuthPrincipal me) {
        Course c = courses.requireTeaching(courseId, me);
        List<CourseSection> list = sections.findByCourseIdOrderByPosition(c.getId());
        Map<String, CourseSection> byId = requireSameSet(list, CourseSection::getPublicId, ids);
        for (int i = 0; i < ids.size(); i++) {
            byId.get(ids.get(i)).setPosition(i + 1);
        }
        audit.record(me.userId(), AuditService.SECTIONS_REORDERED, c.displayCode(), null);
        return ids.stream().map(byId::get).map(CourseContentService::sectionView).toList();
    }

    /** Xóa mục; bài học và tài liệu trong mục chuyển sang "chưa phân mục". */
    @Transactional
    public void deleteSection(String courseId, String sectionId, AuthPrincipal me) {
        Course c = courses.requireTeaching(courseId, me);
        CourseSection s = requireSection(c, sectionId);
        lessons.detachFromSection(s.getId());
        files.detachFromSection(s.getId());
        sections.delete(s);
        audit.record(me.userId(), AuditService.SECTION_DELETED, c.displayCode(), s.getTitle());
    }

    private CourseSection requireSection(Course c, String sectionId) {
        return sections.findByPublicIdAndCourseId(sectionId, c.getId())
                .orElseThrow(() -> AppException.notFound("Section"));
    }

    private static SectionView sectionView(CourseSection s) {
        return new SectionView(s.getPublicId(), s.getPosition(), s.getTitle());
    }

    // ---------------------------------------------------------------- bài học

    @Transactional
    public LessonView createLesson(String courseId, LessonRequest req, AuthPrincipal me) {
        Course c = courses.requireTeaching(courseId, me);
        if (c.getFormat() == Course.FORMAT_FILES_ONLY) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "This course has no lessons");
        }
        CourseSection s = optionalSection(c, req.sectionId());
        Long sid = s == null ? null : s.getId();
        Lesson l = lessons.save(new Lesson(c.getId(), sid, lessons.maxPosition(c.getId(), sid) + 1,
                req.title().trim(), CourseService.blankToNull(req.body()), req.hidden()));
        // Bài đang ẩn thì sinh viên chưa thấy: không thông báo dù giảng viên có chọn
        if (req.notifyStudents() && !req.hidden()) {
            notifications.lessonCreated(c, l.getTitle(), s == null ? null : s.getTitle(), me.userId());
        }
        audit.record(me.userId(), AuditService.LESSON_CREATED, c.displayCode(), l.getTitle());
        return CourseService.lessonView(l, s == null ? null : s.getPublicId());
    }

    /** Ghi đè tiêu đề, nội dung, trạng thái ẩn; đổi mục thì bài xuống cuối mục mới, file đính kèm đi theo. */
    @Transactional
    public LessonView updateLesson(String courseId, String lessonId, LessonRequest req, AuthPrincipal me) {
        Course c = courses.requireTeaching(courseId, me);
        Lesson l = requireLesson(c, lessonId);
        CourseSection s = optionalSection(c, req.sectionId());
        Long sid = s == null ? null : s.getId();
        if (!Objects.equals(sid, l.getSectionId())) {
            l.moveTo(sid, lessons.maxPosition(c.getId(), sid) + 1);
            files.moveWithLesson(l.getId(), sid);
        }
        boolean wasHidden = l.isHidden();
        l.setTitle(req.title().trim());
        l.setBody(CourseService.blankToNull(req.body()));
        l.setHidden(req.hidden());
        // flush để @PreUpdate cập nhật updatedAt trước khi trả về
        lessons.saveAndFlush(l);
        audit.record(me.userId(), AuditService.LESSON_UPDATED, c.displayCode(), l.getTitle()
                + (wasHidden == l.isHidden() ? "" : l.isHidden() ? " (ẩn)" : " (hiện)"));
        return CourseService.lessonView(l, s == null ? null : s.getPublicId());
    }

    /** ids phải là đúng tập bài (kể cả bài ẩn) trong mục sectionId; vị trí mới là 1..n. */
    @Transactional
    public void reorderLessons(String courseId, String sectionId, List<String> ids, AuthPrincipal me) {
        Course c = courses.requireTeaching(courseId, me);
        CourseSection s = optionalSection(c, sectionId);
        Long sid = s == null ? null : s.getId();
        List<Lesson> list = lessons.findByCourseIdOrderByPositionAscIdAsc(c.getId()).stream()
                .filter(l -> Objects.equals(l.getSectionId(), sid)).toList();
        Map<String, Lesson> byId = requireSameSet(list, Lesson::getPublicId, ids);
        for (int i = 0; i < ids.size(); i++) {
            byId.get(ids.get(i)).setPosition(i + 1);
        }
        audit.record(me.userId(), AuditService.LESSONS_REORDERED, c.displayCode(),
                s == null ? "Chưa phân mục" : s.getTitle());
    }

    /** Xóa bài; tài liệu đính kèm được giữ lại trong mục của bài. */
    @Transactional
    public void deleteLesson(String courseId, String lessonId, AuthPrincipal me) {
        Course c = courses.requireTeaching(courseId, me);
        Lesson l = requireLesson(c, lessonId);
        files.detachFromLesson(l.getId());
        lessons.delete(l);
        audit.record(me.userId(), AuditService.LESSON_DELETED, c.displayCode(), l.getTitle());
    }

    private Lesson requireLesson(Course c, String lessonId) {
        return lessons.findByPublicIdAndCourseId(lessonId, c.getId())
                .orElseThrow(() -> AppException.notFound("Lesson"));
    }

    /** Mục theo public id; trống là "chưa phân mục". */
    private CourseSection optionalSection(Course c, String sectionId) {
        if (sectionId == null || sectionId.isBlank()) {
            return null;
        }
        return requireSection(c, sectionId);
    }

    /** Danh sách id gửi lên phải trùng khít (không trùng lặp, không thiếu, không thừa) với các phần tử hiện có. */
    static <T> Map<String, T> requireSameSet(List<T> current, Function<T, String> publicId, List<String> ids) {
        Map<String, T> byId = current.stream().collect(Collectors.toMap(publicId, Function.identity()));
        if (ids.size() != byId.size() || !byId.keySet().equals(new HashSet<>(ids))) {
            throw new AppException(ErrorCode.STALE_ORDER);
        }
        return byId;
    }

    // ---------------------------------------------------------------- tài liệu

    /**
     * lessonId có giá trị thì file đính kèm bài đó và nằm trong mục của bài (sectionId bị bỏ qua).
     * notifyStudents: gửi thông báo nếu sinh viên thấy được file (file và bài học chứa nó không bị ẩn).
     */
    @Transactional
    public FileView upload(String courseId, MultipartFile upload, String sectionId, String lessonId,
            boolean hidden, boolean notifyStudents, AuthPrincipal me) {
        Course c = courses.requireTeaching(courseId, me);
        Checked file = uploads.check(upload, UploadChecker.MATERIAL);
        CourseSection section = null;
        Lesson lesson = null;
        if (c.getFormat() != Course.FORMAT_FILES_ONLY) {
            if (lessonId != null && !lessonId.isBlank()) {
                lesson = requireLesson(c, lessonId);
                section = lesson.getSectionId() == null ? null : sections.findById(lesson.getSectionId()).orElse(null);
            } else {
                section = optionalSection(c, sectionId);
            }
        }
        String key = storage.store(file.data());
        try {
            CourseFile f = files.save(new CourseFile(c.getId(), section == null ? null : section.getId(),
                    lesson == null ? null : lesson.getId(), file.name(), file.contentType(), file.data().length,
                    hidden, key));
            if (notifyStudents && !hidden && (lesson == null || !lesson.isHidden())) {
                notifications.fileUploaded(c, f.getName(),
                        lesson != null ? lesson.getTitle() : section == null ? null : section.getTitle(), me.userId());
            }
            audit.record(me.userId(), AuditService.FILE_UPLOADED, c.displayCode(), f.getName() + (hidden ? " (ẩn)" : ""));
            return CourseService.fileView(f, section == null ? null : section.getPublicId(),
                    lesson == null ? null : lesson.getPublicId());
        } catch (RuntimeException e) {
            storage.deleteQuietly(key);
            throw e;
        }
    }

    @Transactional
    public void setHidden(String courseId, String fileId, boolean hidden, AuthPrincipal me) {
        Course c = courses.requireTeaching(courseId, me);
        CourseFile f = requireMaterial(c, fileId);
        f.setHidden(hidden);
        audit.record(me.userId(), hidden ? AuditService.FILE_HIDDEN : AuditService.FILE_SHOWN, c.displayCode(),
                f.getName());
    }

    /** Xóa tài liệu; bài kiểm tra dùng file này làm đề sẽ thành "không có đề" (FK SET NULL). */
    @Transactional
    public void deleteFile(String courseId, String fileId, AuthPrincipal me) {
        Course c = courses.requireTeaching(courseId, me);
        CourseFile f = requireMaterial(c, fileId);
        files.delete(f);
        CourseService.afterCommit(() -> storage.deleteQuietly(f.getStorageKey()));
        audit.record(me.userId(), AuditService.FILE_DELETED, c.displayCode(), f.getName());
    }

    /** Tài liệu của khóa học; đề riêng của bài không được quản lý qua API tài liệu (NOT_FOUND). */
    private CourseFile requireMaterial(Course c, String fileId) {
        return files.findByPublicIdAndCourseId(fileId, c.getId()).filter(f -> !f.isPaper())
                .orElseThrow(() -> AppException.notFound("File"));
    }

    // ---------------------------------------------------------------- bài kiểm tra

    /**
     * Tạo bài. Đề là một PDF trong tài liệu (paperFileId), hoặc file tải lên kèm (paperUpload, khi đó
     * paperFileId bị bỏ qua): file tải lên chỉ thuộc bài này, không nằm trong tài liệu khóa học.
     */
    @Transactional
    public AssessmentView createAssessment(String courseId, AssessmentRequest req, MultipartFile paperUpload,
            AuthPrincipal me) {
        Course c = courses.requireTeaching(courseId, me);
        requireWindow(req.opensAt(), req.closesAt());
        requireScoreRelease(req.scoreRelease(), req.closesAt());
        if (paperUpload != null) {
            Checked file = uploads.check(paperUpload, UploadChecker.MATERIAL);
            if (!PDF.equals(file.contentType())) {
                throw new AppException(ErrorCode.UNSUPPORTED_FILE_TYPE, "Paper must be a PDF");
            }
            String key = storage.store(file.data());
            try {
                CourseFile paper = files.save(CourseFile.paper(c.getId(), file.name(), file.data().length, key));
                return saveAssessment(c, req, paper, me);
            } catch (RuntimeException e) {
                storage.deleteQuietly(key);
                throw e;
            }
        }
        CourseFile paper = null;
        if (req.paperFileId() != null && !req.paperFileId().isBlank()) {
            // Đề riêng của bài khác không dùng lại được: xóa bài đó sẽ xóa luôn file
            paper = requireMaterial(c, req.paperFileId());
            if (!PDF.equals(paper.getContentType())) {
                throw new AppException(ErrorCode.UNSUPPORTED_FILE_TYPE, "Paper must be a PDF");
            }
            // Đề thi không được lộ trong danh sách tài liệu trước giờ thi
            if (req.type() == Assessment.TYPE_EXAM) {
                paper.setHidden(true);
            }
        }
        return saveAssessment(c, req, paper, me);
    }

    private AssessmentView saveAssessment(Course c, AssessmentRequest req, CourseFile paper, AuthPrincipal me) {
        if (req.type() == Assessment.TYPE_EXAM && paper == null) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "An exam needs a PDF paper");
        }
        Assessment a = new Assessment(c.getId(), req.type(), req.title().trim(),
                CourseService.blankToNull(req.description()), paper == null ? null : paper.getId(),
                req.opensAt(), req.closesAt());
        a.setScoreRelease(req.scoreRelease());
        a = assessments.save(a);
        notifications.assessmentCreated(c, a, req.notifyStudents(), me.userId());
        audit.record(me.userId(), AuditService.ASSESSMENT_CREATED, c.displayCode(), a.getTitle());
        return teacherView(a, paper);
    }

    /**
     * Sửa tiêu đề, mô tả, giờ mở/đóng. Bài kiểm tra đã kết thúc không sửa được (ASSESSMENT_CLOSED),
     * để không mở lại bài thi đã chốt điểm.
     */
    @Transactional
    public AssessmentView updateAssessment(String courseId, String assessmentId, AssessmentUpdateRequest req,
            AuthPrincipal me) {
        Course c = courses.requireTeaching(courseId, me);
        Assessment a = requireAssessment(c, assessmentId);
        if (a.isLocked(Instant.now())) {
            throw new AppException(ErrorCode.ASSESSMENT_CLOSED);
        }
        requireWindow(req.opensAt(), req.closesAt());
        // Bài có giám sát cần giờ đóng: hết giờ thì sinh viên tự ra khỏi chế độ thi (lms.proctor)
        if (a.isProctored() && a.getType() == Assessment.TYPE_EXAM && req.closesAt() == null) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "CLOSE_TIME_REQUIRED");
        }
        a.update(req.title().trim(), CourseService.blankToNull(req.description()), req.opensAt(), req.closesAt());
        audit.record(me.userId(), AuditService.ASSESSMENT_UPDATED, c.displayCode(), a.getTitle() + " · mở "
                + (a.getOpensAt() == null ? "ngay" : a.getOpensAt()) + " · đóng "
                + (a.getClosesAt() == null ? "không hạn" : a.getClosesAt()));
        CourseFile paper = a.getPaperFileId() == null ? null : files.findById(a.getPaperFileId()).orElse(null);
        return teacherView(a, paper);
    }

    /**
     * Xóa bài; đề lấy từ tài liệu vẫn giữ trong tài liệu, đề tải lên riêng cho bài bị xóa theo.
     * Bài kiểm tra đã kết thúc không xóa được (ASSESSMENT_CLOSED).
     */
    @Transactional
    public void deleteAssessment(String courseId, String assessmentId, AuthPrincipal me) {
        Course c = courses.requireTeaching(courseId, me);
        Assessment a = requireAssessment(c, assessmentId);
        if (!a.isDeletable(Instant.now())) {
            throw new AppException(ErrorCode.ASSESSMENT_CLOSED);
        }
        // DB xóa dây chuyền bài nộp; file bài làm trên đĩa xóa sau khi commit
        List<String> submissionKeys = submissionFiles.storageKeysByAssessment(a.getId());
        assessments.delete(a);
        CourseService.afterCommit(() -> submissionKeys.forEach(storage::deleteQuietly));
        if (a.getPaperFileId() != null) {
            files.findById(a.getPaperFileId()).filter(CourseFile::isPaper).ifPresent(f -> {
                files.delete(f);
                CourseService.afterCommit(() -> storage.deleteQuietly(f.getStorageKey()));
            });
        }
        audit.record(me.userId(), AuditService.ASSESSMENT_DELETED, c.displayCode(),
                a.getTitle() + " (" + submissionKeys.size() + " file bài nộp)");
    }

    private Assessment requireAssessment(Course c, String assessmentId) {
        return assessments.findByPublicIdAndCourseId(assessmentId, c.getId())
                .orElseThrow(() -> AppException.notFound("Assessment"));
    }

    /** Công bố điểm sau giờ đóng mà bài không có giờ đóng thì không bao giờ tới lúc công bố: báo lỗi. */
    public static void requireScoreRelease(int scoreRelease, Instant closesAt) {
        if (scoreRelease == Assessment.SCORE_AFTER_CLOSE && closesAt == null) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Score release after close needs closesAt");
        }
    }

    private static void requireWindow(Instant opensAt, Instant closesAt) {
        if (opensAt != null && closesAt != null && !closesAt.isAfter(opensAt)) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "closesAt must be after opensAt");
        }
    }

    /** Góc nhìn giảng viên: luôn thấy đề. */
    private AssessmentView teacherView(Assessment a, CourseFile paper) {
        return CourseViews.assessmentView(a, paper, a.usesQuiz() ? quizQuestions.countByAssessmentId(a.getId())
                : a.isCode() ? assessments.countCodeProblems(a.getId()) : 0,
                Instant.now());
    }

    // ---------------------------------------------------------------- tải file

    /**
     * Người quản lý khóa học tải mọi file. Sinh viên đã ghi danh tải file không ẩn (và không thuộc
     * bài học bị ẩn), hoặc file ẩn là đề của một bài đã mở. Còn lại NOT_FOUND.
     */
    @Transactional(readOnly = true)
    public Download download(String fileId, AuthPrincipal me) {
        CourseFile f = files.findByPublicId(fileId).orElseThrow(() -> AppException.notFound("File"));
        Course c = courseRepo.findById(f.getCourseId()).orElseThrow(() -> AppException.notFound("File"));
        boolean manager = c.isManagedBy(me.userId(), me.role() == lms.auth.Role.ADMIN);
        if (!manager) {
            boolean enrolled = enrollments.existsByCourseIdAndUserId(c.getId(), me.userId());
            Instant now = Instant.now();
            boolean openPaper = assessments.findByPaperFileId(f.getId()).stream().anyMatch(a -> a.isOpened(now));
            boolean lessonHidden = f.getLessonId() != null
                    && lessons.findById(f.getLessonId()).map(Lesson::isHidden).orElse(false);
            if (!enrolled || ((f.isHidden() || lessonHidden) && !openPaper)) {
                throw AppException.notFound("File");
            }
        }
        return new Download(f, storage.path(f.getStorageKey()));
    }
}
