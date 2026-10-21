package lms.progress;

import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lms.auth.AuthPrincipal;
import lms.common.AppException;
import lms.course.Assessment;
import lms.course.AssessmentRepository;
import lms.course.Course;
import lms.course.CourseRepository;
import lms.course.CourseSection;
import lms.course.CourseSectionRepository;
import lms.course.CourseService;
import lms.course.EnrollmentRepository;
import lms.course.Lesson;
import lms.course.LessonRepository;
import lms.progress.ProgressViews.CourseProgress;
import lms.progress.ProgressViews.CourseProgressReport;
import lms.progress.ProgressViews.NextLesson;
import lms.progress.ProgressViews.StudentProgress;
import lms.submission.SubmissionRepository;
import lms.user.User;

/**
 * Tiến độ học: bài học sinh viên đã đánh dấu học xong + bài kiểm tra đã nộp, trên tổng số bài học đang hiện
 * và bài kiểm tra của khóa học. Tính lúc đọc, không lưu tổng nên ẩn/xóa bài là tiến độ tự đúng.
 * Khóa học "chỉ danh sách tài liệu" không hiện bài học cho sinh viên nên chỉ tính bài kiểm tra.
 */
@Service
public class ProgressService {

    /** Loại bài sinh viên nộp trên web (khớp SUBMITTABLE_TYPES ở client); bài tập/thực hành không tính. */
    static final int COUNTED_TYPE = Assessment.TYPE_EXAM;

    private final CourseRepository courses;
    private final CourseService courseService;
    private final EnrollmentRepository enrollments;
    private final LessonRepository lessons;
    private final CourseSectionRepository sections;
    private final AssessmentRepository assessments;
    private final SubmissionRepository submissions;
    private final LessonCompletionRepository completions;

    public ProgressService(CourseRepository courses, CourseService courseService, EnrollmentRepository enrollments,
            LessonRepository lessons, CourseSectionRepository sections, AssessmentRepository assessments,
            SubmissionRepository submissions, LessonCompletionRepository completions) {
        this.courses = courses;
        this.courseService = courseService;
        this.enrollments = enrollments;
        this.lessons = lessons;
        this.sections = sections;
        this.assessments = assessments;
        this.submissions = submissions;
        this.completions = completions;
    }

    // ================================================================ sinh viên

    @Transactional(readOnly = true)
    public CourseProgress forCourse(String coursePublicId, AuthPrincipal me) {
        Course c = requireEnrolled(coursePublicId, me);
        return forCourses(me.userId(), List.of(c)).get(c.getId());
    }

    /** Đánh dấu / bỏ đánh dấu đã học; bài bị ẩn coi như không tồn tại. Trả về tiến độ mới. */
    @Transactional
    public CourseProgress setCompleted(String coursePublicId, String lessonPublicId, boolean completed,
            AuthPrincipal me) {
        Course c = requireEnrolled(coursePublicId, me);
        Lesson l = lessons.findByPublicIdAndCourseId(lessonPublicId, c.getId())
                .filter(x -> !x.isHidden() && c.getFormat() != Course.FORMAT_FILES_ONLY)
                .orElseThrow(() -> AppException.notFound("Lesson"));
        if (completed) {
            completions.insertIgnore(l.getId(), me.userId());
        } else {
            completions.deleteOne(l.getId(), me.userId());
        }
        return forCourses(me.userId(), List.of(c)).get(c.getId());
    }

    /** Tiến độ của một sinh viên ở nhiều khóa học (dashboard), theo id khóa học; số truy vấn không phụ thuộc số khóa. */
    @Transactional(readOnly = true)
    public Map<Long, CourseProgress> forCourses(long userId, List<Course> list) {
        if (list.isEmpty()) {
            return Map.of();
        }
        List<Long> ids = list.stream().map(Course::getId).toList();
        List<Long> withLessons = list.stream().filter(c -> c.getFormat() != Course.FORMAT_FILES_ONLY)
                .map(Course::getId).toList();
        boolean none = withLessons.isEmpty();
        Map<Long, List<Lesson>> lessonsByCourse = (none ? List.<Lesson>of() : lessons.findVisibleIn(withLessons))
                .stream().collect(Collectors.groupingBy(Lesson::getCourseId));
        Map<Long, CourseSection> sectionById = (none ? List.<CourseSection>of() : sections.findByCourseIdIn(withLessons))
                .stream().collect(Collectors.toMap(CourseSection::getId, Function.identity()));
        Set<Long> done = none ? Set.of() : new HashSet<>(completions.lessonIdsDone(userId, withLessons));
        Map<Long, Long> totals = counts(assessments.countByType(ids, COUNTED_TYPE));
        Map<Long, Long> finished = counts(submissions.countFinishedByUser(userId, ids, COUNTED_TYPE, Instant.now()));

        Map<Long, CourseProgress> result = new HashMap<>();
        for (Course c : list) {
            List<Lesson> ordered = inCourseOrder(lessonsByCourse.getOrDefault(c.getId(), List.of()), sectionById);
            List<String> completed = ordered.stream().filter(l -> done.contains(l.getId()))
                    .map(Lesson::getPublicId).toList();
            Lesson next = ordered.stream().filter(l -> !done.contains(l.getId())).findFirst().orElse(null);
            int assessmentsTotal = totals.getOrDefault(c.getId(), 0L).intValue();
            int assessmentsDone = finished.getOrDefault(c.getId(), 0L).intValue();
            result.put(c.getId(), new CourseProgress(completed.size(), ordered.size(), assessmentsDone,
                    assessmentsTotal, percent(completed.size() + assessmentsDone, ordered.size() + assessmentsTotal),
                    next == null ? null : nextLesson(next, sectionById), completed));
        }
        return result;
    }

