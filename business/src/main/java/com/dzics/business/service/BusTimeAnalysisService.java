package com.dzics.business.service;

import com.dzics.common.model.response.timeanalysis.DeviceStateDetails;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/10/13.
 * @since
 */
public interface BusTimeAnalysisService {
    List<DeviceStateDetails> getDeviceStateDetails(LocalDate localDate,Date startTime, Date calendaEndAft, Long lineId);

    List<DeviceStateDetails> getGeDeviceStateDetailsStopTime(LocalDate localDate, Date date, Long id);

    /**
     * 根据设备id 查询设备今日作业时间 单位毫秒
     * @param id
     * @return
     */
    Long getEquipmentAvailable(Long id);
}
