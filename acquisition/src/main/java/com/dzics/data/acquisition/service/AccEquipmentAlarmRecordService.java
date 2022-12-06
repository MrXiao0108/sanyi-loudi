package com.dzics.data.acquisition.service;

import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.model.entity.DzEquipmentAlarmRecord;
import com.dzics.common.model.custom.SocketUtilization;

import java.util.Date;
import java.util.List;

/**
 * 设备停止记录接口
 *
 * @author ZhangChengJun
 * Date 2021/3/9.
 * @since
 */
public interface AccEquipmentAlarmRecordService {

    /**
     * 保存设备告警记录
     * @param dzEquipmentAlarmRecord
     */
    void saveDownTimeRecord(DzEquipmentAlarmRecord dzEquipmentAlarmRecord);

    /**
     * 获取设备上次告警记录的时间
     * @param updateDowntimeRecord
     * @return
     */
    Date getLineNoEqNoTypeNoResetIsNo(DzEquipmentAlarmRecord updateDowntimeRecord);

    void updateLineNoEqNoTypeNo(DzEquipmentAlarmRecord updateDowntimeRecord);


    /**
     * 获取所有告警中的设备
     * @return
     */
    List<DzEquipmentAlarmRecord> getAlarmRecord();




}
