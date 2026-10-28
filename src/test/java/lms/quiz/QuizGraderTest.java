package lms.quiz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

class QuizGraderTest {

    /** Câu hỏi có id (bình thường do DB cấp) và các đáp án đúng ở vị trí cho trước. */
    private static QuizQuestion question(long id, boolean multiple, int options, Integer... correct) {
        Set<Integer> right = Set.of(correct);
        QuizQuestion q = new QuizQuestion(1L, (int) id, "Câu " + id, multiple, null, null,
                IntStream.range(0, options).mapToObj(i -> new QuizOption("Đáp án " + i, right.contains(i))).toList());
        try {
            Field f = QuizQuestion.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(q, id);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
        return q;
    }

    @Test
    void multipleChoiceNeedsExactlyTheCorrectSet() {
        QuizQuestion q = question(1, true, 4, 0, 2);
        assertTrue(QuizGrader.isCorrect(q, Set.of(0, 2)));
        assertFalse(QuizGrader.isCorrect(q, Set.of(0)));
        assertFalse(QuizGrader.isCorrect(q, Set.of(0, 1, 2)));
        assertFalse(QuizGrader.isCorrect(q, Set.of()));
        assertFalse(QuizGrader.isCorrect(q, null));
    }

    @Test
    void scoreIsCorrectOverTotalTimesTenRounded() {
        List<QuizQuestion> qs = List.of(question(1, false, 2, 0), question(2, false, 2, 1),
                question(3, true, 3, 0, 1));
        var r = QuizGrader.grade(qs, Map.of(1L, Set.of(0), 2L, Set.of(0), 3L, Set.of(0, 1)));
        assertEquals(2, r.correct());
        assertEquals(3, r.total());
        assertEquals(new BigDecimal("6.67"), r.score());
        assertEquals(new BigDecimal("0.00"), QuizGrader.grade(qs, Map.of()).score());
        assertEquals(new BigDecimal("0.00"), QuizGrader.grade(List.of(), Map.of()).score());
        assertEquals(new BigDecimal("10.00"),
                QuizGrader.grade(List.of(qs.get(0)), Map.of(1L, Set.of(0))).score());
    }

    @Test
    void shuffleIsStablePerSeedAndKeepsElements() {
        List<Integer> list = IntStream.range(0, 20).boxed().toList();
        long seed = QuizGrader.seed("abcdefghijklmnopqrstuv", 0);
        assertEquals(QuizGrader.shuffled(list, seed), QuizGrader.shuffled(list, seed));
        assertEquals(Set.copyOf(list), Set.copyOf(QuizGrader.shuffled(list, seed)));
        assertNotEquals(QuizGrader.shuffled(list, seed),
                QuizGrader.shuffled(list, QuizGrader.seed("zzzzzzzzzzzzzzzzzzzzzz", 0)));
    }
}
