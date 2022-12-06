package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.DzDeviceAlarmConfigMapper;
import com.dzics.common.model.entity.DzDeviceAlarmConfig;
import com.dzics.common.service.DzDeviceAlarmConfigService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 设备告警配置 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-12-30
 */
@Service
public class DzDeviceAlarmConfigServiceImpl extends ServiceImpl<DzDeviceAlarmConfigMapper, DzDeviceAlarmConfig> implements DzDeviceAlarmConfigService {

    @Override
    public List<DzDeviceAlarmConfig> listCfg(String orderId, String lineId, String deivceId, Integer alarmGrade,String equipmentNo) {
        return this.baseMapper.listCfg(orderId,lineId,deivceId,alarmGrade,equipmentNo);
    }
}
