package me.yan.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import me.yan.dto.AiSearchResultDto;
import me.yan.service.aiServer.AiSearchService;
import me.yan.service.aiServer.RagAiSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "AI文章检索", description = "基于AI的智能文章检索功能")
@Controller
@RequestMapping("/ai-search")
@Slf4j
public class AiSearchController {

    @Autowired
    private AiSearchService aiSearchService;
    
    @Autowired
    private RagAiSearchService ragAiSearchService;

    @Operation(summary = "AI检索首页")
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "文章智能检索");
        return "site/ai-search";
    }

    @Operation(summary = "AI检索API")
    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> search(
            @Parameter(name = "query", description = "用户提问内容", required = true)
            @RequestParam("query") String query,
            @Parameter(name = "limit", description = "返回结果数量", required = false)
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        
        Map<String, Object> response = new HashMap<>();

        log.info("AI检索请求 - 输入哈哈哈: {}", query);
        if (query == null || query.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "请输入提问内容");
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }

        if (query.length() < 2) {
            response.put("success", false);
            response.put("message", "提问内容至少需要2个字符");
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // 使用RAG增强的AI检索服务
            AiSearchResultDto result = ragAiSearchService.searchWithRag(query, limit);
            
            response.put("success", true);
            response.put("message", "检索完成");
            response.put("data", result);
            
            log.info("AI检索请求 - 查询词: {}, 结果数: {}", query, result.getTotalCount());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("AI检索异常 - 查询词: {}, 错误: {}", query, e.getMessage(), e);
            response.put("success", false);
            response.put("message", "检索过程中发生错误，请稍后重试");
            response.put("data", null);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @Operation(summary = "AI检索（页面跳转方式）")
    @GetMapping("/query/{query}")
    public String searchPage(
            @Parameter(name = "query", description = "用户提问内容", required = true)
            @PathVariable("query") String query,
            Model model) {
        
        try {
            // 使用RAG增强的AI检索服务
            AiSearchResultDto result = ragAiSearchService.searchWithRag(query, 10);
            model.addAttribute("results", result.getMatches());
            model.addAttribute("query", query);
            model.addAttribute("totalCount", result.getTotalCount());
            model.addAttribute("responseTime", result.getResponseTime());
            model.addAttribute("aiAnswer", result.getAiAnswer());
            
        } catch (Exception e) {
            log.error("AI检索页面跳转异常", e);
            model.addAttribute("error", "检索过程中发生错误");
        }
        
        return "site/ai-search";
    }
}