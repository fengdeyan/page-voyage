package me.yan.service.article.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.yan.dao.ArticleMapper;
import me.yan.dto.cond.ArticleCond;
import me.yan.pojo.ArticleDomain;
import me.yan.pojo.MetaDomain;
import me.yan.service.article.ArticleService;
import me.yan.service.web.HotArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private HotArticleService hotArticleService;

    @Override
    public Page<ArticleDomain> getArticlesByCond(ArticleCond cond, int page, int size) {
        Page<ArticleDomain> ap = new Page<>(page, size);
        LambdaQueryWrapper<ArticleDomain> alw =
                new LambdaQueryWrapper<ArticleDomain>();
        //按时间降序
        alw.orderByDesc(ArticleDomain::getCreate_time);
        if (cond != null) {
            if(cond.getCategory() != null) {
                alw.eq(ArticleDomain::getCategory, cond.getCategory());
            }
        }
        if (cond.getStatus() != null) {
            alw.eq(ArticleDomain::getStatus, cond.getStatus());
        }
        articleMapper.selectPage(ap, alw);
        return ap;
    }

    @Override
    public ArticleDomain getArticleById(int id) {
        ArticleDomain articleDomain = articleMapper.selectById(id);
        return articleDomain;
    }

    @Override
    public void updateArticleHitById(int id,int hits_exceed) {
        UpdateWrapper<ArticleDomain> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("aid", id);
        updateWrapper.setSql("hit_counts = hit_counts + "+hits_exceed);
        articleMapper.update(null, updateWrapper);
    }

    @Override
    public List<Long> listYears() {
        List<Long> years = articleMapper.listYears();
        return years;
    }

    @Override
    public List<ArticleDomain> listArticlesBySpecificYear(long year) {
        List<Map<String, Object>> maps = articleMapper.listArticlesBySpecificYear(year);
        System.out.println("maps:"+maps);
        List<ArticleDomain> articleList = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            // 第64行替换为：
            Integer id = ((Long) map.get("aid")).intValue();
            // 1. 获取字段值（key 是数据库字段名，需与 SQL 查询的字段一致）
            String title = (String) map.get("title");// 文章标题（String 类型）
            long createTime = (long) map.get("create_time"); // 创建时间（long 时间戳）
            String coverPic = (String) map.get("coverPic"); // 封面图（String 类型，可能为 null）
            ArticleDomain articleDomain = new ArticleDomain();
            articleDomain.setAid(id);
            articleDomain.setTitle(title);
            articleDomain.setCreate_time(createTime);
            articleDomain.setCoverPic(coverPic);
            articleList.add(articleDomain);
        }
        return articleList;
    }

    @Override
    public void addArticle(ArticleDomain articleDomain) {
        articleMapper.insert(articleDomain);
        hotArticleService.clearHotArticleCache();
    }

    @Override
    public void deleteArticleById(int id) {
        articleMapper.deleteById(id);
        hotArticleService.clearHotArticleCache();
    }

    @Override
    public int countArticlesByCategory(MetaDomain category) {
        LambdaQueryWrapper<ArticleDomain> alw =
                new LambdaQueryWrapper<ArticleDomain>();
        alw.eq(ArticleDomain::getCategory, category.getMname());
        return articleMapper.selectCount(alw).intValue();
    }

    @Override
    public void modifyArticle(ArticleDomain articleDomain) {
        articleMapper.updateById(articleDomain);
        hotArticleService.clearHotArticleCache();
    }

    @Override
    public List<ArticleDomain> listArticlesByKeyword(String keyword) {
        LambdaQueryWrapper<ArticleDomain> alw =
                new LambdaQueryWrapper<ArticleDomain>();
        alw.like(ArticleDomain::getTitle, keyword);
        return articleMapper.selectList(alw);
    }

}
