package me.yan.utils;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * token工具类 - 改进版，支持技术术语识别
 * @Author: 冯德衍
 */
@Component
public class TokenUtils {

    /**
     * 技术关键词列表 - 这些词应该作为一个整体
     */
    private static final Set<String> TECH_KEYWORDS = new HashSet<>(Arrays.asList(
            // 编程语言
            "c语言", "c++", "csharp", "java", "python", "javascript", "typescript",
            "go", "golang", "rust", "kotlin", "scala", "swift", "objectivec", "php",
            "ruby", "perl", "lua", "python3", "python2", "nodejs", "node.js",
            // 框架
            "spring", "springboot", "springmvc", "vue", "react", "angular", "flutter",
            "django", "flask", "express", "nestjs", "gin", "beego",
            // 数据库
            "mysql", "postgresql", "oracle", "sqlserver", "sqlite", "mongodb",
            "redis", "elasticsearch", "cassandra", "kafka", "rabbitmq", "rocketmq",
            // 技术术语
            "jvm", "jre", "jdk", "api", "http", "https", "tcp", "udp", "json", "xml",
            "html", "css", "docker", "kubernetes", "k8s", "linux", "unix", "windows",
            "git", "github", "gitlab", "jenkins", "maven", "gradle", "npm", "yarn",
            // 技术概念
            "微服务", "分布式", "高并发", "负载均衡", "缓存", "索引", "算法", "数据结构",
            "设计模式", "面向对象", "函数式编程", "响应式编程", "异步编程",
            // 常用缩写
            "ai", "ml", "dl", "nlp", "ocr", "api", "sdk", "ide", "cli", "gui",
            "cpu", "gpu", "ram", "rom", "i/o", "iot", "devops", "ci/cd", "restful"
    ));

    /**
     * 技术关键词正则 - 用于优先匹配技术术语
     */
    private static final Pattern TECH_PATTERN;

    static {
        // 按长度降序排序，确保长词优先匹配
        List<String> sortedKeywords = new ArrayList<>(TECH_KEYWORDS);
        sortedKeywords.sort((a, b) -> b.length() - a.length());
        
        StringBuilder patternBuilder = new StringBuilder();
        for (String keyword : sortedKeywords) {
            if (patternBuilder.length() > 0) {
                patternBuilder.append("|");
            }
            // 转义特殊字符
            patternBuilder.append(Pattern.quote(keyword));
        }
        TECH_PATTERN = Pattern.compile(patternBuilder.toString(), Pattern.CASE_INSENSITIVE);
    }

    /**
     * 通用分词正则 - 匹配中文词和英文词
     */
    private static final Pattern WORD_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]{2,}|[a-zA-Z]{2,}|[a-zA-Z]");

    public List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return tokens;
        }

        String remainingText = text.toLowerCase().trim();
        
        // 第一步：优先匹配技术关键词
        Matcher techMatcher = TECH_PATTERN.matcher(remainingText);
        while (techMatcher.find()) {
            String keyword = techMatcher.group().toLowerCase();
            tokens.add(keyword);
            // 从剩余文本中移除已匹配的关键词
            remainingText = remainingText.replaceFirst(Pattern.quote(keyword), " ");
        }
        
        // 第二步：对剩余文本进行通用分词
        remainingText = remainingText.trim();
        if (!remainingText.isEmpty()) {
            Matcher wordMatcher = WORD_PATTERN.matcher(remainingText);
            while (wordMatcher.find()) {
                String word = wordMatcher.group();
                // 过滤掉已在技术关键词中匹配过的词
                if (!TECH_KEYWORDS.contains(word) && word.length() >= 1) {
                    tokens.add(word);
                }
            }
        }
        
        // 去重并保持顺序
        List<String> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (String token : tokens) {
            if (!seen.contains(token)) {
                seen.add(token);
                result.add(token);
            }
        }
        
        return result;
    }
}