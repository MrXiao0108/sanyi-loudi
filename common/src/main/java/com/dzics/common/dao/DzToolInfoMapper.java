package com.dzics.common.dao;

import com.dzics.common.model.entity.DzToolInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 刀具表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
@Mapper
public interface DzToolInfoMapper extends BaseMapper<DzToolInfo> {

    /**
     * 根据设备id和刀具组编号  查询未绑定该设备的刀具
     * @param equipmentId
     * @param groupNo
     * @return
     */
    List<DzToolInfo> getToolByEqIdAndGroupNo(@Param("equipmentId") Long equipmentId,
                                             @Param("groupNo") Integer groupNo,
                                             @Param("toolGroupsId")Long toolGroupsId);
}
