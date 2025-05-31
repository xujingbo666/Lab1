import static org.junit.jupiter.api.Assertions.*;

import org.example.WordGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

public class WordGraphTest_w {

    private WordGraph graph;

    @BeforeEach
    public void setUp() {
        graph = new WordGraph();
        // 构造示例图结构，方便测试
        // A -> B
        graph.addEdge("apple", "orange", 1);
        graph.addEdge("orange", "banana", 1);
        // 额外构造一些无桥接词的边
        graph.addEdge("apple", "grape", 1);
        graph.addEdge("coco", "melon", 1);
    }

    @Test
    public void testTC1_emptyWord1Edges() {
        // "kiwi"存在，但没有出边，构造特殊情况
        graph.addEdge("kiwi", "kiwi", 0);
        graph.printGraph();
        Set<String> result = graph.getBridgeWords("kiwi", "banana");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testTC2_noBridgeWords() {
        Set<String> result = graph.getBridgeWords("apple", "melon");
        assertNotNull(result);
        assertTrue(result.isEmpty(), "应该没有桥接词");
    }

    @Test
    public void testTC3_withBridgeWords() {
        graph.printGraph();
        Set<String> result = graph.getBridgeWords("apple", "banana");
        assertNotNull(result);
        assertTrue(result.contains("orange"), "应包含桥接词 'orange'");
        assertEquals(1, result.size());
    }

    @Test
    public void testTC4_wordNotInGraph() {
        assertTrue(graph.getBridgeWords("notExistWord", "banana").isEmpty());
        assertTrue(graph.getBridgeWords("apple", "notExistWord").isEmpty());
        assertTrue(graph.getBridgeWords("notExistWord1", "notExistWord2").isEmpty());
    }
}