    // ================================================================ giảng viên

    @Transactional(readOnly = true)
    public CourseProgressReport forStudents(String coursePublicId, AuthPrincipal me) {
        Course c = courseService.requireManaged(coursePublicId, me);
        boolean withLessons = c.getFormat() != Course.FORMAT_FILES_ONLY;
        int lessonsTotal = withLessons ? (int) lessons.countVisible(c.getId()) : 0;
        Map<Long, Long> lessonsDone = withLessons ? counts(completions.countByUser(c.getId())) : Map.of();
        int assessmentsTotal = counts(assessments.countByType(List.of(c.getId()), COUNTED_TYPE))
                .getOrDefault(c.getId(), 0L).intValue();
        Map<Long, Long> finished = counts(submissions.countFinishedByCourse(c.getId(), COUNTED_TYPE, Instant.now()));
        List<StudentProgress> rows = enrollments.findWithUsers(c.getId()).stream().map(row -> {
            User u = (User) row[1];
            int ld = lessonsDone.getOrDefault(u.getId(), 0L).intValue();
            int ad = finished.getOrDefault(u.getId(), 0L).intValue();
            return new StudentProgress(u.getPublicId(), ld, ad, percent(ld + ad, lessonsTotal + assessmentsTotal));
        }).toList();
        return new CourseProgressReport(lessonsTotal, assessmentsTotal, rows);
    }

    /**
     * Tiến độ trung bình (làm tròn) của sinh viên đang ghi danh ở từng khóa học, cho tổng quan giảng viên.
     * Không có trong map: khóa chưa có sinh viên hoặc chưa có gì để tính. Số truy vấn không phụ thuộc số khóa.
     */
    @Transactional(readOnly = true)
    public Map<Long, Integer> averagePercent(List<Course> list, Map<Long, Set<Long>> enrolledByCourse) {
        if (list.isEmpty()) {
            return Map.of();
        }
        List<Long> ids = list.stream().map(Course::getId).toList();
        List<Long> withLessons = list.stream().filter(c -> c.getFormat() != Course.FORMAT_FILES_ONLY)
                .map(Course::getId).toList();
        boolean none = withLessons.isEmpty();
        Map<Long, Long> lessonTotals = none ? Map.of() : counts(lessons.countVisibleByCourse(withLessons));
        Map<String, Long> lessonsDone = none ? Map.of() : pairCounts(completions.countByCourseAndUser(withLessons));
        Map<Long, Long> assessmentTotals = counts(assessments.countByType(ids, COUNTED_TYPE));
        Map<String, Long> finished = pairCounts(
                submissions.countFinishedByCourseAndUser(ids, COUNTED_TYPE, Instant.now()));

        Map<Long, Integer> result = new HashMap<>();
        for (Course c : list) {
            Set<Long> students = enrolledByCourse.getOrDefault(c.getId(), Set.of());
            int total = lessonTotals.getOrDefault(c.getId(), 0L).intValue()
                    + assessmentTotals.getOrDefault(c.getId(), 0L).intValue();
            if (students.isEmpty() || total == 0) {
                continue;
            }
            double sum = 0;
            for (Long u : students) {
                String key = c.getId() + ":" + u;
                int done = lessonsDone.getOrDefault(key, 0L).intValue() + finished.getOrDefault(key, 0L).intValue();
                sum += percent(done, total);
            }
            result.put(c.getId(), (int) Math.round(sum / students.size()));
        }
        return result;
    }

    // ================================================================ nội bộ

    /** [courseId, userId, count] → map "courseId:userId". */
    private static Map<String, Long> pairCounts(Collection<Object[]> rows) {
        return rows.stream().collect(Collectors.toMap(r -> r[0] + ":" + r[1], r -> (Long) r[2]));
    }

    private Course requireEnrolled(String coursePublicId, AuthPrincipal me) {
        return courses.findByPublicId(coursePublicId)
                .filter(c -> enrollments.existsByCourseIdAndUserId(c.getId(), me.userId()))
                .orElseThrow(() -> AppException.notFound("Course"));
    }

    private static NextLesson nextLesson(Lesson l, Map<Long, CourseSection> sectionById) {
        CourseSection s = l.getSectionId() == null ? null : sectionById.get(l.getSectionId());
        return new NextLesson(l.getPublicId(), s == null ? null : s.getPublicId(), l.getTitle());
    }

    /** [id, count] → map. */
    private static Map<Long, Long> counts(Collection<Object[]> rows) {
        return rows.stream().collect(Collectors.toMap(r -> (Long) r[0], r -> (Long) r[1]));
    }

    /** Làm tròn xuống để chỉ hiện 100% khi xong hết; null khi không có gì để tính. */
    static Integer percent(int done, int total) {
        return total == 0 ? null : Math.min(100, done * 100 / total);
    }

    /**
     * Thứ tự hiển thị như groupContent ở client: theo vị trí mục, bài chưa phân mục xếp cuối,
     * trong mục theo vị trí bài.
     */
    static List<Lesson> inCourseOrder(List<Lesson> list, Map<Long, CourseSection> sectionById) {
        Comparator<Lesson> bySection = Comparator.comparingInt(l -> {
            CourseSection s = l.getSectionId() == null ? null : sectionById.get(l.getSectionId());
            return s == null ? Integer.MAX_VALUE : s.getPosition();
        });
        return list.stream()
                .sorted(bySection.thenComparingInt(Lesson::getPosition)
                        .thenComparing(Lesson::getId, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }
}
