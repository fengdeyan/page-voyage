package me.yan.task;

import lombok.extern.slf4j.Slf4j;
import me.yan.dao.ArticleMapper;
import me.yan.pojo.ArticleDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 文章浏览量同步任务
 * 定时将 Redis 中的浏览量数据同步到数据库
 */
@Component
@Slf4j
public class ArticleHitSyncTask {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 每 5 分钟同步一次 Redis 浏览量到数据库
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void syncArticleHitsToDatabase() {
        Set<String> keys = stringRedisTemplate.keys("article:hit:*");
        if (keys.isEmpty()) {
            log.debug("【同步任务】没有需要同步的浏览量数据");
            return;
        }
        int successCount = 0;
        for (String key : keys) {
            try {
                String articleId = key.replace("article:hit:", "");
                String hitCountStr = stringRedisTemplate.opsForValue().get(key);

                if (hitCountStr == null) {
                    continue;
                }

                int hitCount = Integer.parseInt(hitCountStr);
                ArticleDomain article = articleMapper.selectById(Integer.parseInt(articleId));
                
                if (article != null) {
                    int newHits = article.getHit_counts() + hitCount;
                    article.setHit_counts(newHits);
                    articleMapper.updateById(article);
                    
                    // 删除 Redis 中的计数（避免重复更新）
                    stringRedisTemplate.delete(key);
                    
                    successCount++;
                    log.info("【同步浏览量】文章 ID={}, 新增浏览量={}, 总浏览量={}", 
                             articleId, hitCount, newHits);
                }
            } catch (Exception e) {
                log.error("【同步失败】文章 ID={}, 错误信息={}", key, e.getMessage());
            }
        }

        log.info("【同步完成】共处理 {} 篇文章，成功 {} 篇", keys.size(), successCount);
    }
}