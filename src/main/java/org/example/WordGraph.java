package org.example;

import java.util.*;

public class WordGraph {
    final Map<String, Map<String, Integer>> graph = new HashMap<>();

    public void addEdge(String from, String to, int weight) {
        graph.computeIfAbsent(from, k -> new HashMap<>());
        graph.computeIfAbsent(to, k -> new HashMap<>()); // 确保 to 也存在
        Map<String, Integer> edges = graph.get(from);
        edges.put(to, edges.getOrDefault(to, 0) + 1);
    }


    public void buildGraph(String[] words) {
        for (int i = 0; i < words.length - 1; i++) {
            String from = words[i];
            String to = words[i + 1];
            addEdge(from, to, 1);
        }
    }

    public void printGraph() {
        System.out.println("当前图中所有节点：" + graph.keySet());
        for (String from : graph.keySet()) {
            Map<String, Integer> neighbors = graph.get(from);
            for (String to : neighbors.keySet()) {
                int weight = neighbors.get(to);
                System.out.printf("%s -> %s (weight = %d)%n", from, to, weight);
            }
        }
    }

    public boolean containsNode(String word) {
        return graph.containsKey(word);
    }

    public Set<String> getBridgeWords(String word1, String word2) {
        Set<String> result = new HashSet<>();

        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            return new HashSet<>(); // 表示至少有一个词不在图中
        }

        Map<String, Integer> word1Edges = graph.get(word1);
        for (String bridge : word1Edges.keySet()) {
            Map<String, Integer> bridgeEdges = graph.get(bridge);
            if (bridgeEdges != null && bridgeEdges.containsKey(word2)) {
                result.add(bridge);
            }
        }
        return result;
    }
    public static class PathResult {
        public final List<String> path; // 节点列表
        public final int distance;       // 权重和

        public PathResult(List<String> path, int distance) {
            this.path = path;
            this.distance = distance;
        }
    }

    // 计算最短路径，返回PathResult或null表示不可达或不存在
    public PathResult getShortestPath(String start, String end) {
        if (!graph.containsKey(start) || !graph.containsKey(end)) {
            return null;
        }

        // Dijkstra实现
        Map<String, Integer> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        Set<String> visited = new HashSet<>();
        PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));

        for (String node : graph.keySet()) {
            dist.put(node, Integer.MAX_VALUE);
        }
        dist.put(start, 0);
        pq.offer(new AbstractMap.SimpleEntry<>(start, 0));

        while (!pq.isEmpty()) {
            String u = pq.poll().getKey();
            if (visited.contains(u)) continue;
            visited.add(u);
            if (u.equals(end)) break;

            Map<String, Integer> neighbors = graph.getOrDefault(u, Collections.emptyMap());
            for (Map.Entry<String, Integer> neighbor : neighbors.entrySet()) {
                String v = neighbor.getKey();
                int weight = neighbor.getValue();
                if (dist.get(u) + weight < dist.getOrDefault(v, Integer.MAX_VALUE)) {
                    dist.put(v, dist.get(u) + weight);
                    prev.put(v, u);
                    pq.offer(new AbstractMap.SimpleEntry<>(v, dist.get(v)));
                }
            }
        }

        if (!dist.containsKey(end) || dist.get(end) == Integer.MAX_VALUE) {
            return null; // 不可达
        }

        // 回溯路径
        LinkedList<String> path = new LinkedList<>();
        String cur = end;
        while (cur != null) {
            path.addFirst(cur);
            cur = prev.get(cur);
        }

        return new PathResult(path, dist.get(end));
    }
    public Map<String, Double> computePageRank(double dampingFactor, int maxIter, double tol) {
        int N = graph.size();
        if (N == 0) return Collections.emptyMap();

        // 初始化PageRank，均匀分布
        Map<String, Double> pr = new HashMap<>();
        double initPR = 1.0 / N;
        for (String node : graph.keySet()) {
            pr.put(node, initPR);
        }

        // 迭代计算
        for (int iter = 0; iter < maxIter; iter++) {
            Map<String, Double> newPr = new HashMap<>();
            double diff = 0.0;

            for (String v : graph.keySet()) {
                double rankSum = 0.0;

                // 找所有指向v的节点u
                for (String u : graph.keySet()) {
                    Map<String, Integer> neighbors = graph.get(u);
                    if (neighbors != null && neighbors.containsKey(v)) {
                        double w_uv = neighbors.get(v);
                        int outWeightSum = neighbors.values().stream().mapToInt(Integer::intValue).sum();
                        rankSum += pr.get(u) * ((double) w_uv / outWeightSum);
                    }
                }

                double newRank = (1 - dampingFactor) / N + dampingFactor * rankSum;
                newPr.put(v, newRank);
                diff += Math.abs(newRank - pr.get(v));
            }

            pr = newPr;
            if (diff < tol) break; // 收敛
        }

        return pr;
    }

    // 支持初始PR值加权版
    public Map<String, Double> computePageRank(double dampingFactor, int maxIter, double tol, Map<String, Double> initPRMap) {
        int N = graph.size();
        if (N == 0) return Collections.emptyMap();

        Map<String, Double> pr = new HashMap<>();

        // 初始化PageRank，使用initPRMap，没给的节点用0
        double sumInit = initPRMap.values().stream().mapToDouble(Double::doubleValue).sum();
        if (sumInit == 0) sumInit = 1.0; // 防止除0

        for (String node : graph.keySet()) {
            double val = initPRMap.getOrDefault(node, 0.0) / sumInit;
            pr.put(node, val);
        }

        // 和上面迭代过程一致
        for (int iter = 0; iter < maxIter; iter++) {
            Map<String, Double> newPr = new HashMap<>();
            double diff = 0.0;

            for (String v : graph.keySet()) {
                double rankSum = 0.0;
                for (String u : graph.keySet()) {
                    Map<String, Integer> neighbors = graph.get(u);
                    if (neighbors != null && neighbors.containsKey(v)) {
                        double w_uv = neighbors.get(v);
                        int outWeightSum = neighbors.values().stream().mapToInt(Integer::intValue).sum();
                        rankSum += pr.get(u) * ((double) w_uv / outWeightSum);
                    }
                }
                double newRank = (1 - dampingFactor) / N + dampingFactor * rankSum;
                newPr.put(v, newRank);
                diff += Math.abs(newRank - pr.get(v));
            }

            pr = newPr;
            if (diff < tol) break;
        }

        return pr;
    }
    public Map<String, Integer> computeTF(String[] words) {
        Map<String, Integer> tf = new HashMap<>();
        for (String w : words) {
            tf.put(w, tf.getOrDefault(w, 0) + 1);
        }
        return tf;
    }


}
