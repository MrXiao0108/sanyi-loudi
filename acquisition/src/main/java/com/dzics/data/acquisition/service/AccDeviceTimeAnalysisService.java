package com.dzics.data.acquisition.service;

import com.dzics.common.model.entity.DzEquipmentTimeAnalysis;

import java.util.Date;
import java.util.List;

/**
 * 设备用时分析接口类
 *
 * @author ZhangChengJun
 * Date 2021/10/11.
 * @since
 */
public interface AccDeviceTimeAnalysisService {

    DzEquipmentTimeAnalysis getResetTimeIsNull(Long deviceId);

    void saveTimeAnlysis(DzEquipmentTimeAnalysis analysis);

    void updateByIdTimeAnalysis(DzEquipmentTimeAnalysis timeAnalysis);

    List<DzEquipmentTimeAnalysis> getRestTimeIsNull(String shardingParameter);

    List<DzEquipmentTimeAnalysis> getRestTimeIsNullDeviceId(Long deviceId);
    void updateByIdList(List<DzEquipmentTimeAnalysis> analysisList);

    void insTimeAnalysis(List<DzEquipmentTimeAnalysis> inst);

    Date getUpdateTimeDesc();


    boolean saveList(List<DzEquipmentTimeAnalysis> dzEquipmentTimeAnalyses);
}
