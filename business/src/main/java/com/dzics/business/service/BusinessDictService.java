package com.dzics.business.service;

import com.dzics.common.model.entity.SysDict;
import com.dzics.common.model.request.DictVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.PageLimit;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

public interface BusinessDictService {

    //新增字典类型
    Result addDict(String sub, DictVo dictVo);
    //删除字典类型
    Result delDict(String sub, Integer id);
    //修改字典类型
    Result updDict(String sub, DictVo dictVo);


    //根据id查询字典类型
    Result selectDictById(Integer id);
    //查询数据字典list
    Result listDict(PageLimit pageLimit, String dictName, String dictCode, String description);
}
