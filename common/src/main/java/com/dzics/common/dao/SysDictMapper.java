package com.dzics.common.dao;

import com.dzics.common.model.entity.SysDict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.request.DictVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 系统字典表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Mapper
@Repository
public interface SysDictMapper extends BaseMapper<SysDict> {

    Integer hasDict(DictVo dictVo);
}
