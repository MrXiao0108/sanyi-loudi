package com.dzics.business.service;


import com.dzics.common.model.entity.SysDictItem;
import com.dzics.common.model.request.DictItemVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.PageLimit;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;

/**
 * <p>
 * 系统字典详情 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
public interface BusinessDictItemService  {
    @CacheEvict(cacheNames = {"cacheService.getSystemConfigDepart","cacheService.getIndexIsShowNg"}, allEntries = true)
    Result addDictItem(String sub, DictItemVo dictItemVo);

    Result delDictItem(String sub, Integer id);
    @CacheEvict(cacheNames = {"cacheService.getSystemConfigDepart","cacheService.getIndexIsShowNg"}, allEntries = true)
    Result updateDictItem(String sub, DictItemVo dictItemVo);

    Result<SysDictItem> listDictItem(PageLimit pageLimit, Integer dictId);

    Result<SysDictItem> getDictItem(String dictCode);

    Result<SysDictItem> getItemListByCode(String dictCode);
}
