package me.yan.dto.cond;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章查询条件
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCond {
    /**
     * 文章类型
     */
    private String category;

    /**
     * 文章状态
     */
    private String status;
}
