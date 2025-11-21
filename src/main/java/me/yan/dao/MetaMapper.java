package me.yan.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.yan.pojo.MetaDomain;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MetaMapper extends BaseMapper<MetaDomain> {
}
