package lms.question;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import lms.question.QuestionImportParser.Option;

class QuestionImportParserTest {

    private final QuestionImportParser parser = new QuestionImportParser(100);

    @Test
    void parsesAikenBlocksWithNumberPrefixAndMeta() {
        var r = parser.parse("""
                # chương 1

                Câu 1: Java chạy trên gì?
                A. JVM
                B) CLR
                ĐÁP ÁN: A
                MỨC ĐỘ: dễ
                GIẢI THÍCH: JVM là máy ảo Java

                2. Chọn các kiểu nguyên thủy
                A. int
                B. String
                C. boolean
                ANSWER: A, C
                """);
        assertEquals(List.of(), r.errors());
        assertEquals(2, r.questions().size());
        var q1 = r.questions().get(0);
        assertEquals(3, q1.line());
        assertEquals("Java chạy trên gì?", q1.body());
        assertFalse(q1.multiple());
        assertEquals(BankQuestion.DIFFICULTY_EASY, q1.difficulty());
        assertEquals("JVM là máy ảo Java", q1.explanation());
        assertEquals(List.of(new Option("JVM", true), new Option("CLR", false)), q1.options());
        var q2 = r.questions().get(1);
        assertEquals("Chọn các kiểu nguyên thủy", q2.body());
        assertTrue(q2.multiple());
        assertEquals(BankQuestion.DIFFICULTY_MEDIUM, q2.difficulty());
        assertNull(q2.explanation());
        assertEquals(List.of(true, false, true), q2.options().stream().map(Option::correct).toList());
    }

    @Test
    void acceptsStarMarkersAndQuestionsWithoutBlankLineAfterAnswer() {
        var r = parser.parse("""
                Thủ đô Việt Nam?
                a) Huế
                *b) Hà Nội
                Đáp án: B
                1 + 1 = ?
                *A. 2
                B. 3
                """);
        assertEquals(List.of(), r.errors());
        assertEquals(2, r.questions().size());
        assertTrue(r.questions().get(0).options().get(1).correct());
        assertTrue(r.questions().get(1).options().get(0).correct());
        assertEquals(5, r.questions().get(1).line());
    }

    @Test
    void keepsMultiLineBodyWithCodeAndBlankLines() {
        var r = parser.parse("""
                Đoạn mã sau in gì?
                #include <stdio.h>

                int main() { printf("%d", 2.5 + 1); }
                A. 3.5
                B. Lỗi biên dịch
                ANSWER: B
                """);
        assertEquals(List.of(), r.errors());
        assertEquals("""
                Đoạn mã sau in gì?
                #include <stdio.h>

                int main() { printf("%d", 2.5 + 1); }""", r.questions().get(0).body());
    }

    @Test
    void optionsMustBeInOrderAndContinueOnNextLine() {
        var r = parser.parse("""
                Câu 3. Chọn đáp án
                B. dòng này thuộc nội dung câu hỏi
                A. một
                hai
                B. ba
                ĐA: A
                """);
        // "ĐA" không phải từ khóa: thành phần nối của đáp án B, câu thiếu đáp án đúng
        assertEquals(List.of(new QuestionImportParser.LineIssue(1, QuestionImportParser.MISSING_ANSWER)),
                r.errors());

        var ok = parser.parse("""
                Câu 3. Chọn đáp án
                B. dòng này thuộc nội dung câu hỏi
                A. một
                hai
                B. ba
                ĐÁP ÁN: A
                """);
        var q = ok.questions().get(0);
        assertEquals("Chọn đáp án\nB. dòng này thuộc nội dung câu hỏi", q.body());
        assertEquals("một\nhai", q.options().get(0).body());
    }

