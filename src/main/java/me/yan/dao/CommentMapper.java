package me.yan.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.yan.pojo.CommentDomain;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommentMapper extends BaseMapper<CommentDomain> {
    /**
     * 传入文章aid列表，返回 Map<aid, 评论数>
     */
    @MapKey("article_id")
    List<Map<String, Long>> batchCountByAids(@Param("aidList") List<Integer> aidList);
}
