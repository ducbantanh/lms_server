package lms.code;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/** Kết quả một test của một lần chạy; vị trí trong CodeAttempt.results là thứ tự test lúc chấm. */
@Embeddable
public class CodeResult {

    @Column(nullable = false, length = 8)
    private String verdict;

    @Column(name = "time_ms", nullable = false)
    private int timeMs;

    private String output;

    private String error;

    protected CodeResult() {
        // JPA
    }

    public CodeResult(String verdict, int timeMs, String output, String error) {
        this.verdict = verdict;
        this.timeMs = timeMs;
        this.output = output;
        this.error = error;
    }

    public String getVerdict() {
        return verdict;
    }

    public int getTimeMs() {
        return timeMs;
    }

    public String getOutput() {
        return output;
    }

    public String getError() {
        return error;
    }
}
