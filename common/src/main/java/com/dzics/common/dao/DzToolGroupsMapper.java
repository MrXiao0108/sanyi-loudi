package com.dzics.common.dao;

import com.dzics.common.model.entity.DzToolGroups;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 刀具组表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
@Mapper
public interface DzToolGroupsMapper extends BaseMapper<DzToolGroups> {

    List<DzToolGroups> getToolGroupsList(@Param("field") String field, @Param("type") String type, @Param("orgCode") String orgCode, @Param("groupNo") String groupNo);
}
