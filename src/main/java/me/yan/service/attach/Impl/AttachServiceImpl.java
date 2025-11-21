package me.yan.service.attach.Impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.yan.dao.AttachMapper;
import me.yan.dto.cond.AttachCond;
import me.yan.pojo.ArticleDomain;
import me.yan.pojo.AttachDomain;
import me.yan.service.attach.AttachService;
import me.yan.utils.OSSUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttachServiceImpl implements AttachService {
    @Autowired
    private OSSUploadUtil ossUploadUtil;
    @Autowired
    private AttachMapper attachMapper;
    @Override
    public Integer upload(String fname, String ftype, String fkey) {
        AttachDomain attachDomain = new AttachDomain();
        attachDomain.setFname(fname);
        attachDomain.setFtype(ftype);
        attachDomain.setFkey(fkey);
        attachMapper.insert(attachDomain);
        return attachDomain.getFid();
    }

    @Override
    public List<AttachDomain> getAtts(AttachCond cond, int page, int limit) {
        Page<AttachDomain> ap = new Page<>(page, limit);
        LambdaQueryWrapper<AttachDomain> alw =
                new LambdaQueryWrapper<AttachDomain>();
        //按时间降序
        alw.orderByDesc(AttachDomain::getCreate_time);
        attachMapper.selectPage(ap, alw);
        return ap.getRecords();
    }

    @Override
    public AttachDomain getAttAchById(Integer id) {
        return attachMapper.selectById(id);
    }

    @Override
    public void deleteAttAch(Integer id) {
        //删除oss文件
        AttachDomain attachDomain = attachMapper.selectById(id);
        String fkey = attachDomain.getFkey();
        String domain = "page-voyage.oss-cn-beijing.aliyuncs.com";
        String targetPath = fkey.substring(fkey.indexOf(domain) + domain.length() + 1);
        System.out.println("targetPath====="+targetPath);
        ossUploadUtil.delete(targetPath);
        attachMapper.deleteById(id);
    }
}
