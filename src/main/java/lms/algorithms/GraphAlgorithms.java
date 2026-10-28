package lms.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Thư viện các thuật toán đồ thị nâng cao (Graph Algorithms Engine).
 * Hỗ trợ đường đi ngắn nhất (Dijkstra, Bellman-Ford, Floyd-Warshall),
 * cây khung nhỏ nhất (Kruskal, Prim), thứ tự Topo (Kahn), và thành phần liên thông mạnh (Tarjan).
 */
public final class GraphAlgorithms {

    public record Edge(int u, int v, double weight) {
    }

    public record ShortestPathResult(
            double[] distances,
            int[] predecessors,
            boolean hasNegativeCycle) {
    }

    private GraphAlgorithms() {
    }

    /**
     * Thuật toán Dijkstra tìm đường đi ngắn nhất từ đỉnh nguồn trên đồ thị trọng số không âm (O((V + E) log V)).
     */
    public static ShortestPathResult dijkstra(int numVertices, List<List<Edge>> adjList, int source) {
        double[] dist = new double[numVertices];
        int[] prev = new int[numVertices];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(prev, -1);

        dist[source] = 0.0;
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> dist[a[0]]));
        pq.offer(new int[] { source });

        boolean[] visited = new boolean[numVertices];

        while (!pq.isEmpty()) {
            int u = pq.poll()[0];
            if (visited[u]) continue;
            visited[u] = true;

            for (Edge edge : adjList.get(u)) {
                int v = edge.v();
                double weight = edge.weight();
                if (!visited[v] && dist[u] + weight < dist[v]) {
                    dist[v] = dist[u] + weight;
                    prev[v] = u;
                    pq.offer(new int[] { v });
                }
            }
        }

        return new ShortestPathResult(dist, prev, false);
    }

    /**
     * Thuật toán Bellman-Ford tìm đường đi ngắn nhất và phát hiện chu trình âm (O(V * E)).
     */
    public static ShortestPathResult bellmanFord(int numVertices, List<Edge> edges, int source) {
        double[] dist = new double[numVertices];
        int[] prev = new int[numVertices];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(prev, -1);
        dist[source] = 0.0;

        // Lặp V - 1 lần nới cạnh (relaxation)
        for (int i = 1; i < numVertices; i++) {
            for (Edge edge : edges) {
                if (dist[edge.u()] != Double.POSITIVE_INFINITY && dist[edge.u()] + edge.weight() < dist[edge.v()]) {
                    dist[edge.v()] = dist[edge.u()] + edge.weight();
                    prev[edge.v()] = edge.u();
                }
            }
        }

        // Kiểm tra chu trình âm
        boolean hasNegativeCycle = false;
        for (Edge edge : edges) {
            if (dist[edge.u()] != Double.POSITIVE_INFINITY && dist[edge.u()] + edge.weight() < dist[edge.v()]) {
                hasNegativeCycle = true;
                break;
            }
        }

        return new ShortestPathResult(dist, prev, hasNegativeCycle);
    }

    /**
     * Thuật toán Floyd-Warshall tìm đường đi ngắn nhất giữa mọi cặp đỉnh (O(V^3)).
     */
    public static double[][] floydWarshall(int numVertices, double[][] matrix) {
        double[][] dist = new double[numVertices][numVertices];
        for (int i = 0; i < numVertices; i++) {
            System.arraycopy(matrix[i], 0, dist[i], 0, numVertices);
        }

        for (int k = 0; k < numVertices; k++) {
            for (int i = 0; i < numVertices; i++) {
                for (int j = 0; j < numVertices; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }
        return dist;
    }

    /**
     * Thuật toán Kruskal tìm cây khung nhỏ nhất (Minimum Spanning Tree - O(E log E)).
     */
    public static List<Edge> kruskalMST(int numVertices, List<Edge> edges) {
        List<Edge> sortedEdges = new ArrayList<>(edges);
        sortedEdges.sort(Comparator.comparingDouble(Edge::weight));

        int[] parent = new int[numVertices];
        for (int i = 0; i < numVertices; i++) parent[i] = i;

        List<Edge> mst = new ArrayList<>();
        for (Edge edge : sortedEdges) {
            int rootU = find(parent, edge.u());
            int rootV = find(parent, edge.v());
            if (rootU != rootV) {
                mst.add(edge);
                parent[rootU] = rootV;
                if (mst.size() == numVertices - 1) {
                    break;
                }
            }
        }
        return mst;
    }

    private static int find(int[] parent, int i) {
        if (parent[i] == i) return i;
        return parent[i] = find(parent, parent[i]);
    }

    /**
     * Sắp xếp Topo trên đồ thị có hướng (Kahn's Topological Sort Algorithm - O(V + E)).
     * Trả về danh sách rỗng nếu phát hiện đồ thị có chu trình.
     */
    public static List<Integer> topologicalSort(int numVertices, List<List<Integer>> adj) {
        int[] inDegree = new int[numVertices];
        for (int u = 0; u < numVertices; u++) {
            for (int v : adj.get(u)) {
                inDegree[v]++;
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numVertices; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }

        List<Integer> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            result.add(u);

            for (int v : adj.get(u)) {
                inDegree[v]--;
                if (inDegree[v] == 0) {
                    queue.offer(v);
                }
            }
        }

        return result.size() == numVertices ? result : Collections.emptyList();
    }

    /**
     * Duyệt đồ thị theo chiều rộng (BFS) từ đỉnh nguồn.
     */
    public static List<Integer> bfs(int numVertices, List<List<Integer>> adj, int startVertex) {
        List<Integer> order = new ArrayList<>();
        boolean[] visited = new boolean[numVertices];
        Queue<Integer> q = new LinkedList<>();

        visited[startVertex] = true;
        q.offer(startVertex);

        while (!q.isEmpty()) {
            int u = q.poll();
            order.add(u);
            for (int v : adj.get(u)) {
                if (!visited[v]) {
                    visited[v] = true;
                    q.offer(v);
                }
            }
        }
        return order;
    }
}
