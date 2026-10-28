package lms.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Thư viện các cấu trúc dữ liệu nâng cao (Advanced Data Structures Engine).
 * Cung cấp:
 * 1. Cây phân đoạn (Segment Tree) hỗ trợ Range Sum Query và Point Update.
 * 2. Cây Fenwick (Binary Indexed Tree - BIT).
 * 3. Tập hợp không giao nhau (Disjoint Set Union - DSU với Union by Rank và Path Compression).
 * 4. Cây tiền tố (Trie) hỗ trợ tìm kiếm và gợi ý từ khóa tự động (Autocomplete).
 * 5. Bộ nhớ đệm LRU (Least Recently Used Cache).
 */
public final class AdvancedDataStructures {

    private AdvancedDataStructures() {
    }

    // =========================================================================
    // 1. SEGMENT TREE (CÂY PHÂN ĐOẠN)
    // =========================================================================
    public static class SegmentTree {
        private final long[] tree;
        private final int n;

        public SegmentTree(int[] arr) {
            this.n = arr.length;
            this.tree = new long[4 * n];
            if (n > 0) {
                build(arr, 1, 0, n - 1);
            }
        }

        private void build(int[] arr, int node, int start, int end) {
            if (start == end) {
                tree[node] = arr[start];
                return;
            }
            int mid = (start + end) / 2;
            build(arr, 2 * node, start, mid);
            build(arr, 2 * node + 1, mid + 1, end);
            tree[node] = tree[2 * node] + tree[2 * node + 1];
        }

        public void update(int idx, int val) {
            update(1, 0, n - 1, idx, val);
        }

        private void update(int node, int start, int end, int idx, int val) {
            if (start == end) {
                tree[node] = val;
                return;
            }
            int mid = (start + end) / 2;
            if (start <= idx && idx <= mid) {
                update(2 * node, start, mid, idx, val);
            } else {
                update(2 * node + 1, mid + 1, end, idx, val);
            }
            tree[node] = tree[2 * node] + tree[2 * node + 1];
        }

        public long query(int l, int r) {
            return query(1, 0, n - 1, l, r);
        }

        private long query(int node, int start, int end, int l, int r) {
            if (r < start || end < l) {
                return 0;
            }
            if (l <= start && end <= r) {
                return tree[node];
            }
            int mid = (start + end) / 2;
            return query(2 * node, start, mid, l, r) + query(2 * node + 1, mid + 1, end, l, r);
        }
    }

    // =========================================================================
    // 2. FENWICK TREE (BINARY INDEXED TREE)
    // =========================================================================
    public static class FenwickTree {
        private final long[] tree;
        private final int size;

        public FenwickTree(int size) {
            this.size = size;
            this.tree = new long[size + 1];
        }

        public void add(int index, long delta) {
            for (int i = index + 1; i <= size; i += (i & -i)) {
                tree[i] += delta;
            }
        }

        public long prefixSum(int index) {
            long sum = 0;
            for (int i = index + 1; i > 0; i -= (i & -i)) {
                sum += tree[i];
            }
            return sum;
        }

        public long rangeSum(int left, int right) {
            return prefixSum(right) - (left > 0 ? prefixSum(left - 1) : 0);
        }
    }

    // =========================================================================
    // 3. DISJOINT SET UNION (DSU)
    // =========================================================================
    public static class DisjointSetUnion {
        private final int[] parent;
        private final int[] rank;
        private int componentCount;

        public DisjointSetUnion(int n) {
            this.parent = new int[n];
            this.rank = new int[n];
            this.componentCount = n;
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                rank[i] = 0;
            }
        }

        public int find(int i) {
            if (parent[i] == i) {
                return i;
            }
            return parent[i] = find(parent[i]); // Path compression
        }

        public boolean union(int i, int j) {
            int rootI = find(i);
            int rootJ = find(j);
            if (rootI == rootJ) {
                return false;
            }
            // Union by rank
            if (rank[rootI] < rank[rootJ]) {
                parent[rootI] = rootJ;
            } else if (rank[rootI] > rank[rootJ]) {
                parent[rootJ] = rootI;
            } else {
                parent[rootJ] = rootI;
                rank[rootI]++;
            }
            componentCount--;
            return true;
        }

        public boolean connected(int i, int j) {
            return find(i) == find(j);
        }

        public int getComponentCount() {
            return componentCount;
        }
    }

    // =========================================================================
    // 4. TRIE (CÂY TIỀN TỐ)
    // =========================================================================
    public static class Trie {
        private static class TrieNode {
            private final Map<Character, TrieNode> children = new HashMap<>();
            private boolean isEndOfWord;
        }

        private final TrieNode root = new TrieNode();

        public void insert(String word) {
            if (word == null || word.isBlank()) return;
            TrieNode current = root;
            for (char c : word.toLowerCase().toCharArray()) {
                current = current.children.computeIfAbsent(c, k -> new TrieNode());
            }
            current.isEndOfWord = true;
        }

        public boolean search(String word) {
            if (word == null) return false;
            TrieNode node = findNode(word.toLowerCase());
            return node != null && node.isEndOfWord;
        }

        public boolean startsWith(String prefix) {
            if (prefix == null) return false;
            return findNode(prefix.toLowerCase()) != null;
        }

        public List<String> autocomplete(String prefix, int limit) {
            List<String> results = new ArrayList<>();
            if (prefix == null) return results;
            TrieNode startNode = findNode(prefix.toLowerCase());
            if (startNode != null) {
                collectWords(startNode, new StringBuilder(prefix.toLowerCase()), results, limit);
            }
            return results;
        }

        private TrieNode findNode(String str) {
            TrieNode current = root;
            for (char c : str.toCharArray()) {
                current = current.children.get(c);
                if (current == null) {
                    return null;
                }
            }
            return current;
        }

        private void collectWords(TrieNode node, StringBuilder current, List<String> results, int limit) {
            if (results.size() >= limit) return;
            if (node.isEndOfWord) {
                results.add(current.toString());
            }
            for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
                current.append(entry.getKey());
                collectWords(entry.getValue(), current, results, limit);
                current.deleteCharAt(current.length() - 1);
            }
        }
    }

    // =========================================================================
    // 5. LRU CACHE (LEAST RECENTLY USED)
    // =========================================================================
    public static class LruCache<K, V> extends LinkedHashMap<K, V> {
        private final int capacity;

        public LruCache(int capacity) {
            super(capacity, 0.75f, true);
            this.capacity = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity;
        }
    }
}
