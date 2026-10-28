package lms.code.plagiarism;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Thuật toán băm chọn lọc cửa sổ trượt Winnowing (Stanford MOSS Fingerprinting Algorithm).
 * Chuyển đổi chuỗi Token thành các tập fingerprint đặc trưng để đối chiếu trùng lặp với độ chính xác cao
 * và khả năng chống chịu các biến đổi như đảo thứ tự hàm, đổi tên biến, thêm khoảng trắng.
 */
public final class WinnowingFingerprinter {

    private static final int DEFAULT_K = 5;  // Độ dài k-gram token
    private static final int DEFAULT_W = 4;  // Kích thước cửa sổ trượt window

    private WinnowingFingerprinter() {
    }

    /**
     * Sinh tập hợp mã băm dấu vân tay (Fingerprint Hashes) từ danh sách token mã nguồn.
     */
    public static Set<Long> computeFingerprints(List<String> tokens) {
        return computeFingerprints(tokens, DEFAULT_K, DEFAULT_W);
    }

    /**
     * Thuật toán Winnowing với tham số tùy chọn.
     *
     * @param tokens Danh sách token
     * @param k      Độ dài k-gram
     * @param w      Kích thước cửa sổ lựa chọn giá trị nhỏ nhất
     * @return Tập hợp fingerprint hashes
     */
    public static Set<Long> computeFingerprints(List<String> tokens, int k, int w) {
        Set<Long> fingerprints = new HashSet<>();
        if (tokens == null || tokens.size() < k) {
            return fingerprints;
        }

        // Bước 1: Tính toán băm cho mọi k-gram liên tiếp bằng Rolling Hash (Rabin-Karp)
        List<Long> hashes = new ArrayList<>();
        int numGrams = tokens.size() - k + 1;

        for (int i = 0; i < numGrams; i++) {
            long hash = 0;
            for (int j = 0; j < k; j++) {
                hash = (hash * 31L + tokens.get(i + j).hashCode()) & 0x7FFFFFFFFFFFFFFFL;
            }
            hashes.add(hash);
        }

        if (hashes.size() < w) {
            // Khi số lượng hash nhỏ hơn kích thước cửa sổ, lấy phần tử nhỏ nhất
            long min = hashes.stream().min(Long::compareTo).orElse(0L);
            fingerprints.add(min);
            return fingerprints;
        }

        // Bước 2: Winnowing chọn giá trị hash nhỏ nhất trong mỗi cửa sổ kích thước w
        int minIndex = -1;
        for (int i = 0; i <= hashes.size() - w; i++) {
            long minVal = Long.MAX_VALUE;
            int currentMinIdx = -1;

            // Tìm giá trị nhỏ nhất trong cửa sổ [i, i + w - 1]
            for (int j = 0; j < w; j++) {
                long val = hashes.get(i + j);
                if (val <= minVal) { // Chọn phần tử bên phải nhất nếu có nhiều giá trị bằng nhau
                    minVal = val;
                    currentMinIdx = i + j;
                }
            }

            if (currentMinIdx != minIndex) {
                minIndex = currentMinIdx;
                fingerprints.add(hashes.get(minIndex));
            }
        }

        return fingerprints;
    }

    /**
     * Tính toán chỉ số tương đồng Jaccard giữa hai tập fingerprint (Thang đo 0.0 - 1.0).
     */
    public static double calculateSimilarity(Set<Long> fp1, Set<Long> fp2) {
        if (fp1 == null || fp2 == null || fp1.isEmpty() || fp2.isEmpty()) {
            return 0.0;
        }

        Set<Long> intersection = new HashSet<>(fp1);
        intersection.retainAll(fp2);

        Set<Long> union = new HashSet<>(fp1);
        union.addAll(fp2);

        if (union.isEmpty()) {
            return 0.0;
        }

        double score = (double) intersection.size() / union.size();
        return Math.round(score * 1000.0) / 1000.0;
    }
}
