package lms.user.importing;

import java.util.List;

/**
 * Kết quả import trả về cho admin. Tách riêng 3 nhóm: đã tạo, trùng, lỗi.
 * KHÔNG bao giờ chứa mật khẩu.
 */
public record ImportReport(
        int totalLines,
        List<CreatedRow> created,
        List<StudentImportParser.LineIssue> duplicates,
        List<StudentImportParser.LineIssue> errors) {

    public record CreatedRow(int line, String username, String fullName) {
    }
}
