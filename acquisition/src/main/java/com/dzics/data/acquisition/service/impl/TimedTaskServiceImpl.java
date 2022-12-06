package com.dzics.data.acquisition.service.impl;
import java.time.LocalDate;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.dao.DzEquipmentAlarmRecordMapper;
import com.dzics.common.dao.DzEquipmentDowntimeRecordMapper;
import com.dzics.common.dao.DzEquipmentRunTimeMapper;
import com.dzics.common.model.entity.DzEquipmentAlarmRecord;
import com.dzics.common.model.entity.DzEquipmentDowntimeRecord;
import com.dzics.common.model.entity.DzEquipmentRunTime;
import com.dzics.data.acquisition.service.TimedTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.ParseException;
import java.util.Date;
import java.util.List;


@Slf4j
@Service
public class TimedTaskServiceImpl implements TimedTaskService {

    @Autowired
    DzEquipmentRunTimeMapper dzEquipmentRunTimeMapper;
    @Autowired
    DzEquipmentDowntimeRecordMapper dzEquipmentDowntimeRecordMapper;
    @Autowired
    DzEquipmentAlarmRecordMapper dzEquipmentAlarmRecordMapper;
    /**
     * 设备运时间 跨天截取
     */
    @Override
    @Transactional
    public void putEquipmentRunTime(Date nowDate){

        //查询所有 设备开始运行时间在当天之前的 且设备还未停机的记录，
        QueryWrapper<DzEquipmentRunTime> wrapper=new QueryWrapper<>();
        wrapper.isNull("reset_time");
        wrapper.lt("stop_time",nowDate);
        List<DzEquipmentRunTime> list=dzEquipmentRunTimeMapper.selectList(wrapper);
        for (DzEquipmentRunTime dzEquipmentRunTime:list) {
            long duration = nowDate.getTime() - dzEquipmentRunTime.getStopTime().getTime();
            dzEquipmentRunTime.setResetTime(nowDate);//设停机时间默认给到 当前0点
            dzEquipmentRunTime.setDuration(duration);//设备运行时间
            dzEquipmentRunTimeMapper.updateById(dzEquipmentRunTime);//0点以前的运行记录
            DzEquipmentRunTime runTime=new DzEquipmentRunTime();
            runTime.setOrderNo(dzEquipmentRunTime.getOrderNo());
            runTime.setLineNo(dzEquipmentRunTime.getLineNo());
            runTime.setEquipmentNo(dzEquipmentRunTime.getEquipmentNo());
            runTime.setEquipmentType(dzEquipmentRunTime.getEquipmentType());
            runTime.setStopTime(nowDate);
            runTime.setResetTime(null);//设备停止时间
            runTime.setStopData(LocalDate.now());
            runTime.setOrgCode(dzEquipmentRunTime.getOrgCode());
            runTime.setDelFlag(false);
            dzEquipmentRunTimeMapper.insert(runTime);//0点以后的运行记录
        }
    }

    @Override
    @Transactional
    public void putEquipmentDownTime(Date nowDate) {
        //查询所有 设备开始停机时间在当天之前的 且设备还未运行的记录，
        QueryWrapper<DzEquipmentDowntimeRecord>wrapper=new QueryWrapper<>();
        wrapper.isNull("reset_time");
        wrapper.lt("stop_time",nowDate);
        List<DzEquipmentDowntimeRecord> list=dzEquipmentDowntimeRecordMapper.selectList(wrapper);
        for (DzEquipmentDowntimeRecord record:list) {
            long duration = nowDate.getTime() - record.getStopTime().getTime();
            record.setResetTime(nowDate);//设备重启运行时间默认给到 当前0点
            record.setDuration(duration);//设备停机时间
            dzEquipmentDowntimeRecordMapper.updateById(record);//0点以前的停机记录
            DzEquipmentDowntimeRecord runTime=new DzEquipmentDowntimeRecord();
            runTime.setOrderNo(record.getOrderNo());
            runTime.setLineNo(record.getLineNo());
            runTime.setEquipmentNo(record.getEquipmentNo());
            runTime.setEquipmentType(record.getEquipmentType());
            runTime.setStopTime(nowDate);
            runTime.setResetTime(null);//设备重启时间
            runTime.setStopData(LocalDate.now());
            runTime.setOrgCode(record.getOrgCode());
            runTime.setDelFlag(false);
            dzEquipmentDowntimeRecordMapper.insert(runTime);//0点以后的停机记录
        }
    }

    @Override
    public void putEquipmentAlarmTime(Date nowDate) {
        //查询所有 设备开始告警时间在当天之前的 且设备告警还未消失的记录，
        QueryWrapper<DzEquipmentAlarmRecord>wrapper=new QueryWrapper<>();
        wrapper.isNull("reset_time");
        wrapper.lt("stop_time",nowDate);
        List<DzEquipmentAlarmRecord> list=dzEquipmentAlarmRecordMapper.selectList(wrapper);
        for (DzEquipmentAlarmRecord record:list) {
            long duration = nowDate.getTime() - record.getStopTime().getTime();
            record.setResetTime(nowDate);//设备开始告警时间默认给到 当前0点
            record.setDuration(duration);//设备告警消失时间
            dzEquipmentAlarmRecordMapper.updateById(record);//0点以前的告警记录
            DzEquipmentAlarmRecord runTime=new DzEquipmentAlarmRecord();
            runTime.setOrderNo(record.getOrderNo());
            runTime.setLineNo(record.getLineNo());
            runTime.setEquipmentNo(record.getEquipmentNo());
            runTime.setEquipmentType(record.getEquipmentType());
            runTime.setStopTime(nowDate);
            runTime.setResetTime(null);//设备告警消失时间
            runTime.setStopData(LocalDate.now());
            runTime.setOrgCode(record.getOrgCode());
            runTime.setDelFlag(false);
            dzEquipmentAlarmRecordMapper.insert(runTime);//0点以后的告警记录
        }
    }


}
