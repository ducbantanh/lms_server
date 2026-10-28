package lms.export;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lms.common.AppException;
import lms.common.ErrorCode;
import lms.common.util.CsvUtils;
import lms.course.Assessment;
import lms.course.AssessmentRepository;
import lms.course.Course;
import lms.course.CourseRepository;
import lms.course.EnrollmentRepository;
import lms.submission.Submission;
import lms.submission.SubmissionRepository;
import lms.user.User;

/**
 * Dịch vụ trích xuất bảng điểm học phần định dạng CSV tương thích Microsoft Excel (BOM UTF-8).
 */
@Service
public class GradebookExportService {

    private final CourseRepository courseRepository;
    private final AssessmentRepository assessmentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SubmissionRepository submissionRepository;

    public GradebookExportService(
            CourseRepository courseRepository,
            AssessmentRepository assessmentRepository,
            EnrollmentRepository enrollmentRepository,
            SubmissionRepository submissionRepository) {
        this.courseRepository = courseRepository;
        this.assessmentRepository = assessmentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.submissionRepository = submissionRepository;
    }

    /**
     * Xuất toàn bộ bảng điểm của khóa học ra mảng byte CSV.
     */
    @Transactional(readOnly = true)
    public byte[] exportGradebookCsv(String coursePublicId) {
        Course course = courseRepository.findByPublicId(coursePublicId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy khóa học"));

        // Lấy danh sách sinh viên ghi danh
        List<Object[]> enrollmentUsers = enrollmentRepository.findWithUsers(course.getId());
        List<User> students = enrollmentUsers.stream()
                .map(row -> (User) row[1])
                .toList();

        // Lấy danh sách các cột bài kiểm tra / bài tập
        List<Assessment> assessments = assessmentRepository.findByCourseIdOrderByCreatedAt(course.getId());

        // Lấy toàn bộ bài nộp
        List<Long> assessmentIds = assessments.stream().map(Assessment::getId).toList();
        List<Submission> allSubmissions = assessmentIds.isEmpty() ?
                Collections.emptyList() :
                submissionRepository.findByAssessmentIdIn(assessmentIds);

        // Map (assessmentId + "_" + userId) -> điểm
        Map<String, BigDecimal> scoreMap = new HashMap<>();
        for (Submission s : allSubmissions) {
            if (s.getScore() != null) {
                scoreMap.put(s.getAssessmentId() + "_" + s.getUserId(), s.getScore());
            }
        }

        StringBuilder csv = new StringBuilder();
        csv.append(CsvUtils.UTF8_BOM);

        // Header dòng 1: Thông tin học phần
        csv.append(CsvUtils.buildLine(List.of("HỌC PHẦN:", course.getTitle(), "MÃ:", course.getCode(), "NHÓM:", course.getGroupName() == null ? "" : course.getGroupName()))).append("\n");
        csv.append("\n");

        // Header dòng tiêu đề cột
        List<String> headers = new ArrayList<>();
        headers.add("STT");
        headers.add("Mã sinh viên");
        headers.add("Họ và tên");
        headers.add("Email");

        for (Assessment a : assessments) {
            headers.add(a.getTitle());
        }
        headers.add("Điểm trung bình");
        csv.append(CsvUtils.buildLine(headers)).append("\n");

        // Dữ liệu từng sinh viên
        int index = 1;
        for (User student : students) {
            List<Object> row = new ArrayList<>();
            row.add(index++);
            row.add(student.getUsername());
            row.add(student.getFullName());
            row.add(student.getEmail() == null ? "" : student.getEmail());

            double totalScore = 0.0;
            int scoredCount = 0;

            for (Assessment a : assessments) {
                BigDecimal score = scoreMap.get(a.getId() + "_" + student.getId());
                if (score != null) {
                    row.add(score.toString());
                    totalScore += score.doubleValue();
                    scoredCount++;
                } else {
                    row.add("-");
                }
            }

            if (scoredCount > 0) {
                double avg = Math.round((totalScore / scoredCount) * 100.0) / 100.0;
                row.add(String.valueOf(avg));
            } else {
                row.add("-");
            }

            csv.append(CsvUtils.buildLine(row)).append("\n");
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }
}
