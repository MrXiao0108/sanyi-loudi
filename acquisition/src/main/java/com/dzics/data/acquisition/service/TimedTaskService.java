package com.dzics.data.acquisition.service;

import java.text.ParseException;
import java.util.Date;

public interface TimedTaskService {
    /**
     * 设备运时间 跨天截取更新
     */
    void putEquipmentRunTime(Date nowDate);

    /**
     * 设备停机时间 跨天截取更新
     */
    void putEquipmentDownTime(Date nowDate);

    /**
     * 设备告警时间 跨天截取更新
     * @param nowDate
     */
    void putEquipmentAlarmTime(Date nowDate);
}
