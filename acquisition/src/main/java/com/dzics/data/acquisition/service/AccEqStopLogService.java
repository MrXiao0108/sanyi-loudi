package com.dzics.data.acquisition.service;

import com.dzics.common.model.custom.UpdateDownTimeDate;
import com.dzics.common.model.entity.DzEquipmentDowntimeRecord;

import java.util.Date;

/**
 * 设备停止记录的接口类
 *
 * @author ZhangChengJun
 * Date 2021/1/20.
 * @since
 */
public interface AccEqStopLogService {

    /**
     * @param nowDowntimeRecord 保存停机记录
     */
    void saveDownTimeRecord(DzEquipmentDowntimeRecord nowDowntimeRecord);

    /**
     * @param nowDowntimeRecord 根据停机恢复时间
     */
    void updateLineNoEqNoTypeNo(DzEquipmentDowntimeRecord nowDowntimeRecord);

    /**
     * 查询停机记录表 未生成恢复运行时间的记录
     * @param updateDowntimeRecord
     * @return 上次记录停止时间
     */
    UpdateDownTimeDate getLineNoEqNoTypeNoResetIsNo(DzEquipmentDowntimeRecord updateDowntimeRecord);

    /**
     * @param orderNo
     * @param lineNo
     * @param type
     * @param eqNo
     * @return
     */
    DzEquipmentDowntimeRecord getLineNoEqNoTypeNoResetIsDzeq(String orderNo,String lineNo,String type,String eqNo);
}
