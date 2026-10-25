package lms.code;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lms.audit.AuditService;
import lms.auth.AuthPrincipal;
import lms.code.CodeViews.ArchiveCheck;
import lms.code.CodeViews.LibraryCreateRequest;
import lms.code.CodeViews.LibraryDetail;
import lms.code.CodeViews.LibraryProblem;
import lms.code.CodeViews.LibraryUpdateRequest;
import lms.code.CodeViews.ProblemRequest;
import lms.common.AppException;
import lms.common.ErrorCode;
import lms.course.CourseService;
import lms.question.QuestionBankService;
import lms.question.QuestionBankViews.Subject;

/**
 * Kho bài lập trình của giảng viên (kiểu danh sách bài trên các trang OJ): câu lập trình không thuộc bài nào, mỗi
 * câu có đề, giới hạn và bộ test như câu của bài (CodeProblem có ownerId). Tạo bài lập trình thì chọn câu từ kho:
 * CodeService chép câu và bộ test sang bài (ProblemRequest.source), sửa/xóa câu trong kho không đổi bài đã tạo.
 * Môn học chọn trong các học phần mình dạy, như ngân hàng câu hỏi.
 */
@Service
public class CodeLibraryService {

    private final CodeProblemRepository problems;
    private final CodeTestRepository tests;
    private final CodeService codeService;
    private final QuestionBankService questionBanks;
    private final AuditService audit;

    public CodeLibraryService(CodeProblemRepository problems, CodeTestRepository tests, CodeService codeService,
            QuestionBankService questionBanks, AuditService audit) {
        this.problems = problems;
        this.tests = tests;
        this.codeService = codeService;
        this.questionBanks = questionBanks;
        this.audit = audit;
    }

    @Transactional(readOnly = true)
    public List<LibraryProblem> list(AuthPrincipal me) {
        return summaries(problems.findByOwnerIdOrderByUpdatedAtDescIdDesc(me.userId()));
    }

    @Transactional(readOnly = true)
    public LibraryDetail detail(String id, AuthPrincipal me) {
        return detail(require(id, me));
    }

