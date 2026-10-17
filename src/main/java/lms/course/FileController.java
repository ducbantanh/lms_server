package lms.course;

import java.nio.charset.StandardCharsets;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.auth.AuthPrincipal;
import lms.auth.Permissions;
import lms.auth.RequirePermission;

/** GET /api/v1/files/{id}: tải tài liệu khóa học; quyền kiểm tra trong CourseContentService.download. */
@RestController
@RequestMapping("/api/v1/files")
@RequirePermission(Permissions.COURSES_VIEW)
public class FileController {

    private final CourseContentService content;

    public FileController(CourseContentService content) {
        this.content = content;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> download(@PathVariable String id, AuthPrincipal me) {
        CourseContentService.Download d = content.download(id, me);
        CourseFile f = d.file();
        // PDF mở trong trình duyệt, DOCX/PPTX tải về; tên tiếng Việt mã hóa theo RFC 5987
        ContentDisposition disposition = (CourseContentService.PDF.equals(f.getContentType())
                ? ContentDisposition.inline()
                : ContentDisposition.attachment()).filename(f.getName(), StandardCharsets.UTF_8).build();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(f.getContentType()))
                .contentLength(f.getSize())
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .header("X-Content-Type-Options", "nosniff")
                .header(HttpHeaders.CACHE_CONTROL, "private, no-store")
                .body(new FileSystemResource(d.path()));
    }
}
