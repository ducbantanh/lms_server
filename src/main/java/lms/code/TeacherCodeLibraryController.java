package lms.code;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lms.auth.AuthPrincipal;
import lms.auth.Permissions;
import lms.auth.RequirePermission;
import lms.code.CodeViews.ArchiveCheck;
import lms.code.CodeViews.LibraryCreateRequest;
import lms.code.CodeViews.LibraryDeleteRequest;
import lms.code.CodeViews.LibraryDetail;
import lms.code.CodeViews.LibraryProblem;
import lms.code.CodeViews.LibraryUpdateRequest;
import lms.common.ApiResponse;

/**
 * API kho bài lập trình của giảng viên: danh sách câu, xem/sửa một câu, thêm nhiều câu (kèm file ZIP bộ test),
 * xóa nhiều câu. Tạo bài từ kho dùng ProblemRequest.source ở API tạo bài lập trình.
 * Đường dẫn cố định ("delete", "test-archive") được ưu tiên hơn /{id} (public id luôn 22 ký tự).
 */
@RestController
@RequestMapping("/api/v1/teacher/code-problems")
@RequirePermission(Permissions.COURSES_MANAGE)
public class TeacherCodeLibraryController {

    private final CodeLibraryService service;

    public TeacherCodeLibraryController(CodeLibraryService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<LibraryProblem>> list(AuthPrincipal me) {
        return ApiResponse.ok(service.list(me));
    }

    @GetMapping("/{id}")
    public ApiResponse<LibraryDetail> detail(@PathVariable String id, AuthPrincipal me) {
        return ApiResponse.ok(service.detail(id, me));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<List<LibraryProblem>> create(@Valid @RequestBody LibraryCreateRequest body,
            AuthPrincipal me) {
        return ApiResponse.ok(service.create(body, Map.of(), me));
    }

    /** Kèm file ZIP bộ test: part `data` (JSON như trên) + mỗi file ZIP một part (ProblemRequest.archive). */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<LibraryProblem>> createWithArchives(@Valid @RequestPart("data") LibraryCreateRequest body,
            @RequestParam Map<String, MultipartFile> files, AuthPrincipal me) {
        return ApiResponse.ok(service.create(body, files, me));
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<LibraryDetail> update(@PathVariable String id, @Valid @RequestBody LibraryUpdateRequest body,
            AuthPrincipal me) {
        return ApiResponse.ok(service.update(id, body, Map.of(), me));
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<LibraryDetail> updateWithArchives(@PathVariable String id,
            @Valid @RequestPart("data") LibraryUpdateRequest body, @RequestParam Map<String, MultipartFile> files,
            AuthPrincipal me) {
        return ApiResponse.ok(service.update(id, body, files, me));
    }

    @PostMapping("/delete")
    public ApiResponse<Void> delete(@Valid @RequestBody LibraryDeleteRequest body, AuthPrincipal me) {
        service.delete(body.ids(), me);
        return ApiResponse.ok();
    }

    /** Đọc thử file ZIP bộ test (part `tests`); không lưu gì. */
    @PostMapping(path = "/test-archive", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ArchiveCheck> checkArchive(@RequestPart("tests") MultipartFile tests) {
        return ApiResponse.ok(service.checkArchive(tests));
    }
}
