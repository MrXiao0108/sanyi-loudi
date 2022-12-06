package com.dzics.data.acquisition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.model.custom.UpdateDownTimeDate;
import com.dzics.common.model.entity.DzEquipmentDowntimeRecord;
import com.dzics.common.service.DzEquipmentDowntimeRecordService;
import com.dzics.data.acquisition.service.AccEqStopLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/1/20.
 * @since
 */
@Service
@Slf4j
public class AccEqStopLogServiceImpl implements AccEqStopLogService {
    @Autowired
    private DzEquipmentDowntimeRecordService downtimeRecordService;

    @Override
    public void saveDownTimeRecord(DzEquipmentDowntimeRecord nowDowntimeRecord) {
        nowDowntimeRecord.setCreateTime(new Date());
        downtimeRecordService.save(nowDowntimeRecord);
    }

    @Override
    public void updateLineNoEqNoTypeNo(DzEquipmentDowntimeRecord nowDowntimeRecord) {
        nowDowntimeRecord.setUpdateTime(new Date());
        QueryWrapper<DzEquipmentDowntimeRecord> wp = new QueryWrapper<>();
        wp.eq("line_no", nowDowntimeRecord.getLineNo());
        wp.eq("equipment_no", nowDowntimeRecord.getEquipmentNo());
        wp.eq("equipment_type", nowDowntimeRecord.getEquipmentType());
        wp.eq("order_no", nowDowntimeRecord.getOrderNo());
        wp.isNull("reset_time");
        downtimeRecordService.update(nowDowntimeRecord, wp);

    }

    @Override
    public UpdateDownTimeDate getLineNoEqNoTypeNoResetIsNo(DzEquipmentDowntimeRecord upT) {
        QueryWrapper<DzEquipmentDowntimeRecord> wp = new QueryWrapper<>();
        wp.eq("line_no", upT.getLineNo());
        wp.eq("equipment_no", upT.getEquipmentNo());
        wp.eq("equipment_type", upT.getEquipmentType());
        wp.eq("order_no", upT.getOrderNo());
        wp.isNull("reset_time");
        wp.select("id","stop_time");
        List<DzEquipmentDowntimeRecord> list = downtimeRecordService.list(wp);
        if (CollectionUtils.isNotEmpty(list)) {
            if (list.size() > 1) {
                log.error("停机记录冲突：line_no：{},equipment_no:{},equipment_type:{},存在多条未设置恢复运行时间的记录");
            }
            DzEquipmentDowntimeRecord downtimeRecord = list.get(0);
            UpdateDownTimeDate downTimeDate = new UpdateDownTimeDate();
            downTimeDate.setId(downtimeRecord.getId());
            downTimeDate.setUpStopTime(downtimeRecord.getStopTime());
            return downTimeDate;
        }
        return null;
    }


    @Override
    public DzEquipmentDowntimeRecord getLineNoEqNoTypeNoResetIsDzeq(String orderNo, String lineNo, String type, String eqNo) {
        QueryWrapper<DzEquipmentDowntimeRecord> wp = new QueryWrapper<>();
        wp.select("id");
        wp.eq("order_no", orderNo);
        wp.eq("line_no", lineNo);
        wp.eq("equipment_type", type);
        wp.eq("equipment_no", eqNo);
        wp.isNull("reset_time");
        List<DzEquipmentDowntimeRecord> list = downtimeRecordService.list(wp);
        if (CollectionUtils.isNotEmpty(list)) {
            if (list.size() > 1) {
                log.error("停机记录冲突：line_no：{},equipment_no:{},equipment_type:{},存在多条未设置恢复运行时间的记录");
            }
            return list.get(0);
        }
        return null;
    }
}
