package org.example;

public class TextCleaner {
    public static String cleanText(String raw) {
        // 替换换行符为空格
        raw = raw.replaceAll("[\\r\\n]+", " ");
        // 将非字母字符当作分隔符处理
        raw = raw.replaceAll("[^A-Za-z]", " ");
        // 将多个空格合并为一个空格，并转小写
        return raw.toLowerCase().replaceAll("\\s+", " ").trim();
    }
}
