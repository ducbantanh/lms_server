package lms.code.plagiarism;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlagiarismDetectionTest {

    @Test
    @DisplayName("CodeTokenizer bóc tách token và bỏ qua chú thích và khoảng trắng")
    void testTokenizer() {
        String code = """
        // Chú thích một dòng
        int add(int a, int b) {
            /* Chú thích nhiều dòng */
            return a + b;
        }
        """;
        List<String> tokens = CodeTokenizer.tokenizeToRepresentation(code);
        assertThat(tokens).contains("KW_INT", "ID", "KW_RETURN", "+");
        assertThat(tokens).doesNotContain("//");
    }

    @Test
    @DisplayName("WinnowingFingerprinter phát hiện sự tương đồng cao giữa hai đoạn mã đổi tên biến")
    void testFingerprinting() {
        String code1 = """
        int sum(int x, int y) {
            int result = x + y;
            return result;
        }
        """;

        String code2 = """
        int tongHaiSo(int soThuNhat, int soThuHai) {
            int ketQua = soThuNhat + soThuHai;
            return ketQua;
        }
        """;

        List<String> t1 = CodeTokenizer.tokenizeToRepresentation(code1);
        List<String> t2 = CodeTokenizer.tokenizeToRepresentation(code2);

        Set<Long> fp1 = WinnowingFingerprinter.computeFingerprints(t1, 4, 3);
        Set<Long> fp2 = WinnowingFingerprinter.computeFingerprints(t2, 4, 3);

        double sim = WinnowingFingerprinter.calculateSimilarity(fp1, fp2);
        assertThat(sim).isGreaterThan(0.7);
    }

    @Test
    @DisplayName("PlagiarismDetectionService phát hiện các cặp bài nộp có mức độ trùng lặp cao")
    void testBatchAnalysis() {
        PlagiarismDetectionService service = new PlagiarismDetectionService();

        var sub1 = new PlagiarismDetectionService.SubmissionInput("B21DCCN001", "Sinh viên A", "int f(int n) { if (n <= 1) return 1; return n * f(n - 1); }");
        var sub2 = new PlagiarismDetectionService.SubmissionInput("B21DCCN002", "Sinh viên B", "int giaiThua(int x) { if (x <= 1) return 1; return x * giaiThua(x - 1); }");
        var sub3 = new PlagiarismDetectionService.SubmissionInput("B21DCCN003", "Sinh viên C", "void inChuoi() { printf(\"Xin chao the gioi\\n\"); }");

        PlagiarismReport report = service.analyzeBatch(List.of(sub1, sub2, sub3), 60.0);
        assertThat(report.totalSubmissionsAnalyzed()).isEqualTo(3);
        assertThat(report.suspiciousPairs()).isNotEmpty();
        assertThat(report.suspiciousPairs().get(0).student1Username()).isEqualTo("B21DCCN001");
        assertThat(report.suspiciousPairs().get(0).student2Username()).isEqualTo("B21DCCN002");
    }
}
