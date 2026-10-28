package lms.algorithms;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StringMatchAlgorithmsTest {

    @Test
    @DisplayName("kmpSearch tìm đúng tất cả các vị trí xuất hiện của chuỗi mẫu")
    void testKmpSearch() {
        String text = "ABABDABACDABABCABAB";
        String pattern = "ABABCABAB";

        List<Integer> matches = StringMatchAlgorithms.kmpSearch(text, pattern);
        assertThat(matches).containsExactly(10);
    }

    @Test
    @DisplayName("rabinKarpSearch tìm đúng vị trí xuất hiện bằng rolling hash")
    void testRabinKarp() {
        String text = "AABAACAADAABAABA";
        String pattern = "AABA";

        List<Integer> matches = StringMatchAlgorithms.rabinKarpSearch(text, pattern);
        assertThat(matches).containsExactly(0, 9, 12);
    }

    @Test
    @DisplayName("computeZArray tính toán đúng mảng Z-Array")
    void testZArray() {
        String text = "aabzaa";
        int[] z = StringMatchAlgorithms.computeZArray(text);
        assertThat(z[0]).isEqualTo(0);
        assertThat(z[1]).isEqualTo(1);
        assertThat(z[4]).isEqualTo(2);
    }
}
