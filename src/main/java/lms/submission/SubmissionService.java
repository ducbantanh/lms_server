package lms.submission;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lms.auth.AuthPrincipal;
import lms.auth.Role;
import lms.common.AppException;
import lms.common.ErrorCode;
import lms.config.AppProperties;
import lms.course.Assessment;
import lms.course.AssessmentRepository;
import lms.course.Course;
import lms.course.CourseRepository;
import lms.course.Enrollment;
import lms.course.EnrollmentRepository;
import lms.course.FileStorage;
import lms.submission.SubmissionViews.GradeRequest;
import lms.submission.SubmissionViews.StudentSubmissionView;
import lms.submission.SubmissionViews.TeacherSubmissionRow;
import lms.user.User;
import lms.user.UserRepository;

@Service
public class SubmissionService {

    public record Download(Path path, String fileName, String contentType, long size) {
    }

    private final SubmissionRepository submissions;
    private final AssessmentRepository assessments;
    private final CourseRepository courses;
    private final EnrollmentRepository enrollments;
    private final UserRepository users;
    private final FileStorage fileStorage;
    private final AppProperties props;

    public SubmissionService(SubmissionRepository submissions,
                             AssessmentRepository assessments,
                             CourseRepository courses,
                             EnrollmentRepository enrollments,
                             UserRepository users,
                             FileStorage fileStorage,
                             AppProperties props) {
        this.submissions = submissions;
        this.assessments = assessments;
        this.courses = courses;
        this.enrollments = enrollments;
        this.users = users;
        this.fileStorage = fileStorage;
        this.props = props;
    }

    /** Sinh viên nộp hoặc nộp lại bài làm. */
    @Transactional
    public StudentSubmissionView submit(String assessmentPublicId, MultipartFile file, AuthPrincipal me) {
        Assessment assessment = assessments.findByPublicId(assessmentPublicId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        Course course = courses.findById(assessment.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        if (!enrollments.existsByCourseIdAndUserId(course.getId(), me.userId())) {
            throw new AppException(ErrorCode.NOT_ENROLLED);
        }

        Instant now = Instant.now();
        if (!assessment.isOpened(now)) {
            throw new AppException(ErrorCode.ASSESSMENT_NOT_OPEN);
        }
        if (assessment.getClosesAt() != null && assessment.getClosesAt().isBefore(now)) {
            throw new AppException(ErrorCode.ASSESSMENT_CLOSED);
        }

        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "File is required");
        }
        if (file.getSize() > props.storage().maxFileBytes()) {
            throw new AppException(ErrorCode.PAYLOAD_TOO_LARGE);
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read upload", e);
        }

        String storageKey = fileStorage.store(bytes);
        String originalName = file.getOriginalFilename() == null ? "submission.bin" : file.getOriginalFilename();
        String contentType = file.getContentType() == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : file.getContentType();

        Optional<Submission> existing = submissions.findByAssessmentIdAndUserId(assessment.getId(), me.userId());
        Submission submission;
        if (existing.isPresent()) {
            submission = existing.get();
            fileStorage.deleteQuietly(submission.getStorageKey());
            submission.updateFile(originalName, storageKey, contentType, file.getSize());
        } else {
            submission = new Submission(assessment.getId(), me.userId(), originalName, storageKey, contentType, file.getSize());
        }

        return StudentSubmissionView.from(submissions.save(submission));
    }

    /** Sinh viên xem bài nộp của chính mình. */
    @Transactional(readOnly = true)
    public StudentSubmissionView getMySubmission(String assessmentPublicId, AuthPrincipal me) {
        Assessment assessment = assessments.findByPublicId(assessmentPublicId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        return submissions.findByAssessmentIdAndUserId(assessment.getId(), me.userId())
                .map(StudentSubmissionView::from)
                .orElse(null);
    }

    /** Giảng viên xem danh sách toàn bộ bài nộp của assessment. */
    @Transactional(readOnly = true)
    public List<TeacherSubmissionRow> listSubmissions(String assessmentPublicId, AuthPrincipal me) {
        Assessment assessment = assessments.findByPublicId(assessmentPublicId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        Course course = courses.findById(assessment.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        ensureCanManageCourse(course, me);

        // Danh sách sinh viên ghi danh
        List<Object[]> enrolled = enrollments.findWithUsers(course.getId());
        List<Submission> subList = submissions.findByAssessmentId(assessment.getId());

        Map<Long, Submission> subMap = new HashMap<>();
        for (Submission s : subList) {
            subMap.put(s.getUserId(), s);
        }

        // Lấy tên giảng viên chấm bài
        Map<Long, String> graderNames = new HashMap<>();

        List<TeacherSubmissionRow> rows = new ArrayList<>(enrolled.size());
        for (Object[] row : enrolled) {
            User student = (User) row[1];
            Submission s = subMap.get(student.getId());

            String graderName = null;
            if (s != null && s.getGradedBy() != null) {
                graderName = graderNames.computeIfAbsent(s.getGradedBy(),
                        id -> users.findById(id).map(User::getFullName).orElse(null));
            }

            rows.add(new TeacherSubmissionRow(
                    s == null ? null : s.getPublicId(),
                    student.getPublicId(),
                    student.getUsername(),
                    student.getFullName(),
                    s == null ? null : s.getFileName(),
                    s == null ? 0 : s.getSizeBytes(),
                    s == null ? null : s.getSubmittedAt(),
                    s == null ? null : s.getScore(),
                    s == null ? null : s.getFeedback(),
                    s == null ? null : s.getGradedAt(),
                    graderName
            ));
        }

        return rows;
    }

    /** Giảng viên chấm điểm cho một bài nộp. */
    @Transactional
    public TeacherSubmissionRow grade(String submissionPublicId, GradeRequest req, AuthPrincipal me) {
        Submission submission = submissions.findByPublicId(submissionPublicId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        Assessment assessment = assessments.findById(submission.getAssessmentId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        Course course = courses.findById(assessment.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        ensureCanManageCourse(course, me);

        submission.grade(req.score(), req.feedback(), me.userId());
        Submission saved = submissions.save(submission);

        User student = users.findById(saved.getUserId()).orElseThrow();
        return new TeacherSubmissionRow(
                saved.getPublicId(),
                student.getPublicId(),
                student.getUsername(),
                student.getFullName(),
                saved.getFileName(),
                saved.getSizeBytes(),
                saved.getSubmittedAt(),
                saved.getScore(),
                saved.getFeedback(),
                saved.getGradedAt(),
                me.fullName()
        );
    }

    /** Tải file bài nộp (sinh viên chính chủ hoặc giảng viên quản lý khóa học). */
    @Transactional(readOnly = true)
    public Download download(String submissionPublicId, AuthPrincipal me) {
        Submission submission = submissions.findByPublicId(submissionPublicId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        Assessment assessment = assessments.findById(submission.getAssessmentId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        Course course = courses.findById(assessment.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        boolean isStudentOwner = submission.getUserId().equals(me.userId());
        boolean isCourseManager = me.role() == Role.ADMIN
                || (course.getTeacher() != null && course.getTeacher().getId().equals(me.userId()));

        if (!isStudentOwner && !isCourseManager) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        Path path = fileStorage.path(submission.getStorageKey());
        return new Download(path, submission.getFileName(), submission.getContentType(), submission.getSizeBytes());
    }

    private void ensureCanManageCourse(Course course, AuthPrincipal me) {
        if (me.role() == Role.ADMIN) return;
        if (course.getTeacher() == null || !course.getTeacher().getId().equals(me.userId())) {
            throw new AppException(ErrorCode.FORBIDDEN, "Not the teacher of this course");
        }
    }
}
