package org.example;

import java.util.HashMap;
import java.util.Map;

public class GraphBuilder {
    private Map<String, Map<String, Integer>> graph = new HashMap<>();

    public void buildGraphFromText(String text) {
        graph.clear();
        text = text.replaceAll("[^a-zA-Z ]", "").toLowerCase();
        String[] words = text.split("\\s+");
        for (int i = 0; i < words.length - 1; i++) {
            String from = words[i];
            String to = words[i + 1];
            graph.putIfAbsent(from, new HashMap<>());
            Map<String, Integer> edges = graph.get(from);
            edges.put(to, edges.getOrDefault(to, 0) + 1);
        }
    }

    public Map<String, Map<String, Integer>> getGraph() {
        Map<String, Map<String, Integer>> copy = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
            copy.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
        return copy;
    }

}
