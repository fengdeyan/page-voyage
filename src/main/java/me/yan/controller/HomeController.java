package me.yan.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.yan.constant.WebConst;
import me.yan.dto.CommentDto;
import me.yan.dto.cond.ArticleCond;
import me.yan.pojo.ArticleDomain;
import me.yan.pojo.CommentDomain;
import me.yan.pojo.MetaDomain;
import me.yan.utils.CommentResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "首页接口", description = "首页相关的所有接口")
@Controller
public class HomeController extends BaseController {

    @Operation(summary = "首页")
    @GetMapping(value = {"/","/index"})
    public String index(@RequestParam(value = "limit", defaultValue = "5") int limit, Model model) {
        //转发请求
        List<ArticleDomain> hotArticleList = hotArticleService.getHotArticleList(5);
        loadCommonData(model);
        Page<ArticleDomain> hotPage = new Page<>(1, limit, hotArticleList.size());
        hotPage.setRecords(hotArticleList);
        model.addAttribute("articles", hotPage);
        return "site/index";
    }


    @Operation(summary = "分页查询文章")
    @GetMapping("/blog/page/{p}")
    public String page(@PathVariable(name = "p") int page,@RequestParam(value = "limit", defaultValue = "3") int limit, Model model) {
        ArticleCond cond = new ArticleCond("blog","publish");
        Page<ArticleDomain> articlesByCond = articleService.getArticlesByCond(cond, page, limit);
        loadCommonData( model);
        model.addAttribute("articles", articlesByCond);
        return "site/index";
    }

    @Operation(summary = "查询文章详情")
    @GetMapping("/article/{id}")
    public String article(@PathVariable(name = "id") int id, Model model) {
        this.updateArticleHit(id);
        ArticleDomain articleById = articleService.getArticleById(id);
        model.addAttribute("article", articleById);
        return "site/article";
    }

    @Operation(summary = "提交评论")
    @PostMapping("/comment/submit")
    @ResponseBody
    public CommentResponse submitComment(@RequestBody CommentDto commentDto) {
        commentService.AddComment(commentDto);
        return CommentResponse.success("评论提交成功，正在等待审核");
    }

    @Operation(summary = "查询评论列表")    
    @GetMapping("/comment/list")
    @ResponseBody
    public CommentResponse list(@RequestParam(value = "articleId") int articleId) {
        List<CommentDomain> commentDomains = commentService.SelectCommentsByArticleId(articleId);
        CommentResponse success = CommentResponse.success("success");
        success.setData(commentDomains);
        return success;
    }

    @Operation(summary = "查询归档列表")
    @GetMapping("/archives/")
    public String archives(Model model) {
        List<MetaDomain> categorys= metaService.getMetasByType("category");
        Map<Long, List<ArticleDomain>> archivesMap = new LinkedHashMap<>();
        for (Long listYear : articleService.listYears()) {
            List<ArticleDomain> articleDomains = articleService.listArticlesBySpecificYear(listYear);
            archivesMap.put(listYear, articleDomains);
        }
        model.addAttribute("archivesMap", archivesMap);
        model.addAttribute("categorys",categorys);
        return "site/archives";
    }

    @Operation(summary = "查询分类列表")
    @GetMapping("/categories/{cg}")
    public String categories(@PathVariable(name = "cg") String cg, Model model) {
        ArticleCond cond = new ArticleCond(cg,"publish");
        Page<ArticleDomain> articlesByCond = articleService.getArticlesByCond(cond, 1, 9999);
        model.addAttribute("articles", articlesByCond);
        model.addAttribute("currentCategory", cg);
        loadCommonData(model);
        return "site/category";
    }

    @Operation(summary = "查询归档列表")
    @GetMapping("/archives/{year}")
    public String archives(@PathVariable(name = "year") long year, Model model) {
        List<MetaDomain> categorys= metaService.getMetasByType("category");
        Map<Long, List<ArticleDomain>> archivesMap = new LinkedHashMap<>();
        List<ArticleDomain> articleDomains = articleService.listArticlesBySpecificYear(year);
        archivesMap.put(year, articleDomains);
        model.addAttribute("archivesMap", archivesMap);
        model.addAttribute("categorys",categorys);
        return "site/archives";
    }
    @Operation(summary = "查询搜索列表")
    @GetMapping("/search/")
    public String search(Model model) {
        return "site/search";
    }
    
    @Operation(summary = "查询搜索结果")
    @GetMapping("/search/{keyword}")
    public String search(@PathVariable(name = "keyword") String keyword, Model model) {
        List<ArticleDomain> articlesByKeyword = articleService.listArticlesByKeyword(keyword);
        model.addAttribute("articles", articlesByKeyword);
        model.addAttribute("keyword", keyword);
        return "site/search";
    }

    @Operation(summary = "查询关于列表")
    @GetMapping("/about/")
    public String about(Model model) {
        return "site/about";
    }

    private void updateArticleHit(int id){
        String key = "article:hits:counter:" + id;

        // Redis 未注入，直接降级写库
        if (redisTemplate == null) {
            articleService.updateArticleHitById(id, 1);
            return;
        }

        try {
            // 重点：用 Long 接收，防止自动拆箱 NPE
            Long count = redisTemplate.opsForValue().increment(key, 1);

            // 判空兜底
            if (count == null) {
                articleService.updateArticleHitById(id, 1);
                return;
            }

            // 达到阈值批量更新
            if (count % WebConst.HITS_EXCEED == 0) {
                articleService.updateArticleHitById(id, WebConst.HITS_EXCEED);
            }

        } catch (Exception e) {
            // Redis 挂了 / 超时 → 降级，保证浏览量不丢
            articleService.updateArticleHitById(id, 1);
        }
    }

    private void loadCommonData(Model model){
        List<MetaDomain> categorys= metaService.getMetasByType("category");
        model.addAttribute("categorys",categorys);
        List<Long> years = articleService.listYears();
        model.addAttribute("years",years);
    }



}
