package lms.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Tiện ích đọc và xuất dữ liệu định dạng CSV chuẩn RFC-4180.
 * Xử lý chính xác dấu phẩy, dấu nháy kép, dấu xuống dòng và mã hóa UTF-8 BOM.
 */
public final class CsvUtils {

    /** UTF-8 Byte Order Mark để các phần mềm như Microsoft Excel hiển thị đúng tiếng Việt có dấu */
    public static final String UTF8_BOM = "\uFEFF";

    private CsvUtils() {
    }

    /**
     * Định dạng một trường dữ liệu theo chuẩn RFC-4180 (bao đóng bằng dấu nháy kép nếu chứa ký tự đặc biệt).
     */
    public static String escapeField(Object value) {
        if (value == null) {
            return "";
        }
        String str = String.valueOf(value);
        if (str.contains(",") || str.contains("\"") || str.contains("\n") || str.contains("\r")) {
            return "\"" + str.replace("\"", "\"\"") + "\"";
        }
        return str;
    }

    /**
     * Dựng một dòng CSV từ danh sách các giá trị trường.
     */
    public static String buildLine(List<?> fields) {
        if (fields == null || fields.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(escapeField(fields.get(i)));
        }
        return sb.toString();
    }

    /**
     * Phân tích một dòng CSV thành danh sách các trường (hỗ trợ dấu nháy kép bọc quanh trường).
     */
    public static List<String> parseLine(String line) {
        List<String> result = new ArrayList<>();
        if (line == null) {
            return result;
        }

        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        int len = line.length();

        for (int i = 0; i < len; i++) {
            char c = line.charAt(i);

            if (c == '\"') {
                if (inQuotes && i + 1 < len && line.charAt(i + 1) == '\"') {
                    current.append('\"');
                    i++; // bỏ qua dấu nháy kép thoát
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        result.add(current.toString().trim());
        return result;
    }
}
