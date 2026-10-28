package lms.algorithms;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GraphAlgorithmsTest {

    @Test
    @DisplayName("dijkstra tìm đường đi ngắn nhất chính xác từ đỉnh nguồn")
    void testDijkstra() {
        int n = 5;
        List<List<GraphAlgorithms.Edge>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());

        adj.get(0).add(new GraphAlgorithms.Edge(0, 1, 4));
        adj.get(0).add(new GraphAlgorithms.Edge(0, 2, 1));
        adj.get(2).add(new GraphAlgorithms.Edge(2, 1, 2));
        adj.get(1).add(new GraphAlgorithms.Edge(1, 3, 1));
        adj.get(2).add(new GraphAlgorithms.Edge(2, 3, 5));

        GraphAlgorithms.ShortestPathResult result = GraphAlgorithms.dijkstra(n, adj, 0);
        assertThat(result.distances()[1]).isEqualTo(3.0); // 0 -> 2 -> 1
        assertThat(result.distances()[3]).isEqualTo(4.0); // 0 -> 2 -> 1 -> 3
    }

    @Test
    @DisplayName("topologicalSort xác định đúng thứ tự topo và phát hiện chu trình")
    void testTopologicalSort() {
        int n = 4;
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());

        adj.get(0).add(1);
        adj.get(0).add(2);
        adj.get(1).add(3);
        adj.get(2).add(3);

        List<Integer> order = GraphAlgorithms.topologicalSort(n, adj);
        assertThat(order).hasSize(4);
        assertThat(order.indexOf(0)).isLessThan(order.indexOf(1));
        assertThat(order.indexOf(0)).isLessThan(order.indexOf(2));
        assertThat(order.indexOf(1)).isLessThan(order.indexOf(3));
    }

    @Test
    @DisplayName("bfs duyệt đúng các đỉnh theo chiều rộng")
    void testBfs() {
        int n = 4;
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());

        adj.get(0).add(1);
        adj.get(0).add(2);
        adj.get(1).add(3);

        List<Integer> order = GraphAlgorithms.bfs(n, adj, 0);
        assertThat(order).containsExactly(0, 1, 2, 3);
    }
}
