package lms.progress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import lms.course.CourseSection;
import lms.course.Lesson;

class ProgressServiceTest {

    private static <T> T withId(T entity, long id) {
        try {
            Field f = entity.getClass().getDeclaredField("id");
            f.setAccessible(true);
            f.set(entity, id);
            return entity;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Lesson lesson(long id, Long sectionId, int position) {
        return withId(new Lesson(1L, sectionId, position, "L" + id, null, false), id);
    }

    @Test
    void percentRoundsDownAndIsNullWithoutItems() {
        assertNull(ProgressService.percent(0, 0));
        assertEquals(0, ProgressService.percent(0, 3));
        assertEquals(66, ProgressService.percent(2, 3));
        assertEquals(99, ProgressService.percent(199, 200));
        assertEquals(100, ProgressService.percent(3, 3));
    }

    @Test
    void orderFollowsSectionPositionThenLessonPositionWithUnsectionedLast() {
        // Mục 20 đứng trước mục 10 vì position nhỏ hơn
        Map<Long, CourseSection> sections = Map.of(
                10L, withId(new CourseSection(1L, 2, "Tuần 2"), 10L),
                20L, withId(new CourseSection(1L, 1, "Tuần 1"), 20L));
        List<Lesson> lessons = List.of(
                lesson(1, null, 1),
                lesson(2, 10L, 2),
                lesson(3, 10L, 1),
                lesson(4, 20L, 1),
                lesson(5, 99L, 1)); // mục không còn: coi như chưa phân mục

        List<Long> order = ProgressService.inCourseOrder(lessons, sections).stream().map(Lesson::getId).toList();

        assertEquals(List.of(4L, 3L, 2L, 1L, 5L), order);
    }
}
