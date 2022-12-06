package com.dzics.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.api.R;
import com.dzics.business.model.vo.DeviceParms;
import com.dzics.business.service.DataDeviceService;
import com.dzics.common.dao.DzDataDeviceMapper;
import com.dzics.common.dao.DzEquipmentMapper;
import com.dzics.common.enums.Message;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.entity.DzDataDevice;
import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.model.request.datadevice.AddDataDeviceVo;
import com.dzics.common.model.request.datadevice.GetDataDeviceVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.sany.SanyDeviceData;
import com.dzics.common.util.UnderlineTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DataDeviceServiceImpl implements DataDeviceService {
    @Autowired
    private DzDataDeviceMapper dzDataDeviceMapper;

    @Autowired
    private DzEquipmentMapper dzEquipmentMapper;

    @Override
    public Result add(AddDataDeviceVo dataDeviceVo) {
        dataDeviceVo.setDeviceKey(null);//id置空
        Long deviceId = dataDeviceVo.getDeviceId();//三一设备id
        Long equipmentId = dataDeviceVo.getEquipmentId();//大正设备id
        //判断三一设备id是否存在 (去重判断)
        List<DzDataDevice> list = dzDataDeviceMapper.selectList(new QueryWrapper<DzDataDevice>().eq("device_id", deviceId));
        if (list.size() > 0) {
            log.warn("三一设备管理-添加设备:要添加的三一设备id已经存在,deviceId:", deviceId);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_120);
        }
        //判断要绑定的大正设备是否存在(数据非法判断)
        List<DzDataDevice> eqList = dzDataDeviceMapper.selectList(new QueryWrapper<DzDataDevice>().eq("equipment_id", equipmentId));
        if (eqList.size() > 0) {
            log.warn("三一设备管理-添加设备：要添加的大正设备id已经绑定三一设备了,equipmentId:", equipmentId);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_121);
        }
        //判断是否是焊接机器人，如果是 则软件版本 ，序列号 ，焊接类型必填
        if (dataDeviceVo.getDeviceType().intValue() == 2) {
            if (StringUtils.isEmpty(dataDeviceVo.getSerNum()) || StringUtils.isEmpty(dataDeviceVo.getNcVer()) || StringUtils.isEmpty(dataDeviceVo.getSolderingType())) {
                log.error("三一设备管理-添加设备：添加的设备为焊接机器人，软件版本 ，序列号 ，焊接类型必填：{}", dataDeviceVo);
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_123);
            }
        }
        DzDataDevice dzDataDevice = new DzDataDevice();
        BeanUtils.copyProperties(dataDeviceVo, dzDataDevice);
        dzDataDeviceMapper.insert(dzDataDevice);
        return Result.OK(dzDataDevice);
    }

    @Override
    public Result list(GetDataDeviceVo dataDeviceVo) {

        Long deviceId = dataDeviceVo.getDeviceId();
        String deviceName = dataDeviceVo.getDeviceName();
        Integer deviceType = dataDeviceVo.getDeviceType();
        String deviceTypeCode = dataDeviceVo.getDeviceTypeCode();
        String type = dataDeviceVo.getType();
        String field = dataDeviceVo.getField();
        String lineId = dataDeviceVo.getLineId();
        String orderNo = dataDeviceVo.getOrderNo();
        PageHelper.startPage(dataDeviceVo.getPage(), dataDeviceVo.getLimit());
        List<SanyDeviceData> dzDataDevices = dzDataDeviceMapper.getSanyDevice(deviceId, deviceName, deviceType, deviceTypeCode, orderNo, lineId, field, type);
        PageInfo<SanyDeviceData> info = new PageInfo<>(dzDataDevices);
        return new Result(CustomExceptionType.OK, info.getList(), info.getTotal());
    }

    @Override
    public Result update(AddDataDeviceVo dataDeviceVo) {
        DzDataDevice dzDataDevice = dzDataDeviceMapper.selectById(dataDeviceVo.getDeviceKey());
        if (dzDataDevice == null) {
            log.error("三一设备管理-编辑设备:设备id不存在:{}", dataDeviceVo.getDeviceKey());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_122);
        }
