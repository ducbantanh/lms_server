package lms.submission;

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

/** GET /api/v1/submission-files/{id}: tải file bài làm; quyền kiểm tra trong SubmissionService.download. */
@RestController
@RequestMapping("/api/v1/submission-files")
@RequirePermission(Permissions.COURSES_VIEW)
public class SubmissionFileController {

    private final SubmissionService service;

    public SubmissionFileController(SubmissionService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> download(@PathVariable String id, AuthPrincipal me) {
        SubmissionService.Download d = service.download(id, me);
        SubmissionFile f = d.file();
        // File do sinh viên tải lên: luôn tải về, không mở trong trình duyệt
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(f.getName(), StandardCharsets.UTF_8).build();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(f.getContentType()))
                .contentLength(f.getSize())
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .header("X-Content-Type-Options", "nosniff")
                .header(HttpHeaders.CACHE_CONTROL, "private, no-store")
                .body(new FileSystemResource(d.path()));
    }
}
