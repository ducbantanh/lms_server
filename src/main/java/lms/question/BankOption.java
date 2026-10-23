package lms.question;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/** Một đáp án của câu hỏi; thứ tự (A, B, C...) là cột position do BankQuestion.options quản lý. */
@Embeddable
public class BankOption {

    @Column(nullable = false, length = 1000)
    private String body;

    @Column(nullable = false)
    private boolean correct;

    protected BankOption() {
        // JPA
    }

    public BankOption(String body, boolean correct) {
        this.body = body;
        this.correct = correct;
    }

    public String getBody() {
        return body;
    }

    public boolean isCorrect() {
        return correct;
    }
}
