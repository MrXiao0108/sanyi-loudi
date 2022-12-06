package com.dzics.common.service;

import com.dzics.common.model.entity.DzEquipmentTimeAnalysis;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.response.timeanalysis.DeviceStateDetails;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-10-11
 */
public interface DzEquipmentTimeAnalysisService extends IService<DzEquipmentTimeAnalysis> {

    DzEquipmentTimeAnalysis getResetTimeIsNull(Long deviceId);

    List<DeviceStateDetails> getDeviceStateDetails(LocalDate localDate,Date startTime, Date endTime, Long id);

    List<DeviceStateDetails> getDeviceStateDetailsStopTime(LocalDate localDate, Date date, Long id);

    Date getUpdateTimeDesc();

    /**
     * 警告！警告！警告！
     * 设备用时分析表 1s关机数据修改
     * */
    Integer updateTimeWran(String data);

}
