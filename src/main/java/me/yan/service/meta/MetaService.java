package me.yan.service.meta;

import me.yan.pojo.MetaDomain;

import java.util.List;

public interface MetaService {



    List<MetaDomain> getMetasByType(String type);

    /**
     * 保存分类或标签
     * @param type 分类或标签
     * @param cname 分类或标签名
     * @param mid 父分类或标签编号
     */
    void saveMeta(String type,String cname,Integer mid);
     /**
      * 删除分类或标签
      * @param mid 分类或标签编号
      */
    void deleteMetaById(Integer mid);
}
