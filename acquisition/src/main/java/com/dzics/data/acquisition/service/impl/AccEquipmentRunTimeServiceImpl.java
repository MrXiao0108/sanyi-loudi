package com.dzics.data.acquisition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.model.entity.DzEquipmentRunTime;
import com.dzics.common.service.DzEquipmentRunTimeService;
import com.dzics.data.acquisition.service.AccEquipmentRunTimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/3/10.
 * @since
 */
@Service
@Slf4j
public class AccEquipmentRunTimeServiceImpl implements AccEquipmentRunTimeService {
    @Autowired
    private DzEquipmentRunTimeService runTimeService;

    @Override
    public DzEquipmentRunTime getRunTimeRecord(String orderNumber, String lineNum, String deviceNum, Integer deviceType) {
        QueryWrapper<DzEquipmentRunTime> wp = new QueryWrapper<>();
        wp.eq("equipment_no", deviceNum);
        wp.eq("equipment_type", deviceType);
        wp.eq("order_no", orderNumber);
        wp.eq("line_no", lineNum);
        wp.isNull("reset_time");
        List<DzEquipmentRunTime> runTimes = runTimeService.list(wp);
        if (CollectionUtils.isNotEmpty(runTimes)) {
            if (runTimes.size() > 1) {
                log.warn("获取设备运行时间记录存在多条未恢复的运行时间记录");
            }
            return runTimes.get(0);
        }
        return null;
    }

    @Override
    public DzEquipmentRunTime insertRunTimeRecord(DzEquipmentRunTime dzEquipmentRunTime) {
        runTimeService.save(dzEquipmentRunTime);
        return dzEquipmentRunTime;
    }

    @Override
    public DzEquipmentRunTime updateRunTimeRecord(DzEquipmentRunTime dzEquipmentRunTime) {
        runTimeService.updateById(dzEquipmentRunTime);
        return dzEquipmentRunTime;
    }




    @Override
    public Long getRunTimeAll(String equipmentNo, Integer equipmentType) {
        return runTimeService.getRunTimeAll(equipmentNo, equipmentType);
    }

    @Override
    public Long getRunTimeIsRestNnull(String equipmentNo, Integer equipmentType) {
        return runTimeService.getRunTimeIsRestNnull(equipmentNo, equipmentType);
    }
}