    @Test
    void reportsErrorsWithStartLine() {
        var r = parser.parse("""
                Câu 1: thiếu đáp án

                Câu 2: chỉ một đáp án
                A. x
                ANSWER: A

                Câu 3: đáp án ngoài phạm vi
                A. x
                B. y
                ANSWER: D

                Câu 4: mức độ lạ
                *A. x
                B. y
                MỨC ĐỘ: siêu khó

                Câu 5: không đánh dấu
                A. x
                B. y
                """);
        assertEquals(List.of(
                new QuestionImportParser.LineIssue(1, QuestionImportParser.NO_OPTIONS),
                new QuestionImportParser.LineIssue(3, QuestionImportParser.TOO_FEW_OPTIONS),
                new QuestionImportParser.LineIssue(7, QuestionImportParser.INVALID_ANSWER),
                new QuestionImportParser.LineIssue(12, QuestionImportParser.INVALID_DIFFICULTY),
                new QuestionImportParser.LineIssue(17, QuestionImportParser.MISSING_ANSWER)), r.errors());
        assertEquals(0, r.questions().size());
    }

    @Test
    void handlesBomAndWindowsLineEndings() {
        var r = parser.parse("﻿Câu 1: x?\r\nA. a\r\nB. b\r\nANSWER: b\r\n");
        assertEquals(1, r.questions().size());
        assertTrue(r.questions().get(0).options().get(1).correct());
    }

    @Test
    void rejectsTooManyQuestions() {
        var small = new QuestionImportParser(1);
        assertThrows(QuestionImportParser.TooManyQuestionsException.class,
                () -> small.parse("Q1\n*A. a\nB. b\n\nQ2\n*A. a\nB. b\n"));
    }

    @Test
    void problemChecksOptionCountAndCorrectAnswers() {
        assertEquals(QuestionImportParser.TOO_FEW_OPTIONS,
                QuestionImportParser.problem(false, List.of(new Option("a", true))));
        assertEquals(QuestionImportParser.MISSING_ANSWER,
                QuestionImportParser.problem(false, List.of(new Option("a", false), new Option("b", false))));
        assertEquals(QuestionImportParser.SINGLE_MANY_CORRECT,
                QuestionImportParser.problem(false, List.of(new Option("a", true), new Option("b", true))));
        assertNull(QuestionImportParser.problem(true, List.of(new Option("a", true), new Option("b", true))));
    }

    @Test
    void questionWordStartsNewQuestionRightAfterOptions() {
        // Đề chép từ Word/PDF thường không có dòng trống giữa các câu
        var r = parser.parse("""
                Câu 1: 1 + 1 = ?
                A. 1
                *B. 2
                Câu 2: Thủ đô?
                *A. Hà Nội
                B. Huế
                """);
        assertEquals(List.of(), r.errors());
        assertEquals(2, r.questions().size());
        assertEquals("2", r.questions().get(0).options().get(1).body());
        assertEquals(4, r.questions().get(1).line());
    }

    @Test
    void skipsUnnumberedPreambleAndEndMarkAndToleratesBlankBeforeAnswer() {
        // Như văn bản chép từ PDF: tiêu đề đề thi, khoảng cách đoạn trước dòng ĐÁP ÁN, dòng "HẾT"
        var r = parser.parse("""
                TRƯỜNG ĐẠI HỌC X
                ĐỀ KIỂM TRA GIỮA KỲ
                Câu 1:CSS là gì?
                A. Cascading Style Sheets
                B. Computer Style Sheets

                ĐÁP ÁN: A

                MỨC ĐỘ: dễ

                Câu 2: 2 + 2?
                A. 3
                B. 4

                ĐÁP ÁN: B
                ----- HẾT -----
                """);
        assertEquals(List.of(), r.errors());
        assertEquals(2, r.questions().size());
        assertEquals("CSS là gì?", r.questions().get(0).body());
        assertEquals(BankQuestion.DIFFICULTY_EASY, r.questions().get(0).difficulty());
        assertTrue(r.questions().get(1).options().get(1).correct());
    }

    @Test
    void numberedQuestionWithoutOptionsIsStillAnError() {
        var r = parser.parse("Câu 1: quên đáp án\n\nCâu 2: x?\n*A. a\nB. b\n");
        assertEquals(List.of(new QuestionImportParser.LineIssue(1, QuestionImportParser.NO_OPTIONS)), r.errors());
    }
}
