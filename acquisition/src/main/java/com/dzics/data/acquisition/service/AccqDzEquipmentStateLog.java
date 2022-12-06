package com.dzics.data.acquisition.service;

import com.dzics.common.model.entity.DzEquipmentStateLog;

/**
 * 设备运行状态日志接口
 *
 * @author ZhangChengJun
 * Date 2021/1/20.
 * @since
 */
public interface AccqDzEquipmentStateLog {
    void saveRunSatetLog(DzEquipmentStateLog stateLog);
}
