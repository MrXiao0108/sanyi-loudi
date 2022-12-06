package com.dzics.business.service;

import com.dzics.common.model.request.GetByEquipmentNoVo;
import com.dzics.common.model.request.charts.RobotDataChartsListVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.PageLimit;

import java.time.LocalDate;

public interface BusinessEquipmentDowntimeRecordService {
    Result getByEquipmentNo(String sub, GetByEquipmentNoVo getByEquipmentNoVo, PageLimit pageLimit);

    /**
     * 设备运行率分析
     *
     * @param sub
     * @param robotDataChartsListVo
     * @return
     */
    Result operation(String sub, RobotDataChartsListVo robotDataChartsListVo);

    /**
     * 获取设备在此时间内的停止时间
     * @param equipmentNo   设备编号
     * @param equipmentType 设备类型
     * @param startTime     开始时间
     * @param endTime       结束时间
     * @return
     */
    Long getTimeDuration(String lineNo,String orderNo,String equipmentNo, Integer equipmentType, LocalDate startTime, LocalDate endTime);
}
