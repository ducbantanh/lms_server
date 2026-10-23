package lms.report;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lms.audit.AuditService;
import lms.auth.AuthPrincipal;
import lms.common.AppException;
import lms.course.Assessment;
import lms.course.AssessmentRepository;
import lms.course.Course;
import lms.course.CourseService;
import lms.course.EnrollmentRepository;
import lms.course.FileStorage;
import lms.progress.ProgressService;
import lms.progress.ProgressViews.StudentProgress;
import lms.submission.Submission;
import lms.submission.SubmissionFile;
import lms.submission.SubmissionFileRepository;
import lms.submission.SubmissionRepository;
import lms.submission.SubmissionService;
import lms.submission.SubmissionViews;
import lms.user.User;

/**
 * Xuất dữ liệu cho giảng viên: bảng điểm CSV của cả khóa học, và file bài nộp của một bài dạng ZIP.
 * Chỉ người quản lý khóa học (NOT_FOUND nếu không), mỗi lần xuất ghi audit.
 */
@Service
public class ReportService {

    /** Ký hiệu loại bài trong tiêu đề cột bảng điểm, theo Assessment.TYPE_*. */
    private static final String[] TYPE_PREFIX = { "BT", "TH", "KT" };

    /** Bảng điểm đã dựng xong: tên file và nội dung (UTF-8 có BOM để Excel đọc đúng tiếng Việt). */
    public record Csv(String fileName, byte[] content) {
    }

    /** Một file trong ZIP: đường dẫn trong ZIP và file trên đĩa. */
    public record ZipItem(String name, Path path) {
    }

    public record Archive(String fileName, List<ZipItem> items) {
    }

    private final CourseService courses;
    private final AssessmentRepository assessments;
    private final EnrollmentRepository enrollments;
    private final SubmissionRepository submissions;
    private final SubmissionFileRepository files;
    private final ProgressService progress;
    private final FileStorage storage;
    private final AuditService audit;

    public ReportService(CourseService courses, AssessmentRepository assessments, EnrollmentRepository enrollments,
            SubmissionRepository submissions, SubmissionFileRepository files, ProgressService progress,
            FileStorage storage, AuditService audit) {
        this.courses = courses;
        this.assessments = assessments;
        this.enrollments = enrollments;
        this.submissions = submissions;
        this.files = files;
        this.progress = progress;
        this.storage = storage;
        this.audit = audit;
    }

