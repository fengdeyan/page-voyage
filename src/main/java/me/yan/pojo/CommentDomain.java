package me.yan.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.yan.dto.CommentDto;

import java.util.Date;

@Data
@TableName("comment")
@AllArgsConstructor
@NoArgsConstructor
public class CommentDomain {
    @TableId(type = IdType.AUTO)
    private Integer cid;
    private int article_id;
    private String nickname;
    private String content;
    private Date create_time;
    private int parent_id;
    private String status;

    public CommentDomain(CommentDto commentDto) {
        // 直接给当前对象的属性赋值（this可省略，但加上更清晰）
        this.article_id = commentDto.getArticleId();
        this.nickname = commentDto.getUsername();
        this.content = commentDto.getContent();
        this.create_time = new Date(); // 初始化创建时间为当前时间
        this.parent_id = -1; // 默认为0，表示非回复评论
        this.status = "not_audit"; // 默认为未审核
    }
}
