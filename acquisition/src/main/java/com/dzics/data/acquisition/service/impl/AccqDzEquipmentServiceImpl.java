package com.dzics.data.acquisition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.enums.IndexEnumDataType;
import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.model.response.JCEquiment;
import com.dzics.common.service.DzEquipmentService;
import com.dzics.data.acquisition.model.index.IndexBaseType;
import com.dzics.data.acquisition.model.index.IndexDeviceState;
import com.dzics.data.acquisition.model.index.IndexDowmSum;
import com.dzics.data.acquisition.service.AccqDzEquipmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/1/20.
 * @since
 */
@Service
@Slf4j
public class AccqDzEquipmentServiceImpl implements AccqDzEquipmentService {
    @Autowired
    private DzEquipmentService equipmentService;

    @Override
    public int updateByLineNoAndEqNo(DzEquipment dzEquipment) {
        return equipmentService.updateByLineNoAndEqNo(dzEquipment);
    }

    @Override
    public DzEquipment getTypeLingEqNo(String eqNo, String lineNoe, String type, String orderNumber) {
        QueryWrapper<DzEquipment> wp = new QueryWrapper<>();
        wp.eq("equipment_no", eqNo);
        wp.eq("line_no", lineNoe);
        wp.eq("equipment_type", type);
        wp.eq("order_no", orderNumber);
        return equipmentService.getOne(wp);
    }

    @Override
    public List<DzEquipment> list() {
        QueryWrapper<DzEquipment> wp = new QueryWrapper<>();
        wp.select("equipment_no", "equipment_type", "equipment_name", "start_run_time");
        return equipmentService.list(wp);
    }

    @Override
    public List<JCEquiment> listMap() {
        return equipmentService.listjcjqr();
    }

    @Override
    public DzEquipment getEqNoEqType(String eqNo, Integer eqType, String orderNo, String lineNo) {
        QueryWrapper<DzEquipment> wp = new QueryWrapper<>();
        wp.eq("equipment_no", eqNo);
        wp.eq("equipment_type", eqType);
        wp.eq("order_no", orderNo);
        wp.eq("line_no", lineNo);
        wp.select("equipment_no", "equipment_type", "equipment_name", "start_run_time");
        return equipmentService.getOne(wp);
    }

    @Override
    public IndexBaseType getByEquiMentId(String deviceId) {
        DzEquipment byId = equipmentService.getById(Long.valueOf(deviceId));
        IndexBaseType baseType = new IndexBaseType();
        IndexDeviceState deviceState = new IndexDeviceState();
        deviceState.setConnectState(byId.getConnectState());
        deviceState.setRunStatus(byId.getRunStatus());
        deviceState.setAlarmStatus(byId.getAlarmStatus());
        baseType.setType(IndexEnumDataType.RUNSTATUS);
        baseType.setData(deviceState);
        return baseType;
    }

    @Override
    public IndexBaseType getByEquiMentIdDown(String deviceId) {
        LocalDate localDate = LocalDate.now();
        DzEquipment byId = equipmentService.listjcjqrdeviceid(Long.valueOf(deviceId), localDate);
        IndexBaseType baseType = new IndexBaseType();
        IndexDowmSum deviceState = new IndexDowmSum();
        deviceState.setDownSum(byId.getDownSum() == null ? 0 : byId.getDownSum());
        baseType.setType(IndexEnumDataType.DOWNSTATUS);
        baseType.setData(deviceState);
        return baseType;
    }

    @Override
    public List<DzEquipment> getRunStaTimeIsNotNull() {
        return equipmentService.getRunStaTimeIsNotNull();
    }

    @Override
    public List<DzEquipment> getDeviceOrderNoLineNo(String orderNo, String lineNo) {
        return equipmentService.getDeviceOrderNoLineNo(orderNo, lineNo);
    }

    @Override
    public Integer getDeviceSignalvalue(String orderNumber, String lineNum, String deviceType, String deviceNum) {
        QueryWrapper<DzEquipment> wp = new QueryWrapper<>();
        wp.eq("order_no", orderNumber);
        wp.eq("line_no", lineNum);
        wp.eq("equipment_no", deviceNum);
        wp.eq("equipment_type", deviceType);
        wp.select("signal_value");
        DzEquipment one = equipmentService.getOne(wp);
        if (one != null && one.getSignalValue() != null) {
            return one.getSignalValue();
        }
        return 1;
    }

    @Override
    public int updateByLineNoAndEqNoDownTime(DzEquipment downEq) {
        return equipmentService.updateByLineNoAndEqNoDownTime(downEq);
    }


}
