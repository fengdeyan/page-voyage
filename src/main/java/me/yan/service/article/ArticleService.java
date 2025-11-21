package me.yan.service.article;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.yan.dto.cond.ArticleCond;
import me.yan.pojo.ArticleDomain;
import me.yan.pojo.MetaDomain;

import java.util.List;
import java.util.Map;

public interface ArticleService {
    /**
     * 根据条件获取文章列表
     * @param cond
     * @param page
     * @param size
     * @return
     */
    Page<ArticleDomain> getArticlesByCond(ArticleCond cond, int page, int size);

    /**
     * 根据id查到文章
     * @param id
     * @return
     */
    ArticleDomain getArticleById(int id);

    /**
     * 根据文章id来增加文章的点击量
     * @param id
     */
    void updateArticleHitById(int id,int hits_exceed);

    List<Long> listYears();
    List<ArticleDomain> listArticlesBySpecificYear(long year);
    /**
     * 添加文章
     * @param articleDomain
     */
    void addArticle(ArticleDomain articleDomain);
    /**
     * 删除文章
     * @param id
     */
    void deleteArticleById(int id);
    /**
     * 根据分类查询说章数
     * @param category
     * @return
     */
     int countArticlesByCategory(MetaDomain category);
     /**
     * 修改文章
     * @param articleDomain
     */
    void modifyArticle(ArticleDomain articleDomain);
    /**
     * 根据关键词查询文章
     * @param keyword
     * @return
     */
    List<ArticleDomain> listArticlesByKeyword(String keyword);
}
