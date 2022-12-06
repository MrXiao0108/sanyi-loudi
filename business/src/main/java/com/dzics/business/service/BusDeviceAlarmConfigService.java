package com.dzics.business.service;

import com.dzics.business.model.vo.alarm.AddDeviceAlarmConfig;
import com.dzics.business.model.vo.alarm.GetDeivceAlarmConfig;
import com.dzics.common.model.response.Result;

/**
 * 告警配置
 *
 * @author ZhangChengJun
 * Date 2021/12/30.
 * @since
 */
public interface BusDeviceAlarmConfigService {
    Result addGiveAlarmConfig(AddDeviceAlarmConfig alarmConfig, String sub);

    Result putGiveAlarmConfig(AddDeviceAlarmConfig alarmConfig, String sub);

    Result getGiveAlarmConfig(GetDeivceAlarmConfig alarmConfig, String sub);

    Result delGiveAlarmConfig(String alarmConfigId, String sub);
}
