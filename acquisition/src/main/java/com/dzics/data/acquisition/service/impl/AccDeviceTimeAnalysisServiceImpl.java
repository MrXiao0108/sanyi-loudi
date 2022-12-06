package com.dzics.data.acquisition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.model.entity.DzEquipmentTimeAnalysis;
import com.dzics.common.service.DzEquipmentTimeAnalysisService;
import com.dzics.data.acquisition.service.AccDeviceTimeAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/10/11.
 * @since
 */
@Slf4j
@Service
public class AccDeviceTimeAnalysisServiceImpl implements AccDeviceTimeAnalysisService {
    @Autowired
    private DzEquipmentTimeAnalysisService timeAnalysisService;

    @Override
    public DzEquipmentTimeAnalysis getResetTimeIsNull(Long deviceId) {
        return timeAnalysisService.getResetTimeIsNull(deviceId);

    }

    @Override
    public void saveTimeAnlysis(DzEquipmentTimeAnalysis analysis) {
        timeAnalysisService.save(analysis);
    }

    @Override
    public void updateByIdTimeAnalysis(DzEquipmentTimeAnalysis timeAnalysis) {
        timeAnalysisService.updateById(timeAnalysis);
    }

    @Override
    public List<DzEquipmentTimeAnalysis> getRestTimeIsNull(String shardingParameter) {
        QueryWrapper<DzEquipmentTimeAnalysis> wp = new QueryWrapper<>();
        wp.eq("equipment_no", shardingParameter);
        wp.isNull("reset_time");
        return timeAnalysisService.list(wp);
    }

    @Override
    public List<DzEquipmentTimeAnalysis> getRestTimeIsNullDeviceId(Long deviceId) {
        QueryWrapper<DzEquipmentTimeAnalysis> wp = new QueryWrapper<>();
        wp.eq("device_id", deviceId);
        wp.isNull("reset_time");
        return timeAnalysisService.list(wp);
    }

    @Override
    public void updateByIdList(List<DzEquipmentTimeAnalysis> analysisList) {
        timeAnalysisService.updateBatchById(analysisList);
    }

    @Override
    public void insTimeAnalysis(List<DzEquipmentTimeAnalysis> inst) {
        timeAnalysisService.saveBatch(inst);
    }

    @Override
    public Date getUpdateTimeDesc() {
        return timeAnalysisService.getUpdateTimeDesc();
    }

    @Override
    public boolean saveList(List<DzEquipmentTimeAnalysis> dzEquipmentTimeAnalyses) {
        return timeAnalysisService.saveBatch(dzEquipmentTimeAnalyses);
    }
}
