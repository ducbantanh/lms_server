package lms.common.util;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CollectionUtilsTest {

    @Test
    @DisplayName("partition chia nhỏ danh sách thành các phần tử kích thước tối đa")
    void testPartition() {
        List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7);
        List<List<Integer>> chunks = CollectionUtils.partition(list, 3);

        assertThat(chunks).hasSize(3);
        assertThat(chunks.get(0)).containsExactly(1, 2, 3);
        assertThat(chunks.get(1)).containsExactly(4, 5, 6);
        assertThat(chunks.get(2)).containsExactly(7);
    }

    @Test
    @DisplayName("slicePage cắt trang an toàn trong bộ nhớ")
    void testSlicePage() {
        List<String> items = List.of("A", "B", "C", "D", "E");

        List<String> page0 = CollectionUtils.slicePage(items, 0, 2);
        assertThat(page0).containsExactly("A", "B");

        List<String> page1 = CollectionUtils.slicePage(items, 1, 2);
        assertThat(page1).containsExactly("C", "D");

        List<String> page2 = CollectionUtils.slicePage(items, 2, 2);
        assertThat(page2).containsExactly("E");

        List<String> page3 = CollectionUtils.slicePage(items, 3, 2);
        assertThat(page3).isEmpty();
    }
}
