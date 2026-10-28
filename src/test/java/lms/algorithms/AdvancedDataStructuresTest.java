package lms.algorithms;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AdvancedDataStructuresTest {

    @Test
    @DisplayName("SegmentTree tính đúng tổng đoạn và cập nhật điểm O(log N)")
    void testSegmentTree() {
        int[] arr = { 1, 3, 5, 7, 9, 11 };
        AdvancedDataStructures.SegmentTree st = new AdvancedDataStructures.SegmentTree(arr);

        assertThat(st.query(1, 3)).isEqualTo(15); // 3 + 5 + 7 = 15
        st.update(1, 10); // arr[1] = 10
        assertThat(st.query(1, 3)).isEqualTo(22); // 10 + 5 + 7 = 22
    }

    @Test
    @DisplayName("FenwickTree tính đúng tổng tiền tố và cập nhật")
    void testFenwickTree() {
        AdvancedDataStructures.FenwickTree bit = new AdvancedDataStructures.FenwickTree(10);
        bit.add(0, 5);
        bit.add(1, 3);
        bit.add(2, 7);

        assertThat(bit.prefixSum(2)).isEqualTo(15);
        assertThat(bit.rangeSum(1, 2)).isEqualTo(10);
    }

    @Test
    @DisplayName("DisjointSetUnion liên thông và đếm số thành phần liên thông")
    void testDSU() {
        AdvancedDataStructures.DisjointSetUnion dsu = new AdvancedDataStructures.DisjointSetUnion(5);
        dsu.union(0, 1);
        dsu.union(1, 2);

        assertThat(dsu.connected(0, 2)).isTrue();
        assertThat(dsu.connected(0, 3)).isFalse();
        assertThat(dsu.getComponentCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("Trie tìm kiếm tiền tố và gợi ý từ khóa autocomplete")
    void testTrie() {
        AdvancedDataStructures.Trie trie = new AdvancedDataStructures.Trie();
        trie.insert("apple");
        trie.insert("app");
        trie.insert("application");
        trie.insert("banana");

        assertThat(trie.search("apple")).isTrue();
        assertThat(trie.search("appl")).isFalse();
        assertThat(trie.startsWith("app")).isTrue();

        List<String> suggestions = trie.autocomplete("app", 5);
        assertThat(suggestions).contains("app", "apple", "application");
    }

    @Test
    @DisplayName("LruCache đẩy phần tử cũ nhất ra khi vượt ngưỡng dung lượng")
    void testLruCache() {
        AdvancedDataStructures.LruCache<String, Integer> cache = new AdvancedDataStructures.LruCache<>(2);
        cache.put("k1", 1);
        cache.put("k2", 2);
        cache.get("k1"); // truy cập k1, làm k2 thành cũ nhất
        cache.put("k3", 3); // loại bỏ k2

        assertThat(cache.containsKey("k1")).isTrue();
        assertThat(cache.containsKey("k2")).isFalse();
        assertThat(cache.containsKey("k3")).isTrue();
    }
}
