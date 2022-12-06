package com.dzics.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.entity.DzDeviceAlarmConfig;

import java.util.List;

/**
 * <p>
 * 设备告警配置 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-12-30
 */
public interface DzDeviceAlarmConfigService extends IService<DzDeviceAlarmConfig> {

    List<DzDeviceAlarmConfig> listCfg(String orderId, String lineId, String deivceId, Integer alarmGrade, String equipmentNo);

}
