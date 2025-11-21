package me.yan.service.site;

import me.yan.dto.StatisticsDto;
import me.yan.pojo.ArticleDomain;
import me.yan.pojo.CommentDomain;

import java.util.List;

public interface SiteService {
    /**
     * 获取最新的评论
     * @param limit
     * @return
     */
    List<CommentDomain> getComments(int limit);
    /**
     * 获取最新的文章
     * @param limit
     * @return
     */
    List<ArticleDomain> getNewArticles(int limit);
    /**
     * 获取站点统计信息
     * @return
     */
    StatisticsDto getStatistics();
}
