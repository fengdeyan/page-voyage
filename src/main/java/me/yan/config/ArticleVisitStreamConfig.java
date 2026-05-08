package me.yan.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import java.time.Duration;

@Configuration
@Slf4j
@EnableKafkaStreams
public class ArticleVisitStreamConfig {
    public static final String INPUT_TOPIC = "article_visit";

    private static final Duration WINDOW_SIZE = Duration.ofSeconds(5);

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Bean
    public KStream<String, String> visitStream(StreamsBuilder streamsBuilder) {
        log.error("【5秒聚合】===== 开始构建流处理拓扑");
        KStream<String, String> stream = streamsBuilder.stream(INPUT_TOPIC);
        
        log.error("【5秒聚合】===== 正在监听 topic: {}", INPUT_TOPIC);

        KTable<Windowed<String>, Long> countTable = stream
                .groupBy((key, articleId) -> articleId, Grouped.with(Serdes.String(), Serdes.String()))
                .windowedBy(TimeWindows.of(WINDOW_SIZE).grace(Duration.ZERO))
                .count(Materialized.as("article-hit-5s-store"));

        log.error("【5秒聚合】===== 聚合逻辑已注册");

        countTable.toStream().foreach((windowedKey, count) -> {
            String articleId = windowedKey.key();
            
            log.error("【foreach触发】===== 收到数据：窗口key={}, 访问次数={}", windowedKey, count);

            stringRedisTemplate.opsForValue().increment("article:hit:" + articleId, count);

            log.error("【5秒聚合】===== 文章{} → 累加访问量：{}", articleId, count);
        });

        log.error("【5秒聚合】===== 流处理拓扑构建完成，等待数据...");
        return stream;
    }
}