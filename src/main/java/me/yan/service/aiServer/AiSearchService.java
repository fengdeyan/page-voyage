package me.yan.service.aiServer;

import me.yan.dto.AiSearchResultDto;

public interface AiSearchService {
    /**
     * 搜索文章
     * @param query
     * @param topN
     * @return
     */
    AiSearchResultDto searchArticles(String query, int topN);
}