package me.yan.service.site.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.yan.dao.ArticleMapper;
import me.yan.dao.CommentMapper;
import me.yan.dto.StatisticsDto;
import me.yan.pojo.ArticleDomain;
import me.yan.pojo.CommentDomain;
import me.yan.service.site.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SiteServiceImpl implements SiteService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public List<CommentDomain> getComments(int limit) {
        Page<CommentDomain> ap = new Page<>(1, limit);
        LambdaQueryWrapper<CommentDomain> alw =
                new LambdaQueryWrapper<>();
        // 按创建时间降序排序
        alw.orderByDesc(CommentDomain::getCreate_time);
        Page<CommentDomain> commentDomainPage = commentMapper.selectPage(ap, alw);
        return commentDomainPage.getRecords();
    }

    @Override
    public List<ArticleDomain> getNewArticles(int limit) {
        Page<ArticleDomain> ap = new Page<>(1, limit);
        LambdaQueryWrapper<ArticleDomain> alw =
                new LambdaQueryWrapper<>();
        // 按创建时间降序排序
        alw.orderByDesc(ArticleDomain::getCreate_time);
        Page<ArticleDomain> articleDomainPage = articleMapper.selectPage(ap, alw);
        return articleDomainPage.getRecords();
    }

    @Override
    public StatisticsDto getStatistics() {
        StatisticsDto statisticsDto = new StatisticsDto();
        statisticsDto.setArticleCount(articleMapper.selectCount(null));
        statisticsDto.setCommentCount(commentMapper.selectCount(null));
        return statisticsDto;
    }
}
