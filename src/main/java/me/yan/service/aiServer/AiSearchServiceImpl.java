package me.yan.service.aiServer;

import lombok.extern.slf4j.Slf4j;
import me.yan.dto.AiSearchResultDto;
import me.yan.pojo.ArticleDomain;
import me.yan.service.article.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AiSearchServiceImpl implements AiSearchService {

    @Autowired
    private ArticleService articleService;

    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]");
    private static final Pattern WORD_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]+|[a-zA-Z]+");
    
    private static final Map<String, List<String>> SYNONYM_MAP = new HashMap<>();
    private static final Map<String, String> QUESTION_PATTERNS = new HashMap<>();
    private static final Map<String, List<String>> CONCEPT_MAP = new HashMap<>();
    private static final Map<String, String> QUESTION_TYPE_DESC = new HashMap<>();
    private static final Map<String, String> ANSWER_TEMPLATES = new HashMap<>();
    private static final Map<String, String> KNOWLEDGE_BASE = new HashMap<>();
    
    static {
        initSynonyms();
        initQuestionPatterns();
        initConcepts();
        initQuestionTypeDescriptions();
        initAnswerTemplates();
        initKnowledgeBase();
    }

    private static void initSynonyms() {
        SYNONYM_MAP.put("学习", Arrays.asList("学习", "教程", "入门", "指南", "学习方法", "自学", "课程", "培训"));
        SYNONYM_MAP.put("编程", Arrays.asList("编程", "代码", "开发", "编程技术", "编码", "程序设计", "软件开发"));
        SYNONYM_MAP.put("java", Arrays.asList("java", "jdk", "jvm", "java编程", "java开发", "java基础"));
        SYNONYM_MAP.put("python", Arrays.asList("python", "python编程", "python开发", "python基础"));
        SYNONYM_MAP.put("数据库", Arrays.asList("数据库", "mysql", "sql", "oracle", "数据存储", "postgres"));
        SYNONYM_MAP.put("算法", Arrays.asList("算法", "数据结构", "算法题", "leetcode", "算法学习", "acm"));
        SYNONYM_MAP.put("前端", Arrays.asList("前端", "javascript", "html", "css", "vue", "react", "angular"));
        SYNONYM_MAP.put("后端", Arrays.asList("后端", "server", "api", "服务端", "nodejs"));
        SYNONYM_MAP.put("面试", Arrays.asList("面试", "面试题", "面经", "求职", "招聘", "笔试"));
        SYNONYM_MAP.put("性能", Arrays.asList("性能", "优化", "调优", "性能优化", "性能提升", "性能调优"));
        SYNONYM_MAP.put("框架", Arrays.asList("框架", "spring", "springboot", "django", "框架学习", "框架实战"));
        SYNONYM_MAP.put("设计", Arrays.asList("设计", "架构", "设计模式", "系统设计", "架构设计"));
        SYNONYM_MAP.put("安全", Arrays.asList("安全", "漏洞", "防护", "网络安全", "信息安全", "渗透测试"));
        SYNONYM_MAP.put("云计算", Arrays.asList("云计算", "云服务", "aws", "阿里云", "云原生", "docker"));
        SYNONYM_MAP.put("微服务", Arrays.asList("微服务", "分布式", "服务治理", "springcloud", "microservices"));
        SYNONYM_MAP.put("redis", Arrays.asList("redis", "缓存", "nosql", "缓存策略", "分布式缓存"));
        SYNONYM_MAP.put("spring", Arrays.asList("spring", "springboot", "springmvc", "spring框架"));
    }

    private static void initQuestionPatterns() {
        QUESTION_PATTERNS.put("what", "^(什么是|什么叫|什么是|定义|概念|解释|是什么|介绍)");
        QUESTION_PATTERNS.put("why", "^(为什么|为何|原因|理由|原理|为什么说|为何会)");
        QUESTION_PATTERNS.put("how", "^(如何|怎么|怎样|方法|步骤|教程|实现|怎么做)");
        QUESTION_PATTERNS.put("compare", "^(对比|比较|区别|差异|哪个好|哪个更)");
        QUESTION_PATTERNS.put("best", "^(最佳|推荐|最好|首选|建议|应该)");
        QUESTION_PATTERNS.put("problem", "^(问题|错误|异常|bug|报错|解决|处理)");
    }

    private static void initConcepts() {
        CONCEPT_MAP.put("java虚拟机", Arrays.asList("JVM", "java虚拟机", "jvm原理", "jvm调优", "java内存模型"));
        CONCEPT_MAP.put("分布式系统", Arrays.asList("分布式", "分布式锁", "分布式事务", "一致性", "cap理论"));
        CONCEPT_MAP.put("高并发", Arrays.asList("高并发", "并发编程", "线程安全", "锁", "并发控制"));
        CONCEPT_MAP.put("缓存", Arrays.asList("缓存", "redis", "memcached", "缓存策略", "缓存击穿"));
        CONCEPT_MAP.put("消息队列", Arrays.asList("消息队列", "kafka", "rabbitmq", "mq", "消息中间件"));
        CONCEPT_MAP.put("数据库优化", Arrays.asList("索引", "sql优化", "数据库调优", "查询优化"));
        CONCEPT_MAP.put("微服务架构", Arrays.asList("微服务", "服务发现", "熔断", "降级", "网关"));
        CONCEPT_MAP.put("设计模式", Arrays.asList("设计模式", "单例", "工厂", "观察者", "策略模式"));
    }

    private static void initQuestionTypeDescriptions() {
        QUESTION_TYPE_DESC.put("what", "定义/概念");
        QUESTION_TYPE_DESC.put("why", "原因分析");
        QUESTION_TYPE_DESC.put("how", "方法/教程");
        QUESTION_TYPE_DESC.put("compare", "对比分析");
        QUESTION_TYPE_DESC.put("best", "推荐建议");
        QUESTION_TYPE_DESC.put("problem", "问题解决");
        QUESTION_TYPE_DESC.put("general", "通用查询");
    }

    private static void initAnswerTemplates() {
        ANSWER_TEMPLATES.put("what", "根据我的分析，{topic}是指{definition}。它是{domain}领域中的重要概念，主要用于{purpose}。");
        ANSWER_TEMPLATES.put("why", "这个问题涉及{topic}的核心原理。主要原因包括：{reasons}。了解这些原因有助于深入理解{topic}的本质。");
        ANSWER_TEMPLATES.put("how", "要实现{topic}，通常需要以下步骤：{steps}。建议按照这个顺序逐步实践，遇到问题可以参考相关文档。");
        ANSWER_TEMPLATES.put("compare", "在比较{topic1}和{topic2}时，需要从多个维度进行分析：{dimensions}。根据您的需求选择最适合的方案。");
        ANSWER_TEMPLATES.put("best", "基于行业最佳实践和综合评估，推荐{recommendation}。主要考虑因素包括：{factors}。");
        ANSWER_TEMPLATES.put("problem", "针对{problem}问题，常见的解决方案包括：{solutions}。建议先从{firstStep}开始排查。");
        ANSWER_TEMPLATES.put("general", "关于{topic}，我为您整理了以下相关信息：{summary}。您可以参考推荐的文章获取更详细的内容。");
    }

    private static void initKnowledgeBase() {
        KNOWLEDGE_BASE.put("jvm", "Java虚拟机（JVM）是Java平台的核心组件，负责执行Java字节码。它提供内存管理、垃圾回收、即时编译等核心功能。");
        KNOWLEDGE_BASE.put("微服务", "微服务架构是一种将应用程序构建为一组小型、独立服务的方法，每个服务运行在自己的进程中，通过轻量级机制通信。");
        KNOWLEDGE_BASE.put("redis", "Redis是一个开源的内存数据结构存储系统，可用作数据库、缓存和消息代理。支持多种数据结构如字符串、哈希、列表等。");
        KNOWLEDGE_BASE.put("springboot", "Spring Boot是一个用于构建生产级Spring应用的框架，简化了Spring应用的配置和部署过程。");
        KNOWLEDGE_BASE.put("数据库优化", "数据库优化涉及索引优化、查询优化、缓存策略等多个方面，目的是提升数据库查询性能和响应速度。");
        KNOWLEDGE_BASE.put("设计模式", "设计模式是软件开发中常见问题的可复用解决方案，如单例模式、工厂模式、观察者模式等。");
        KNOWLEDGE_BASE.put("高并发", "高并发处理涉及线程安全、锁机制、并发控制等技术，确保系统在大量用户同时访问时的稳定性和性能。");
        KNOWLEDGE_BASE.put("分布式系统", "分布式系统由多个独立计算机组成，通过网络协同工作，实现高可用、可扩展的服务。");
        KNOWLEDGE_BASE.put("消息队列", "消息队列是一种异步通信机制，用于解耦系统组件，实现削峰填谷和异步处理。");
        KNOWLEDGE_BASE.put("缓存", "缓存是一种数据存储技术，将常用数据存储在高速存储介质中，以减少对后端系统的访问压力。");
        KNOWLEDGE_BASE.put("学习编程", "学习编程需要掌握编程语言基础、数据结构、算法、设计模式等核心知识，建议通过实践项目来巩固。");
        KNOWLEDGE_BASE.put("面试", "技术面试通常包括算法题、系统设计、项目经验等方面，建议提前准备并进行模拟练习。");
        KNOWLEDGE_BASE.put("性能优化", "性能优化涉及代码优化、数据库优化、缓存策略、异步处理等多个层面，需要系统性地分析和改进。");
        KNOWLEDGE_BASE.put("架构设计", "架构设计需要考虑系统的可扩展性、高可用性、安全性等因素，选择合适的架构模式和技术栈。");
        KNOWLEDGE_BASE.put("安全", "信息安全涉及漏洞防护、数据加密、访问控制等方面，是软件开发中不可或缺的重要环节。");
    }

    @Override
    public AiSearchResultDto searchArticles(String query, int topN) {
        long startTime = System.currentTimeMillis();
        
        if (query == null || query.trim().isEmpty()) {
            return AiSearchResultDto.builder()
                    .matches(new ArrayList<>())
                    .totalCount(0)
                    .responseTime(0)
                    .analysis(createEmptyAnalysis())
                    .build();
        }

        List<ArticleDomain> allArticles = getAllArticles();
        
        if (allArticles.isEmpty()) {
            return AiSearchResultDto.builder()
                    .matches(new ArrayList<>())
                    .totalCount(0)
                    .responseTime(System.currentTimeMillis() - startTime)
                    .analysis(createEmptyAnalysis())
                    .build();
        }

        // 获取问题类型
        String questionType = analyzeQuestionType(query);
        // 拆成token关键词
        List<String> originalKeywords = tokenize(query);
        log.info("拆成的token :{}",originalKeywords);
        // 拓展查询
        List<String> expandedQuery = expandQueryWithSynonyms(query);
        log.info("拓展完的token :{}",expandedQuery);
        // 识别匹配的 concepts
        List<String> matchedConcepts = identifyMatchedConcepts(expandedQuery);
        log.info("识别的concepts :{}",matchedConcepts);
        // 计算智能分数
        Map<ArticleDomain, Double> scores = calculateIntelligentScore(query, expandedQuery, questionType, allArticles);
        log.info("计算的分数：{}",scores);
        // 计算语义匹配数
        int semanticMatches = countSemanticMatches(expandedQuery, allArticles);
        log.info("语义匹配数：{}",semanticMatches);
        // 获取概念匹配数
        int conceptMatches = countConceptMatches(matchedConcepts, allArticles);
        log.info("概念匹配数:{}",conceptMatches);
        // 获取匹配列表
        List<AiSearchResultDto.ArticleMatch> matches = scores.entrySet().stream()
                .filter(entry -> entry.getValue() > 0.5)
                .sorted(Map.Entry.<ArticleDomain, Double>comparingByValue().reversed())
                .limit(topN)
                .map(entry -> {
                    ArticleDomain article = entry.getKey();
                    String highlight = generateSmartHighlight(article, query, expandedQuery);
                    return AiSearchResultDto.ArticleMatch.builder()
                            .aid(article.getAid())
                            .title(article.getTitle())
                            .content(article.getContent())
                            .category(article.getCategory())
                            .coverPic(article.getCoverPic())
                            .create_time(article.getCreate_time())
                            .relevanceScore(Math.round(entry.getValue() * 100.0) / 100.0)
                            .highlightSnippet(highlight)
                            .build();
                })
                .collect(Collectors.toList());
        log.info("匹配列表数量：{}",matches.size());
        log.info("匹配列表：{}",matches);
        log.info("================================");
        long responseTime = System.currentTimeMillis() - startTime;
        
        String analysisSummary = generateAnalysisSummary(query, questionType, matchedConcepts, semanticMatches, conceptMatches);
        
        AiSearchResultDto.AiAnalysis analysis = AiSearchResultDto.AiAnalysis.builder()
                .questionType(questionType)
                .questionTypeDesc(QUESTION_TYPE_DESC.getOrDefault(questionType, "通用查询"))
                .originalKeywords(originalKeywords)
                .expandedKeywords(expandedQuery)
                .matchedConcepts(matchedConcepts)
                .semanticMatches(semanticMatches)
                .conceptMatches(conceptMatches)
                .analysisSummary(analysisSummary)
                .build();
        
        AiSearchResultDto.AiAnswer aiAnswer = generateAiAnswer(query, questionType, matches, matchedConcepts);
        
        log.info("AI检索完成，查询词: {}, 问题类型: {}, 扩展词数量: {}, 匹配概念: {}, 语义匹配数: {}, 概念匹配数: {}, 返回结果数: {}, 耗时: {}ms", 
                query, questionType, expandedQuery.size(), matchedConcepts.size(), semanticMatches, conceptMatches, matches.size(), responseTime);

        return AiSearchResultDto.builder()
                .matches(matches)
                .totalCount(matches.size())
                .responseTime(responseTime)
                .analysis(analysis)
                .aiAnswer(aiAnswer)
                .build();
    }

    private AiSearchResultDto.AiAnalysis createEmptyAnalysis() {
        return AiSearchResultDto.AiAnalysis.builder()
                .questionType("general")
                .questionTypeDesc("通用查询")
                .originalKeywords(new ArrayList<>())
                .expandedKeywords(new ArrayList<>())
                .matchedConcepts(new ArrayList<>())
                .semanticMatches(0)
                .conceptMatches(0)
                .build();
    }

    /**
     * 分析问题类型
     */
    private String analyzeQuestionType(String query) {
        for (Map.Entry<String, String> entry : QUESTION_PATTERNS.entrySet()) {
            if (Pattern.matches(entry.getValue(), query)) {
                return entry.getKey();
            }
        }
        return "general";
    }

    // 扩展查询
    private List<String> expandQueryWithSynonyms(String query) {
        List<String> tokens = tokenize(query);
        Set<String> expanded = new HashSet<>(tokens);
        
        for (String token : tokens) {
            if (SYNONYM_MAP.containsKey(token)) {
                expanded.addAll(SYNONYM_MAP.get(token));
            }
            for (Map.Entry<String, List<String>> concept : CONCEPT_MAP.entrySet()) {
                if (concept.getValue().contains(token.toLowerCase())) {
                    expanded.addAll(concept.getValue());
                    expanded.add(concept.getKey());
                }
            }
        }
        
        return new ArrayList<>(expanded);
    }

    /**
     * 识别匹配的 concepts
     * @param expandedQuery
     * @return
     */
    private List<String> identifyMatchedConcepts(List<String> expandedQuery) {
        List<String> matched = new ArrayList<>();
        Set<String> querySet = new HashSet<>(expandedQuery);
        
        for (Map.Entry<String, List<String>> concept : CONCEPT_MAP.entrySet()) {
            for (String term : concept.getValue()) {
                if (querySet.contains(term.toLowerCase())) {
                    matched.add(concept.getKey());
                    break;
                }
            }
        }
        
        return matched;
    }

    private int countSemanticMatches(List<String> expandedQuery, List<ArticleDomain> articles) {
        Set<String> querySet = new HashSet<>(expandedQuery);
        int matches = 0;
        
        for (ArticleDomain article : articles) {
            Set<String> articleTerms = new HashSet<>();
            articleTerms.addAll(tokenize(article.getTitle()));
            if (article.getContent() != null) {
                articleTerms.addAll(tokenize(article.getContent()));
            }
            
            for (String term : querySet) {
                if (articleTerms.contains(term.toLowerCase())) {
                    matches++;
                }
            }
        }
        
        return matches;
    }

    private int countConceptMatches(List<String> concepts, List<ArticleDomain> articles) {
        int matches = 0;
        
        for (ArticleDomain article : articles) {
            String text = (article.getTitle() + " " + (article.getContent() != null ? article.getContent() : "")).toLowerCase();
            for (String concept : concepts) {
                if (text.contains(concept.toLowerCase())) {
                    matches++;
                }
            }
        }
        
        return matches;
    }

    private Map<ArticleDomain, Double> calculateIntelligentScore(String originalQuery, List<String> expandedQuery, 
                                                                 String questionType, List<ArticleDomain> articles) {
        Map<ArticleDomain, Double> scores = new HashMap<>();
        
        Map<String, Double> idfMap = calculateEnhancedIDF(articles);
        
        for (ArticleDomain article : articles) {
            double score = calculateEnhancedSimilarity(originalQuery, expandedQuery, questionType, article, idfMap);
            if (score > 0) {
                scores.put(article, score);
            }
        }
        
        return scores;
    }

    private double calculateEnhancedSimilarity(String originalQuery, List<String> expandedQuery,
                                              String questionType, ArticleDomain article, Map<String, Double> idfMap) {
        List<String> titleTokens = tokenize(article.getTitle());
        List<String> contentTokens = tokenize(article.getContent() != null ? article.getContent() : "");
        
        double baseScore = calculateTfIdfScore(expandedQuery, titleTokens, contentTokens, idfMap);
        double semanticScore = calculateSemanticScore(originalQuery, article);
        double questionScore = calculateQuestionTypeScore(questionType, article);
        double freshnessScore = calculateFreshnessScore(article);
        
        double finalScore = (baseScore * 0.5) + (semanticScore * 0.3) + (questionScore * 0.1) + (freshnessScore * 0.1);
        
        return finalScore;
    }

    private double calculateTfIdfScore(List<String> queryTokens, List<String> titleTokens, 
                                       List<String> contentTokens, Map<String, Double> idfMap) {
        Map<String, Double> queryTF = calculateTF(queryTokens);
        Map<String, Double> docTF = new HashMap<>();
        
        for (String token : titleTokens) {
            docTF.merge(token.toLowerCase(), 2.0, Double::sum);
        }
        for (String token : contentTokens) {
            docTF.merge(token.toLowerCase(), 1.0, Double::sum);
        }
        
        double maxTF = docTF.values().stream().max(Double::compare).orElse(1.0);
        for (Map.Entry<String, Double> entry : docTF.entrySet()) {
            docTF.put(entry.getKey(), entry.getValue() / maxTF);
        }
        
        double dotProduct = 0;
        double queryNorm = 0;
        double docNorm = 0;
        
        Set<String> allTerms = new HashSet<>();
        allTerms.addAll(queryTF.keySet());
        allTerms.addAll(docTF.keySet());
        
        for (String term : allTerms) {
            String lowerTerm = term.toLowerCase();
            double queryWeight = queryTF.getOrDefault(term, 0.0) * idfMap.getOrDefault(lowerTerm, 1.0);
            double docWeight = docTF.getOrDefault(lowerTerm, 0.0) * idfMap.getOrDefault(lowerTerm, 1.0);
            
            dotProduct += queryWeight * docWeight;
            queryNorm += queryWeight * queryWeight;
            docNorm += docWeight * docWeight;
        }
        
        if (queryNorm == 0 || docNorm == 0) {
            return 0;
        }
        
        return dotProduct / (Math.sqrt(queryNorm) * Math.sqrt(docNorm));
    }

    private double calculateSemanticScore(String query, ArticleDomain article) {
        List<String> queryTokens = tokenize(query);
        List<String> titleTokens = tokenize(article.getTitle());
        List<String> contentTokens = tokenize(article.getContent() != null ? article.getContent() : "");
        
        Set<String> querySet = new HashSet<>(queryTokens);
        Set<String> titleSet = new HashSet<>(titleTokens);
        Set<String> contentSet = new HashSet<>(contentTokens);
        
        int titleMatches = 0;
        int contentMatches = 0;
        
        for (String token : querySet) {
            String lowerToken = token.toLowerCase();
            if (titleSet.contains(lowerToken)) {
                titleMatches++;
            }
            if (contentSet.contains(lowerToken)) {
                contentMatches++;
            }
        }
        
        double titleScore = querySet.isEmpty() ? 0 : (double) titleMatches / querySet.size();
        double contentScore = querySet.isEmpty() ? 0 : (double) contentMatches / querySet.size();
        
        double conceptScore = 0;
        int conceptMatches = 0;
        int totalConcepts = 0;
        for (Map.Entry<String, List<String>> concept : CONCEPT_MAP.entrySet()) {
            boolean queryHasConcept = concept.getValue().stream().anyMatch(t -> querySet.contains(t.toLowerCase()));
            boolean docHasConcept = concept.getValue().stream().anyMatch(t -> titleSet.contains(t.toLowerCase())) 
                                || concept.getValue().stream().anyMatch(t -> contentSet.contains(t.toLowerCase()));
            if (queryHasConcept) {
                totalConcepts++;
                if (docHasConcept) {
                    conceptMatches++;
                }
            }
        }
        conceptScore = totalConcepts > 0 ? (double) conceptMatches / totalConcepts : 0;
        
        return (titleScore * 0.5 + contentScore * 0.3 + conceptScore * 0.2);
    }

    private double calculateQuestionTypeScore(String questionType, ArticleDomain article) {
        String content = article.getContent() != null ? article.getContent() : "";
        String title = article.getTitle();
        
        switch (questionType) {
            case "what":
                return containsDefinitionKeywords(title, content) ? 1.0 : 0.5;
            case "why":
                return containsReasoningKeywords(title, content) ? 1.0 : 0.5;
            case "how":
                return containsStepKeywords(title, content) ? 1.0 : 0.5;
            case "problem":
                return containsSolutionKeywords(title, content) ? 1.0 : 0.5;
            case "compare":
                return containsCompareKeywords(title, content) ? 1.0 : 0.5;
            case "best":
                return containsRecommendKeywords(title, content) ? 1.0 : 0.5;
            default:
                return 0.7;
        }
    }

    private boolean containsDefinitionKeywords(String title, String content) {
        String text = (title + " " + content).toLowerCase();
        String[] keywords = {"定义", "概念", "解释", "是什么", "介绍", "概述", "说明", "含义", "定义为"};
        return containsAnyKeyword(text, keywords);
    }

    private boolean containsReasoningKeywords(String title, String content) {
        String text = (title + " " + content).toLowerCase();
        String[] keywords = {"原因", "因为", "所以", "原理", "机制", "为什么", "理由", "导致", "引起"};
        return containsAnyKeyword(text, keywords);
    }

    private boolean containsStepKeywords(String title, String content) {
        String text = (title + " " + content).toLowerCase();
        String[] keywords = {"步骤", "方法", "教程", "如何", "步骤", "实现", "做法", "步骤是", "首先", "其次"};
        return containsAnyKeyword(text, keywords);
    }

    private boolean containsSolutionKeywords(String title, String content) {
        String text = (title + " " + content).toLowerCase();
        String[] keywords = {"解决", "修复", "处理", "问题", "错误", "bug", "异常", "解决方案", "解决方法"};
        return containsAnyKeyword(text, keywords);
    }

    private boolean containsCompareKeywords(String title, String content) {
        String text = (title + " " + content).toLowerCase();
        String[] keywords = {"对比", "比较", "区别", "差异", "优缺点", "不同", "对比分析"};
        return containsAnyKeyword(text, keywords);
    }

    private boolean containsRecommendKeywords(String title, String content) {
        String text = (title + " " + content).toLowerCase();
        String[] keywords = {"推荐", "最佳", "建议", "应该", "首选", "推荐方案", "建议使用"};
        return containsAnyKeyword(text, keywords);
    }

    private boolean containsAnyKeyword(String text, String[] keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private double calculateFreshnessScore(ArticleDomain article) {
        if (article.getCreate_time() == null) {
            return 0.5;
        }
        
        long now = System.currentTimeMillis();
        long articleTime = article.getCreate_time();
        long daysSinceCreation = (now - articleTime) / (1000 * 60 * 60 * 24);
        
        if (daysSinceCreation < 30) {
            return 1.0;
        } else if (daysSinceCreation < 180) {
            return 0.8;
        } else if (daysSinceCreation < 365) {
            return 0.6;
        } else {
            return 0.4;
        }
    }

    private Map<String, Double> calculateEnhancedIDF(List<ArticleDomain> articles) {
        Map<String, Double> idfMap = new HashMap<>();
        int totalDocuments = articles.size();
        
        Map<String, Integer> docCount = new HashMap<>();
        
        for (ArticleDomain article : articles) {
            Set<String> uniqueTerms = new HashSet<>();
            uniqueTerms.addAll(tokenize(article.getTitle()).stream().map(String::toLowerCase).collect(Collectors.toList()));
            if (article.getContent() != null) {
                uniqueTerms.addAll(tokenize(article.getContent()).stream().map(String::toLowerCase).collect(Collectors.toList()));
            }
            
            for (String term : uniqueTerms) {
                docCount.merge(term, 1, Integer::sum);
            }
        }
        
        for (Map.Entry<String, Integer> entry : docCount.entrySet()) {
            double idf = Math.log((double) (totalDocuments + 1) / (entry.getValue() + 0.5)) + 1;
            idfMap.put(entry.getKey(), idf);
        }
        
        return idfMap;
    }

    private List<ArticleDomain> getAllArticles() {
        return articleService.getArticlesByCond(null, 1, Integer.MAX_VALUE).getRecords();
    }

    // 获取单词列表
    private List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return tokens;
        }
        Matcher matcher = WORD_PATTERN.matcher(text.toLowerCase());
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        return tokens;
    }

    private Map<String, Double> calculateTF(List<String> tokens) {
        Map<String, Double> tf = new HashMap<>();
        int totalTokens = tokens.size();
        
        for (String token : tokens) {
            tf.merge(token, 1.0, Double::sum);
        }
        
        for (Map.Entry<String, Double> entry : tf.entrySet()) {
            tf.put(entry.getKey(), entry.getValue() / totalTokens);
        }
        
        return tf;
    }

    private String generateSmartHighlight(ArticleDomain article, String originalQuery, List<String> expandedQuery) {
        String content = article.getContent();
        if (content == null || content.isEmpty()) {
            return "";
        }
        
        String highlightContent = content.length() > 1000 ? content.substring(0, 1000) : content;
        
        Set<String> allTerms = new HashSet<>();
        allTerms.addAll(tokenize(originalQuery));
        allTerms.addAll(expandedQuery);
        
        String result = highlightContent;
        for (String term : allTerms) {
            String pattern = "(?i)" + Pattern.quote(term);
            result = result.replaceAll(pattern, "<mark>$0</mark>");
        }
        
        int maxLength = 200;
        if (result.length() > maxLength) {
            int markIndex = result.indexOf("<mark>");
            if (markIndex != -1) {
                int start = Math.max(0, markIndex - maxLength / 3);
                int end = Math.min(result.length(), start + maxLength);
                String snippet = result.substring(start, end);
                if (start > 0) {
                    snippet = "..." + snippet;
                }
                if (end < result.length()) {
                    snippet = snippet + "...";
                }
                return snippet;
            } else {
                return result.substring(0, maxLength) + "...";
            }
        }
        
        return result;
    }

    private String generateAnalysisSummary(String query, String questionType, List<String> matchedConcepts, 
                                          int semanticMatches, int conceptMatches) {
        StringBuilder summary = new StringBuilder();
        
        String typeDesc = QUESTION_TYPE_DESC.getOrDefault(questionType, "通用查询");
        summary.append("我已分析您的问题，这是一个「").append(typeDesc).append("」类型的问题。");
        
        if (!matchedConcepts.isEmpty()) {
            summary.append(" 识别到相关概念：").append(String.join("、", matchedConcepts)).append("。");
        }
        
        if (semanticMatches > 0) {
            summary.append(" 发现").append(semanticMatches).append("处语义匹配，");
        }
        
        if (conceptMatches > 0) {
            summary.append(conceptMatches).append("处概念匹配。");
        }
        
        return summary.toString();
    }

    private AiSearchResultDto.AiAnswer generateAiAnswer(String query, String questionType, 
                                                        List<AiSearchResultDto.ArticleMatch> matches,
                                                        List<String> matchedConcepts) {
        String answer = generateAnswerText(query, questionType, matches, matchedConcepts);
        String confidence = calculateConfidence(matches, matchedConcepts);
        String answerType = QUESTION_TYPE_DESC.getOrDefault(questionType, "通用回答");
        
        List<String> sources = new ArrayList<>();
        for (int i = 0; i < Math.min(3, matches.size()); i++) {
            sources.add(matches.get(i).getTitle());
        }
        
        return AiSearchResultDto.AiAnswer.builder()
                .answer(answer)
                .confidence(confidence)
                .answerType(answerType)
                .sources(sources)
                .build();
    }

    private String generateAnswerText(String query, String questionType, 
                                     List<AiSearchResultDto.ArticleMatch> matches,
                                     List<String> matchedConcepts) {
        String template = ANSWER_TEMPLATES.getOrDefault(questionType, ANSWER_TEMPLATES.get("general"));
        String topic = extractTopic(query);
        
        String knowledge = getKnowledgeFromBase(topic, matchedConcepts);
        
        switch (questionType) {
            case "what":
                return template.replace("{topic}", topic)
                        .replace("{definition}", knowledge.isEmpty() ? "一个重要的技术概念" : knowledge)
                        .replace("{domain}", "计算机技术")
                        .replace("{purpose}", "解决相关技术问题");
            
            case "why":
                return template.replace("{topic}", topic)
                        .replace("{reasons}", knowledge.isEmpty() ? "多个技术因素共同作用" : knowledge);
            
            case "how":
                return template.replace("{topic}", topic)
                        .replace("{steps}", knowledge.isEmpty() ? "首先了解基础知识，然后实践练习，最后总结经验" : knowledge);
            
            case "problem":
                return template.replace("{problem}", topic)
                        .replace("{solutions}", knowledge.isEmpty() ? "检查日志、分析原因、尝试解决方案" : knowledge)
                        .replace("{firstStep}", "查看错误日志");
            
            case "best":
                return template.replace("{recommendation}", topic)
                        .replace("{factors}", "性能、稳定性、社区支持");
            
            case "compare":
                return template.replace("{topic1}", topic)
                        .replace("{topic2}", "其他方案")
                        .replace("{dimensions}", "性能、易用性、扩展性");
            
            default:
                return template.replace("{topic}", topic)
                        .replace("{summary}", knowledge.isEmpty() ? "相关技术信息" : knowledge);
        }
    }

    private String extractTopic(String query) {
        query = query.replaceAll("[？?。，,]", "");
        
        String[] prefixes = {"什么是", "什么叫", "什么是", "定义", "概念", "解释", "是什么", "介绍",
                            "为什么", "为何", "原因", "理由", "原理",
                            "如何", "怎么", "怎样", "方法", "步骤", "教程", "实现",
                            "对比", "比较", "区别", "差异", "哪个好",
                            "最佳", "推荐", "最好", "首选", "建议",
                            "问题", "错误", "异常", "bug", "报错", "解决"};
        
        for (String prefix : prefixes) {
            if (query.startsWith(prefix)) {
                return query.substring(prefix.length()).trim();
            }
        }
        
        return query.trim();
    }

    private String getKnowledgeFromBase(String topic, List<String> matchedConcepts) {
        String lowerTopic = topic.toLowerCase();
        
        for (Map.Entry<String, String> entry : KNOWLEDGE_BASE.entrySet()) {
            if (lowerTopic.contains(entry.getKey()) || entry.getKey().length() >= 2 && lowerTopic.length() >= 2) {
                if (lowerTopic.contains(entry.getKey().substring(0, Math.min(2, entry.getKey().length())))) {
                    return entry.getValue();
                }
            }
        }
        
        for (String concept : matchedConcepts) {
            String lowerConcept = concept.toLowerCase();
            for (Map.Entry<String, String> entry : KNOWLEDGE_BASE.entrySet()) {
                if (lowerConcept.contains(entry.getKey()) || entry.getKey().contains(lowerConcept)) {
                    return entry.getValue();
                }
            }
        }
        
        return "";
    }

    private String calculateConfidence(List<AiSearchResultDto.ArticleMatch> matches, List<String> matchedConcepts) {
        int score = 0;
        
        if (!matches.isEmpty()) {
            score += Math.min(matches.size() * 10, 50);
            
            double avgScore = matches.stream()
                    .mapToDouble(AiSearchResultDto.ArticleMatch::getRelevanceScore)
                    .average()
                    .orElse(0);
            score += (int) (avgScore * 30);
        }
        
        if (!matchedConcepts.isEmpty()) {
            score += Math.min(matchedConcepts.size() * 10, 20);
        }
        
        score = Math.min(score, 95);
        
        if (score >= 80) return "高";
        if (score >= 60) return "中";
        return "低";
    }
}