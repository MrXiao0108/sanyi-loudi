package com.dzics.common.dao;

import com.dzics.common.model.custom.UpValueDeviceSignal;
import com.dzics.common.model.entity.DzEquipmentProNumDetailsSignal;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 设备生产数量详情表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-23
 */
@Mapper
public interface DzEquipmentProNumDetailsSignalMapper extends BaseMapper<DzEquipmentProNumDetailsSignal> {

    UpValueDeviceSignal getupsaveddnumlinnuty(@Param("lineNum") String lineNum, @Param("deviceNum") String deviceNum, @Param("deviceType") String deviceType, @Param("orderNumber") String orderNumber, @Param("dayId") Long dayId);

}
