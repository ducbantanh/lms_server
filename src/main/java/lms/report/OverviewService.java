package lms.report;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lms.auth.AuthPrincipal;
import lms.auth.Role;
import lms.course.Assessment;
import lms.course.AssessmentRepository;
import lms.course.Course;
import lms.course.CourseRepository;
import lms.course.Enrollment;
import lms.course.EnrollmentRepository;
import lms.progress.ProgressService;
import lms.report.OverviewViews.CourseRow;
import lms.report.OverviewViews.GradingTask;
import lms.report.OverviewViews.ScheduleItem;
import lms.report.OverviewViews.Stats;
import lms.report.OverviewViews.TeacherOverview;
import lms.submission.Submission;
import lms.submission.SubmissionFileRepository;
import lms.submission.SubmissionRepository;
import lms.submission.SubmissionService;
import lms.submission.SubmissionViews;

/**
 * Tổng quan giảng viên: việc cần làm (bài chờ chấm), lịch bài đang/sắp mở, tình hình từng khóa học.
 * Phạm vi giống danh sách khóa học giảng viên (admin: mọi khóa học). Chỉ tính sinh viên đang ghi danh,
 * như trang bài nộp. Số truy vấn cố định, không phụ thuộc số khóa học hay số bài.
 */
@Service
public class OverviewService {

    static final int UPCOMING_DAYS = 14;
    static final int LIST_LIMIT = 10;

    private final CourseRepository courses;
    private final EnrollmentRepository enrollments;
    private final AssessmentRepository assessments;
    private final SubmissionRepository submissions;
    private final SubmissionFileRepository files;
    private final ProgressService progress;

    public OverviewService(CourseRepository courses, EnrollmentRepository enrollments,
            AssessmentRepository assessments, SubmissionRepository submissions, SubmissionFileRepository files,
            ProgressService progress) {
        this.courses = courses;
        this.enrollments = enrollments;
        this.assessments = assessments;
        this.submissions = submissions;
        this.files = files;
        this.progress = progress;
    }

    @Transactional(readOnly = true)
    public TeacherOverview overview(AuthPrincipal me) {
        Instant now = Instant.now();
        List<Course> list = me.role() == Role.ADMIN
                ? courses.findAllNewestFirst()
                : courses.findByTeacherNewestFirst(me.userId());
        if (list.isEmpty()) {
            return new TeacherOverview(new Stats(0, 0, 0, 0), List.of(), 0, List.of(), 0, List.of(), now);
        }
        List<Long> ids = list.stream().map(Course::getId).toList();
        Map<Long, Course> courseById = list.stream().collect(Collectors.toMap(Course::getId, c -> c));
        Map<Long, Set<Long>> enrolled = enrollments.findByCourseIdIn(ids).stream().collect(
                Collectors.groupingBy(Enrollment::getCourseId, Collectors.mapping(Enrollment::getUserId,
                        Collectors.toSet())));

        List<Assessment> all = assessments.findByCourseIdInOrderByCreatedAt(ids);
        List<Submission> subs = all.isEmpty() ? List.of()
                : submissions.findByAssessmentIdIn(all.stream().map(Assessment::getId).toList());
        Map<Long, Long> fileCounts = subs.isEmpty() ? Map.of()
                : files.countBySubmissionIds(subs.stream().map(Submission::getId).toList()).stream()
                        .collect(Collectors.toMap(r -> (Long) r[0], r -> (Long) r[1]));
        Map<Long, Map<Long, Submission>> byAssessment = subs.stream().collect(Collectors.groupingBy(
                Submission::getAssessmentId, Collectors.toMap(Submission::getUserId, s -> s)));

        List<GradingTask> tasks = new ArrayList<>();
        List<ScheduleItem> schedule = new ArrayList<>();
        Map<Long, Integer> pendingByCourse = new HashMap<>();
        Map<Long, Integer> openByCourse = new HashMap<>();
        Instant horizon = now.plus(Duration.ofDays(UPCOMING_DAYS));
        for (Assessment a : all) {
            Course c = courseById.get(a.getCourseId());
            Set<Long> students = enrolled.getOrDefault(c.getId(), Set.of());
            Map<Long, Submission> mine = byAssessment.getOrDefault(a.getId(), Map.of());
            int submitted = 0;
            int pending = 0;
            for (Long u : students) {
                Submission s = mine.get(u);
                int count = s == null ? 0 : fileCounts.getOrDefault(s.getId(), 0L).intValue();
                String status = SubmissionService.statusOf(a, s, count, now);
                if (SubmissionViews.SUBMITTED.equals(status) || SubmissionViews.AUTO_SUBMITTED.equals(status)) {
                    submitted++;
                    // Bài lập trình không chấm điểm: không có việc cần chấm
                    if (!a.isCode() && !s.isGraded()) {
                        pending++;
                    }
                }
            }
            if (pending > 0) {
                tasks.add(new GradingTask(c.getPublicId(), c.displayCode(), a.getPublicId(), a.getType(), a.getTitle(),
                        a.getClosesAt(), pending, submitted, students.size()));
                pendingByCourse.merge(c.getId(), pending, Integer::sum);
            }
            String status = a.statusAt(now);
            boolean upcomingSoon = "upcoming".equals(status) && a.getOpensAt().isBefore(horizon);
            if ("open".equals(status) || upcomingSoon) {
                schedule.add(new ScheduleItem(c.getPublicId(), c.displayCode(), a.getPublicId(), a.getType(),
                        a.getTitle(), "open".equals(status) ? "open" : "upcoming", a.getOpensAt(), a.getClosesAt(),
                        submitted, students.size()));
            }
            if ("open".equals(status)) {
                openByCourse.merge(c.getId(), 1, Integer::sum);
            }
        }
        // Chấm bài hết hạn sớm nhất trước; bài không hạn xếp cuối
        tasks.sort(Comparator.comparing(GradingTask::closesAt, Comparator.nullsLast(Comparator.naturalOrder())));
        schedule.sort(Comparator.comparing(OverviewService::scheduleTime, Comparator.nullsLast(Comparator.naturalOrder())));

        Map<Long, Integer> average = progress.averagePercent(list, enrolled);
        List<CourseRow> rows = list.stream().map(c -> new CourseRow(c.getPublicId(), c.getCode(), c.getGroupName(),
                c.getTitle(), c.getFormat(), c.getTeacher() == null ? null : c.getTeacher().getFullName(),
                enrolled.getOrDefault(c.getId(), Set.of()).size(), average.get(c.getId()),
                pendingByCourse.getOrDefault(c.getId(), 0), openByCourse.getOrDefault(c.getId(), 0),
                c.getCreatedAt())).toList();

        Set<Long> students = new HashSet<>();
        enrolled.values().forEach(students::addAll);
        Stats stats = new Stats(list.size(), students.size(), tasks.stream().mapToInt(GradingTask::pending).sum(),
                (int) schedule.stream().filter(i -> "open".equals(i.kind())).count());
        return new TeacherOverview(stats, head(tasks), Math.max(0, tasks.size() - LIST_LIMIT), head(schedule),
                Math.max(0, schedule.size() - LIST_LIMIT), rows, now);
    }

    /** Mốc thời gian sắp tới của một mục lịch: giờ đóng của bài đang mở, giờ mở của bài sắp mở. */
    static Instant scheduleTime(ScheduleItem i) {
        return "open".equals(i.kind()) ? i.closesAt() : i.opensAt();
    }

    private static <T> List<T> head(List<T> list) {
        return list.size() <= LIST_LIMIT ? list : list.subList(0, LIST_LIMIT);
    }
}
