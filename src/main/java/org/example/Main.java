package org.example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    private static final Random random = new Random();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in, "UTF-8");
        String filePath;

        if (args.length >= 1) {
            filePath = args[0];
        } else {
            System.out.print("请输入文本文件路径: ");
            filePath = scanner.nextLine();
        }

        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            String cleaned = TextCleaner.cleanText(content);
            System.out.println("清洗后的文本：");
            System.out.println(cleaned);
            System.out.println("\n--- 图构建结果 ---");

            String[] words = cleaned.split(" ");
            WordGraph graph = new WordGraph();
            graph.buildGraph(words);
            graph.printGraph();

            while (true) {
                System.out.println("\n请选择操作：");
                System.out.println("1. 查询桥接词");
                System.out.println("2. 根据桥接词生成新文本");
                System.out.println("3. 计算两个单词之间的最短路径");
                System.out.println("4. 计算图中节点的PageRank");
                System.out.println("5. 退出");
                System.out.print("输入选项数字：");

                String option = scanner.nextLine().trim();
                if (option.equals("5")) break;

                switch (option) {
                    case "1":
                        queryBridgeWords(scanner, graph);
                        break;
                    case "2":
                        generateNewTextWithBridgeWords(scanner, graph);
                        break;
                    case "3":
                        shortestPathInteraction(scanner, graph);
                        break;
                    case "4":
                        computePageRankInteraction(scanner, graph, words);
                        break;
                    default:
                        System.out.println("无效选项，请重试。");
                }
            }

        } catch (IOException e) {
            System.err.println("读取文件失败: " + e.getMessage());
        }
    }

    private static void queryBridgeWords(Scanner scanner, WordGraph graph) {
        System.out.print("请输入两个英文单词 (word1 word2): ");
        String line = scanner.nextLine().trim().toLowerCase();
        String[] inputWords = line.split("\\s+");
        if (inputWords.length != 2) {
            System.out.println("请输入两个有效英文单词。");
            return;
        }
        String word1 = inputWords[0];
        String word2 = inputWords[1];
        Set<String> bridges = graph.getBridgeWords(word1, word2);
        if (bridges == null) {
            System.out.printf("No %s or %s in the graph!%n", word1, word2);
        } else if (bridges.isEmpty()) {
            System.out.printf("No bridge words from %s to %s!%n", word1, word2);
        } else {
            System.out.printf("The bridge words from %s to %s are: ", word1, word2);
            String list = String.join(", ", bridges);
            if (bridges.size() > 1) {
                int lastComma = list.lastIndexOf(", ");
                if (lastComma != -1) {
                    list = list.substring(0, lastComma) + " and" + list.substring(lastComma + 1);
                }
            }
            System.out.println(list + ".");
        }
    }

    private static void generateNewTextWithBridgeWords(Scanner scanner, WordGraph graph) {
        System.out.print("请输入一行英文文本：");
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) {
            System.out.println("输入不能为空。");
            return;
        }

        // 清洗用户输入文本（只保留英文单词，小写）
        String cleanedInput = TextCleaner.cleanText(line);
        String[] inputWords = cleanedInput.split(" ");

        if (inputWords.length == 0) {
            System.out.println("输入无有效英文单词。");
            return;
        }

        List<String> resultWords = new ArrayList<>();
        resultWords.add(inputWords[0]);

        for (int i = 0; i < inputWords.length - 1; i++) {
            String w1 = inputWords[i];
            String w2 = inputWords[i + 1];
            Set<String> bridges = graph.getBridgeWords(w1, w2);

            if (bridges != null && !bridges.isEmpty()) {
                // 随机选一个桥接词插入
                String bridgeWord = getRandomElement(bridges);
                resultWords.add(bridgeWord);
            }
            resultWords.add(w2);
        }

        // 输出拼接后的新文本（首字母保持小写，原句中用户大小写会丢失，如需保留可后续扩展）
        System.out.println("插入桥接词后的新文本：");
        System.out.println(String.join(" ", resultWords));
    }

    private static String getRandomElement(Set<String> set) {
        int index = random.nextInt(set.size());
        Iterator<String> iter = set.iterator();
        for (int i = 0; i < index; i++) iter.next();
        return iter.next();
    }
    private static void shortestPathInteraction(Scanner scanner, WordGraph graph) {
        System.out.println("请输入两个单词，以空格分隔，计算它们之间的最短路径；");
        System.out.println("如果只输入一个单词，计算该单词到所有可达单词的最短路径；");
        System.out.print("输入：");
        String line = scanner.nextLine().trim().toLowerCase();
        if (line.isEmpty()) {
            System.out.println("输入不能为空。");
            return;
        }

        String[] inputs = line.split("\\s+");
        if (inputs.length == 1) {
            String start = inputs[0];
            if (!graph.containsNode(start)) {
                System.out.printf("单词%s不在图中！%n", start);
                return;
            }
            System.out.printf("单词 %s 到图中其他节点的最短路径：%n", start);
            for (String node : graph.graph.keySet()) {
                if (node.equals(start)) continue;
                WordGraph.PathResult result = graph.getShortestPath(start, node);
                if (result == null) {
                    System.out.printf("不可达 %s%n", node);
                } else {
                    System.out.printf("%s -> %s : 路径 %s，长度 %d%n",
                            start, node, String.join("->", result.path), result.distance);
                }
            }
        } else if (inputs.length == 2) {
            String start = inputs[0];
            String end = inputs[1];
            if (!graph.containsNode(start) || !graph.containsNode(end)) {
                System.out.printf("单词%s或%s不在图中！%n", start, end);
                return;
            }
            WordGraph.PathResult result = graph.getShortestPath(start, end);
            if (result == null) {
                System.out.printf("单词%s和%s不可达！%n", start, end);
            } else {
                System.out.printf("单词%s到%s的最短路径为：%s，路径长度为%d%n",
                        start, end, String.join("->", result.path), result.distance);
            }
        } else {
            System.out.println("输入单词个数错误，请输入1个或2个单词。");
        }
    }
    private static void computePageRankInteraction(Scanner scanner, WordGraph graph, String[] words) {
        System.out.println("是否用词频TF初始化PageRank？(y/n): ");
        String ans = scanner.nextLine().trim().toLowerCase();
        Map<String, Double> initPR = null;

        if (ans.equals("y")) {
            Map<String, Integer> tf = graph.computeTF(words);
            initPR = new HashMap<>();
            // 转成double方便归一化
            for (Map.Entry<String, Integer> e : tf.entrySet()) {
                initPR.put(e.getKey(), e.getValue().doubleValue());
            }
        }

        double dampingFactor = 0.85;
        int maxIter = 100;
        double tol = 1e-6;

        Map<String, Double> prResult;
        if (initPR != null) {
            prResult = graph.computePageRank(dampingFactor, maxIter, tol, initPR);
        } else {
            prResult = graph.computePageRank(dampingFactor, maxIter, tol);
        }

        // 排序输出前20个PR最高的单词
        List<Map.Entry<String, Double>> list = new ArrayList<>(prResult.entrySet());
        list.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        System.out.println("PageRank 排名前20的单词及其PR值：");
        for (int i = 0; i < Math.min(20, list.size()); i++) {
            Map.Entry<String, Double> e = list.get(i);
            System.out.printf("%d. %s : %.6f%n", i + 1, e.getKey(), e.getValue());
        }
    }
}
