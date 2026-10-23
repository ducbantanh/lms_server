package lms.quiz;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lms.audit.AuditService;
import lms.auth.AuthPrincipal;
import lms.common.AppException;
import lms.common.ErrorCode;
import lms.course.Assessment;
import lms.course.AssessmentRepository;
import lms.course.Course;
import lms.course.CourseContentService;
import lms.course.CourseRepository;
import lms.course.CourseService;
import lms.course.CourseViews;
import lms.course.CourseViews.AssessmentView;
import lms.course.EnrollmentRepository;
import lms.notification.NotificationService;
import lms.question.BankQuestion;
import lms.question.BankQuestionRepository;
import lms.quiz.QuizViews.AnswerSaved;
import lms.quiz.QuizViews.CreateQuizRequest;
import lms.quiz.QuizViews.OptionView;
import lms.quiz.QuizViews.QuestionStat;
import lms.quiz.QuizViews.QuestionView;
import lms.quiz.QuizViews.QuizState;
import lms.quiz.QuizViews.ResultView;
import lms.quiz.QuizViews.StudentReview;
import lms.quiz.QuizViews.TeacherQuiz;
import lms.submission.Submission;
import lms.submission.SubmissionRepository;
import lms.user.User;
import lms.user.UserRepository;

/**
 * Bài trắc nghiệm (Assessment.MODE_QUIZ). Giảng viên phụ trách tạo bài từ câu hỏi trong ngân hàng của mình
 * (câu hỏi được chép sang bài). Sinh viên làm một lần: bấm "Bắt đầu", mỗi lần chọn đáp án được lưu ngay,
 * bấm nộp hoặc hết giờ (QuizSweeper) thì server chốt bài và chấm (QuizGrader). Hạn làm bài là giờ đóng,
 * hoặc giờ bắt đầu + số phút nếu sớm hơn; server kiểm tra hạn, đồng hồ ở client chỉ để hiển thị.
 */
@Service
public class QuizService {

    public static final int MAX_QUESTIONS = 200;

    private final AssessmentRepository assessments;
    private final CourseRepository courses;
    private final CourseService courseService;
    private final EnrollmentRepository enrollments;
    private final SubmissionRepository submissions;
    private final UserRepository users;
    private final QuizQuestionRepository questions;
    private final QuizAnswerRepository answers;
    private final BankQuestionRepository bankQuestions;
    private final NotificationService notifications;
    private final AuditService audit;

    public QuizService(AssessmentRepository assessments, CourseRepository courses, CourseService courseService,
            EnrollmentRepository enrollments, SubmissionRepository submissions, UserRepository users,
            QuizQuestionRepository questions, QuizAnswerRepository answers, BankQuestionRepository bankQuestions,
            NotificationService notifications, AuditService audit) {
        this.assessments = assessments;
        this.courses = courses;
        this.courseService = courseService;
        this.enrollments = enrollments;
        this.submissions = submissions;
        this.users = users;
        this.questions = questions;
        this.answers = answers;
        this.bankQuestions = bankQuestions;
        this.notifications = notifications;
        this.audit = audit;
    }

    // ================================================================ giảng viên

