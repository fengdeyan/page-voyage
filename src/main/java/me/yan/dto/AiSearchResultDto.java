package me.yan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiSearchResultDto {
    private List<ArticleMatch> matches;
    private long totalCount;
    private long responseTime;
    private AiAnswer aiAnswer;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArticleMatch {
        private Integer aid;
        private String title;
        private String content;
        private String category;
        private String coverPic;
        private Long create_time;
        private Double relevanceScore;
        private String highlightSnippet;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiAnswer {
        private String answer;
        private String confidence;
        private String answerType;
        private List<String> sources;
    }
}