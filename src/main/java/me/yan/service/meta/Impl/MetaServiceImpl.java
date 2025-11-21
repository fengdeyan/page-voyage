package me.yan.service.meta.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import me.yan.dao.MetaMapper;
import me.yan.pojo.MetaDomain;
import me.yan.service.meta.MetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetaServiceImpl implements MetaService {

    @Autowired
    private MetaMapper metaMapper;

    @Override
    public List<MetaDomain> getMetasByType(String type) {
        LambdaQueryWrapper<MetaDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MetaDomain::getType, type);
        List<MetaDomain> metaDomains = metaMapper.selectList(wrapper);
        return metaDomains;
    }

    @Override
    public void saveMeta(String type, String cname, Integer mid) {
        if(mid != null) {
            metaMapper.updateById(new MetaDomain(mid, cname, type, null, 0));
            return;
        }
        MetaDomain metaDomain = new MetaDomain();
        metaDomain.setType(type);
        metaDomain.setMname(cname);
        metaMapper.insert(metaDomain);
    }

    @Override
    public void deleteMetaById(Integer mid) {
        metaMapper.deleteById(mid);
    }
}
