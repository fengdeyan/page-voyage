package me.yan.service.attach;

import me.yan.dto.cond.AttachCond;
import me.yan.pojo.AttachDomain;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

public interface AttachService {
    /**
     * 上传文件
     * @param fname 文件名
     * @param ftype 文件类型
     * @param fkey 文件key
     * @return 文件id
     */
     Integer upload(String fname, String ftype, String fkey);
     /**
     * 获取文件列表
     * @param page 页数
     * @param limit 条数
     * @return 文件列表
     */
     List<AttachDomain> getAtts(AttachCond cond, int page, int limit);
     /**
     * 获取文件详情
     * @param id 文件id
     * @return 文件详情
     */
     AttachDomain getAttAchById(Integer id);
     /**
     * 删除文件
     * @param id 文件id
     */
     void deleteAttAch(Integer id);
}
