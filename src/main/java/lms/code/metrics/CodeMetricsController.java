package lms.code.metrics;

import org.springframework.web.bind.annotation.*;

import lms.common.ApiResponse;

/**
 * REST API phân tích tĩnh độ phức tạp mã nguồn phục vụ học phần Thực hành Lập trình.
 */
@RestController
@RequestMapping("/api/v1/code/metrics")
public class CodeMetricsController {

    private final CodeMetricsAnalyzer analyzer;

    public CodeMetricsController(CodeMetricsAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    public record AnalysisRequest(String code, String language) {}

    @PostMapping("/analyze")
    public ApiResponse<CodeComplexityMetrics> analyze(@RequestBody AnalysisRequest req) {
        if (req == null || req.code() == null) {
            return ApiResponse.ok(analyzer.analyze("", "java"));
        }
        return ApiResponse.ok(analyzer.analyze(req.code(), req.language()));
    }
}
