package com.dzics.common.dao;

import com.dzics.common.model.statistics.MakeQuantity;
import com.dzics.common.model.entity.DzEquipmentProTotalSignal;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 设备总生产数量 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2022-06-22
 */
@Mapper
public interface DzEquipmentProTotalSignalMapper extends BaseMapper<DzEquipmentProTotalSignal> {

    List<MakeQuantity> sumNumber(@Param("table") String table, @Param("localData") String localData);
}
