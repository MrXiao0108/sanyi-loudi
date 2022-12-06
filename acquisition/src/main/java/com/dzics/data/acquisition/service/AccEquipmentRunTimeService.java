package com.dzics.data.acquisition.service;

import com.dzics.common.model.entity.DzEquipmentRunTime;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 运行时间记录接口
 *
 * @author ZhangChengJun
 * Date 2021/3/10.
 * @since
 */
public interface AccEquipmentRunTimeService {

    /**
     * 根据设备号 设备类型 获取设备正在运行记录
     *
     * @param deviceNum
     * @param deviceType
     * @return
     */
    DzEquipmentRunTime getRunTimeRecord(String orderNumber, String lineNum,String deviceNum, Integer deviceType);

    /**
     * 插入新的设备运行记录
     *
     * @param dzEquipmentRunTime
     * @return
     */
    DzEquipmentRunTime insertRunTimeRecord(DzEquipmentRunTime dzEquipmentRunTime);

    /**
     * 修改设备的运行记录
     *
     * @param dzEquipmentRunTime
     * @return
     */
    DzEquipmentRunTime updateRunTimeRecord(DzEquipmentRunTime dzEquipmentRunTime);




    Long getRunTimeAll(String equipmentNo, Integer equipmentType);

    Long getRunTimeIsRestNnull(String equipmentNo, Integer equipmentType);
}
