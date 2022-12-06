package com.dzics.business.service.impl;

import com.dzics.business.model.vo.alarm.AddDeviceAlarmConfig;
import com.dzics.business.model.vo.alarm.GetDeivceAlarmConfig;
import com.dzics.business.service.BusDeviceAlarmConfigService;
import com.dzics.business.service.BusinessDzProductionLineService;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.entity.DzDeviceAlarmConfig;
import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.model.entity.DzProductionLine;
import com.dzics.common.model.entity.SysUser;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.DzDeviceAlarmConfigService;
import com.dzics.common.service.DzEquipmentService;
import com.dzics.common.service.SysUserServiceDao;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/12/30.
 * @since
 */
@Service
public class BusDeviceAlarmConfigServiceImpl implements BusDeviceAlarmConfigService {
    @Autowired
    private DzDeviceAlarmConfigService alarmConfigService;
    @Autowired
    private SysUserServiceDao sysUserServiceDao;

    @Autowired
    private BusinessDzProductionLineService lineService;
    @Autowired
    private DzEquipmentService equipmentService;

    @Override
    public Result addGiveAlarmConfig(AddDeviceAlarmConfig alarmConfig, String sub) {
        DzEquipment byId = equipmentService.getById(alarmConfig.getDeviceId());
        DzProductionLine lineId = lineService.getLineId(Long.parseLong(alarmConfig.getLineId()));
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        DzDeviceAlarmConfig deviceAlarmConfig = new DzDeviceAlarmConfig();
        deviceAlarmConfig.setEquipmentNo(byId.getEquipmentNo());
        deviceAlarmConfig.setEquipmentType(byId.getEquipmentType());
        deviceAlarmConfig.setOrderId(alarmConfig.getOrderId());
        deviceAlarmConfig.setLineId(alarmConfig.getLineId());
        deviceAlarmConfig.setOrderNo(lineId.getOrderNo());
        deviceAlarmConfig.setLineNo(lineId.getLineNo());
        deviceAlarmConfig.setDeviceId(alarmConfig.getDeviceId());
        deviceAlarmConfig.setLocationData(alarmConfig.getLocationData());
        deviceAlarmConfig.setAlarmName(alarmConfig.getAlarmName());
        deviceAlarmConfig.setAlarmGrade(alarmConfig.getAlarmGrade());
        deviceAlarmConfig.setOrgCode(byUserName.getUseOrgCode());
        deviceAlarmConfig.setDelFlag(false);
        deviceAlarmConfig.setCreateBy(byUserName.getUsername());
        alarmConfigService.save(deviceAlarmConfig);
        return new Result(CustomExceptionType.OK);
    }

    @Override
    public Result putGiveAlarmConfig(AddDeviceAlarmConfig alarmConfig, String sub) {
        DzProductionLine lineId = lineService.getLineId(Long.parseLong(alarmConfig.getLineId()));
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        DzDeviceAlarmConfig deviceAlarmConfig = new DzDeviceAlarmConfig();
        deviceAlarmConfig.setAlarmConfigId(alarmConfig.getAlarmConfigId());
        deviceAlarmConfig.setOrderId(alarmConfig.getOrderId());
        deviceAlarmConfig.setLineId(alarmConfig.getLineId());
        deviceAlarmConfig.setOrderNo(lineId.getOrderNo());
        deviceAlarmConfig.setLineNo(lineId.getLineNo());
        deviceAlarmConfig.setDeviceId(alarmConfig.getDeviceId());
        deviceAlarmConfig.setLocationData(alarmConfig.getLocationData());
        deviceAlarmConfig.setAlarmName(alarmConfig.getAlarmName());
        deviceAlarmConfig.setAlarmGrade(alarmConfig.getAlarmGrade());
        deviceAlarmConfig.setOrgCode(byUserName.getUseOrgCode());
        deviceAlarmConfig.setUpdateBy(byUserName.getUsername());
        alarmConfigService.updateById(deviceAlarmConfig);
        return new Result(CustomExceptionType.OK);
    }

    @Override
    public Result getGiveAlarmConfig(GetDeivceAlarmConfig alarmConfig, String sub) {
        PageHelper.startPage(alarmConfig.getPage(), alarmConfig.getLimit());
        List<DzDeviceAlarmConfig> list = alarmConfigService.listCfg(alarmConfig.getOrderId(),alarmConfig.getLineId(),alarmConfig.getDeivceId(),alarmConfig.getAlarmGrade(),alarmConfig.getEquipmentNo());
        PageInfo<DzDeviceAlarmConfig> info = new PageInfo<>(list);
        Result ok = Result.OK(info.getList());
        ok.setCount(info.getTotal());
        return ok;
    }

    @Override
    public Result delGiveAlarmConfig(String alarmConfigId, String sub) {
        alarmConfigService.removeById(alarmConfigId);
        return new Result(CustomExceptionType.OK);
    }
}
