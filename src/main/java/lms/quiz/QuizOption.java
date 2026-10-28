package lms.quiz;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/** Một đáp án của câu trong bài; vị trí (cột position) là định danh mà câu trả lời của sinh viên trỏ tới. */
@Embeddable
public class QuizOption {

    @Column(nullable = false, length = 1000)
    private String body;

    @Column(nullable = false)
    private boolean correct;

    protected QuizOption() {
        // JPA
    }

    public QuizOption(String body, boolean correct) {
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