    /** Thêm nhiều câu (vd. nhập nhanh từ thư mục) cùng một môn; ZIP bộ test như khi tạo bài. */
    @Transactional
    public List<LibraryProblem> create(LibraryCreateRequest req, Map<String, MultipartFile> files,
            AuthPrincipal me) {
        Subject subject = resolveSubject(req.subjectCode(), null, me);
        List<ProblemRequest> list = req.problems().stream().map(CodeLibraryService::withoutSource).toList();
        List<List<TestArchive.Test>> archives = CodeService.archives(list, files);
        List<CodeProblem> saved = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ProblemRequest r = list.get(i);
            CodeProblem p = CodeProblem.library(me.userId());
            apply(p, r, subject);
            p = problems.save(p);
            codeService.applyTests(p, r.tests(), archives.get(i));
            saved.add(p);
        }
        audit.record(me.userId(), AuditService.CODE_PROBLEMS_ADDED, saved.size() + " câu",
                saved.stream().map(CodeProblem::getTitle).limit(10).collect(Collectors.joining(", ")));
        return summaries(saved);
    }

    /** Sửa đề, giới hạn, môn và bộ test (giữ test theo id, thêm test mới, thêm test từ ZIP) của một câu. */
    @Transactional
    public LibraryDetail update(String id, LibraryUpdateRequest req, Map<String, MultipartFile> files,
            AuthPrincipal me) {
        CodeProblem p = require(id, me);
        Subject subject = resolveSubject(req.subjectCode(), p, me);
        List<List<TestArchive.Test>> archives = CodeService.archives(List.of(withoutSource(req.problem())), files);
        apply(p, req.problem(), subject);
        p.touch();
        codeService.applyTests(p, req.problem().tests(), archives.get(0));
        audit.record(me.userId(), AuditService.CODE_PROBLEM_UPDATED, p.getTitle(), p.getSubjectCode());
        return detail(p);
    }

    @Transactional
    public void delete(List<String> ids, AuthPrincipal me) {
        List<CodeProblem> list = problems.findByPublicIdInAndOwnerId(ids, me.userId());
        if (list.size() != ids.stream().distinct().count()) {
            throw AppException.notFound("Library problem");
        }
        // Bộ test xóa theo (ON DELETE CASCADE); bài đã tạo từ kho có bản chép riêng
        problems.deleteAll(list);
        audit.record(me.userId(), AuditService.CODE_PROBLEMS_DELETED, list.size() + " câu",
                list.stream().map(CodeProblem::getTitle).limit(10).collect(Collectors.joining(", ")));
    }

    /** Đọc thử file ZIP bộ test (như CodeService.checkArchive, không gắn khóa học); không lưu gì. */
    public ArchiveCheck checkArchive(MultipartFile archive) {
        return CodeService.checkArchive(archive);
    }

    // ---------------------------------------------------------------- nội bộ

    private CodeProblem require(String id, AuthPrincipal me) {
        return problems.findByPublicIdAndOwnerId(id, me.userId())
                .orElseThrow(() -> AppException.notFound("Library problem"));
    }

    private static ProblemRequest withoutSource(ProblemRequest r) {
        return r.source() == null ? r
                : new ProblemRequest(r.id(), r.title(), r.statement(), r.timeLimitMs(), r.memoryLimitMb(),
                        r.maxAttempts(), r.tests(), r.archive(), null);
    }

    private static void apply(CodeProblem p, ProblemRequest r, Subject subject) {
        p.update(0, r.title().strip(), r.statement().strip(), r.timeLimitMs(), r.memoryLimitMb(), r.maxAttempts());
        p.setSubject(subject == null ? null : subject.code(), subject == null ? null : subject.name());
    }

    /** Mã học phần mình đang dạy; môn đang gắn của câu vẫn giữ được khi đã thôi dạy (như ngân hàng câu hỏi). */
    private Subject resolveSubject(String subjectCode, CodeProblem current, AuthPrincipal me) {
        String code = CourseService.blankToNull(subjectCode);
        if (code == null) {
            return null;
        }
        if (current != null && code.equalsIgnoreCase(current.getSubjectCode())) {
            return new Subject(current.getSubjectCode(), current.getSubjectName());
        }
        return questionBanks.subjects(me).stream().filter(s -> s.code().equalsIgnoreCase(code)).findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.VALIDATION_FAILED, "Unknown subject"));
    }

    private List<LibraryProblem> summaries(List<CodeProblem> list) {
        Map<Long, Object[]> counts = list.isEmpty() ? Map.of()
                : tests.countWithSamples(list.stream().map(CodeProblem::getId).toList()).stream()
                        .collect(Collectors.toMap(r -> (Long) r[0], r -> r));
        return list.stream().map(p -> {
            Object[] c = counts.get(p.getId());
            return summary(p, c == null ? 0 : ((Number) c[1]).intValue(), c == null ? 0 : ((Number) c[2]).intValue());
        }).toList();
    }

    private static LibraryProblem summary(CodeProblem p, int testCount, int sampleCount) {
        return new LibraryProblem(p.getPublicId(), p.getTitle(), p.getSubjectCode(), p.getSubjectName(),
                p.getTimeLimitMs(), p.getMemoryLimitMb(), p.getMaxAttempts(), testCount, sampleCount,
                p.getCreatedAt(), p.getUpdatedAt());
    }

    private LibraryDetail detail(CodeProblem p) {
        List<CodeTestInfo> list = tests.findInfos(p.getId());
        int samples = (int) list.stream().filter(CodeTestInfo::sample).count();
        return new LibraryDetail(summary(p, list.size(), samples), p.getStatement(), CodeService.testViews(list));
    }
}
