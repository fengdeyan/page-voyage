package me.yan.config;

import me.yan.service.article.ArticleService;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

@Configuration
public class KafkaStreamConfig {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ArticleService articleService;

    private static final int HITS_LIMIT = 100;   // 每100次写DB
    private static final int WINDOW_SECONDS = 5; // 5秒时间窗口

    @Bean
    public KStream<String, String> kStream(StreamsBuilder streamsBuilder) {

        KStream<String, String> stream = streamsBuilder.stream("article_visit_topic");

        // 1. 按文章ID分组
        KGroupedStream<String, String> groupedStream = stream
                .groupBy((key, articleId) -> articleId);

        // 2. 【时间窗口】5秒聚合一次（核心！）
        TimeWindowedKStream<String, String> windowedStream = groupedStream
                .windowedBy(TimeWindows.of(Duration.ofSeconds(WINDOW_SECONDS)));

        // 3. 窗口内计数
        KTable<Windowed<String>, Long> countTable = windowedStream.count();

        // 4. 聚合结果 → 每5秒写入一次Redis
        countTable.toStream().foreach((windowedKey, count) -> {
            String articleId = windowedKey.key();

            //  Redis 5秒才写入一次！
            redisTemplate.opsForValue().increment("article:hit:" + articleId, count);

            System.out.println("文章" + articleId + " 5秒内浏览：" + count);

            // 批量写数据库逻辑不变
            long totalHits = Long.parseLong(redisTemplate.opsForValue().get("article:hit:" + articleId).toString());
            if (totalHits % HITS_LIMIT == 0) {
                int id = Integer.parseInt(articleId);
                articleService.updateArticleHitById(id, HITS_LIMIT);
            }
        });

        return stream;
    }
}