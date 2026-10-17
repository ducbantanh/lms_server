package lms.course;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lms.auth.AuthPrincipal;
import lms.common.AppException;
import lms.common.ErrorCode;
import lms.config.AppProperties;
import lms.course.CourseViews.AssessmentRequest;
import lms.course.CourseViews.AssessmentView;
import lms.course.CourseViews.FileView;
import lms.course.CourseViews.SectionView;

/** Nội dung khóa học: mục/tuần, tài liệu, bài kiểm tra; và tải tài liệu có kiểm tra quyền. */
@Service
public class CourseContentService {

    public static final String PDF = "application/pdf";
    /** Chỉ nhận PDF/DOCX/PPTX; kiểm tra cả đuôi file lẫn magic bytes. */
    private static final Map<String, String> TYPES = Map.of(
            "pdf", PDF,
            "docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
    private static final byte[] PDF_MAGIC = "%PDF-".getBytes();
    private static final byte[] ZIP_MAGIC = { 0x50, 0x4b, 0x03, 0x04 };

    /** File để controller trả về: metadata + đường dẫn trên đĩa. */
    public record Download(CourseFile file, Path path) {
    }

    private final CourseService courses;
    private final CourseRepository courseRepo;
    private final CourseSectionRepository sections;
    private final CourseFileRepository files;
    private final AssessmentRepository assessments;
    private final EnrollmentRepository enrollments;
    private final FileStorage storage;
    private final long maxFileBytes;

    public CourseContentService(CourseService courses, CourseRepository courseRepo,
            CourseSectionRepository sections, CourseFileRepository files, AssessmentRepository assessments,
            EnrollmentRepository enrollments, FileStorage storage, AppProperties props) {
        this.courses = courses;
        this.courseRepo = courseRepo;
        this.sections = sections;
        this.files = files;
        this.assessments = assessments;
        this.enrollments = enrollments;
        this.storage = storage;
        this.maxFileBytes = props.storage().maxFileBytes();
    }

    // ---------------------------------------------------------------- mục

    @Transactional
    public SectionView addSection(String courseId, String title, AuthPrincipal me) {
        Course c = courses.requireManaged(courseId, me);
        if (c.getFormat() == Course.FORMAT_FILES_ONLY) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "This course has no sections");
        }
        CourseSection s = sections.save(new CourseSection(c.getId(), sections.maxPosition(c.getId()) + 1,
                title.trim()));
        return new SectionView(s.getPublicId(), s.getPosition(), s.getTitle());
    }

    /** Xóa mục; tài liệu trong mục chuyển sang "chưa phân mục". */
    @Transactional
    public void deleteSection(String courseId, String sectionId, AuthPrincipal me) {
        Course c = courses.requireManaged(courseId, me);
        CourseSection s = sections.findByPublicIdAndCourseId(sectionId, c.getId())
                .orElseThrow(() -> AppException.notFound("Section"));
        files.detachFromSection(s.getId());
        sections.delete(s);
    }

    // ---------------------------------------------------------------- tài liệu

    @Transactional
    public FileView upload(String courseId, MultipartFile upload, String sectionId, boolean hidden,
            AuthPrincipal me) {
        Course c = courses.requireManaged(courseId, me);
        if (upload == null || upload.isEmpty()) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Empty file");
        }
        if (upload.getSize() > maxFileBytes) {
            throw new AppException(ErrorCode.PAYLOAD_TOO_LARGE);
        }
        String name = baseName(upload.getOriginalFilename());
        String ext = name.contains(".") ? name.substring(name.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT) : "";
        String contentType = TYPES.get(ext);
        byte[] data;
        try {
            data = upload.getBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        if (contentType == null || !startsWith(data, PDF.equals(contentType) ? PDF_MAGIC : ZIP_MAGIC)) {
            throw new AppException(ErrorCode.UNSUPPORTED_FILE_TYPE);
        }
        Long section = null;
        if (c.getFormat() != Course.FORMAT_FILES_ONLY && sectionId != null && !sectionId.isBlank()) {
            section = sections.findByPublicIdAndCourseId(sectionId, c.getId())
                    .orElseThrow(() -> AppException.notFound("Section")).getId();
        }
        String key = storage.store(data);
        try {
            CourseFile f = files.save(new CourseFile(c.getId(), section, name, contentType, data.length, hidden, key));
            return CourseService.fileView(f, sectionId == null || section == null ? null : sectionId);
        } catch (RuntimeException e) {
            storage.deleteQuietly(key);
            throw e;
        }
    }

    @Transactional
    public void setHidden(String courseId, String fileId, boolean hidden, AuthPrincipal me) {
        Course c = courses.requireManaged(courseId, me);
        files.findByPublicIdAndCourseId(fileId, c.getId()).orElseThrow(() -> AppException.notFound("File"))
                .setHidden(hidden);
    }

    /** Xóa tài liệu; bài kiểm tra dùng file này làm đề sẽ thành "không có đề" (FK SET NULL). */
    @Transactional
    public void deleteFile(String courseId, String fileId, AuthPrincipal me) {
        Course c = courses.requireManaged(courseId, me);
        CourseFile f = files.findByPublicIdAndCourseId(fileId, c.getId())
                .orElseThrow(() -> AppException.notFound("File"));
        files.delete(f);
        CourseService.afterCommit(() -> storage.deleteQuietly(f.getStorageKey()));
    }

    // ---------------------------------------------------------------- bài kiểm tra

    @Transactional
    public AssessmentView createAssessment(String courseId, AssessmentRequest req, AuthPrincipal me) {
        Course c = courses.requireManaged(courseId, me);
        if (req.opensAt() != null && req.closesAt() != null && !req.closesAt().isAfter(req.opensAt())) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "closesAt must be after opensAt");
        }
        CourseFile paper = null;
        if (req.paperFileId() != null && !req.paperFileId().isBlank()) {
            paper = files.findByPublicIdAndCourseId(req.paperFileId(), c.getId())
                    .orElseThrow(() -> AppException.notFound("File"));
            if (!PDF.equals(paper.getContentType())) {
                throw new AppException(ErrorCode.UNSUPPORTED_FILE_TYPE, "Paper must be a PDF");
            }
            // Đề thi không được lộ trong danh sách tài liệu trước giờ thi
            if (req.type() == Assessment.TYPE_EXAM) {
                paper.setHidden(true);
            }
        }
        if (req.type() == Assessment.TYPE_EXAM && paper == null) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "An exam needs a PDF paper");
        }
        Assessment a = assessments.save(new Assessment(c.getId(), req.type(), req.title().trim(),
                CourseService.blankToNull(req.description()), paper == null ? null : paper.getId(),
                req.opensAt(), req.closesAt()));
        Instant now = Instant.now();
        return new AssessmentView(a.getPublicId(), a.getType(), a.getTitle(), a.getDescription(),
                paper == null ? null : paper.getPublicId(), paper == null ? null : paper.getName(),
                a.getOpensAt(), a.getClosesAt(), a.statusAt(now));
    }

    /** Xóa bài; file đề vẫn giữ trong tài liệu. */
    @Transactional
    public void deleteAssessment(String courseId, String assessmentId, AuthPrincipal me) {
        Course c = courses.requireManaged(courseId, me);
        assessments.delete(assessments.findByPublicIdAndCourseId(assessmentId, c.getId())
                .orElseThrow(() -> AppException.notFound("Assessment")));
    }

    // ---------------------------------------------------------------- tải file

    /**
     * Người quản lý khóa học tải mọi file. Sinh viên đã ghi danh tải file không ẩn,
     * hoặc file ẩn là đề của một bài đã mở. Còn lại NOT_FOUND.
     */
    @Transactional(readOnly = true)
    public Download download(String fileId, AuthPrincipal me) {
        CourseFile f = files.findByPublicId(fileId).orElseThrow(() -> AppException.notFound("File"));
        Course c = courseRepo.findById(f.getCourseId()).orElseThrow(() -> AppException.notFound("File"));
        boolean manager = c.isManagedBy(me.userId(), me.role() == lms.auth.Role.ADMIN);
        if (!manager) {
            boolean enrolled = enrollments.existsByCourseIdAndUserId(c.getId(), me.userId());
            Instant now = Instant.now();
            boolean openPaper = assessments.findByPaperFileId(f.getId()).stream().anyMatch(a -> a.isOpened(now));
            if (!enrolled || (f.isHidden() && !openPaper)) {
                throw AppException.notFound("File");
            }
        }
        return new Download(f, storage.path(f.getStorageKey()));
    }

    private static String baseName(String original) {
        String name = original == null ? "" : original.replace('\\', '/');
        name = name.substring(name.lastIndexOf('/') + 1).strip();
        if (name.isEmpty()) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Missing file name");
        }
        return name.length() > 255 ? name.substring(name.length() - 255) : name;
    }

    private static boolean startsWith(byte[] data, byte[] magic) {
        return data.length >= magic.length && Arrays.equals(data, 0, magic.length, magic, 0, magic.length);
    }
}
