package lms.catalog;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

/**
 * Dịch vụ tra cứu tài nguyên học tập và ngân hàng câu hỏi, bài tập CNTT tập trung.
 */
@Service
public class CatalogQueryService {

    public List<ComputerScienceProblemBank.ProblemItem> getProblems(String category, String difficulty, String query) {
        if (category != null && !category.isBlank()) {
            return ComputerScienceProblemBank.findByCategory(category);
        }
        if (difficulty != null && !difficulty.isBlank()) {
            return ComputerScienceProblemBank.findByDifficulty(difficulty);
        }
        if (query != null && !query.isBlank()) {
            return ComputerScienceProblemBank.search(query);
        }
        return ComputerScienceProblemBank.getAllProblems();
    }

    public Optional<ComputerScienceProblemBank.ProblemItem> getProblemByCode(String code) {
        return ComputerScienceProblemBank.findByCode(code);
    }

    public List<QuizQuestionBankCatalog.QuizQuestionItem> getQuestions(String subjectCode, String difficulty, String query) {
        if (subjectCode != null && !subjectCode.isBlank()) {
            return QuizQuestionBankCatalog.findBySubject(subjectCode);
        }
        if (difficulty != null && !difficulty.isBlank()) {
            return QuizQuestionBankCatalog.findByDifficulty(difficulty);
        }
        if (query != null && !query.isBlank()) {
            return QuizQuestionBankCatalog.search(query);
        }
        return QuizQuestionBankCatalog.getAllQuestions();
    }

    public List<CourseCurriculumCatalog.CurriculumItem> getCurricula(String query) {
        if (query != null && !query.isBlank()) {
            return CourseCurriculumCatalog.search(query);
        }
        return CourseCurriculumCatalog.getAllCurricula();
    }

    public Optional<CourseCurriculumCatalog.CurriculumItem> getCurriculumByCode(String code) {
        return CourseCurriculumCatalog.findByCourseCode(code);
    }

    public List<DatabaseSchemaCatalog.SchemaItem> getSchemas(String domain) {
        if (domain != null && !domain.isBlank()) {
            return DatabaseSchemaCatalog.findByDomain(domain);
        }
        return DatabaseSchemaCatalog.getAllSchemas();
    }

    public List<DesignPatternsCatalog.DesignPatternItem> getDesignPatterns(String category) {
        if (category != null && !category.isBlank()) {
            return DesignPatternsCatalog.findByCategory(category);
        }
        return DesignPatternsCatalog.getAllPatterns();
    }
}
