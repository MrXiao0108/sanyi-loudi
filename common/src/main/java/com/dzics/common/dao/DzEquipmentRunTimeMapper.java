package com.dzics.common.dao;

import com.dzics.common.model.entity.DzEquipmentRunTime;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 稼动记录 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-03-10
 */
@Mapper
@Repository
public interface DzEquipmentRunTimeMapper extends BaseMapper<DzEquipmentRunTime> {

    Long getDayRunTime(@Param("orderNo") String orderNo, @Param("lineNo") String lineNo, @Param("equipmentNo") String equipmentNo, @Param("equipmentType") Integer equipmentType, @Param("nowDay") LocalDate nowDay);

    Long getDayRunTimeIsRestNnull(@Param("orderNo") String orderNo, @Param("lineNo") String lineNo, @Param("equipmentNo") String equipmentNo, @Param("equipmentType") Integer equipmentType, @Param("nowDay") LocalDate nowDay);

    Long getRunTimeAll(@Param("equipmentNo") String equipmentNo, @Param("equipmentType") Integer equipmentType);

    Long getRunTimeIsRestNnull(@Param("equipmentNo") String equipmentNo, @Param("equipmentType") Integer equipmentType);

    Long getDayRunTimeSum(@Param("equipmentNo") String equipmentNo, @Param("equipmentType") Integer equipmentType,
                          @Param("startTime") LocalDate startTime, @Param("endTime") LocalDate endTime);

    Long getDayRunTimeSumOrderLine(@Param("orderNo") String orderNo, @Param("lineNo") String lineNo,
                                   @Param("equipmentNo") String equipmentNo, @Param("equipmentType") Integer equipmentType,
                                   @Param("startTime") LocalDate startTime, @Param("endTime") LocalDate endTime);

    Long getDayRunTimeIsRestNnullSum(@Param("equipmentNo") String equipmentNo, @Param("equipmentType") Integer equipmentType,
                                     @Param("startTime") LocalDate startTime, @Param("endTime") LocalDate endTime);

    List<Map<String,Object>> getRunTime(@Param("lineNo") String lineNo, @Param("orderNo") String orderNo, @Param("equipmentNo") String equipmentNo, @Param("equipmentType") Integer equipmentType, @Param("nowDate") LocalDate nowDate);

    Long getDayRunTimeIsRestNnullSumOderNo(@Param("orderNo") String orderNo, @Param("lineNo") String lineNo,
                                           @Param("equipmentNo") String equipmentNo, @Param("equipmentType") Integer equipmentType,
                                           @Param("startTime") LocalDate startTime, @Param("endTime") LocalDate endTime);

}
