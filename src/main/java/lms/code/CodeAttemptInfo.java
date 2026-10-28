package lms.code;

import java.time.Instant;

/** Thông tin một lần chạy/nộp không kèm mã nguồn và kết quả từng test (CodeAttemptRepository, danh sách). */
public record CodeAttemptInfo(Long id, String publicId, Long problemId, Long userId, int kind, String language,
        int status, String verdict, Integer passed, Integer total, Instant createdAt, Instant judgedAt) {

    public boolean isSubmit() {
        return kind == CodeAttempt.KIND_SUBMIT;
    }

    public boolean isDone() {
        return status == CodeAttempt.DONE;
    }

    /** Lỗi máy chấm: không tính là một lần nộp, không tính vào kết quả. */
    public boolean isSystemError() {
        return isDone() && CodeJudge.SE.equals(verdict);
    }

    public boolean isAccepted() {
        return isDone() && CodeJudge.AC.equals(verdict);
    }
}
