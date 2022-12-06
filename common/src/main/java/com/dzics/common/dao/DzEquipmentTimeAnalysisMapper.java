package com.dzics.common.dao;

import com.dzics.common.model.entity.DzEquipmentTimeAnalysis;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.response.timeanalysis.DeviceStateDetails;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-10-11
 */
public interface DzEquipmentTimeAnalysisMapper extends BaseMapper<DzEquipmentTimeAnalysis> {

    List<DeviceStateDetails> getDeviceStateDetails(@Param("localDate") LocalDate localDate, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("deviceId") Long deviceId);

    List<DeviceStateDetails> getDeviceStateDetailsStopTime(@Param("localDate") LocalDate localDate, @Param("stopTime") Date stopTime, @Param("deviceId") Long deviceId);

    Date getUpdateTimeDesc();

    Integer updateTimeWran(@Param("localDate")String localDate);

}
