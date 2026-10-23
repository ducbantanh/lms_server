package lms.question;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lms.audit.AuditService;
import lms.auth.AuthPrincipal;
import lms.common.AppException;
import lms.common.ErrorCode;
import lms.course.Course;
import lms.course.CourseRepository;
import lms.course.CourseService;
import lms.question.QuestionBankViews.BankDetail;
import lms.question.QuestionBankViews.BankRequest;
import lms.question.QuestionBankViews.BankSummary;
import lms.question.QuestionBankViews.ImportReport;
import lms.question.QuestionBankViews.OptionView;
import lms.question.QuestionBankViews.QuestionRequest;
import lms.question.QuestionBankViews.QuestionView;
import lms.question.QuestionBankViews.Subject;

/**
 * Ngân hàng câu hỏi trắc nghiệm. Mỗi ngân hàng thuộc về người tạo, chỉ người đó xem và sửa được
 * (người khác, kể cả admin, nhận NOT_FOUND). Ngân hàng gắn với môn học (mã học phần) chứ không gắn khóa học:
 * các nhóm cùng học phần dùng chung, bài thi sẽ chép câu hỏi sang.
 */
@Service
public class QuestionBankService {

    public static final int MAX_QUESTIONS = 2000;
    /** Số câu tối đa mỗi lần dán. */
    public static final int MAX_IMPORT = 500;
    /** Độ dài nội dung câu hỏi ghi vào audit. */
    private static final int AUDIT_PREVIEW = 80;

    private final QuestionBankRepository banks;
    private final BankQuestionRepository questions;
    private final CourseRepository courses;
    private final AuditService audit;
    private final QuestionImportParser parser = new QuestionImportParser(MAX_IMPORT);

    public QuestionBankService(QuestionBankRepository banks, BankQuestionRepository questions,
            CourseRepository courses, AuditService audit) {
        this.banks = banks;
        this.questions = questions;
        this.courses = courses;
        this.audit = audit;
    }

    // ================================================================ ngân hàng

    @Transactional(readOnly = true)
    public List<BankSummary> list(AuthPrincipal me) {
        List<QuestionBank> list = banks.findByOwnerIdOrderByUpdatedAtDescIdDesc(me.userId());
        Map<Long, Long> counts = list.isEmpty() ? Map.of()
                : questions.countByBankIds(list.stream().map(QuestionBank::getId).toList()).stream()
                        .collect(Collectors.toMap(r -> (Long) r[0], r -> (Long) r[1]));
        return list.stream().map(b -> summary(b, counts.getOrDefault(b.getId(), 0L))).toList();
    }

    /** Môn học chọn được khi tạo/sửa ngân hàng: học phần của các khóa học mình phụ trách, theo mã. */
    @Transactional(readOnly = true)
    public List<Subject> subjects(AuthPrincipal me) {
        return teachingSubjects(me.userId());
    }

    @Transactional
    public BankSummary create(BankRequest req, AuthPrincipal me) {
        Subject subject = resolveSubject(req.subjectCode(), null, me.userId());
        QuestionBank b = banks.save(new QuestionBank(me.userId(), req.name().strip(),
                CourseService.blankToNull(req.description()), subject == null ? null : subject.code(),
                subject == null ? null : subject.name()));
        audit.record(me.userId(), AuditService.QUESTION_BANK_CREATED, b.getName(), b.getSubjectCode());
        return summary(b, 0);
    }

    @Transactional
    public BankSummary update(String bankId, BankRequest req, AuthPrincipal me) {
        QuestionBank b = requireOwned(bankId, me);
        String old = b.getName();
        Subject subject = resolveSubject(req.subjectCode(), b, me.userId());
        b.update(req.name().strip(), CourseService.blankToNull(req.description()),
                subject == null ? null : subject.code(), subject == null ? null : subject.name());
        banks.flush();
        audit.record(me.userId(), AuditService.QUESTION_BANK_UPDATED, b.getName(),
                old.equals(b.getName()) ? null : old + " → " + b.getName());
        return summary(b, questions.countByBankId(b.getId()));
    }

    /** Xóa ngân hàng kéo theo mọi câu hỏi (ON DELETE CASCADE); bài thi đã chép câu hỏi không bị ảnh hưởng. */
    @Transactional
    public void delete(String bankId, AuthPrincipal me) {
        QuestionBank b = requireOwned(bankId, me);
        long count = questions.countByBankId(b.getId());
        banks.delete(b);
        audit.record(me.userId(), AuditService.QUESTION_BANK_DELETED, b.getName(), count + " câu");
    }

    @Transactional(readOnly = true)
    public BankDetail detail(String bankId, AuthPrincipal me) {
        QuestionBank b = requireOwned(bankId, me);
        List<BankQuestion> list = questions.findByBankIdOrderById(b.getId());
        return new BankDetail(summary(b, list.size()), list.stream().map(QuestionBankService::view).toList());
    }

    // ================================================================ câu hỏi

    @Transactional
    public QuestionView addQuestion(String bankId, QuestionRequest req, AuthPrincipal me) {
        QuestionBank b = requireOwned(bankId, me);
        if (questions.countByBankId(b.getId()) >= MAX_QUESTIONS) {
            throw new AppException(ErrorCode.QUESTION_LIMIT);
        }
        List<BankOption> options = options(req);
        BankQuestion q = questions.save(new BankQuestion(b.getId(), req.body().strip(), req.multiple(),
                req.difficulty(), CourseService.blankToNull(req.explanation()), options));
        b.touch();
        audit.record(me.userId(), AuditService.QUESTION_CREATED, b.getName(), preview(q.getBody()));
        return view(q);
    }

