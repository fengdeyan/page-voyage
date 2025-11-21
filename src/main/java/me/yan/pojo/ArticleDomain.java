package me.yan.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("article")
public class ArticleDomain {
    @TableId(type = IdType.AUTO)
    private Integer aid;
    private String title;
    private String content;
    private String category;
    private String coverPic;
    private int hit_counts;
    private Long create_time;
    private Long update_time;
    private String slug;
    private Long comments_num;
    private String status;
}
