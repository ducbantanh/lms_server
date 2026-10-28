package lms.code;

import java.util.ArrayList;
import java.util.List;

/**
 * So output của chương trình với output mong đợi, dễ dãi như phần lớn hệ chấm: bỏ qua khác biệt xuống dòng
 * (\r\n / \n), khoảng trắng cuối mỗi dòng và các dòng trống ở cuối. Khoảng trắng giữa dòng vẫn phải khớp.
 */
public final class OutputChecker {

    private OutputChecker() {
    }

    public static boolean matches(String actual, String expected) {
        return lines(actual).equals(lines(expected));
    }

    static List<String> lines(String text) {
        List<String> out = new ArrayList<>();
        for (String line : text.replace("\r\n", "\n").replace('\r', '\n').split("\n", -1)) {
            out.add(line.stripTrailing());
        }
        while (!out.isEmpty() && out.get(out.size() - 1).isEmpty()) {
            out.remove(out.size() - 1);
        }
        return out;
    }
}
