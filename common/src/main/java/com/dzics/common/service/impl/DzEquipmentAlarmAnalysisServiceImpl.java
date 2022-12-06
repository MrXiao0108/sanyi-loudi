package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.DzEquipmentAlarmAnalysisMapper;
import com.dzics.common.model.entity.DzEquipmentAlarmAnalysis;
import com.dzics.common.service.DzEquipmentAlarmAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-12-16
 */
@Service
@Slf4j
public class DzEquipmentAlarmAnalysisServiceImpl extends ServiceImpl<DzEquipmentAlarmAnalysisMapper, DzEquipmentAlarmAnalysis> implements DzEquipmentAlarmAnalysisService {

    @Override
    public List<DzEquipmentAlarmAnalysis> getRestTimeIsNull(String shardingParameter) {
        QueryWrapper<DzEquipmentAlarmAnalysis> wp = new QueryWrapper<>();
        wp.isNull("reset_time");
        wp.eq("equipment_no", shardingParameter);
        return list(wp);
    }

    @Override
    public List<DzEquipmentAlarmAnalysis> getRestTimeIsNullDeviceId(Long deviceId) {
        QueryWrapper<DzEquipmentAlarmAnalysis> wp = new QueryWrapper<>();
        wp.isNull("reset_time");
        wp.eq("device_id", deviceId);
        return list(wp);
    }


    @Override
    public void insTimeAnalysis(List<DzEquipmentAlarmAnalysis> inst) {
        saveBatch(inst);
    }

    @Override
    public DzEquipmentAlarmAnalysis getResetTimeIsNull(Long deviceId, String alarmName) {
        QueryWrapper<DzEquipmentAlarmAnalysis> wp = new QueryWrapper<>();
        wp.eq("device_id", deviceId);
        wp.eq("alarm_type",alarmName);
        wp.isNull("reset_time");
        DzEquipmentAlarmAnalysis alarmAnalyses = baseMapper.selectOne(wp);
        return alarmAnalyses;
    }

    @Override
    public void saveTimeAnlysis(DzEquipmentAlarmAnalysis dzEquipmentTimeAnalysis) {
        save(dzEquipmentTimeAnalysis);
    }

    @Override
    public void saveTimeAnlysis(List<DzEquipmentAlarmAnalysis> dzEquipmentTimeAnalysis) {
        saveBatch(dzEquipmentTimeAnalysis);
    }

    @Override
    public void updateByIdTimeAnalysis(DzEquipmentAlarmAnalysis alarmAnalysis) {
        updateById(alarmAnalysis);
    }


}
