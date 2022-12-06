package com.dzics.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.business.service.BusTimeAnalysisService;
import com.dzics.common.model.entity.DzEquipmentTimeAnalysis;
import com.dzics.common.model.response.timeanalysis.DeviceStateDetails;
import com.dzics.common.service.DzEquipmentTimeAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/10/13.
 * @since
 */
@Slf4j
@Service
public class BusTimeAnalysisServiceImpl implements BusTimeAnalysisService {
    @Autowired
    private DzEquipmentTimeAnalysisService timeAnalysisService;

    @Override
    public List<DeviceStateDetails> getDeviceStateDetails(LocalDate localDate,Date startTime, Date endTime, Long id) {
        return timeAnalysisService.getDeviceStateDetails(localDate,startTime,endTime, id);
    }

    @Override
    public List<DeviceStateDetails> getGeDeviceStateDetailsStopTime(LocalDate localDate, Date date, Long id) {
        return timeAnalysisService.getDeviceStateDetailsStopTime(localDate,date, id);
    }

    @Override
    public Long getEquipmentAvailable(Long id) {
        Long runTime=0L;
        QueryWrapper<DzEquipmentTimeAnalysis>wrapper=new QueryWrapper();
        wrapper.eq("device_id",id);
        wrapper.eq("stop_data",LocalDate.now());
        wrapper.eq("work_state",1);
        wrapper.select("work_state","duration","stop_time","reset_time");
        List<DzEquipmentTimeAnalysis> list = timeAnalysisService.list(wrapper);
        if(CollectionUtils.isNotEmpty(list)){
            for (DzEquipmentTimeAnalysis dzEquipmentTimeAnalysis:list) {
                runTime+=dzEquipmentTimeAnalysis.getDuration();
                if(dzEquipmentTimeAnalysis.getResetTime()==null){
                    long time=System.currentTimeMillis()- dzEquipmentTimeAnalysis.getStopTime().getTime();
                    if(time>0){
                        runTime+=time;
                    }
                }
            }
        }
        return runTime;
    }
}
