package me.yan.utils;

import me.yan.pojo.ArticleDomain;

/**
 * 仅依赖：浏览量、评论数、发布时间
 * 企业级热点文章打分工具
 */
public class HotScoreUtil {

    /**
     * 计算文章热点分数
     * @param article 文章（含viewCount、commentCount、createTime）
     * @return 热点分数，分数越高越热门
     */
    public static double calculateHotScore(ArticleDomain article) {
        // 1. 计算当前与发布时间的小时差
        long nowTime = System.currentTimeMillis();
        long createTime = article.getCreate_time();
        long hourDiff = (nowTime - createTime) / (1000 * 60 * 60);
        // 最小间隔1小时，防止刚发布除0报错
        hourDiff = Math.max(hourDiff, 1);

        // 2. 三大维度加权计算
        double viewScore = article.getHit_counts() * 0.6;
        double commentScore = article.getComments_num() * 0.3;
        double timeDecayScore = (1.0 / hourDiff) * 100 * 0.1;

        // 3. 汇总总分
        return viewScore + commentScore + timeDecayScore;
    }
}