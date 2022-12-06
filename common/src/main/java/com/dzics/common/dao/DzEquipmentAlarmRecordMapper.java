package com.dzics.common.dao;

import com.dzics.common.model.entity.DzEquipmentAlarmRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

/**
 * <p>
 * 设备报警记录 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-03-09
 */
@Mapper
@Repository
public interface DzEquipmentAlarmRecordMapper extends BaseMapper<DzEquipmentAlarmRecord> {

    /**
     * 设备当日告警时长
     *
     * @param equipmentNo
     * @param equipmentType
     * @param dayNow
     * @return
     */
    Long getTimeDurationNowDay(@Param("equipmentNo") String equipmentNo, @Param("equipmentType") Integer equipmentType, @Param("dayNow") LocalDate dayNow);

    Long getTimeDurationHistory(@Param("equipmentNo") String equipmentNo, @Param("equipmentType") Integer equipmentType);

    Long getTimeDurationNowDayResetTimeIsNull(@Param("equipmentNo") String equipmentNo, @Param("equipmentType") Integer equipmentType, @Param("dayNow") LocalDate dayNow);

    Long getTimeDurationHistoryResetTimeIsNull(@Param("equipmentNo") String equipmentNo, @Param("equipmentType") Integer equipmentType);
}