    @Transactional
    public QuestionView updateQuestion(String bankId, String questionId, QuestionRequest req, AuthPrincipal me) {
        QuestionBank b = requireOwned(bankId, me);
        BankQuestion q = questions.findByPublicIdAndBankId(questionId, b.getId())
                .orElseThrow(() -> AppException.notFound("Question"));
        q.update(req.body().strip(), req.multiple(), req.difficulty(), CourseService.blankToNull(req.explanation()),
                options(req));
        b.touch();
        questions.flush();
        audit.record(me.userId(), AuditService.QUESTION_UPDATED, b.getName(), preview(q.getBody()));
        return view(q);
    }

    /** Xóa một hoặc nhiều câu; id không thuộc ngân hàng này thì NOT_FOUND và không xóa gì. */
    @Transactional
    public void deleteQuestions(String bankId, List<String> questionIds, AuthPrincipal me) {
        QuestionBank b = requireOwned(bankId, me);
        List<BankQuestion> found = questions.findByBankIdAndPublicIdIn(b.getId(), new HashSet<>(questionIds));
        if (found.size() != new HashSet<>(questionIds).size()) {
            throw AppException.notFound("Question");
        }
        questions.deleteAll(found);
        b.touch();
        audit.record(me.userId(), AuditService.QUESTIONS_DELETED, b.getName(),
                found.size() == 1 ? preview(found.get(0).getBody()) : found.size() + " câu");
    }

    /** Nhập câu hỏi từ văn bản (QuestionImportParser): có lỗi ở câu nào thì không thêm câu nào. */
    @Transactional
    public ImportReport importText(String bankId, String content, AuthPrincipal me) {
        QuestionBank b = requireOwned(bankId, me);
        QuestionImportParser.Result parsed;
        try {
            parsed = parser.parse(content);
        } catch (QuestionImportParser.TooManyQuestionsException e) {
            throw new AppException(ErrorCode.QUESTION_LIMIT, e.getMessage());
        }
        if (!parsed.errors().isEmpty() || parsed.questions().isEmpty()) {
            return new ImportReport(0, parsed.errors());
        }
        if (questions.countByBankId(b.getId()) + parsed.questions().size() > MAX_QUESTIONS) {
            throw new AppException(ErrorCode.QUESTION_LIMIT);
        }
        questions.saveAll(parsed.questions().stream()
                .map(q -> new BankQuestion(b.getId(), q.body(), q.multiple(), q.difficulty(), q.explanation(),
                        q.options().stream().map(o -> new BankOption(o.body(), o.correct())).toList()))
                .toList());
        b.touch();
        audit.record(me.userId(), AuditService.QUESTIONS_IMPORTED, b.getName(), parsed.questions().size() + " câu");
        return new ImportReport(parsed.questions().size(), List.of());
    }

    // ---------------------------------------------------------------- nội bộ

    private QuestionBank requireOwned(String bankId, AuthPrincipal me) {
        return banks.findByPublicIdAndOwnerId(bankId, me.userId())
                .orElseThrow(() -> AppException.notFound("Question bank"));
    }

    /** Học phần (theo mã, không phân biệt hoa thường) của các khóa mình phụ trách; tên lấy từ khóa mới nhất. */
    private List<Subject> teachingSubjects(long userId) {
        Map<String, Subject> byCode = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Course c : courses.findByTeacherNewestFirst(userId)) {
            byCode.putIfAbsent(c.getCode(), new Subject(c.getCode(), c.getTitle()));
        }
        return List.copyOf(byCode.values());
    }

    /**
     * Môn học gửi lên → môn lưu vào ngân hàng; trống = không gắn. Giữ nguyên môn đang gắn thì luôn được
     * (kể cả khi đã thôi dạy học phần đó); đổi sang môn khác thì phải là học phần mình đang phụ trách.
     */
    private Subject resolveSubject(String subjectCode, QuestionBank current, long userId) {
        String code = CourseService.blankToNull(subjectCode);
        if (code == null) {
            return null;
        }
        if (current != null && code.equalsIgnoreCase(current.getSubjectCode())) {
            return new Subject(current.getSubjectCode(), current.getSubjectName());
        }
        return teachingSubjects(userId).stream().filter(s -> s.code().equalsIgnoreCase(code)).findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.VALIDATION_FAILED, "UNKNOWN_SUBJECT"));
    }

    /** Kiểm tra số đáp án và đáp án đúng (QuestionImportParser.problem); lỗi trả mã lý do trong message. */
    private static List<BankOption> options(QuestionRequest req) {
        List<QuestionImportParser.Option> options = req.options().stream()
                .map(o -> new QuestionImportParser.Option(o.body().strip(), o.correct())).toList();
        String problem = QuestionImportParser.problem(req.multiple(), options);
        if (problem != null) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, problem);
        }
        return options.stream().map(o -> new BankOption(o.body(), o.correct())).toList();
    }

    private static BankSummary summary(QuestionBank b, long count) {
        return new BankSummary(b.getPublicId(), b.getName(), b.getDescription(), b.getSubjectCode(),
                b.getSubjectName(), count, b.getCreatedAt(), b.getUpdatedAt());
    }

    private static QuestionView view(BankQuestion q) {
        return new QuestionView(q.getPublicId(), q.getBody(), q.isMultiple(), q.getDifficulty(), q.getExplanation(),
                q.getOptions().stream().map(o -> new OptionView(o.getBody(), o.isCorrect())).toList(),
                q.getCreatedAt(), q.getUpdatedAt());
    }

    /** Dòng đầu của câu hỏi, cắt ngắn cho audit. */
    private static String preview(String body) {
        String first = body.lines().findFirst().orElse("").strip();
        return first.length() <= AUDIT_PREVIEW ? first : first.substring(0, AUDIT_PREVIEW) + "…";
    }
}
