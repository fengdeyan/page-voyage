package me.yan.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import me.yan.controller.BaseController;
import me.yan.dto.StatisticsDto;
import me.yan.pojo.ArticleDomain;
import me.yan.pojo.CommentDomain;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name="后台首页")
@Controller
@RequestMapping("/admin")
@Slf4j
public class IndexController extends BaseController {

    private static final Logger LOGGER=log;

    @Operation(summary = "进入首页")
    @GetMapping(value = {"","/index"})
    public String index(HttpServletRequest request, Model model) {
        LOGGER.info("Enter admin index method");
        List<CommentDomain> comments = siteService.getComments(5);  //获取评论
        List<ArticleDomain> articles = siteService.getNewArticles(5);
        // 2. 批量查评论数（1次 SQL）
        List<Map<String, Long>> aidToCommentsNum = commentService.batchCountByAid(articles);
        HashMap<Long, Long> finalMap = new HashMap<>();
        for (Map<String, Long> map : aidToCommentsNum) {
            Long aid = map.get("aid");
            Long commentsNum = map.get("commentsNum");
            finalMap.put(aid, commentsNum);
        }
        // 3. 给文章赋值评论数（无嵌套查询，循环仅赋值）
        for (ArticleDomain article : articles) {
            // 从 List 中提取 Map 中的值,判断aid键对应的的值是否与article.getAid()相等
            Long longNum = Long.valueOf(article.getAid().intValue());
            // 从 Map 中提取评论数（0 表示无评论）
            Long commentsNum = finalMap.getOrDefault(longNum, 0L);
            article.setComments_num(commentsNum);
        }
        StatisticsDto statistics = siteService.getStatistics(); //获取后台数据
        request.setAttribute("comments", comments);
        request.setAttribute("articles", articles);
        request.setAttribute("statistics", statistics);
        LOGGER.info("Exit admin index method");
        model.addAttribute("commons", commons);
        return "admin/index";
    }
}
