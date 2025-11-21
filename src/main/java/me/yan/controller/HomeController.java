package me.yan.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

import java.util.*;

@Controller
public class HomeController extends BaseController {


    @GetMapping(value = {"/","/index"})
    public String index(@RequestParam(value = "limit", defaultValue = "3") int limit, Model model) {
        //转发请求
        return this.page(1,limit,model);
    }

    @GetMapping("/blog/page/{p}")
    public String page(@PathVariable(name = "p") int page,@RequestParam(value = "limit", defaultValue = "3") int limit, Model model) {
        System.out.println("page->"+page);
        ArticleCond cond = new ArticleCond("blog","publish");
        Page<ArticleDomain> articlesByCond = articleService.getArticlesByCond(cond, page, limit);
        List<MetaDomain> categorys= metaService.getMetasByType("category");
        model.addAttribute("categorys",categorys);
        List<Long> years = articleService.listYears();
        System.out.println("years:"+years);
        model.addAttribute("years",years);
        System.out.println("categorys========"+categorys);
        model.addAttribute("articles", articlesByCond);
        return "site/index";
    }

    @GetMapping("/article/{id}")
    public String article(@PathVariable(name = "id") int id, Model model) {
        ArticleDomain articleById = articleService.getArticleById(id);
        model.addAttribute("article", articleById);
        this.updateArticleHit(id);
        return "site/article";
    }

    @PostMapping("/comment/submit")
    @ResponseBody
    public CommentResponse submitComment(@RequestBody CommentDto commentDto) {
        commentService.AddComment(commentDto);
        return CommentResponse.success("评论提交成功，正在等待审核");
    }

    @GetMapping("/comment/list")
    @ResponseBody
    public CommentResponse list(@RequestParam(value = "articleId") int articleId) {
        List<CommentDomain> commentDomains = commentService.SelectCommentsByArticleId(articleId);
        CommentResponse success = CommentResponse.success("success");
        success.setData(commentDomains);
        return success;
    }

    @GetMapping("/archives/")
    public String archives(Model model) {
        List<MetaDomain> categorys= metaService.getMetasByType("category");
        Map<Long, List<ArticleDomain>> archivesMap = new LinkedHashMap<>();
        for (Long listYear : articleService.listYears()) {
            System.out.println("listYear:"+listYear);
            List<ArticleDomain> articleDomains = articleService.listArticlesBySpecificYear(listYear);
            System.out.println("articleDomains:"+articleDomains);
            archivesMap.put(listYear, articleDomains);
            System.out.println("archivesMap:"+archivesMap);
        }
        model.addAttribute("archivesMap", archivesMap);
        model.addAttribute("categorys",categorys);
        return "site/archives";
    }

    @GetMapping("/categories/{cg}")
    public String categories(@PathVariable(name = "cg") String cg, Model model) {
        List<MetaDomain> categorys= metaService.getMetasByType("category");
        model.addAttribute("categorys",categorys);
        ArticleCond cond = new ArticleCond(cg,"publish");
        Page<ArticleDomain> articlesByCond = articleService.getArticlesByCond(cond, 1, 9999);
        List<Long> years = articleService.listYears();
        System.out.println("years:"+years);
        model.addAttribute("years",years);
        model.addAttribute("articles", articlesByCond);
        model.addAttribute("currentCategory", cg);
        return "site/category";
    }

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
    @GetMapping("/search/")
    public String search(Model model) {
        return "site/search";
    }
    @GetMapping("/search/{keyword}")
    public String search(@PathVariable(name = "keyword") String keyword, Model model) {
        List<ArticleDomain> articlesByKeyword = articleService.listArticlesByKeyword(keyword);
        model.addAttribute("articles", articlesByKeyword);
        model.addAttribute("keyword", keyword);
        return "site/search";
    }

    @GetMapping("/about/")
    public String about(Model model) {
        return "site/about";
    }

    private void updateArticleHit(int id){
        System.out.println("enter updateArticleHit"+System.currentTimeMillis());
        Integer hget = (Integer) cache.hget("article", "hits");
        int value;
        if (hget==null){
            value=0;
        }else {
            value=hget;
        }
        value=value+1;
        System.out.println("value:"+value);
        if(value>= WebConst.HITS_EXCEED){
            value-=WebConst.HITS_EXCEED;
            articleService.updateArticleHitById(id,WebConst.HITS_EXCEED);
            cache.hset("article", "hits",value,-1);
        }else {
            cache.hset("article", "hits",value,-1);
        }
    }


}
