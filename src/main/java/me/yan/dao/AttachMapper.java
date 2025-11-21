package me.yan.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.yan.pojo.AttachDomain;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
public interface AttachMapper extends BaseMapper<AttachDomain> {
}
