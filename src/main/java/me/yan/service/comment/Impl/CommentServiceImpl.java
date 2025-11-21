package me.yan.service.comment.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.yan.dao.CommentMapper;
import me.yan.dto.CommentDto;
import me.yan.dto.cond.CommentCond;
import me.yan.pojo.ArticleDomain;
import me.yan.pojo.CommentDomain;
import me.yan.service.comment.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public void AddComment(CommentDto comment) {
        CommentDomain commentDomain = new CommentDomain(comment);
        commentMapper.insert(commentDomain);
    }

    @Override
    public List<CommentDomain> SelectCommentsByArticleId(int aid) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("article_id", aid);
        map.put("status", "approved");
        return commentMapper.selectByMap(map);
    }

    @Override
    public List<Map<String, Long>> batchCountByAid(List<ArticleDomain> articles) {
        // 1. 提取所有文章的 aid 到列表
        List<Integer> aidList = articles.stream()
                .map(ArticleDomain::getAid) // 假设 Article 有 getAid() 方法
                .collect(Collectors.toList());
        // 2. 批量查询评论数（1次 SQL）
        List<Map<String, Long>> countMap = commentMapper.batchCountByAids(aidList);
        // 3. 防止 Map 为 null（返回空Map，避免 NPE）
        return countMap;
    }

     @Override
    public List<CommentDomain> SelectCommentsByCond(CommentCond cond, Integer pageNum, Integer pageSize) {
        // 1. 构建查询条件
        LambdaQueryWrapper<CommentDomain> lqw = new LambdaQueryWrapper<>();
        if (cond != null) {
            if (cond.getArticleId() != null) {
                lqw.eq(CommentDomain::getArticle_id, cond.getArticleId());
            }
        }
        lqw.orderByDesc(CommentDomain::getCreate_time);

        // 2. 分页查询
        Page<CommentDomain> page = new Page<>(pageNum, pageSize);
        commentMapper.selectPage(page, lqw);
        return page.getRecords();
    }

    @Override
    public CommentDomain SelectCommentById(int cid) {
        return commentMapper.selectById(cid);
    }

    @Override
    public void updateCommentStatus(int cid, String status) {
        CommentDomain commentDomain = commentMapper.selectById(cid);
        if (null != commentDomain){
            commentDomain.setStatus(status);
            commentMapper.updateById(commentDomain);
        }
    }

    @Override
    public void deleteComment(int cid) {
        commentMapper.deleteById(cid);
    }
}
