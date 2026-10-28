package lms.catalog;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lms.common.ApiResponse;

/**
 * Controller cung cấp API tra cứu ngân hàng bài tập, câu hỏi và đề cương học phần.
 */
@RestController
@RequestMapping("/api/v1/catalog")
public class CatalogController {

    private final CatalogQueryService catalogService;

    public CatalogController(CatalogQueryService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/problems")
    public ApiResponse<List<ComputerScienceProblemBank.ProblemItem>> listProblems(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "difficulty", required = false) String difficulty,
            @RequestParam(value = "q", required = false) String query) {
        return ApiResponse.ok(catalogService.getProblems(category, difficulty, query));
    }

    @GetMapping("/problems/{code}")
    public ApiResponse<ComputerScienceProblemBank.ProblemItem> getProblem(@PathVariable("code") String code) {
        return ApiResponse.ok(catalogService.getProblemByCode(code).orElse(null));
    }

    @GetMapping("/questions")
    public ApiResponse<List<QuizQuestionBankCatalog.QuizQuestionItem>> listQuestions(
            @RequestParam(value = "subject", required = false) String subject,
            @RequestParam(value = "difficulty", required = false) String difficulty,
            @RequestParam(value = "q", required = false) String query) {
        return ApiResponse.ok(catalogService.getQuestions(subject, difficulty, query));
    }

    @GetMapping("/curricula")
    public ApiResponse<List<CourseCurriculumCatalog.CurriculumItem>> listCurricula(
            @RequestParam(value = "q", required = false) String query) {
        return ApiResponse.ok(catalogService.getCurricula(query));
    }

    @GetMapping("/curricula/{code}")
    public ApiResponse<CourseCurriculumCatalog.CurriculumItem> getCurriculum(@PathVariable("code") String code) {
        return ApiResponse.ok(catalogService.getCurriculumByCode(code).orElse(null));
    }

    @GetMapping("/schemas")
    public ApiResponse<List<DatabaseSchemaCatalog.SchemaItem>> listSchemas(
            @RequestParam(value = "domain", required = false) String domain) {
        return ApiResponse.ok(catalogService.getSchemas(domain));
    }

    @GetMapping("/patterns")
    public ApiResponse<List<DesignPatternsCatalog.DesignPatternItem>> listPatterns(
            @RequestParam(value = "category", required = false) String category) {
        return ApiResponse.ok(catalogService.getDesignPatterns(category));
    }
}
