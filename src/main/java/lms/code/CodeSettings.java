package lms.code;

import java.util.Arrays;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Cài đặt chung của một bài lập trình (Assessment.MODE_CODE): ngôn ngữ cho phép. Các câu ở CodeProblem. */
@Entity
@Table(name = "code_settings")
public class CodeSettings {

    @Id
    @Column(name = "assessment_id")
    private Long assessmentId;

    /** Mã CodeLanguage ngăn bởi dấu phẩy. */
    @Column(nullable = false, length = 100)
    private String languages;

    protected CodeSettings() {
        // JPA
    }

    public CodeSettings(long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public void setLanguages(List<CodeLanguage> languages) {
        this.languages = String.join(",", languages.stream().map(CodeLanguage::code).toList());
    }

    /** Theo thứ tự của enum (C, C++, Java, Python); mã không còn hỗ trợ bị bỏ qua. */
    public List<CodeLanguage> getLanguages() {
        List<String> codes = Arrays.asList(languages.split(","));
        return Arrays.stream(CodeLanguage.values()).filter(l -> codes.contains(l.code())).toList();
    }
}
