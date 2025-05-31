import org.example.WordGraph;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class WordGraphTest {

    // 工具方法：向图中添加一条边
    private void addEdge(WordGraph graph, String from, String to, int weight) {
        graph.addEdge(from, to, weight);
    }

    @Test
    public void testTC1_emptyGraph() {
        WordGraph graph = new WordGraph(); // 空图
        Map<String, Double> pr = graph.computePageRank(0.85, 100, 1e-6);
        pr.forEach((k, v) -> System.out.println("TC1 - " + k + ": " + v));
        assertTrue(pr.isEmpty());
    }

    @Test
    public void testTC2_cycleGraph_normalParams() {
        WordGraph graph = new WordGraph();
        addEdge(graph, "A", "B", 1);
        addEdge(graph, "B", "C", 1);
        addEdge(graph, "C", "A", 1);

        Map<String, Double> pr = graph.computePageRank(0.85, 100, 1e-6);
        pr.forEach((k, v) -> System.out.println("TC2 - " + k + ": " + v));
        assertEquals(3, pr.size());
        assertTrue(Math.abs(pr.get("A") + pr.get("B") + pr.get("C") - 1.0) < 1e-5);
    }

    @Test
    public void testTC3_dampingFactorZero() {
        WordGraph graph = new WordGraph();
        addEdge(graph, "A", "B", 1);
        addEdge(graph, "B", "C", 1);
        addEdge(graph, "C", "A", 1);

        Map<String, Double> pr = graph.computePageRank(0.0, 100, 1e-6);
        pr.forEach((k, v) -> System.out.println("TC3 - " + k + ": " + v));
        assertEquals(3, pr.size());
        assertEquals(1.0 / 3, pr.get("A"), 1e-6);
        assertEquals(1.0 / 3, pr.get("B"), 1e-6);
        assertEquals(1.0 / 3, pr.get("C"), 1e-6);
    }

    @Test
    public void testTC4_dampingFactorGreaterThanOne() {
        WordGraph graph = new WordGraph();
        addEdge(graph, "A", "B", 1);
        addEdge(graph, "B", "C", 1);
        addEdge(graph, "C", "A", 1);

        Map<String, Double> pr = graph.computePageRank(1.2, 100, 1e-6);
        pr.forEach((k, v) -> System.out.println("TC4 - " + k + ": " + v));
        assertEquals(3, pr.size());
    }

    @Test
    public void testTC5_maxIterZero() {
        WordGraph graph = new WordGraph();
        addEdge(graph, "A", "B", 1);
        addEdge(graph, "B", "C", 1);
        addEdge(graph, "C", "A", 1);

        Map<String, Double> pr = graph.computePageRank(0.85, 0, 1e-6);
        pr.forEach((k, v) -> System.out.println("TC5 - " + k + ": " + v));
        assertEquals(3, pr.size());
        assertEquals(1.0 / 3, pr.get("A"), 1e-6);
    }

    @Test
    public void testTC6_tolZero() {
        WordGraph graph = new WordGraph();
        addEdge(graph, "A", "B", 1);
        addEdge(graph, "B", "C", 1);
        addEdge(graph, "C", "A", 1);

        Map<String, Double> pr = graph.computePageRank(0.85, 100, 0.0);
        pr.forEach((k, v) -> System.out.println("TC6 - " + k + ": " + v));
        assertEquals(3, pr.size());
    }

    @Test
    public void testTC7_chainGraph() {
        WordGraph graph = new WordGraph();
        addEdge(graph, "A", "B", 1);
        addEdge(graph, "B", "C", 1);
        addEdge(graph, "C", "D", 1);

        Map<String, Double> pr = graph.computePageRank(0.85, 100, 1e-6);
        pr.forEach((k, v) -> System.out.println("TC7 - " + k + ": " + v));
        assertEquals(3, pr.size());
    }
}