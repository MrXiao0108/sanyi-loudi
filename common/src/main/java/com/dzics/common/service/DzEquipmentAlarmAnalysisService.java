package com.dzics.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.entity.DzEquipmentAlarmAnalysis;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-12-16
 */
public interface DzEquipmentAlarmAnalysisService extends IService<DzEquipmentAlarmAnalysis> {

    List<DzEquipmentAlarmAnalysis> getRestTimeIsNull(String shardingParameter);

    List<DzEquipmentAlarmAnalysis> getRestTimeIsNullDeviceId(Long deviceId);


    void insTimeAnalysis(List<DzEquipmentAlarmAnalysis> inst);

    DzEquipmentAlarmAnalysis getResetTimeIsNull(Long deviceId, String alarmName);

    void saveTimeAnlysis(DzEquipmentAlarmAnalysis dzEquipmentTimeAnalysis);
    void saveTimeAnlysis(List<DzEquipmentAlarmAnalysis> dzEquipmentTimeAnalysis);
    void updateByIdTimeAnalysis(DzEquipmentAlarmAnalysis timeAnalysis);

}
