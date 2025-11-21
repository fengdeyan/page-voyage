package me.yan.service.comment;

import me.yan.dao.CommentMapper;
import me.yan.dto.CommentDto;
import me.yan.dto.cond.CommentCond;
import me.yan.pojo.ArticleDomain;
import me.yan.pojo.CommentDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public interface CommentService {
    void AddComment(CommentDto comment);
    List<CommentDomain> SelectCommentsByArticleId(int aid);
    /**
     * 传入文章列表，返回 Map<aid, 评论数>
     */
     List<Map<String, Long>> batchCountByAid(List<ArticleDomain> articles);
     /**
      * 根据条件，返回评论列表,页数和大小可以不传入，默认第一页，每页100条
      */
     List<CommentDomain> SelectCommentsByCond(CommentCond cond, Integer pageNum,Integer pageSize);
     /**
      * 根据评论id查询评论
      */
     CommentDomain SelectCommentById(int cid);
     /**
      * 根据评论id更新评论
      */
     void updateCommentStatus(int cid, String status);
     /**
      * 根据评论id删除评论
      */
     void deleteComment(int cid);
}
