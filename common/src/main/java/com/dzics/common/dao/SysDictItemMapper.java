package com.dzics.common.dao;

import com.dzics.common.model.entity.SysDictItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 系统字典详情 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Mapper
@Repository
public interface SysDictItemMapper extends BaseMapper<SysDictItem> {
    String getDictTest(@Param("dictCode") String dictCode, @Param("key")String key);
    String getDictCodeAndItemText(@Param("dictCode") String dictCode, @Param("itemText")String itemText);
}
