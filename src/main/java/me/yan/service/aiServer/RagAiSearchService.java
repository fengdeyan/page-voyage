package me.yan.service.aiServer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.yan.config.DashScopeConfig;
import me.yan.dto.AiSearchResultDto;
import me.yan.pojo.ArticleDomain;
import me.yan.service.article.ArticleService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RAG增强的AI检索服务
 * 基于DashScope百炼平台大模型实现真正的AI问答
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RagAiSearchService {

    private final DashScopeConfig dashScopeConfig;
    private final ArticleService articleService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String DASHSCOPE_API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

    /**
     * 系统提示词 - 严格限制AI只能基于博客内容回答
     */
    private static final String SYSTEM_PROMPT = """
            你是一个专业的博客助手，你的知识仅来源于用户提供的博客文章内容。
            
            规则：
            1. 只回答与博客内容相关的问题，如果问题与博客内容无关，请礼貌拒绝回答
            2. 必须基于提供的上下文信息回答，不得编造内容
            3. 如果上下文没有相关信息，明确告知用户"根据现有博客内容，无法回答此问题"
            4. 回答要简洁明了，引用内容时注明来源文章
            5. 使用中文回答，语气友好专业
            6. 回答长度控制在300字以内
            """;

    /**
     * 判断问题是否与博客主题相关
     */
    private boolean isRelatedToBlog(String query) {
        String[] techKeywords = {
                "java", "spring", "springboot", "数据库", "mysql", "redis", "缓存",
                "jvm", "并发", "多线程", "锁", "线程", "线程池",
                "微服务", "分布式", "架构", "设计模式", "算法",
                "linux", "docker", "k8s", "kubernetes", "devops",
                "前端", "vue", "react", "javascript", "typescript",
                "python", "go", "golang", "面试", "学习", "教程",
                "优化", "性能", "安全", "网络", "http", "https",
                "消息队列", "kafka", "rabbitmq", "rocketmq",
                "elasticsearch", "es", "搜索引擎",
                "git", "github", "gitlab", "ci/cd",
                "测试", "单元测试", "集成测试", "自动化",
                "什么是", "为什么", "如何", "怎么", "介绍", "原理"
        };
        
        String lowerQuery = query.toLowerCase();
        for (String keyword : techKeywords) {
            if (lowerQuery.contains(keyword)) {
                return true;
            }
        }
        
        return query.length() >= 3;
    }

    /**
     * 从博客文章中检索相关内容
     */
    private List<ArticleDomain> retrieveRelevantArticles(String query, int limit) {
        try {
            return articleService.searchArticles(query, limit);
        } catch (Exception e) {
            log.error("检索博客文章失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 构建RAG上下文
     */
    private String buildContext(List<ArticleDomain> articles) {
        if (articles == null || articles.isEmpty()) {
            return "暂无相关博客文章。";
        }
        
        StringBuilder context = new StringBuilder();
        context.append("以下是相关的博客文章内容：\n\n");
        
        for (int i = 0; i < articles.size(); i++) {
            ArticleDomain article = articles.get(i);
            context.append("【文章").append(i + 1).append("】");
            context.append("标题：").append(article.getTitle()).append("\n");
            
            String content = article.getContent();
            if (content != null && !content.isEmpty()) {
                content = content.replaceAll("<[^>]+>", "");
                if (content.length() > 500) {
                    content = content.substring(0, 500) + "...";
                }
                context.append("内容：").append(content).append("\n");
            }
            
            context.append("\n");
        }
        
        return context.toString();
    }

    /**
     * 使用HTTP调用百炼平台API
     */
    public AiSearchResultDto.AiAnswer generateAiAnswer(String query, List<ArticleDomain> articles) {
        try {
            if (dashScopeConfig.getApiKey() == null || dashScopeConfig.getApiKey().isEmpty()) {
                log.error("DashScope API Key未配置");
                return buildFallbackAnswer("API配置错误，请联系管理员");
            }
            
            if (!isRelatedToBlog(query)) {
                return AiSearchResultDto.AiAnswer.builder()
                        .answer("抱歉，这个问题与博客的技术主题不太相关。我只能回答与Java、Spring、数据库、架构设计等技术话题相关的问题。请尝试提问与这些主题相关的问题。")
                        .confidence("高")
                        .answerType("拒绝回答")
                        .sources(new ArrayList<>())
                        .build();
            }
            
            String context = buildContext(articles);
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", dashScopeConfig.getModel());
            
            List<Map<String, String>> messages = new ArrayList<>();
            
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", SYSTEM_PROMPT);
            messages.add(systemMessage);
            
            String userContent = String.format("""
                    %s
                    
                    用户问题：%s
                    
                    请基于上述博客内容回答用户的问题。如果无法回答，请明确说明。
                    """, context, query);
            
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", userContent);
            messages.add(userMessage);
            
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", dashScopeConfig.getMaxTokens());
            requestBody.put("temperature", dashScopeConfig.getTemperature());
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + dashScopeConfig.getApiKey());
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(
                    DASHSCOPE_API_URL, 
                    request, 
                    String.class
            );
            
            // 解析响应
            String answer = parseResponse(response.getBody());
            
            List<String> sources = new ArrayList<>();
            for (ArticleDomain article : articles) {
                sources.add(article.getTitle());
            }
            
            String confidence = articles.isEmpty() ? "低" : (articles.size() >= 3 ? "高" : "中");
            
            return AiSearchResultDto.AiAnswer.builder()
                    .answer(answer)
                    .confidence(confidence)
                    .answerType("AI智能回答")
                    .sources(sources)
                    .build();
            
        } catch (Exception e) {
            log.error("调用百炼API失败", e);
            return buildFallbackAnswer("AI服务暂时不可用，请稍后重试");
        }
    }

    /**
     * 解析API响应
     */
    private String parseResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                JsonNode message = choices.get(0).path("message");
                return message.path("content").asText("无法获取AI回答");
            }
            return "无法获取AI回答";
        } catch (Exception e) {
            log.error("解析API响应失败", e);
            return "解析AI回答时发生错误";
        }
    }

    /**
     * 构建降级回答
     */
    private AiSearchResultDto.AiAnswer buildFallbackAnswer(String message) {
        return AiSearchResultDto.AiAnswer.builder()
                .answer(message)
                .confidence("低")
                .answerType("系统提示")
                .sources(new ArrayList<>())
                .build();
    }

    /**
     * 完整的RAG搜索流程
     */
    public AiSearchResultDto searchWithRag(String query, int topN) {
        long startTime = System.currentTimeMillis();
        
        List<ArticleDomain> articles = retrieveRelevantArticles(query, topN);
        
        AiSearchResultDto.AiAnswer aiAnswer = generateAiAnswer(query, articles);
        
        List<AiSearchResultDto.ArticleMatch> matches = new ArrayList<>();
        for (int i = 0; i < articles.size(); i++) {
            ArticleDomain article = articles.get(i);
            matches.add(AiSearchResultDto.ArticleMatch.builder()
                    .aid(article.getAid())
                    .title(article.getTitle())
                    .content(article.getContent())
                    .category(article.getCategory())
                    .coverPic(article.getCoverPic())
                    .create_time(article.getCreate_time())
                    .relevanceScore(1.0 - (i * 0.1))
                    .highlightSnippet(article.getContent() != null && article.getContent().length() > 200 
                            ? article.getContent().substring(0, 200) + "..." 
                            : article.getContent())
                    .build());
        }
        
        long responseTime = System.currentTimeMillis() - startTime;
        
        AiSearchResultDto.AiAnalysis analysis = AiSearchResultDto.AiAnalysis.builder()
                .questionType("AI智能问答")
                .questionTypeDesc("基于Qwen-Max大模型的智能问答")
                .originalKeywords(List.of(query))
                .expandedKeywords(new ArrayList<>())
                .matchedConcepts(new ArrayList<>())
                .semanticMatches(articles.size())
                .conceptMatches(0)
                .analysisSummary("已使用Qwen-Max大模型进行智能问答，基于检索到的博客内容生成回答。")
                .build();
        
        return AiSearchResultDto.builder()
                .matches(matches)
                .totalCount(matches.size())
                .responseTime(responseTime)
                .analysis(analysis)
                .aiAnswer(aiAnswer)
                .build();
    }
}