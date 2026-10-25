package lms.code;

import java.time.Instant;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO bài lập trình, khớp lms_client/lib/code.ts; mọi id trả ra ngoài là public_id. Bài lập trình không có
 * điểm: kết quả là số test đúng / tổng số test của mỗi câu.
 */
public final class CodeViews {

    private CodeViews() {
    }

    public static final int MAX_PROBLEMS = 20;
    public static final int MAX_STATEMENT = 20_000;
    public static final int MAX_TESTS = 200;
    /** Mỗi input/output của một test soạn tay; tổng bộ test của một câu giới hạn ở CodeService.MAX_TEST_BYTES. */
    public static final int MAX_TEST_CHARS = 1_000_000;
    /**
     * Test có input/output không quá chừng này byte thì giao diện nhận đủ nội dung (sửa tại chỗ được); test mẫu
     * không được lớn hơn. Test lớn hơn chỉ có bản xem trước (CodeTestInfo).
     */
    public static final int INLINE_LIMIT = CodeTestInfo.PREVIEW_FULL;
    public static final int MAX_SOURCE = 65_536;

    /** Loại lần chạy và trạng thái chấm. */
    public static final String RUN = "run";
    public static final String SUBMIT = "submit";
    public static final String QUEUED = "queued";
    public static final String RUNNING = "running";
    public static final String DONE = "done";

    /**
     * Tình trạng một câu của một sinh viên: chưa nộp / đang chấm / đã có lần nộp AC / đã nộp nhưng chưa AC /
     * đã nộp mà kết quả chưa công bố (bài công bố kết quả sau giờ đóng hoặc không công bố).
     */
    public static final String NONE = "none";
    public static final String PENDING = "pending";
    public static final String ACCEPTED = "accepted";
    public static final String REJECTED = "rejected";
    public static final String SUBMITTED = "submitted";

    // ---------------------------------------------------------------- response

    public record LanguageView(String code, String label, int timeFactor) {
    }

    public record SampleView(String input, String expected) {
    }

    /**
     * Kết quả một test theo thứ tự test lúc chấm (chạy thử: thứ tự trong các test mẫu). Sinh viên chỉ có ở lần chạy
     * thử; output/error null khi không giữ lại.
     */
    public record ResultView(int index, boolean sample, String verdict, int timeMs, String output, String error) {
    }

    /**
     * Một lần chạy thử / nộp bài một câu. verdict, passed, total null khi chưa chấm xong hoặc là lần nộp mà kết
     * quả chưa công bố (sinh viên). source, compileOutput, results chỉ có ở API xem chi tiết một lần; lần nộp bài
     * của sinh viên không có results (chỉ có kết quả chung), compileOutput chỉ khi lỗi biên dịch.
     */
    public record AttemptView(String id, String problemId, String kind, String language, String status,
            String verdict, Integer passed, Integer total, Instant createdAt, Instant judgedAt, String source,
            String compileOutput, List<ResultView> results) {
    }

    /** Lần nộp tốt nhất của một câu: AC nếu có, không thì lần đúng nhiều test nhất. */
    public record Best(String verdict, int passed, int total) {
    }

    /** status: NONE / PENDING / ACCEPTED / REJECTED / SUBMITTED; best null khi chưa có hoặc chưa công bố. */
    public record ProblemSummary(String id, int number, String title, int timeLimitMs, int memoryLimitMb,
            Integer maxAttempts, int attemptsUsed, int testCount, String status, Best best) {
    }

    /** Danh sách câu của bài cho sinh viên. resultsHidden: kết quả các lần nộp chưa công bố; open: đang làm bài. */
    public record CodeState(List<LanguageView> languages, List<ProblemSummary> problems, boolean resultsHidden,
            boolean open, Instant serverTime) {
    }

    /** Một câu cho sinh viên: đề, test mẫu, các lần chạy/nộp của câu (mới nhất trước). */
    public record ProblemDetail(ProblemSummary problem, String statement, List<SampleView> samples,
            List<AttemptView> attempts, List<LanguageView> languages, boolean resultsHidden, boolean open,
            Instant serverTime) {
    }

    /**
     * Một test cho giảng viên. input/expected là bản xem trước (300 ký tự đầu) khi inputTruncated /
     * expectedTruncated; size tính bằng byte. name: tên file trong ZIP, null với test soạn tay.
     */
    public record TestView(String id, String name, String input, String expected, boolean inputTruncated,
            boolean expectedTruncated, long inputSize, long expectedSize, boolean sample) {
    }

    public record TeacherProblem(String id, String title, String statement, int timeLimitMs, int memoryLimitMb,
            Integer maxAttempts, List<TestView> tests) {
    }

    /** Đề và bộ test của cả bài. pending: số lần chờ/đang chấm; editable: sửa được (giảng viên phụ trách). */
    public record TeacherCode(List<String> languages, List<TeacherProblem> problems, long pending,
            boolean editable) {
    }

    public record ProblemRef(String id, String title) {
    }

    /** Một ô của bảng kết quả: tình trạng câu của sinh viên (như phía sinh viên, luôn có kết quả) và số lần nộp. */
    public record ResultCell(String problemId, String status, Best best, int attempts) {
    }

    public record ResultRow(String studentId, String username, String fullName, List<ResultCell> cells) {
    }

