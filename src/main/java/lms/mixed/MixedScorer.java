package lms.mixed;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lms.code.CodeAttemptInfo;
import lms.code.CodeAttemptRepository;
import lms.code.CodeProblemRepository;
import lms.course.Assessment;
import lms.quiz.QuizAnswer;
import lms.quiz.QuizAnswerRepository;
import lms.quiz.QuizGrader;
import lms.quiz.QuizQuestion;
import lms.quiz.QuizQuestionRepository;
import lms.submission.Submission;

/**
 * Điểm máy chấm của bài tổng hợp (phần trắc nghiệm + phần lập trình) cho một bài làm. Chỉ dùng repository để
 * CodeJudgeQueue gọi được khi chấm xong một lần nộp (điểm phần lập trình đổi theo kết quả chấm).
 */
@Component
public class MixedScorer {

    /** Phần trắc nghiệm: số câu đúng / tổng số câu, điểm của phần. */
    public record QuizPart(int correct, int total, BigDecimal points) {
    }

    /** Phần lập trình: số câu có lần nộp AC / tổng số câu; pending = còn lần nộp chờ chấm. */
    public record CodePart(int accepted, int total, BigDecimal points, boolean pending) {
    }

    /** quiz / code null khi bài không có phần đó; auto = tổng điểm hai phần. */
    public record Parts(QuizPart quiz, CodePart code, BigDecimal auto) {
    }

    private final QuizQuestionRepository questions;
    private final QuizAnswerRepository answers;
    private final CodeProblemRepository problems;
    private final CodeAttemptRepository attempts;

    public MixedScorer(QuizQuestionRepository questions, QuizAnswerRepository answers,
            CodeProblemRepository problems, CodeAttemptRepository attempts) {
        this.questions = questions;
        this.answers = answers;
        this.problems = problems;
        this.attempts = attempts;
    }

    public Parts parts(Assessment a, Submission s) {
        QuizPart quiz = null;
        CodePart code = null;
        BigDecimal auto = BigDecimal.ZERO.setScale(2);
        if (a.getQuizWeight() != null) {
            List<QuizQuestion> qs = questions.findByAssessmentIdOrderByPosition(a.getId());
            Map<Long, Set<Integer>> chosen = s == null ? Map.of()
                    : answers.findBySubmissionId(s.getId()).stream()
                            .collect(Collectors.groupingBy(QuizAnswer::getQuestionId,
                                    Collectors.mapping(QuizAnswer::getOptionPosition, Collectors.toSet())));
            QuizGrader.Result r = QuizGrader.grade(qs, chosen);
            quiz = new QuizPart(r.correct(), r.total(), MixedGrader.points(a.getQuizWeight(), r.correct(), r.total()));
            auto = auto.add(quiz.points());
        }
        if (a.getCodeWeight() != null) {
            int total = problems.findByAssessmentIdOrderByPosition(a.getId()).size();
            List<CodeAttemptInfo> mine = s == null ? List.of()
                    : attempts.infosOfUser(a.getId(), s.getUserId()).stream()
                            .filter(x -> x.isSubmit() && !x.isSystemError()).toList();
            int accepted = (int) mine.stream().filter(CodeAttemptInfo::isAccepted).map(CodeAttemptInfo::problemId)
                    .distinct().count();
            boolean pending = mine.stream().anyMatch(x -> !x.isDone());
            code = new CodePart(accepted, total, MixedGrader.points(a.getCodeWeight(), accepted, total), pending);
            auto = auto.add(code.points());
        }
        return new Parts(quiz, code, auto);
    }

    /** Tính lại điểm máy chấm của bài làm đã chốt (bài chưa chốt thì chưa có điểm). */
    public void rescore(Assessment a, Submission s, Instant at) {
        if (!a.isMixed() || !s.isSubmitted()) {
            return;
        }
        s.mixedAutoGrade(parts(a, s).auto(), a.usesFiles(), at);
    }
}
