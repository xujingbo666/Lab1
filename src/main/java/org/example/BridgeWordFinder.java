package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BridgeWordFinder {
    private final Map<String, Map<String, Integer>> graph;

    public BridgeWordFinder(Map<String, Map<String, Integer>> graph) {
        this.graph = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
            this.graph.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
    }

    public List<String> findBridgeWords(String word1, String word2) {
        List<String> bridgeWords = new ArrayList<>();
        if (!graph.containsKey(word1)) return bridgeWords;
        for (String mid : graph.get(word1).keySet()) {
            if (graph.containsKey(mid) && graph.get(mid).containsKey(word2)) {
                bridgeWords.add(mid);
            }
        }
        return bridgeWords;
    }
}

