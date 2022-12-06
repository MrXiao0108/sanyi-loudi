package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.DzEquipmentRunTimeMapper;
import com.dzics.common.model.entity.DzEquipmentRunTime;
import com.dzics.common.service.DzEquipmentRunTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * <p>
 * 稼动记录 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-03-10
 */
@Service
public class DzEquipmentRunTimeServiceImpl extends ServiceImpl<DzEquipmentRunTimeMapper, DzEquipmentRunTime> implements DzEquipmentRunTimeService {
    @Autowired
    private DzEquipmentRunTimeMapper runTimeMapper;

    @Override
    public BigDecimal getDayRunTime(String orderNo, String lineNo, String equipmentNo, Integer equipmentType, LocalDate nowDay) {
//      毫秒
        Long dayRunTime = runTimeMapper.getDayRunTime(orderNo,lineNo,equipmentNo, equipmentType, nowDay);
        if (dayRunTime == null) {
            dayRunTime = 0L;
        }
        Long dayRunTimeIsRestNnull = runTimeMapper.getDayRunTimeIsRestNnull(orderNo,lineNo,equipmentNo, equipmentType, nowDay);
        if (dayRunTimeIsRestNnull != null) {
            dayRunTime = dayRunTime + dayRunTimeIsRestNnull;
        }
//        转化为秒
        BigDecimal subtract = new BigDecimal(dayRunTime).divide(new BigDecimal(1000), 0, RoundingMode.HALF_UP);
        return subtract;
    }



    @Override
    public Long getRunTimeAll(String equipmentNo, Integer equipmentType) {
        return runTimeMapper.getRunTimeAll(equipmentNo, equipmentType);
    }

    @Override
    public Long getRunTimeIsRestNnull(String equipmentNo, Integer equipmentType) {
        return runTimeMapper.getRunTimeIsRestNnull(equipmentNo, equipmentType);
    }

    @Override
    public Long getDayRunTime(String equipmentNo, Integer equipmentType, LocalDate startTime, LocalDate endTime) {
        return runTimeMapper.getDayRunTimeSum(equipmentNo, equipmentType, startTime, endTime);
    }

    @Override
    public Long getDayRunTimeIsRestNnull(String equipmentNo, Integer equipmentType, LocalDate startTime, LocalDate endTime) {
        return runTimeMapper.getDayRunTimeIsRestNnullSum(equipmentNo, equipmentType, startTime, endTime);
    }
}