    /** Tạo bài thực hành / kiểm tra trắc nghiệm; luôn thông báo cho sinh viên như bài nộp file cùng loại. */
    @Transactional
    public AssessmentView create(String courseId, CreateQuizRequest req, AuthPrincipal me) {
        Course c = courseService.requireTeaching(courseId, me);
        if (req.opensAt() != null && req.closesAt() != null && !req.closesAt().isAfter(req.opensAt())) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "closesAt must be after opensAt");
        }
        CourseContentService.requireScoreRelease(req.scoreRelease(), req.closesAt());
        List<String> ids = req.questionIds().stream().distinct().toList();
        Map<String, BankQuestion> found = bankQuestions.findOwnedByPublicIds(ids, me.userId()).stream()
                .collect(Collectors.toMap(BankQuestion::getPublicId, Function.identity()));
        if (found.size() != ids.size()) {
            throw AppException.notFound("Question");
        }
        Assessment a = assessments.save(Assessment.quiz(c.getId(), req.type(), req.title().strip(),
                CourseService.blankToNull(req.description()), req.opensAt(), req.closesAt(),
                req.timeLimitMinutes(), req.shuffle(), req.revealMode(), req.scoreRelease()));
        questions.saveAll(IntStream.range(0, ids.size()).mapToObj(i -> {
            BankQuestion b = found.get(ids.get(i));
            return new QuizQuestion(a.getId(), i, b.getBody(), b.isMultiple(), b.getExplanation(), b.getId(),
                    b.getOptions().stream().map(o -> new QuizOption(o.getBody(), o.isCorrect())).toList());
        }).toList());
        notifications.assessmentCreated(c, a, true, me.userId());
        audit.record(me.userId(), AuditService.ASSESSMENT_CREATED, c.displayCode(),
                a.getTitle() + " (trắc nghiệm " + ids.size() + " câu)");
        return CourseViews.assessmentView(a, null, ids.size(), Instant.now());
    }

    /** Đề của bài (thứ tự gốc, kèm đáp án) và tỷ lệ đúng từng câu trên các bài đã chốt; admin xem được. */
    @Transactional
    public TeacherQuiz teacherQuiz(String courseId, String assessmentId, AuthPrincipal me) {
        Assessment a = requireManagedQuiz(courseId, assessmentId, me);
        Instant now = Instant.now();
        List<QuizQuestion> qs = questions.findByAssessmentIdOrderByPosition(a.getId());
        List<Submission> finished = new ArrayList<>();
        for (Submission s : submissions.findByAssessmentId(a.getId())) {
            finalizeIfDue(a, s, now);
            if (s.isSubmitted()) {
                finished.add(s);
            }
        }
        Map<Long, Map<Long, Set<Integer>>> chosenBySubmission = finished.isEmpty() ? Map.of()
                : groupChosen(answers.findBySubmissionIdIn(finished.stream().map(Submission::getId).toList()));
        List<QuestionStat> stats = qs.stream().map(q -> {
            int answered = 0;
            int correct = 0;
            for (Map<Long, Set<Integer>> chosen : chosenBySubmission.values()) {
                Set<Integer> picked = chosen.get(q.getId());
                if (picked != null) {
                    answered++;
                    if (QuizGrader.isCorrect(q, picked)) {
                        correct++;
                    }
                }
            }
            return new QuestionStat(q.getPublicId(), answered, correct);
        }).toList();
        return new TeacherQuiz(qs.stream().map(q -> view(q, Set.of(), true, null)).toList(), stats,
                finished.size());
    }

    /** Bài làm của một sinh viên (thứ tự gốc của đề, kèm đáp án đúng) để giảng viên xem khi chấm/khiếu nại. */
    @Transactional
    public StudentReview studentReview(String courseId, String assessmentId, String studentId, AuthPrincipal me) {
        Assessment a = requireManagedQuiz(courseId, assessmentId, me);
        User u = users.findByPublicId(studentId)
                .filter(x -> enrollments.existsByCourseIdAndUserId(a.getCourseId(), x.getId()))
                .orElseThrow(() -> AppException.notFound("Student"));
        Submission s = submissions.findByAssessmentIdAndUserId(a.getId(), u.getId()).orElse(null);
        List<QuizQuestion> qs = questions.findByAssessmentIdOrderByPosition(a.getId());
        if (s == null || s.getStartedAt() == null) {
            return new StudentReview(u.getPublicId(), u.getUsername(), u.getFullName(), null, null, null,
                    qs.stream().map(q -> view(q, Set.of(), true, null)).toList());
        }
        finalizeIfDue(a, s, Instant.now());
        Map<Long, Set<Integer>> chosen = chosenBy(s);
        return new StudentReview(u.getPublicId(), u.getUsername(), u.getFullName(), s.getStartedAt(),
                s.getSubmittedAt(), s.isSubmitted() ? result(s, qs, chosen) : null,
                qs.stream().map(q -> view(q, chosen.getOrDefault(q.getId(), Set.of()), true, null)).toList());
    }

    // ================================================================ sinh viên

    @Transactional
    public QuizState state(String assessmentId, AuthPrincipal me) {
        Target t = requireEnrolledQuiz(assessmentId, me);
        Instant now = Instant.now();
        Submission s = submissions.findByAssessmentIdAndUserId(t.assessment().getId(), me.userId()).orElse(null);
        if (s != null) {
            finalizeIfDue(t.assessment(), s, now);
        }
        return state(t.assessment(), s, now);
    }

    /** Bắt đầu làm bài (gọi lại khi đã bắt đầu thì chỉ trả về trạng thái hiện tại). */
    @Transactional
    public QuizState start(String assessmentId, AuthPrincipal me) {
        Target t = requireEnrolledQuiz(assessmentId, me);
        Assessment a = t.assessment();
        Instant now = nowMillis();
        if (!a.isOpened(now)) {
            throw new AppException(ErrorCode.ASSESSMENT_NOT_OPEN);
        }
        Submission s = submissions.findByAssessmentIdAndUserId(a.getId(), me.userId()).orElse(null);
        if (s != null && s.getStartedAt() != null) {
            finalizeIfDue(a, s, now);
            return state(a, s, now);
        }
        if (a.isClosed(now) || (s != null && s.isSubmitted())) {
            throw new AppException(ErrorCode.SUBMISSION_LOCKED);
        }
        if (s == null) {
            s = submissions.save(new Submission(a.getId(), me.userId()));
        }
        s.start(now);
        s.touch();
        // Bằng chứng khi có tranh chấp "em bắt đầu lúc..." (tính hạn theo số phút làm bài)
        audit.record(me.userId(), AuditService.QUIZ_STARTED, t.course().displayCode(), a.getTitle());
        return state(a, s, now);
    }

    /** Lưu câu trả lời một câu (thay các đáp án đã chọn trước đó); quá hạn thì chốt bài và báo SUBMISSION_LOCKED. */
    @Transactional(noRollbackFor = AppException.class)
    public AnswerSaved answer(String assessmentId, String questionId, List<Integer> chosen, AuthPrincipal me) {
        Target t = requireEnrolledQuiz(assessmentId, me);
        Assessment a = t.assessment();
        Instant now = Instant.now();
        Submission s = requireStarted(a, me);
        // Không nạp QuizAnswer trước khi xóa (xem QuizAnswerRepository.deleteAnswer), trừ khi chốt bài
        finalizeIfDue(a, s, now);
        if (s.isSubmitted()) {
            throw new AppException(ErrorCode.SUBMISSION_LOCKED);
        }
        QuizQuestion q = questions.findByPublicIdAndAssessmentId(questionId, a.getId())
                .orElseThrow(() -> AppException.notFound("Question"));
        Set<Integer> picked = new TreeSet<>(chosen);
        boolean valid = picked.size() == chosen.size() && (q.isMultiple() || picked.size() <= 1)
                && picked.stream().allMatch(p -> p >= 0 && p < q.getOptions().size());
        if (!valid) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Invalid options");
        }
        answers.deleteAnswer(s.getId(), q.getId());
        answers.saveAll(picked.stream().map(p -> new QuizAnswer(s.getId(), q.getId(), p)).toList());
        s.touch();
        return new AnswerSaved(q.getPublicId(), List.copyOf(picked), now);
    }

    /** Nộp bài: chốt và chấm ngay; đã chốt rồi (nộp ở tab khác, hết giờ) thì trả về kết quả như cũ. */
    @Transactional
    public QuizState submit(String assessmentId, AuthPrincipal me, String ip) {
        Target t = requireEnrolledQuiz(assessmentId, me);
        Assessment a = t.assessment();
        Instant now = Instant.now();
        Submission s = requireStarted(a, me);
        finalizeIfDue(a, s, now);
        if (!s.isSubmitted()) {
            QuizGrader.Result r = finalize(a, s, nowMillis());
            audit.record(me.userId(), AuditService.SUBMISSION_SUBMITTED, t.course().displayCode(),
                    a.getTitle() + " (trắc nghiệm, đúng " + r.correct() + "/" + r.total() + " = "
                            + r.score().toPlainString() + ")",
                    ip);
        }
        return state(a, s, now);
    }

    // ================================================================ QuizSweeper

    /** Chốt và chấm mọi bài đang làm đã quá hạn; trả về số bài đã chốt. */
    @Transactional
    public int finalizeExpired() {
        Instant now = Instant.now();
        Map<Long, Assessment> byId = new HashMap<>();
        int done = 0;
        for (Submission s : submissions.findQuizInProgress()) {
            Assessment a = byId.computeIfAbsent(s.getAssessmentId(), id -> assessments.findById(id).orElse(null));
            if (a != null && finalizeIfDue(a, s, now)) {
                done++;
            }
        }
        return done;
    }

    // ---------------------------------------------------------------- nội bộ

    private record Target(Assessment assessment, Course course) {
    }

    /** Giờ lưu vào cột DATETIME(3): trả về đúng giá trị đọc lại sau này (không có phần micro/nano giây). */
    private static Instant nowMillis() {
        return Instant.now().truncatedTo(ChronoUnit.MILLIS);
    }

    private Target requireEnrolledQuiz(String assessmentId, AuthPrincipal me) {
        Assessment a = assessments.findByPublicId(assessmentId).filter(Assessment::isQuiz)
                .filter(x -> enrollments.existsByCourseIdAndUserId(x.getCourseId(), me.userId()))
                .orElseThrow(() -> AppException.notFound("Assessment"));
        Course c = courses.findById(a.getCourseId()).orElseThrow(() -> AppException.notFound("Assessment"));
        return new Target(a, c);
    }

    private Assessment requireManagedQuiz(String courseId, String assessmentId, AuthPrincipal me) {
        Course c = courseService.requireManaged(courseId, me);
        return assessments.findByPublicIdAndCourseId(assessmentId, c.getId()).filter(Assessment::isQuiz)
                .orElseThrow(() -> AppException.notFound("Assessment"));
    }

    private Submission requireStarted(Assessment a, AuthPrincipal me) {
        return submissions.findByAssessmentIdAndUserId(a.getId(), me.userId())
                .filter(s -> s.getStartedAt() != null)
                .orElseThrow(() -> new AppException(ErrorCode.BAD_REQUEST, "Quiz not started"));
    }

    /** Đã bắt đầu, chưa chốt mà quá hạn thì chốt với giờ nộp = hạn và chấm; true nếu vừa chốt. */
    private boolean finalizeIfDue(Assessment a, Submission s, Instant now) {
        if (s.getStartedAt() == null || s.isSubmitted()) {
            return false;
        }
        Instant deadline = a.deadlineFor(s.getStartedAt());
        if (deadline == null || now.isBefore(deadline)) {
            return false;
        }
        QuizGrader.Result r = finalize(a, s, deadline);
        String course = courses.findById(a.getCourseId()).map(Course::displayCode).orElse(null);
        String student = users.findById(s.getUserId()).map(User::getUsername).orElse("?");
        audit.record(null, AuditService.QUIZ_TIMED_OUT, course, a.getTitle() + " · " + student + " (đúng "
                + r.correct() + "/" + r.total() + " = " + r.score().toPlainString() + ")");
        return true;
    }

    private QuizGrader.Result finalize(Assessment a, Submission s, Instant at) {
        QuizGrader.Result r = QuizGrader.grade(questions.findByAssessmentIdOrderByPosition(a.getId()), chosenBy(s));
        s.submit(at);
        s.autoGrade(r.score(), at);
        return r;
    }

    private QuizState state(Assessment a, Submission s, Instant now) {
        List<QuizQuestion> qs = questions.findByAssessmentIdOrderByPosition(a.getId());
        if (s == null || s.getStartedAt() == null) {
            return new QuizState(QuizViews.NOT_STARTED, qs.size(), a.getTimeLimitMinutes(), null, null, null, false,
                    List.of(), null, false, now);
        }
        Map<Long, Set<Integer>> chosen = chosenBy(s);
        Instant deadline = a.deadlineFor(s.getStartedAt());
        if (!s.isSubmitted()) {
            return new QuizState(QuizViews.IN_PROGRESS, qs.size(), a.getTimeLimitMinutes(), s.getStartedAt(),
                    deadline, null, false, ordered(a, s, qs, chosen, false), null, false, now);
        }
        // Điểm chưa công bố thì cũng không cho xem đáp án (xem đáp án là tự tính được điểm)
        boolean scoreVisible = a.isScoreVisible(now);
        boolean revealed = scoreVisible && revealed(a, now);
        return new QuizState(QuizViews.FINISHED, qs.size(), a.getTimeLimitMinutes(), s.getStartedAt(), deadline,
                s.getSubmittedAt(), revealed, revealed ? ordered(a, s, qs, chosen, true) : List.of(),
                scoreVisible ? result(s, qs, chosen) : null, !scoreVisible, now);
    }

    /** Thứ tự câu và đáp án sinh viên thấy: xáo theo bài nộp nếu bài bật xáo trộn. */
    private static List<QuestionView> ordered(Assessment a, Submission s, List<QuizQuestion> qs,
            Map<Long, Set<Integer>> chosen, boolean reveal) {
        List<QuizQuestion> order = a.isShuffle() ? QuizGrader.shuffled(qs, QuizGrader.seed(s.getPublicId(), 0))
                : qs;
        return order.stream().map(q -> view(q, chosen.getOrDefault(q.getId(), Set.of()), reveal,
                a.isShuffle() ? QuizGrader.seed(s.getPublicId(), q.getId()) : null)).toList();
    }

    /** optionSeed null = đáp án theo thứ tự gốc. */
    private static QuestionView view(QuizQuestion q, Set<Integer> chosen, boolean reveal, Long optionSeed) {
        List<Integer> positions = IntStream.range(0, q.getOptions().size()).boxed().toList();
        if (optionSeed != null) {
            positions = QuizGrader.shuffled(positions, optionSeed);
        }
        List<OptionView> options = positions.stream().map(p -> {
            QuizOption o = q.getOptions().get(p);
            return new OptionView(p, o.getBody(), reveal ? o.isCorrect() : null);
        }).toList();
        return new QuestionView(q.getPublicId(), q.getBody(), q.isMultiple(), options,
                List.copyOf(new TreeSet<>(chosen)), reveal ? QuizGrader.isCorrect(q, chosen) : null,
                reveal ? q.getExplanation() : null);
    }

    /** Số câu đúng tính lại từ câu trả lời; điểm là điểm chính thức (giảng viên có thể đã sửa). */
    private static ResultView result(Submission s, List<QuizQuestion> qs, Map<Long, Set<Integer>> chosen) {
        QuizGrader.Result r = QuizGrader.grade(qs, chosen);
        return new ResultView(r.correct(), r.total(), s.getScore() != null ? s.getScore() : r.score());
    }

    private static boolean revealed(Assessment a, Instant now) {
        return switch (a.getRevealMode()) {
            case Assessment.REVEAL_AFTER_SUBMIT -> true;
            // Bài không có giờ đóng thì xem được ngay sau khi nộp
            case Assessment.REVEAL_AFTER_CLOSE -> a.getClosesAt() == null || !now.isBefore(a.getClosesAt());
            default -> false;
        };
    }

    private Map<Long, Set<Integer>> chosenBy(Submission s) {
        return groupChosen(answers.findBySubmissionId(s.getId())).getOrDefault(s.getId(), Map.of());
    }

    /** submissionId → (questionId → vị trí đáp án đã chọn). */
    private static Map<Long, Map<Long, Set<Integer>>> groupChosen(List<QuizAnswer> list) {
        return list.stream().collect(Collectors.groupingBy(QuizAnswer::getSubmissionId,
                Collectors.groupingBy(QuizAnswer::getQuestionId,
                        Collectors.mapping(QuizAnswer::getOptionPosition, Collectors.toSet()))));
    }
}
