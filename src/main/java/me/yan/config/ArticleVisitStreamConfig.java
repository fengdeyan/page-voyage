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
        KStream<String, String> stream = streamsBuilder.stream(INPUT_TOPIC);

        KTable<Windowed<String>, Long> countTable = stream
                .groupBy((key, articleId) -> articleId, Grouped.with(Serdes.String(), Serdes.String()))
                .windowedBy(TimeWindows.of(Duration.ofSeconds(5)).grace(Duration.ofSeconds(2))) // 允许迟到2秒
                .count(Materialized.as("article-hit-5s-store"))
                .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()));  // 只在窗口关闭时输出

        countTable.toStream().foreach((windowedKey, count) -> {
            String articleId = windowedKey.key();
            stringRedisTemplate.opsForValue().increment("article:hit:" + articleId, count);
            log.info("【窗口结束】文章 {} 新增 {} 次访问", articleId, count);
        });
        return stream;
    }
}