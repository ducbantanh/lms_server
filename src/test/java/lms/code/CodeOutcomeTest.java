package lms.code;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tình trạng một câu (màu ở danh sách câu và bảng kết quả) từ các lần chạy/nộp, mới nhất trước. */
class CodeOutcomeTest {

    private static long seq = 100;

    private static CodeAttemptInfo submit(int status, String verdict, Integer passed) {
        return new CodeAttemptInfo(seq--, "a" + seq, 1L, 2L, CodeAttempt.KIND_SUBMIT, "c", status, verdict, passed,
                passed == null ? null : 5, Instant.EPOCH, null);
    }

    private static CodeAttemptInfo done(String verdict, int passed) {
        return submit(CodeAttempt.DONE, verdict, passed);
    }

    private static CodeAttemptInfo run() {
        return new CodeAttemptInfo(seq--, "r" + seq, 1L, 2L, CodeAttempt.KIND_RUN, "c", CodeAttempt.DONE, "AC", 2,
                2, Instant.EPOCH, null);
    }

    @Test
    void noSubmitIsNoneAndRunsDoNotCount() {
        CodeService.Outcome o = CodeService.outcome(List.of(run()), true);
        assertEquals(CodeViews.NONE, o.status());
        assertNull(o.best());
        // Lỗi máy chấm không phải lỗi của sinh viên
        assertEquals(CodeViews.NONE, CodeService.outcome(List.of(done("SE", 0)), true).status());
    }

    @Test
    void acceptedOnceStaysAcceptedEvenIfLaterWrong() {
        CodeService.Outcome o = CodeService.outcome(List.of(done("WA", 1), done("AC", 5), done("WA", 3)), true);
        assertEquals(CodeViews.ACCEPTED, o.status());
        assertEquals(new CodeViews.Best("AC", 5, 5), o.best());
        assertEquals(3, o.attempts());
    }

    @Test
    void wrongKeepsBestPassedAndPendingWins() {
        CodeService.Outcome wrong = CodeService.outcome(List.of(done("WA", 2), done("TLE", 4), done("CE", 0)), true);
        assertEquals(CodeViews.REJECTED, wrong.status());
        assertEquals(new CodeViews.Best("TLE", 4, 5), wrong.best());

        CodeService.Outcome pending = CodeService.outcome(
                List.of(submit(CodeAttempt.QUEUED, null, null), done("WA", 2)), true);
        assertEquals(CodeViews.PENDING, pending.status());
        assertEquals(2, pending.best().passed());
    }

    @Test
    void hiddenResultsShowOnlySubmitted() {
        CodeService.Outcome o = CodeService.outcome(List.of(done("AC", 5)), false);
        assertEquals(CodeViews.SUBMITTED, o.status());
        assertNull(o.best());
        assertEquals(CodeViews.PENDING,
                CodeService.outcome(List.of(submit(CodeAttempt.RUNNING, null, null)), false).status());
    }
}
