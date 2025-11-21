package me.yan.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("meta")
public class MetaDomain {
    @TableId(type = IdType.AUTO)
    Integer mid;
    String mname;
    String type;
    String pic;
    int count;
}
