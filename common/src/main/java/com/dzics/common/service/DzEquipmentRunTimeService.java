package com.dzics.common.service;

import com.dzics.common.model.entity.DzEquipmentRunTime;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * <p>
 * 稼动记录 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-03-10
 */
public interface DzEquipmentRunTimeService extends IService<DzEquipmentRunTime> {

    BigDecimal getDayRunTime(String orderNo, String lineNo, String equipmentNo, Integer equipmentType, LocalDate nowDay);


    Long getRunTimeAll(String equipmentNo, Integer equipmentType);

    Long getRunTimeIsRestNnull(String equipmentNo, Integer equipmentType);

    Long getDayRunTime(String equipmentNo, Integer equipmentType, LocalDate startTime, LocalDate endTime);

    Long getDayRunTimeIsRestNnull(String equipmentNo, Integer equipmentType, LocalDate startTime, LocalDate endTime);
}
