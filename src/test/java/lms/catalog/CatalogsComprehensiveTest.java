package lms.catalog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CatalogsComprehensiveTest {

    @Test
    @DisplayName("ComputerScienceProblemBank nạp đầy đủ bài toán và hỗ trợ tìm kiếm")
    void testProblemBank() {
        var problems = ComputerScienceProblemBank.getAllProblems();
        assertThat(problems).hasSizeGreaterThanOrEqualTo(50);

        var twoSum = ComputerScienceProblemBank.findByCode("PROB-001");
        assertThat(twoSum).isPresent();
        assertThat(twoSum.get().title()).isEqualTo("Two Sum");

        var dpProblems = ComputerScienceProblemBank.findByCategory("Dynamic Programming");
        assertThat(dpProblems).isNotEmpty();
    }

    @Test
    @DisplayName("QuizQuestionBankCatalog nạp hơn 100 câu hỏi thi chuẩn hóa")
    void testQuestionBank() {
        var questions = QuizQuestionBankCatalog.getAllQuestions();
        assertThat(questions).hasSizeGreaterThanOrEqualTo(100);

        var netQuestions = QuizQuestionBankCatalog.findBySubject("NET101");
        assertThat(netQuestions).isNotEmpty();
    }

    @Test
    @DisplayName("CourseCurriculumCatalog chứa đầy đủ đề cương 15 tuần")
    void testCurricula() {
        var curricula = CourseCurriculumCatalog.getAllCurricula();
        assertThat(curricula).isNotEmpty();

        var ltm = CourseCurriculumCatalog.findByCourseCode("INT1339");
        assertThat(ltm).isPresent();
        assertThat(ltm.get().weeklySchedule()).hasSize(15);
    }

    @Test
    @DisplayName("DatabaseSchemaCatalog và DesignPatternsCatalog tra cứu chính xác")
    void testSchemasAndPatterns() {
        var schemas = DatabaseSchemaCatalog.getAllSchemas();
        assertThat(schemas).isNotEmpty();

        var patterns = DesignPatternsCatalog.getAllPatterns();
        assertThat(patterns).isNotEmpty();
        assertThat(DesignPatternsCatalog.findById("PAT-01")).isPresent();
    }
}
