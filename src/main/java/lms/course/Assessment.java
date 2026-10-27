package lms.course;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lms.common.RandomTokens;

/** Bài kiểm tra (luyện tập / thực hành / thi), có thể kèm file đề PDF và khung giờ mở. */
@Entity
@Table(name = "assessments")
public class Assessment {

    public static final int TYPE_PRACTICE = 0;
    public static final int TYPE_LAB = 1;
    public static final int TYPE_EXAM = 2;

    public static final int MODE_FILE = 0;
    public static final int MODE_QUIZ = 1;
    /** Bài lập trình: sinh viên nộp mã nguồn, máy chấm chạy bộ test (lms.code). */
    public static final int MODE_CODE = 2;

    /** Bài trắc nghiệm: khi nào sinh viên được xem đáp án đúng và giải thích. */
    public static final int REVEAL_NEVER = 0;
    public static final int REVEAL_AFTER_SUBMIT = 1;
    public static final int REVEAL_AFTER_CLOSE = 2;

    /** Khi nào sinh viên thấy điểm và nhận xét (cột score_release). */
    public static final int SCORE_IMMEDIATE = 0;
    public static final int SCORE_AFTER_CLOSE = 1;
    /** Không bao giờ: chỉ giảng viên thấy điểm (vd. bài kiểm tra lấy điểm để xét, không công bố). */
    public static final int SCORE_NEVER = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, updatable = false, length = 22)
    private String publicId;

    @Column(name = "course_id", nullable = false, updatable = false)
    private Long courseId;

    @Column(nullable = false)
    private int type;

    /** MODE_FILE: nộp file; MODE_QUIZ: trắc nghiệm, câu hỏi ở lms.quiz.QuizQuestion; MODE_CODE: lms.code.CodeProblem. */
    @Column(name = "answer_mode", nullable = false)
    private int answerMode;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(name = "paper_file_id")
    private Long paperFileId;

    /** NULL = mở ngay. */
    @Column(name = "opens_at")
    private Instant opensAt;

    /** NULL = không hạn. */
    @Column(name = "closes_at")
    private Instant closesAt;

    /** Trắc nghiệm: số phút làm bài tính từ lúc bấm "Bắt đầu"; NULL = làm tới giờ đóng. */
    @Column(name = "time_limit_minutes")
    private Integer timeLimitMinutes;

    /** Trắc nghiệm: xáo thứ tự câu và đáp án theo từng sinh viên. */
    @Column(nullable = false)
    private boolean shuffle;

    @Column(name = "reveal_mode", nullable = false)
    private int revealMode;

    @Column(name = "score_release", nullable = false)
    private int scoreRelease;

    /** Bài kiểm tra có giám sát (lms.proctor): sinh viên phải vào phòng thi mới xem được đề và làm bài. */
    @Column(nullable = false)
    private boolean proctored;

    /** IP / dải CIDR được vào thi, ngăn bằng dấu phẩy (lms.proctor.IpRanges); NULL = mọi mạng. */
    @Column(name = "proctor_networks", length = 500)
    private String proctorNetworks;

    /** Phải nhập mã phòng thi giám thị đọc (đổi mỗi phút, lms.proctor.ProctorCode). */
    @Column(name = "proctor_access_code", nullable = false)
    private boolean proctorAccessCode;

    /** Làm bài ở chế độ toàn màn hình; thoát ra tính là vi phạm. */
    @Column(name = "proctor_fullscreen", nullable = false)
    private boolean proctorFullscreen = true;

    /** Đủ số lần vi phạm thì khóa bài chờ giám thị mở; NULL = chỉ ghi lại. */
    @Column(name = "proctor_max_violations")
    private Integer proctorMaxViolations;

    @Column(name = "proctor_secret", length = 43)
    private String proctorSecret;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Assessment() {
        // JPA
    }

    public Assessment(long courseId, int type, String title, String description, Long paperFileId,
            Instant opensAt, Instant closesAt) {
        this.courseId = courseId;
        this.type = type;
        this.title = title;
        this.description = description;
        this.paperFileId = paperFileId;
        this.opensAt = opensAt;
        this.closesAt = closesAt;
    }

    /** Bài trắc nghiệm: không có file đề; câu hỏi chép từ ngân hàng sau khi lưu bài. */
    public static Assessment quiz(long courseId, int type, String title, String description, Instant opensAt,
            Instant closesAt, Integer timeLimitMinutes, boolean shuffle, int revealMode, int scoreRelease) {
        Assessment a = new Assessment(courseId, type, title, description, null, opensAt, closesAt);
        a.answerMode = MODE_QUIZ;
        a.timeLimitMinutes = timeLimitMinutes;
        a.shuffle = shuffle;
        a.revealMode = revealMode;
        a.scoreRelease = scoreRelease;
        return a;
    }

    /** Bài lập trình: đề và bộ test ở lms.code (CodeProblem, CodeTest), lưu sau khi lưu bài. */
    public static Assessment code(long courseId, int type, String title, String description, Instant opensAt,
            Instant closesAt, int scoreRelease) {
        Assessment a = new Assessment(courseId, type, title, description, null, opensAt, closesAt);
        a.answerMode = MODE_CODE;
        a.scoreRelease = scoreRelease;
        return a;
    }

    @PrePersist
    void onCreate() {
        if (publicId == null) {
            publicId = RandomTokens.publicId();
        }
        createdAt = Instant.now();
    }

    /** Sửa bài; loại bài và đề không đổi được (đổi đề thì xóa và tạo bài mới). */
    public void update(String title, String description, Instant opensAt, Instant closesAt) {
        this.title = title;
        this.description = description;
        this.opensAt = opensAt;
        this.closesAt = closesAt;
    }

    /** Bài kiểm tra đã kết thúc là hồ sơ thi: không sửa, không xóa. Bài tập/thực hành sửa lúc nào cũng được. */
    public boolean isLocked(Instant now) {
        return type == TYPE_EXAM && isClosed(now);
    }

    public boolean isOpened(Instant now) {
        return opensAt == null || !opensAt.isAfter(now);
    }

    public boolean isClosed(Instant now) {
        return isOpened(now) && closesAt != null && closesAt.isBefore(now);
    }

    /** upcoming / open / closed, khớp ASSESSMENT_STATUS ở client. */
    public String statusAt(Instant now) {
        if (!isOpened(now)) {
            return "upcoming";
        }
        return isClosed(now) ? "closed" : "open";
    }

    public boolean isDeletable(Instant now) {
        return !isLocked(now);
    }

    public Long getId() {
        return id;
    }

    public String getPublicId() {
        return publicId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Long getPaperFileId() {
        return paperFileId;
    }

    public Instant getOpensAt() {
        return opensAt;
    }

    public Instant getClosesAt() {
        return closesAt;
    }

    public int getAnswerMode() {
        return answerMode;
    }

    public boolean isQuiz() {
        return answerMode == MODE_QUIZ;
    }

    public boolean isCode() {
        return answerMode == MODE_CODE;
    }

    public Integer getTimeLimitMinutes() {
        return timeLimitMinutes;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public int getRevealMode() {
        return revealMode;
    }

    public int getScoreRelease() {
        return scoreRelease;
    }

    public void setScoreRelease(int scoreRelease) {
        this.scoreRelease = scoreRelease;
    }

    public boolean isProctored() {
        return proctored;
    }

    /** Đang trong giờ làm bài của bài kiểm tra có giám sát: mọi thao tác làm bài phải qua phiên thi. */
    public boolean isProctoredAt(Instant now) {
        return proctored && type == TYPE_EXAM && isOpened(now) && !isClosed(now);
    }

    public String getProctorNetworks() {
        return proctorNetworks;
    }

    public boolean isProctorAccessCode() {
        return proctorAccessCode;
    }

    public boolean isProctorFullscreen() {
        return proctorFullscreen;
    }

    public Integer getProctorMaxViolations() {
        return proctorMaxViolations;
    }

    public String getProctorSecret() {
        return proctorSecret;
    }

    /** Cài đặt giám sát; secret chỉ tạo một lần (đổi secret thì mã đang chiếu hết hiệu lực). */
    public void configureProctor(boolean enabled, String networks, boolean accessCode, boolean fullscreen,
            Integer maxViolations) {
        this.proctored = enabled;
        this.proctorNetworks = networks;
        this.proctorAccessCode = accessCode;
        this.proctorFullscreen = fullscreen;
        this.proctorMaxViolations = maxViolations;
        if (proctorSecret == null) {
            proctorSecret = RandomTokens.urlSafe(32);
        }
    }

    /** Sinh viên được xem điểm chưa; công bố sau giờ đóng mà bài không có giờ đóng thì coi như công bố ngay. */
    public boolean isScoreVisible(Instant now) {
        return switch (scoreRelease) {
            case SCORE_NEVER -> false;
            case SCORE_AFTER_CLOSE -> closesAt == null || !now.isBefore(closesAt);
            default -> true;
        };
    }

    /**
     * Hạn làm bài trắc nghiệm của một sinh viên đã bắt đầu: sớm hơn trong giờ đóng và giờ bắt đầu + số phút;
     * null = không hạn.
     */
    public Instant deadlineFor(Instant startedAt) {
        Instant byLimit = timeLimitMinutes == null || startedAt == null ? null
                : startedAt.plusSeconds(timeLimitMinutes * 60L);
        if (byLimit == null) {
            return closesAt;
        }
        return closesAt == null || byLimit.isBefore(closesAt) ? byLimit : closesAt;
    }
}
