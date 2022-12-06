package com.dzics.data.acquisition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.dao.DzEquipmentAlarmRecordMapper;
import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.model.entity.DzEquipmentAlarmRecord;
import com.dzics.common.service.DzEquipmentAlarmRecordService;
import com.dzics.common.model.custom.SocketUtilization;
import com.dzics.data.acquisition.service.AccEquipmentAlarmRecordService;
import com.dzics.data.acquisition.service.AccEquipmentRunTimeService;
import com.dzics.data.acquisition.service.AccqDzEquipmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/3/9.
 * @since
 */
@Service
@Slf4j
public class AccEquipmentAlarmRecordServiceImpl implements AccEquipmentAlarmRecordService {
    @Autowired
    private DzEquipmentAlarmRecordService alarmRecordService;
    @Autowired
    private AccqDzEquipmentService accqDzEquipmentService;


    @Autowired
    private AccEquipmentRunTimeService accEquipmentRunTimeService;

    @Override
    public void saveDownTimeRecord(DzEquipmentAlarmRecord dzEquipmentAlarmRecord) {
        alarmRecordService.save(dzEquipmentAlarmRecord);
    }


    @Override
    public Date getLineNoEqNoTypeNoResetIsNo(DzEquipmentAlarmRecord upT) {
        QueryWrapper<DzEquipmentAlarmRecord> wp = new QueryWrapper<>();
        wp.eq("line_no", upT.getLineNo());
        wp.eq("equipment_no", upT.getEquipmentNo());
        wp.eq("equipment_type", upT.getEquipmentType());
        wp.eq("order_no", upT.getOrderNo());
        wp.isNull("reset_time");
        List<DzEquipmentAlarmRecord> list = alarmRecordService.list(wp);
        if (CollectionUtils.isNotEmpty(list)) {
            if (list.size() > 1) {
                log.error("告警记录冲突：line_no：{},equipment_no:{},equipment_type:{},存在多条未设置恢复告警时间的记录");
            }
            return list.get(0).getStopTime();
        }
        return null;
    }

    @Override
    public void updateLineNoEqNoTypeNo(DzEquipmentAlarmRecord nowDowntimeRecord) {
        nowDowntimeRecord.setUpdateTime(new Date());
        QueryWrapper<DzEquipmentAlarmRecord> wp = new QueryWrapper<>();
        wp.eq("line_no", nowDowntimeRecord.getLineNo());
        wp.eq("equipment_no", nowDowntimeRecord.getEquipmentNo());
        wp.eq("equipment_type", nowDowntimeRecord.getEquipmentType());
        wp.eq("order_no", nowDowntimeRecord.getOrderNo());
        wp.isNull("reset_time");
        alarmRecordService.update(nowDowntimeRecord, wp);
    }


    @Override
    public List<DzEquipmentAlarmRecord> getAlarmRecord() {
        QueryWrapper<DzEquipmentAlarmRecord> wp = new QueryWrapper<>();
        wp.isNull("reset_time");
        List<DzEquipmentAlarmRecord> list = alarmRecordService.list(wp);
        return list;
    }



}
