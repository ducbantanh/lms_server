package lms.question;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse câu hỏi trắc nghiệm dán từ Word/văn bản (mở rộng định dạng Aiken của Moodle). Java thuần để dễ unit test.
 *
 * <pre>
 * Câu 1: Java chạy trên gì?        ← "Câu 1:", "1." ở đầu câu được bỏ; nội dung được nhiều dòng
 * A. JVM
 * B) CLR
 * *C. Cả hai                       ← dấu * trước đáp án: đáp án đúng (khi không có dòng ĐÁP ÁN)
 * ĐÁP ÁN: A                        ← hoặc ANSWER: A; nhiều đáp án đúng: "A, C" → câu chọn nhiều
 * MỨC ĐỘ: dễ                       ← không bắt buộc: dễ / trung bình / khó (mặc định trung bình)
 * GIẢI THÍCH: JVM là máy ảo Java   ← không bắt buộc
 *                                  ← dòng trống ngăn cách các câu
 * </pre>
 *
 * Đáp án phải theo thứ tự A, B, C... Dòng không phải đáp án tiếp theo là phần nối của đáp án trước.
 * Dòng # chỉ là chú thích khi nằm giữa các câu (trong câu hỏi có thể là mã nguồn như #include).
 * Để nhận văn bản chép từ Word/PDF: "Câu N:" luôn mở câu mới (không cần dòng trống), dòng trống trước
 * ĐÁP ÁN / MỨC ĐỘ / GIẢI THÍCH được bỏ qua, phần mở đầu không đánh số và không có đáp án (tiêu đề đề thi)
 * và dòng "--- HẾT ---" không tính là câu hỏi.
 */
public final class QuestionImportParser {

    public static final int MIN_OPTIONS = 2;
    public static final int MAX_OPTIONS = 10;
    public static final int MAX_BODY = 4000;
    public static final int MAX_OPTION = 1000;
    public static final int MAX_EXPLANATION = 2000;

    public static final String EMPTY_QUESTION = "EMPTY_QUESTION";
    public static final String NO_OPTIONS = "NO_OPTIONS";
    public static final String TOO_FEW_OPTIONS = "TOO_FEW_OPTIONS";
    public static final String TOO_MANY_OPTIONS = "TOO_MANY_OPTIONS";
    public static final String MISSING_ANSWER = "MISSING_ANSWER";
    public static final String INVALID_ANSWER = "INVALID_ANSWER";
    public static final String INVALID_DIFFICULTY = "INVALID_DIFFICULTY";
    public static final String TOO_LONG = "TOO_LONG";
    /** Chỉ gặp khi soạn qua API: câu chọn một mà đánh dấu nhiều đáp án đúng. */
    public static final String SINGLE_MANY_CORRECT = "SINGLE_MANY_CORRECT";

