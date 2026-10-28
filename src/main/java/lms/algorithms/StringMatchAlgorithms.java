package lms.algorithms;

import java.util.ArrayList;
import java.util.List;

/**
 * Thư viện các giải thuật so khớp chuỗi mẫu kinh điển (String Pattern Matching Engine).
 * Hỗ trợ Knuth-Morris-Pratt (KMP), Rabin-Karp Rolling Hash, Z-Algorithm.
 */
public final class StringMatchAlgorithms {

    private static final int PRIME_MOD = 1_000_000_007;
    private static final int RADIX_BASE = 256;

    private StringMatchAlgorithms() {
    }

    /**
     * Thuật toán Knuth-Morris-Pratt (KMP) tìm tất cả các vị trí xuất hiện của mẫu P trong chuỗi T (O(N + M)).
     */
    public static List<Integer> kmpSearch(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return matches;
        }

        int[] lps = computeLpsArray(pattern);
        int i = 0; // index text
        int j = 0; // index pattern
        int n = text.length();
        int m = pattern.length();

        while (i < n) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }
            if (j == m) {
                matches.add(i - j);
                j = lps[j - 1];
            } else if (i < n && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return matches;
    }

    private static int[] computeLpsArray(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    /**
     * Thuật toán Rabin-Karp sử dụng mã băm cuốn (Rolling Hash) tìm kiếm chuỗi mẫu (Trung bình O(N + M)).
     */
    public static List<Integer> rabinKarpSearch(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return matches;
        }

        int n = text.length();
        int m = pattern.length();

        long pHash = 0; // hash của pattern
        long tHash = 0; // hash của text window
        long h = 1;

        // Tính h = RADIX_BASE^(m-1) % PRIME_MOD
        for (int i = 0; i < m - 1; i++) {
            h = (h * RADIX_BASE) % PRIME_MOD;
        }

        // Tính hash ban đầu của pattern và cửa sổ đầu tiên của text
        for (int i = 0; i < m; i++) {
            pHash = (RADIX_BASE * pHash + pattern.charAt(i)) % PRIME_MOD;
            tHash = (RADIX_BASE * tHash + text.charAt(i)) % PRIME_MOD;
        }

        // Trượt cửa sổ trên text
        for (int i = 0; i <= n - m; i++) {
            if (pHash == tHash) {
                // Kiểm tra trực tiếp để chống xung đột hash (Hash Collision)
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    matches.add(i);
                }
            }

            // Tính hash cho cửa sổ tiếp theo: bỏ ký tự đầu, thêm ký tự mới
            if (i < n - m) {
                tHash = (RADIX_BASE * (tHash - text.charAt(i) * h) + text.charAt(i + m)) % PRIME_MOD;
                if (tHash < 0) {
                    tHash += PRIME_MOD;
                }
            }
        }

        return matches;
    }

    /**
     * Thuật toán Z-Algorithm xây dựng mảng Z-Array và tìm kiếm chuỗi mẫu (O(N + M)).
     */
    public static int[] computeZArray(String s) {
        int n = s.length();
        int[] z = new int[n];
        int l = 0;
        int r = 0;

        for (int i = 1; i < n; i++) {
            if (i <= r) {
                z[i] = Math.min(r - i + 1, z[i - l]);
            }
            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) {
                z[i]++;
            }
            if (i + z[i] - 1 > r) {
                l = i;
                r = i + z[i] - 1;
            }
        }
        return z;
    }
}
