package com.dzics.common.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.entity.DzEquipmentProNumSignal;
import com.dzics.common.model.response.HourToday;
import com.dzics.common.model.statistics.MakeQuantity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 班次生产记录表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-23
 */
@Mapper
@Repository
public interface DzEquipmentProNumSignalMapper extends BaseMapper<DzEquipmentProNumSignal> {

    List<HourToday> selectTodayByHour(@Param("tableKey") String tableKey, @Param("nowDate") String date, @Param("list") List<Long> list);

    Long getEquimentIdDayProNum(@Param("id") Long id, @Param("nowDay") LocalDate nowDay, @Param("tableKey") String tableKey);

    List<Map<String, Object>> getMonthData(@Param("tableKey") String tableKey, @Param("eqId") Long eqId);

    Long shiftProductionDetails(@Param("tableKey") String tableKey, @Param("id") Long id);

    List<Long> productionDailyReport(Long equipmentId);

    List<Map<String, Object>> getMonthDataShift(@Param("tableKey") String tableKey, @Param("eqId") Long eqId, @Param("year") Integer year);

    MakeQuantity getDeviceProNumber(@Param("tableName") String tableName, @Param("deviceId") Long deviceId, @Param("localDate") LocalDate localDate);
}