    private static final Pattern NUMBER_PREFIX = Pattern.compile(
            "^(?:(?:câu|cau|question)\\s*\\d+\\s*[:.)]?|\\d{1,3}[.)](?=\\s))\\s*",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    /** "Câu 2:", "Question 3." — đủ chắc là đầu câu mới kể cả khi không có dòng trống (đề chép từ Word/PDF). */
    private static final Pattern QUESTION_WORD = Pattern.compile("^(?:câu|cau|question)\\s*\\d+\\s*[:.)]",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    /** Dòng kết thúc đề như "--- HẾT ---", "*** Hết ***": bỏ qua. */
    private static final Pattern END_MARK = Pattern.compile("^[-–—=*_.\\s]*(?:hết|het|end)[-–—=*_.\\s]*$",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern OPTION = Pattern.compile("^(\\*\\s*)?([A-Za-z])[.)]\\s+(.+)$");
    private static final Pattern META = Pattern.compile(
            "^(answer|đáp án|dap an|mức độ|muc do|độ khó|do kho|level|giải thích|giai thich|explanation)\\s*:\\s*(.*)$",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    /** "A", "A, C", "A;C", "A C", "AC". */
    private static final Pattern ANSWER_LETTERS = Pattern.compile("^[A-Za-z](?:\\s*[,;/ ]?\\s*[A-Za-z])*\\.?$");

    private static final Map<String, Integer> DIFFICULTIES = Map.ofEntries(
            Map.entry("dễ", BankQuestion.DIFFICULTY_EASY), Map.entry("de", BankQuestion.DIFFICULTY_EASY),
            Map.entry("easy", BankQuestion.DIFFICULTY_EASY), Map.entry("1", BankQuestion.DIFFICULTY_EASY),
            Map.entry("trung bình", BankQuestion.DIFFICULTY_MEDIUM),
            Map.entry("trung binh", BankQuestion.DIFFICULTY_MEDIUM),
            Map.entry("tb", BankQuestion.DIFFICULTY_MEDIUM), Map.entry("medium", BankQuestion.DIFFICULTY_MEDIUM),
            Map.entry("2", BankQuestion.DIFFICULTY_MEDIUM),
            Map.entry("khó", BankQuestion.DIFFICULTY_HARD), Map.entry("kho", BankQuestion.DIFFICULTY_HARD),
            Map.entry("hard", BankQuestion.DIFFICULTY_HARD), Map.entry("3", BankQuestion.DIFFICULTY_HARD));

    private final int maxQuestions;

    public QuestionImportParser(int maxQuestions) {
        this.maxQuestions = maxQuestions;
    }

    public record Option(String body, boolean correct) {
    }

    /** line: dòng đầu của câu trong văn bản dán vào. */
    public record Question(int line, String body, boolean multiple, int difficulty, String explanation,
            List<Option> options) {
    }

    public record LineIssue(int line, String reason) {
    }

    public record Result(List<Question> questions, List<LineIssue> errors) {
    }

    public static final class TooManyQuestionsException extends RuntimeException {
        public TooManyQuestionsException(int max) {
            super("Too many questions (max " + max + ")");
        }
    }

    public Result parse(String content) {
        List<Question> questions = new ArrayList<>();
        List<LineIssue> errors = new ArrayList<>();
        String text = Normalizer.normalize(content == null ? "" : content, Normalizer.Form.NFC);
        if (text.startsWith("﻿")) {
            text = text.substring(1); // bỏ BOM khi file lưu từ Notepad/Word
        }
        String[] lines = text.split("\\r?\\n|\\r", -1);
        Draft draft = null;
        for (int i = 0; i < lines.length; i++) {
            int lineNo = i + 1;
            String line = lines[i].strip();
            if (draft == null) {
                if (!line.isEmpty() && !skippable(line)) {
                    draft = new Draft(lineNo, line);
                }
                continue;
            }
            if (line.isEmpty()) {
                // Trong nội dung câu hỏi: đoạn trống (vd. mã nguồn); sau đáp án: có thể là hết câu,
                // hoặc chỉ là khoảng cách trước dòng ĐÁP ÁN (văn bản chép từ PDF) — xét ở dòng kế tiếp
                draft.pendingBlank = true;
                continue;
            }
            Matcher meta = META.matcher(line);
            if (meta.matches() && !draft.options.isEmpty()) {
                draft.meta(meta.group(1), meta.group(2).strip());
                draft.pendingBlank = false;
                continue;
            }
            Matcher option = OPTION.matcher(line);
            boolean nextOption = option.matches() && !draft.inMeta
                    && Character.toUpperCase(option.group(2).charAt(0)) == 'A' + draft.options.size();
            boolean afterOptions = !draft.options.isEmpty();
            // Sang câu mới: sau dòng ĐÁP ÁN / MỨC ĐỘ / GIẢI THÍCH; dòng "Câu 2:" (kể cả không có dòng trống,
            // văn bản chép từ Word/PDF); dòng trống sau các đáp án; "2." sau dòng trống khi câu trước thiếu đáp án
            boolean newQuestion = !nextOption && (draft.inMeta
                    || QUESTION_WORD.matcher(line).lookingAt()
                    || (draft.pendingBlank && afterOptions)
                    || (draft.pendingBlank && NUMBER_PREFIX.matcher(line).lookingAt()));
            if (newQuestion) {
                finish(draft, questions, errors);
                draft = skippable(line) ? null : new Draft(lineNo, line);
            } else if (nextOption) {
                draft.options.add(new StringBuilder(option.group(3).strip()));
                draft.starred.add(option.group(1) != null);
                draft.pendingBlank = false;
            } else if (draft.options.isEmpty()) {
                if (draft.pendingBlank) {
                    draft.body.add("");
                    draft.pendingBlank = false;
                }
                draft.body.add(lines[i].stripTrailing());
            } else {
                draft.options.get(draft.options.size() - 1).append('\n').append(line);
            }
            if (questions.size() + errors.size() > maxQuestions) {
                throw new TooManyQuestionsException(maxQuestions);
            }
        }
        if (draft != null) {
            finish(draft, questions, errors);
        }
        if (questions.size() + errors.size() > maxQuestions) {
            throw new TooManyQuestionsException(maxQuestions);
        }
        return new Result(questions, errors);
    }

    // ---------------------------------------------------------------- dùng chung với soạn câu hỏi qua API

    /** Lỗi về số đáp án và đáp án đúng; null = hợp lệ. Độ dài từng phần do bean validation kiểm tra. */
    public static String problem(boolean multiple, List<Option> options) {
        if (options.size() < MIN_OPTIONS) {
            return TOO_FEW_OPTIONS;
        }
        if (options.size() > MAX_OPTIONS) {
            return TOO_MANY_OPTIONS;
        }
        long correct = options.stream().filter(Option::correct).count();
        if (correct == 0) {
            return MISSING_ANSWER;
        }
        return !multiple && correct > 1 ? SINGLE_MANY_CORRECT : null;
    }

    // ---------------------------------------------------------------- nội bộ

    /** Dòng không thuộc câu hỏi nào khi nằm giữa các câu: chú thích (#) và dòng "--- HẾT ---". */
    private static boolean skippable(String line) {
        return line.startsWith("#") || END_MARK.matcher(line).matches();
    }

    /** Câu đang đọc dở. */
    private static final class Draft {
        final int line;
        final List<String> body = new ArrayList<>();
        final List<StringBuilder> options = new ArrayList<>();
        final List<Boolean> starred = new ArrayList<>();
        String answer;
        String difficulty;
        String explanation;
        /** Đã gặp dòng ĐÁP ÁN / MỨC ĐỘ / GIẢI THÍCH: không nhận thêm đáp án. */
        boolean inMeta;
        /** Vừa gặp dòng trống khi chưa có đáp án nào. */
        boolean pendingBlank;

        /** Dòng đầu bắt đầu bằng "Câu 1:", "1."...: chắc chắn là câu hỏi, không phải tiêu đề đề thi. */
        final boolean numbered;

        Draft(int line, String first) {
            this.line = line;
            Matcher prefix = NUMBER_PREFIX.matcher(first);
            this.numbered = prefix.lookingAt();
            body.add(prefix.replaceFirst(""));
        }

        void meta(String keyword, String value) {
            inMeta = true;
            String key = keyword.toLowerCase(Locale.ROOT);
            if (key.startsWith("a") || key.startsWith("đá") || key.startsWith("da")) {
                answer = value;
            } else if (key.startsWith("g") || key.startsWith("e")) {
                explanation = value;
            } else {
                difficulty = value;
            }
        }
    }

    private static void finish(Draft d, List<Question> questions, List<LineIssue> errors) {
        // Phần mở đầu không có đáp án và không đánh số câu (tên trường, tiêu đề đề thi, hướng dẫn): bỏ qua
        if (d.options.isEmpty() && !d.numbered && !d.inMeta && questions.isEmpty() && errors.isEmpty()) {
            return;
        }
        String body = String.join("\n", d.body).strip();
        String reason = null;
        List<Option> options = new ArrayList<>();
        Integer difficulty = d.difficulty == null ? Integer.valueOf(BankQuestion.DIFFICULTY_MEDIUM)
                : DIFFICULTIES.get(d.difficulty.toLowerCase(Locale.ROOT).replaceAll("\\s+", " "));
        if (body.isEmpty()) {
            reason = EMPTY_QUESTION;
        } else if (d.options.isEmpty()) {
            reason = NO_OPTIONS;
        } else {
            Set<Integer> correct = correctIndexes(d);
            if (correct == null) {
                reason = INVALID_ANSWER;
            } else {
                for (int i = 0; i < d.options.size(); i++) {
                    options.add(new Option(d.options.get(i).toString().strip(), correct.contains(i)));
                }
                reason = problem(true, options);
            }
        }
        if (reason == null && difficulty == null) {
            reason = INVALID_DIFFICULTY;
        }
        if (reason == null && (body.length() > MAX_BODY
                || options.stream().anyMatch(o -> o.body().length() > MAX_OPTION)
                || (d.explanation != null && d.explanation.length() > MAX_EXPLANATION))) {
            reason = TOO_LONG;
        }
        if (reason != null) {
            errors.add(new LineIssue(d.line, reason));
            return;
        }
        boolean multiple = options.stream().filter(Option::correct).count() > 1;
        String explanation = d.explanation == null || d.explanation.isBlank() ? null : d.explanation;
        questions.add(new Question(d.line, body, multiple, difficulty, explanation, List.copyOf(options)));
    }

    /** Vị trí đáp án đúng theo dòng ĐÁP ÁN, hoặc theo dấu * khi không có dòng đó; null = chữ cái sai. */
    private static Set<Integer> correctIndexes(Draft d) {
        Set<Integer> result = new LinkedHashSet<>();
        if (d.answer == null) {
            for (int i = 0; i < d.starred.size(); i++) {
                if (d.starred.get(i)) {
                    result.add(i);
                }
            }
            return result;
        }
        if (!ANSWER_LETTERS.matcher(d.answer).matches()) {
            return null;
        }
        for (char ch : d.answer.toUpperCase(Locale.ROOT).toCharArray()) {
            if (ch >= 'A' && ch <= 'Z') {
                int index = ch - 'A';
                if (index >= d.options.size()) {
                    return null;
                }
                result.add(index);
            }
        }
        return result;
    }
}
