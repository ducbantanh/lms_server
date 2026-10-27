package lms.code;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import lms.code.CodeViews.Best;
import lms.code.CodeViews.CodeResults;
import lms.code.CodeViews.ProblemRef;
import lms.code.CodeViews.ResultCell;
import lms.code.CodeViews.ResultRow;

class CodeResultsCsvTest {

    @Test
    void cellTextShowsBestVerdictTestsAndAttempts() {
        assertEquals("", CodeService.cellText(new ResultCell("p", CodeViews.NONE, null, 0)));
        assertEquals("AC 5/5 (2 lần)",
                CodeService.cellText(new ResultCell("p", CodeViews.ACCEPTED, new Best("AC", 5, 5), 2)));
        assertEquals("WA 3/5 (1 lần)",
                CodeService.cellText(new ResultCell("p", CodeViews.REJECTED, new Best("WA", 3, 5), 1)));
        assertEquals("Đang chấm (1 lần)", CodeService.cellText(new ResultCell("p", CodeViews.PENDING, null, 1)));
        assertEquals("TLE 2/5, đang chấm lần mới (3 lần)",
                CodeService.cellText(new ResultCell("p", CodeViews.PENDING, new Best("TLE", 2, 5), 3)));
    }

    @Test
    void csvHasOneRowPerStudentAndTotals() {
        CodeResults r = new CodeResults(List.of(new ProblemRef("p1", "Tổng hai số"), new ProblemRef("p2", "Dãy, con")),
                List.of(new ResultRow("s1", "B21DCCN001", "Nguyễn Văn A", List.of(
                        new ResultCell("p1", CodeViews.ACCEPTED, new Best("AC", 3, 3), 2),
                        new ResultCell("p2", CodeViews.REJECTED, new Best("WA", 1, 4), 1))),
                        new ResultRow("s2", "B21DCCN002", "Trần Thị B", List.of(
                                new ResultCell("p1", CodeViews.NONE, null, 0),
                                new ResultCell("p2", CodeViews.NONE, null, 0)))));

        String csv = CodeService.csv(r);
        assertTrue(csv.startsWith("﻿"), "BOM để Excel đọc đúng UTF-8");
        String[] lines = csv.substring(1).split("\r\n");
        assertEquals("MSSV,Họ tên,Câu 1: Tổng hai số,\"Câu 2: Dãy, con\",Số câu đúng,Số lần nộp", lines[0]);
        assertEquals("B21DCCN001,Nguyễn Văn A,AC 3/3 (2 lần),WA 1/4 (1 lần),1,3", lines[1]);
        assertEquals("B21DCCN002,Trần Thị B,,,0,0", lines[2]);
    }

    @Test
    void systemErrorRetryKeepsCountUntilManualRequeue() {
        CodeAttempt x = new CodeAttempt(1L, 2L, 3L, 1, CodeLanguage.values()[0], "int main(){}");
        x.claim();
        x.retryAfterSystemError(Instant.parse("2026-10-26T10:00:05Z"));
        assertEquals(CodeAttempt.QUEUED, x.getStatus());
        assertNull(x.getClaimToken());
        assertEquals(1, x.getJudgeRetries());
        x.claim();
        x.retryAfterSystemError(Instant.parse("2026-10-26T10:00:15Z"));
        assertEquals(2, x.getJudgeRetries());
        // Giảng viên bấm chấm lại: được tự chấm lại từ đầu
        x.requeue();
        assertEquals(0, x.getJudgeRetries());
    }
}
