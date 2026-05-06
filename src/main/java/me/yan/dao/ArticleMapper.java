package me.yan.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.yan.dto.cond.ArticleCond;
import me.yan.pojo.ArticleDomain;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ArticleMapper extends BaseMapper<ArticleDomain> {
    /**
     * 根据条件查询文章
     * @param cond
     * @return
     */
    List<ArticleDomain> getArticlesByCond(ArticleCond cond);

    /**
     *查询所有年份
     * @return
     */
    List<Long> listYears();

    /**
     * 根据年份查询文章列表
     * @param year 年份
     * @return
     */
    List<Map<String,Object>> listArticlesBySpecificYear(long year);

    /**
     * 查询有效文章列表
     * @return 有效文章列表
     */
    List<ArticleDomain> selectValidArticle();
}