    /**
     * Mỗi sinh viên đang ghi danh một dòng (theo MSSV); mỗi bài một cột điểm (trống = chưa chấm),
     * cột cuối là tiến độ. Số thập phân dùng dấu chấm.
     */
    @Transactional(readOnly = true)
    public Csv gradebook(String courseId, AuthPrincipal me) {
        Course c = courses.requireManaged(courseId, me);
        List<Assessment> list = assessments.findByCourseIdOrderByCreatedAt(c.getId());
        Map<String, BigDecimal> scores = list.isEmpty() ? Map.of()
                : submissions.findByAssessmentIdIn(list.stream().map(Assessment::getId).toList()).stream()
                        .filter(Submission::isGraded)
                        .collect(Collectors.toMap(s -> s.getAssessmentId() + ":" + s.getUserId(), Submission::getScore));
        Map<String, Integer> percent = progress.forStudents(courseId, me).students().stream()
                .filter(p -> p.percent() != null)
                .collect(Collectors.toMap(StudentProgress::studentId, StudentProgress::percent));

        List<String> header = new ArrayList<>(List.of("MSSV", "Họ tên"));
        list.forEach(a -> header.add("[" + TYPE_PREFIX[a.getType()] + "] " + a.getTitle()));
        header.add("Tiến độ (%)");
        StringBuilder out = new StringBuilder("﻿").append(csvLine(header));
        int rows = 0;
        for (Object[] row : enrollments.findWithUsers(c.getId())) {
            User u = (User) row[1];
            List<String> cells = new ArrayList<>(List.of(u.getUsername(), u.getFullName()));
            for (Assessment a : list) {
                BigDecimal score = scores.get(a.getId() + ":" + u.getId());
                cells.add(score == null ? "" : score.stripTrailingZeros().toPlainString());
            }
            Integer p = percent.get(u.getPublicId());
            cells.add(p == null ? "" : p.toString());
            out.append(csvLine(cells));
            rows++;
        }
        audit.record(me.userId(), AuditService.GRADES_EXPORTED, c.displayCode(),
                rows + " sinh viên, " + list.size() + " bài");
        return new Csv("bang-diem-" + fileSafe(c.displayCode()) + ".csv", out.toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * File của các bài đã chốt (đã nộp, hoặc hết giờ có file) của sinh viên đang ghi danh,
     * mỗi sinh viên một thư mục "MSSV - Họ tên". Bài đang làm dở không có trong ZIP.
     */
    @Transactional(readOnly = true)
    public Archive submissionArchive(String courseId, String assessmentId, AuthPrincipal me) {
        Course c = courses.requireManaged(courseId, me);
        Assessment a = assessments.findByPublicIdAndCourseId(assessmentId, c.getId())
                .orElseThrow(() -> AppException.notFound("Assessment"));
        Map<Long, Submission> byUser = submissions.findByAssessmentId(a.getId()).stream()
                .collect(Collectors.toMap(Submission::getUserId, Function.identity()));
        Map<Long, List<SubmissionFile>> filesBySubmission = byUser.isEmpty() ? Map.of()
                : files.findBySubmissionIdInOrderByCreatedAt(byUser.values().stream().map(Submission::getId).toList())
                        .stream().collect(Collectors.groupingBy(SubmissionFile::getSubmissionId));
        Instant now = Instant.now();
        List<ZipItem> items = new ArrayList<>();
        int students = 0;
        for (Object[] row : enrollments.findWithUsers(c.getId())) {
            User u = (User) row[1];
            Submission s = byUser.get(u.getId());
            List<SubmissionFile> fs = s == null ? List.of() : filesBySubmission.getOrDefault(s.getId(), List.of());
            String status = SubmissionService.statusOf(a, s, fs.size(), now);
            if (fs.isEmpty() || !(SubmissionViews.SUBMITTED.equals(status)
                    || SubmissionViews.AUTO_SUBMITTED.equals(status))) {
                continue;
            }
            String folder = fileSafe(u.getUsername() + " - " + u.getFullName());
            Set<String> used = new HashSet<>();
            for (SubmissionFile f : fs) {
                items.add(new ZipItem(folder + "/" + uniqueName(fileSafe(f.getName()), used),
                        storage.path(f.getStorageKey())));
            }
            students++;
        }
        audit.record(me.userId(), AuditService.SUBMISSIONS_DOWNLOADED, c.displayCode(),
                a.getTitle() + " · " + students + " sinh viên, " + items.size() + " file");
        return new Archive(fileSafe(c.displayCode() + " - " + a.getTitle()) + ".zip", items);
    }

    // ---------------------------------------------------------------- tiện ích (package-private để test)

    /**
     * Một dòng CSV: bọc nháy kép khi có dấu phẩy, nháy, xuống dòng; ô bắt đầu bằng = + - @ được thêm ' phía trước
     * để Excel không chạy như công thức (họ tên, tiêu đề bài do người dùng nhập).
     */
    static String csvLine(List<String> cells) {
        return cells.stream().map(ReportService::csvCell).collect(Collectors.joining(",")) + "\r\n";
    }

    static String csvCell(String value) {
        String v = value == null ? "" : value;
        if (!v.isEmpty() && "=+-@\t\r".indexOf(v.charAt(0)) >= 0) {
            v = "'" + v;
        }
        return v.matches("(?s).*[\",\r\n].*") ? "\"" + v.replace("\"", "\"\"") + "\"" : v;
    }

    /** Tên file/thư mục dùng được trên Windows: bỏ ký tự cấm và ký tự điều khiển, cắt còn 100 ký tự. */
    static String fileSafe(String name) {
        String s = name.replaceAll("[\\\\/:*?\"<>|\\p{Cntrl}]", "_").trim().replaceAll("[. ]+$", "");
        if (s.length() > 100) {
            s = s.substring(0, 100).trim();
        }
        return s.isEmpty() ? "_" : s;
    }

    /** Hai file trùng tên trong cùng thư mục: "bai.pdf", "bai (2).pdf"... (so không phân biệt hoa thường như Windows). */
    static String uniqueName(String name, Set<String> used) {
        int dot = name.lastIndexOf('.');
        String base = dot > 0 ? name.substring(0, dot) : name;
        String ext = dot > 0 ? name.substring(dot) : "";
        String candidate = name;
        for (int i = 2; !used.add(candidate.toLowerCase(Locale.ROOT)); i++) {
            candidate = base + " (" + i + ")" + ext;
        }
        return candidate;
    }
}