//        //判断三一设备id是否产生数据
//        List<DzDataCollection> device_id = dzDataCollectionMapper.selectList(new QueryWrapper<DzDataCollection>().eq("device_id", dzDataDevice.getDeviceId()));
//        if(device_id.size()>0){
//            log.error("三一设备管理-编辑设备:当前设备已经采集到的数据了，不允许修改");
//        }
        //判断三一设备id是否重复
        if (dataDeviceVo.getDeviceId().intValue() != dzDataDevice.getDeviceId().intValue()) {
            List<DzDataDevice> device_id1 = dzDataDeviceMapper.selectList(new QueryWrapper<DzDataDevice>().eq("device_id", dataDeviceVo.getDeviceId()));
            if (device_id1.size() > 0) {
                log.warn("三一设备管理-编辑设备:要添加的三一设备id已经存在,deviceId:", dataDeviceVo.getDeviceId());
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_120);
            }
        }

        //判断大正设备id是否重复
        if (dataDeviceVo.getEquipmentId().intValue() != dzDataDevice.getEquipmentId().intValue()) {
            List<DzDataDevice> device_id1 = dzDataDeviceMapper.selectList(new QueryWrapper<DzDataDevice>().eq("equipment_id", dataDeviceVo.getEquipmentId()));
            if (device_id1.size() > 0) {
                log.warn("三一设备管理-编辑设备:要添加的大正设备id已经绑定三一设备了,equipmentId:", dzDataDevice.getEquipmentId());
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_121);
            }
        }
        //判断是否是焊接机器人，如果是 则软件版本 ，序列号 ，焊接类型必填
        if (dataDeviceVo.getDeviceType().intValue() == 2) {
            if (StringUtils.isEmpty(dataDeviceVo.getSerNum()) || StringUtils.isEmpty(dataDeviceVo.getNcVer()) || StringUtils.isEmpty(dataDeviceVo.getSolderingType())) {
                log.error("三一设备管理-添加设备：添加的设备为焊接机器人，软件版本 ，序列号 ，焊接类型必填：{}", dataDeviceVo);
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_123);
            }
        }
        BeanUtils.copyProperties(dataDeviceVo, dzDataDevice);
        dzDataDeviceMapper.updateById(dzDataDevice);
        return Result.OK(dzDataDevice);
    }

    @Override
    public Result del(AddDataDeviceVo dataDeviceVo) {
        int i = dzDataDeviceMapper.deleteById(dataDeviceVo.getDeviceKey());
        return Result.OK(i);
    }

    @Override
    public Result getByKey(Long deviceKey) {
        DzDataDevice dzDataDevice = dzDataDeviceMapper.selectById(deviceKey);
        return Result.OK(dzDataDevice);
    }

    @Override
    public Result getDzEquipment(DeviceParms deviceParms) {
        List<DzDataDevice> dzDataDevices = dzDataDeviceMapper.selectList(new QueryWrapper<DzDataDevice>());
        List<Long> collect = dzDataDevices.stream().map(p -> p.getEquipmentId()).collect(Collectors.toList());
        QueryWrapper<DzEquipment> wrapper = new QueryWrapper<>();
        if (collect.size() > 0) {
            wrapper.notIn("id", collect);
        }
        wrapper.eq("order_no", deviceParms.getOrderNo());
        wrapper.eq("line_no", deviceParms.getLineNo());
        List<DzEquipment> dzEquipments = dzEquipmentMapper.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(dzEquipments)) {
            List<Map<String, String>> list = new ArrayList<>();
            for (DzEquipment dzEquipment : dzEquipments) {
                Map<String, String> map = new HashMap<>();
                map.put("equipmentId", dzEquipment.getId().toString());
                map.put("equipmentName", dzEquipment.getEquipmentName());
                list.add(map);
            }
            return Result.ok(list);
        }
        return Result.ok(dzEquipments);
    }

    @Override
    public Result getByKeyDeviceAll(DeviceParms deviceParms) {
        QueryWrapper<DzEquipment> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", deviceParms.getOrderNo());
        wrapper.eq("line_no", deviceParms.getLineNo());
        List<DzEquipment> dzEquipments = dzEquipmentMapper.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(dzEquipments)) {
            List<Map<String, String>> list = new ArrayList<>();
            for (DzEquipment dzEquipment : dzEquipments) {
                Map<String, String> map = new HashMap<>();
                map.put("equipmentId", dzEquipment.getId().toString());
                map.put("equipmentName", dzEquipment.getEquipmentName());
                list.add(map);
            }
            return Result.ok(list);
        }
        return Result.ok(dzEquipments);
    }

    @Override
    public Integer getById(Long deviceKey) {
        DzDataDevice dzDataDevice = dzDataDeviceMapper.selectById(deviceKey);
        return dzDataDevice.getDeviceType();
    }
}
