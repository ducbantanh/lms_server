package lms.submission;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class SubmissionViews {

    private SubmissionViews() {
    }

    /** View bài nộp cho sinh viên. */
    public record StudentSubmissionView(
            String id,
            String fileName,
            long sizeBytes,
            String contentType,
            Instant submittedAt,
            BigDecimal score,
            String feedback,
            Instant gradedAt
    ) {
        public static StudentSubmissionView from(Submission s) {
            if (s == null) return null;
            return new StudentSubmissionView(
                    s.getPublicId(),
                    s.getFileName(),
                    s.getSizeBytes(),
                    s.getContentType(),
                    s.getSubmittedAt(),
                    s.getScore(),
                    s.getFeedback(),
                    s.getGradedAt()
            );
        }
    }

    /** Dòng bài nộp cho giảng viên chấm điểm. */
    public record TeacherSubmissionRow(
            String id,
            String studentPublicId,
            String username,
            String fullName,
            String fileName,
            long sizeBytes,
            Instant submittedAt,
            BigDecimal score,
            String feedback,
            Instant gradedAt,
            String graderName
    ) {
    }

    /** Request chấm điểm của giảng viên. Thang điểm 0.00 đến 10.00 */
    public record GradeRequest(
            @NotNull
            @DecimalMin("0.0")
            @DecimalMax("10.0")
            BigDecimal score,

            @Size(max = 2000)
            String feedback
    ) {
    }
}
