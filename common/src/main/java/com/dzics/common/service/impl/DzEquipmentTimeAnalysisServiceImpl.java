package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.DzEquipmentTimeAnalysisMapper;
import com.dzics.common.model.entity.DzEquipmentTimeAnalysis;
import com.dzics.common.model.response.timeanalysis.DeviceStateDetails;
import com.dzics.common.service.DzEquipmentTimeAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-10-11
 */
@Service
@Slf4j
public class DzEquipmentTimeAnalysisServiceImpl extends ServiceImpl<DzEquipmentTimeAnalysisMapper, DzEquipmentTimeAnalysis> implements DzEquipmentTimeAnalysisService {

    @Override
    public DzEquipmentTimeAnalysis getResetTimeIsNull(Long deviceId) {
        QueryWrapper<DzEquipmentTimeAnalysis> wp = new QueryWrapper<>();
        wp.eq("device_id", deviceId);
        wp.isNull("reset_time");
        List<DzEquipmentTimeAnalysis> dzEquipmentTimeAnalyses = baseMapper.selectList(wp);
        if (CollectionUtils.isNotEmpty(dzEquipmentTimeAnalyses)) {
            if (dzEquipmentTimeAnalyses.size() > 1) {
                log.warn("设备ID:{} 存在多条未设置结束时间记录: dzEquipmentTimeAnalyses: {}", deviceId, dzEquipmentTimeAnalyses);
            }
            return dzEquipmentTimeAnalyses.get(0);
        }
        return null;
    }

    @Override
    public List<DeviceStateDetails> getDeviceStateDetails(LocalDate localDate,Date startTime, Date endTime, Long id) {
        return baseMapper.getDeviceStateDetails(localDate,startTime,endTime,id);
    }

    @Override
    public List<DeviceStateDetails> getDeviceStateDetailsStopTime(LocalDate localDate, Date date, Long id) {
        return baseMapper.getDeviceStateDetailsStopTime(localDate,date,id);
    }

    @Override
    public Date getUpdateTimeDesc() {
        return baseMapper.getUpdateTimeDesc();
    }

    @Override
    public Integer updateTimeWran(String data) {
        return baseMapper.updateTimeWran(data);
    }
}
