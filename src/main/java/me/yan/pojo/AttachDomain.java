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
@TableName("attach")
public class AttachDomain {
        @TableId(type = IdType.AUTO)
        private Integer fid;
        private String fname;
        private String ftype;
        private String fkey;
        private Long create_time;
}