    /** Bảng sinh viên × câu: mọi sinh viên đang ghi danh (theo MSSV). */
    public record CodeResults(List<ProblemRef> problems, List<ResultRow> rows) {
    }

    /** Các lần chạy/nộp của một sinh viên ở mọi câu (mới nhất trước) cho giảng viên xem. */
    public record StudentCode(String studentId, String username, String fullName, List<AttemptView> attempts) {
    }

    public record ArchiveTest(String name, long inputSize, long expectedSize) {
    }

    /**
     * Kết quả đọc thử file ZIP bộ test (chưa lưu gì). problems: [{name, reason}] với reason là mã
     * TestArchive (MISSING_OUTPUT, ...); ignored: số file không phải input/output.
     */
    public record ArchiveCheck(List<ArchiveTest> tests, List<TestArchive.Problem> problems, int ignored) {
    }

    // ---------------------------------------------------------------- request

    /**
     * Một test trong bộ test mới của câu, theo thứ tự. id: test đang có để giữ lại (đổi được test mẫu, thứ tự);
     * input/expected khác null thì là nội dung mới (test mới, hoặc thay nội dung test id); id null thì bắt buộc
     * có nội dung. Test đang có không nằm trong danh sách bị xóa.
     */
    public record TestRequest(
            @Size(max = 22) String id,
            @Size(max = 100) String name,
            @Size(max = MAX_TEST_CHARS) String input,
            @Size(max = MAX_TEST_CHARS) String expected,
            boolean sample) {
    }

    /**
     * Một câu. id: câu đang có (khi sửa bài), null = câu mới. archive: tên part multipart chứa file ZIP bộ test của
     * câu (test trong ZIP thêm vào sau tests, là test ẩn); tests có thể rỗng khi có ZIP. source: câu mới lấy từ kho
     * bài lập trình của mình (public id): bộ test chép từ kho, bỏ qua tests và archive; tiêu đề, đề, giới hạn
     * lấy theo request (giảng viên sửa được trước khi tạo bài).
     */
    public record ProblemRequest(
            @Size(max = 22) String id,
            @NotBlank @Size(max = 200) String title,
            @NotBlank @Size(max = MAX_STATEMENT) String statement,
            @Min(100) @Max(10_000) int timeLimitMs,
            @Min(16) @Max(1024) int memoryLimitMb,
            @Min(1) @Max(100) Integer maxAttempts,
            @NotNull @Size(max = MAX_TESTS) List<@NotNull @Valid TestRequest> tests,
            @Size(max = 40) String archive,
            @Size(max = 22) String source) {
    }

    /**
     * Ngôn ngữ cho phép (mã CodeLanguage) và các câu theo thứ tự. Khi sửa: câu đang có không nằm trong danh sách bị
     * xóa cùng các lần nộp của câu đó.
     */
    public record CodeRequest(
            @NotEmpty @Size(max = 10) List<@NotBlank String> languages,
            @NotEmpty @Size(max = MAX_PROBLEMS) List<@NotNull @Valid ProblemRequest> problems) {
    }

    /**
     * type: Assessment.TYPE_*; scoreRelease (Assessment.SCORE_*) ở bài lập trình là lúc sinh viên xem được kết quả
     * các lần nộp; notifyStudents chỉ có tác dụng với bài tập (thực hành, kiểm tra luôn thông báo).
     */
    public record CreateCodeRequest(
            @Min(0) @Max(2) int type,
            @NotBlank @Size(max = 200) String title,
            @Size(max = 2000) String description,
            Instant opensAt,
            Instant closesAt,
            @Min(0) @Max(2) int scoreRelease,
            boolean notifyStudents,
            @NotNull @Valid CodeRequest code) {
    }

    public record RunRequest(@NotBlank String language, @NotBlank @Size(max = MAX_SOURCE) String source) {
    }

    // ---------------------------------------------------------------- kho bài lập trình

    /** Số câu tối đa thêm vào kho trong một lần (nhập nhanh gửi nhiều đợt). */
    public static final int MAX_LIBRARY_BATCH = 50;

    /** Một câu trong danh sách kho (không kèm đề, bộ test). */
    public record LibraryProblem(String id, String title, String subjectCode, String subjectName, int timeLimitMs,
            int memoryLimitMb, Integer maxAttempts, int testCount, int sampleCount, Instant createdAt,
            Instant updatedAt) {
    }

    /** Một câu trong kho kèm đề và bộ test (như TeacherProblem). */
    public record LibraryDetail(LibraryProblem summary, String statement, List<TestView> tests) {
    }

    /** Thêm nhiều câu vào kho, cùng một môn học (mã học phần mình dạy; trống = không gắn môn). */
    public record LibraryCreateRequest(
            @Size(max = 32) String subjectCode,
            @NotEmpty @Size(max = MAX_LIBRARY_BATCH) List<@NotNull @Valid ProblemRequest> problems) {
    }

    /** Sửa một câu trong kho; problem.id, problem.source bỏ qua. */
    public record LibraryUpdateRequest(
            @Size(max = 32) String subjectCode,
            @NotNull @Valid ProblemRequest problem) {
    }

    public record LibraryDeleteRequest(@NotEmpty @Size(max = 500) List<@NotBlank @Size(max = 22) String> ids) {
    }
}
