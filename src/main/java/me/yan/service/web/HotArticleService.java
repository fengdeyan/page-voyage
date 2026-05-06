package me.yan.service.web;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import me.yan.constant.WebConst;
import me.yan.dao.ArticleMapper;
import me.yan.pojo.ArticleDomain;
import me.yan.utils.HotScoreUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HotArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;



    /**
     * 获取热点文章列表（Redis缓存 + 企业级热度算法）
     */
    public List<ArticleDomain> getHotArticleList(Integer hotNum) {
        // 1. 命中缓存，直接返回
        List<ArticleDomain> hotCache = (List<ArticleDomain>) redisTemplate.opsForValue().get(WebConst.HOT_ARTICLE_LIST_KEY);
        if (hotCache != null && !hotCache.isEmpty()) {
            System.out.println("热点数据"+hotCache);
            return hotCache;
        }

        // 2. 缓存未命中：查询全量/有效文章
        LambdaQueryWrapper<ArticleDomain> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ArticleDomain::getStatus, "publish");
        List<ArticleDomain> articleList = articleMapper.selectList(lqw);

        // 3. 按自定义热点分数 倒序排序，取前20条热点
        List<ArticleDomain> hotArticleList = articleList.stream()
                .sorted((a1, a2) -> Double.compare(
                        HotScoreUtil.calculateHotScore(a2),
                        HotScoreUtil.calculateHotScore(a1)
                ))
                .limit(hotNum)
                .collect(Collectors.toList());

        // 4. 写入Redis 定时过期
        redisTemplate.opsForValue()
                .set(WebConst.HOT_ARTICLE_LIST_KEY, hotArticleList,WebConst.CACHE_EXPIRE_MIN, TimeUnit.MINUTES);

        return hotArticleList;
    }

    // ========== 数据变更时 主动删除缓存 保证一致性 ==========
    // 新增/编辑文章、新增评论、浏览量增加 统一调用此方法
    public void clearHotArticleCache() {
        redisTemplate.delete(WebConst.HOT_ARTICLE_LIST_KEY);
    }
}