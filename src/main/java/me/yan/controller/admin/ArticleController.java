package me.yan.controller.admin;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import me.yan.constant.Types;
import me.yan.controller.BaseController;
import me.yan.dto.cond.ArticleCond;
import me.yan.pojo.ArticleDomain;
import me.yan.pojo.MetaDomain;
import me.yan.utils.APIResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="文章管理")
@Controller
@RequestMapping("/admin/article")
public class ArticleController extends BaseController {
    @Operation(summary = "发布文章页")
    @GetMapping(value = "/publish")
    public String newArticle(HttpServletRequest request, Model model) {
        List<MetaDomain> metas = metaService.getMetasByType(Types.CATEGORY.getType());
        request.setAttribute("categories", metas);
        model.addAttribute("commons", commons);
        return "admin/article_edit";
    }
    @Operation(summary = "文章页")
    @GetMapping(value = "")
    public String index(
            HttpServletRequest request,
            @Parameter(name = "page", description = "页数", required = false)
            @RequestParam(name = "page", required = false, defaultValue = "1")
            int page,
            @Parameter(name = "limit", description = "每页数量", required = false)
            @RequestParam(name = "limit", required = false, defaultValue = "15")
            int limit,
            Model model
    ){
        Page<ArticleDomain> articles = articleService.getArticlesByCond(new ArticleCond(), page, limit);
        request.setAttribute("articles", articles);
        model.addAttribute("commons", commons);
        return "admin/article_list";
    }

    @Operation(summary = "发布新文章")
    @PostMapping(value = "/publish")
    @ResponseBody
    public APIResponse publishArticle(
            HttpServletRequest request,
            @Parameter(name = "title", description = "标题", required = true)
            @RequestParam(name = "title", required = true)
            String title,
            @Parameter(name = "titlePic", description = "标题图片", required = false)
            @RequestParam(name = "titlePic", required = false)
            String titlePic,
            @Parameter(name = "slug", description = "内容缩略名", required = false)
            @RequestParam(name = "slug", required = false)
            String slug,
            @Parameter(name = "content", description = "内容", required = true)
            @RequestParam(name = "content", required = true)
            String content,
            @Parameter(name = "type", description = "文章类型", required = true)
            @RequestParam(name = "type", required = true)
            String type,
            @Parameter(name = "status", description = "文章状态", required = true)
            @RequestParam(name = "status", required = true)
            String status,
            @Parameter(name = "tags", description = "标签", required = false)
            @RequestParam(name = "tags", required = false)
            String tags,
            @Parameter(name = "categories", description = "分类", required = false)
            @RequestParam(name = "categories", required = false, defaultValue = "默认分类")
            String categories,
            @Parameter(name = "allowComment", description = "是否允许评论", required = true)
            @RequestParam(name = "allowComment", required = false)
            Boolean allowComment
    ){

        System.out.println("进入/admin/publish");
        ArticleDomain articleDomain = new ArticleDomain();
        articleDomain.setTitle(title);
        articleDomain.setCoverPic(titlePic);
        articleDomain.setSlug(slug);
        articleDomain.setContent(content);
        articleDomain.setCategory(type);
        articleDomain.setStatus(status);
        articleDomain.setCreate_time(System.currentTimeMillis());
        //只允许博客文章有分类，防止作品被收入分类
        articleDomain.setCategory(categories);  //只有博客才有分类
        //添加文章
        articleService.addArticle(articleDomain);
        return APIResponse.success();
    }
    @Operation(summary = "修改文章")
    @PostMapping(value = "/modify")
    @ResponseBody
    public APIResponse modifyArticle(
            HttpServletRequest request,
            @Parameter(name = "cid", description = "文章主键", required = true)
            @RequestParam(name = "cid", required = true)
            Integer cid,
            @Parameter(name = "title", description = "标题", required = true)
            @RequestParam(name = "title", required = true)
            String title,
            @Parameter(name = "titlePic", description = "标题图片", required = false)
            @RequestParam(name = "titlePic", required = false)
            String titlePic,
            @Parameter(name = "slug", description = "内容缩略名", required = false)
            @RequestParam(name = "slug", required = false)
            String slug,
            @Parameter(name = "content", description = "内容", required = true)
            @RequestParam(name = "content", required = true)
            String content,
            @Parameter(name = "type", description = "文章类型", required = true)
            @RequestParam(name = "type", required = true)
            String type,
            @Parameter(name = "status", description = "文章状态", required = true)
            @RequestParam(name = "status", required = true)
            String status,
            @Parameter(name = "tags", description = "标签", required = false)
            @RequestParam(name = "tags", required = false)
            String tags,
            @Parameter(name = "categories", description = "分类", required = false)
            @RequestParam(name = "categories", required = false, defaultValue = "默认分类")
            String categories,
            @Parameter(name = "allowComment", description = "是否允许评论", required = true)
            @RequestParam(name = "allowComment", required = false)
            Boolean allowComment
    ){
        System.out.println("status====="+status);
        ArticleDomain articleDomain = new ArticleDomain();
        articleDomain.setAid(cid);
        articleDomain.setTitle(title);
        articleDomain.setCoverPic(titlePic);
        articleDomain.setSlug(slug);
        articleDomain.setContent(content);
        articleDomain.setCategory(type);
        articleDomain.setStatus(status);
        articleDomain.setCreate_time(System.currentTimeMillis());
        //只允许博客文章有分类，防止作品被收入分类
        articleDomain.setCategory(categories);  //只有博客才有分类
        //添加文章
        articleService.modifyArticle(articleDomain);
        return APIResponse.success();
    }
    @Operation(summary = "文章编辑")
    @GetMapping(value = "/{cid}")
    public String editArticle(
            @Parameter(name = "cid", description = "文章编号", required = true)
            @PathVariable
            Integer cid,
            HttpServletRequest request,
            Model model
    ){
        ArticleDomain content = articleService.getArticleById(cid);
        request.setAttribute("contents", content);
        List<MetaDomain> categories = metaService.getMetasByType(Types.CATEGORY.getType());
        request.setAttribute("categories", categories);
        request.setAttribute("active", "article");
        model.addAttribute("commons", commons);
        return "admin/article_edit";
    }

    @Operation(summary = "删除文章")
    @PostMapping(value = "/delete")
    @ResponseBody
    public APIResponse deleteArticle(
            @Parameter(name = "cid", description = "文章ID", required = true)
            @RequestParam(name = "cid", required = true)
            Integer cid
    ){
        articleService.deleteArticleById(cid);
        return APIResponse.success();
    }
}
