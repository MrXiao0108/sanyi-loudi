package com.dzics.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.business.config.task.SysBusTask;
import com.dzics.business.service.BusinessEquipmentService;
import com.dzics.business.service.cache.CachingApi;
import com.dzics.business.service.cache.DzDetectionTemplCache;
import com.dzics.common.dao.DzEquipmentMapper;
import com.dzics.common.dao.DzEquipmentProNumMapper;
import com.dzics.common.dao.DzLineShiftDayMapper;
import com.dzics.common.dao.DzProductionLineMapper;
import com.dzics.common.enums.Message;
import com.dzics.common.enums.UserIdentityEnum;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.entity.*;
import com.dzics.common.model.constant.FinalCode;
import com.dzics.common.model.request.*;
import com.dzics.common.model.response.*;
import com.dzics.common.model.response.device.DeviceMessage;
import com.dzics.common.model.response.equipment.EquipmentAlarmDo;
import com.dzics.common.service.*;
import com.dzics.common.util.DateUtil;
import com.dzics.common.util.PageLimit;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.dzics.common.model.response.Result;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BusinessEquipmentServiceImpl implements BusinessEquipmentService {
    @Autowired
    DzEquipmentMapper equipmentMapper;
    @Autowired
    DzProductionLineMapper dzProductionLineMapper;
    @Autowired
    SysUserServiceDao sysUserServiceDao;
    @Autowired
    DzEquipmentProNumMapper dzEquipmentProNumMapper;
    @Autowired
    DzLineShiftDayMapper dzLineShiftDayMapper;
    @Autowired
    SysBusTask sysBusTask;
    @Autowired
    private DzOrderService dzOrderService;
    @Autowired
    private DzDetectionTemplCache dzDetectionTemplCache;
    @Autowired
    private CachingApi cachingApi;
    @Autowired
    private DzProductionLineService dzProductionLineService;
    @Autowired
    SysDictItemService sysDictItemService;
    @Autowired
    DzEquipmentAlarmAnalysisService dzEquipmentAlarmAnalysisService;
    @Autowired
    DzEquipmentService dzEquipmentService;

    @Override
    public Result listAlarm(String sub, PageLimit pageLimit, EquipmentAlarmVo equipmentAlarmVo) {
        List<EquipmentAlarmDo>equipmentAlarmDos=new ArrayList<>();
        QueryWrapper<DzEquipmentAlarmAnalysis>wrapper=new QueryWrapper<>();
        DzProductionLine line=null;
        if(equipmentAlarmVo.getLineId()!=null){
            line = dzProductionLineService.getById(equipmentAlarmVo.getLineId());
            if(line==null){
                log.error("??????????????????????????????????????????");
                return new Result(CustomExceptionType.OK);
            }
            wrapper.eq("order_no",line.getOrderNo());
            wrapper.eq("line_no",line.getLineNo());
        }
        if(!StringUtils.isEmpty(equipmentAlarmVo.getEquipmentNo())){
            wrapper.eq("equipment_no",equipmentAlarmVo.getEquipmentNo());
        }
        if(!StringUtils.isEmpty(equipmentAlarmVo.getStartTime())){
            wrapper.ge("stop_data",equipmentAlarmVo.getStartTime());
        }
        if(!StringUtils.isEmpty(equipmentAlarmVo.getEndTime())){
            wrapper.le("stop_data",equipmentAlarmVo.getEndTime());
        }
        if(!StringUtils.isEmpty(equipmentAlarmVo.getAlarmText())){
            QueryWrapper<SysDictItem>itemQueryWrapper=new QueryWrapper<>();
            itemQueryWrapper.eq("dict_code","alarm_type");
            itemQueryWrapper.likeLeft("item_text",equipmentAlarmVo.getAlarmText());
            List<SysDictItem> list = sysDictItemService.list(itemQueryWrapper);
            if(list.size()==0){
                return new Result(CustomExceptionType.OK);
            }
            List<String> collect = list.stream().map(p -> p.getItemValue()).collect(Collectors.toList());
            wrapper.in("item_value",collect);
        }
        wrapper.groupBy("group_id");
        wrapper.select("group_id","order_no","line_no","device_id","item_value","alarm_type");
        if(pageLimit.getPage()!=-1){
            PageHelper.startPage(pageLimit.getPage(),pageLimit.getLimit());
        }
        List<DzEquipmentAlarmAnalysis> list = dzEquipmentAlarmAnalysisService.list(wrapper);
        List<DzEquipmentAlarmAnalysis> data=new ArrayList<>();
        Long total=0L;
        if(pageLimit.getPage()!=-1){
            PageInfo<DzEquipmentAlarmAnalysis>info=new PageInfo<>(list);
            data = info.getList();
            total=info.getTotal();
        }else {
            data=list;
            total=Long.valueOf(list.size());
        }
        for (DzEquipmentAlarmAnalysis datum : data) {
            EquipmentAlarmDo equipmentAlarmDo=new EquipmentAlarmDo();
            //??????
            String lineName=line!=null?line.getLineName():null;
            if(lineName==null){
                line=dzProductionLineService.getOne(new QueryWrapper<DzProductionLine>().eq("order_no",datum.getOrderNo()).eq("line_no",datum.getLineNo()));
                lineName=line!=null?line.getLineName():null;
            }
            //??????
            DzEquipment dzEquipment = dzEquipmentService.getById(datum.getDeviceId());
            //????????????
//            SysDictItem one = sysDictItemService.getOne(new QueryWrapper<SysDictItem>().eq("dict_code", "alarm_type").eq("item_value", datum.getItemValue()));
            //????????????, ????????????, ???????????????
            QueryWrapper<DzEquipmentAlarmAnalysis> wrapper1 = new QueryWrapper<>();
            wrapper1.eq("group_id",datum.getGroupId());
            wrapper1.orderByAsc("stop_time");
            wrapper1.select("stop_time","reset_time","duration");
            List<DzEquipmentAlarmAnalysis> alarmAnalyses = dzEquipmentAlarmAnalysisService.list(wrapper1);
            long duration = alarmAnalyses.stream().mapToLong(DzEquipmentAlarmAnalysis::getDuration).sum();
            equipmentAlarmDo.setLineName(lineName);
            equipmentAlarmDo.setEquipmentNo(dzEquipment!=null?dzEquipment.getEquipmentNo():null);
            equipmentAlarmDo.setEquipmentName(dzEquipment!=null?dzEquipment.getEquipmentName():null);
            equipmentAlarmDo.setItemText(datum.getAlarmType());
            equipmentAlarmDo.setItemValue(datum.getItemValue());
            equipmentAlarmDo.setStopTime(DateUtil.getDateStrByLong(alarmAnalyses.get(0).getStopTime().getTime()));
            Date resetTime = alarmAnalyses.get(alarmAnalyses.size() - 1).getResetTime();
            equipmentAlarmDo.setResetTime(resetTime!=null?DateUtil.getDateStrByLong(resetTime.getTime()):null);
            equipmentAlarmDo.setDuration(duration);
            equipmentAlarmDos.add(equipmentAlarmDo);
        }
        return new Result(CustomExceptionType.OK,equipmentAlarmDos,total);
    }

    @Override
    public Result<EquipmentDo> add(String sub, AddEquipmentVo addEquipmentVo) {
        DzProductionLine dzProductionLine = dzProductionLineMapper.selectById(addEquipmentVo.getLineId());
        if (dzProductionLine == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_28);
        }
        DzOrder byId = dzOrderService.getById(dzProductionLine.getOrderId());
        if (byId == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_54);
        }
        //????????????
        QueryWrapper<DzEquipment> wrapper = new QueryWrapper<>();
        wrapper.eq("equipment_no", addEquipmentVo.getEquipmentNo()).
                eq("line_id", addEquipmentVo.getLineId()).
                eq("equipment_type", addEquipmentVo.getEquipmentType());
        List<DzEquipment> equipmentNo = equipmentMapper.selectList(wrapper);
        log.warn("???????????????????????????????????????:" + equipmentNo.size());
        if (equipmentNo.size() > 0) {
            log.error("?????????????????????????????????:" + addEquipmentVo.getEquipmentNo());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_25);
        }
        //????????????
        List<DzEquipment> equipmentCode = equipmentMapper.selectList(new QueryWrapper<DzEquipment>().eq("equipment_code", addEquipmentVo.getEquipmentCode()));
        if (equipmentCode.size() > 0) {
            log.info("?????????????????????????????????:" + addEquipmentVo.getEquipmentNo());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_26);
        }
        //????????????
//        List<DzEquipment> equipmentName = equipmentMapper.selectList(new QueryWrapper<DzEquipment>().eq("equipment_name", addEquipmentVo.getEquipmentName()));
//        if (equipmentName.size() > 0) {
//            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_27);
//        }
        SysUser sysUser = sysUserServiceDao.getByUserName(sub);

        DzEquipment dzEquipment = new DzEquipment();
        BeanUtils.copyProperties(addEquipmentVo, dzEquipment);
//        dzEquipment.setOrgCode(dzProductionLine.getOrgCode());
        if (dzEquipment.getEquipmentType().intValue() == 1) {
            //???????????????????????????????????? ????????????0???
            dzEquipment.setRunStatusValue(0);
        } else if (dzEquipment.getEquipmentType().intValue() == 2) {
            //??????
            dzEquipment.setRunStatusValue(1);
        } else if (dzEquipment.getEquipmentType().intValue() == 3) {
            //?????????
            dzEquipment.setRunStatusValue(2);
        }
        dzEquipment.setDoorCode(addEquipmentVo.getDoorCode());
        dzEquipment.setIsShow(addEquipmentVo.getIsShow());
        dzEquipment.setOrgCode(dzProductionLine.getOrgCode());
        dzEquipment.setLineNo(dzProductionLine.getLineNo());
        dzEquipment.setCreateBy(sysUser.getRealname());
        dzEquipment.setOrderNo(byId.getOrderNo());
        dzEquipment.setNickName(addEquipmentVo.getNickName());
        equipmentMapper.insert(dzEquipment);
        sysBusTask.arrange();
        return new Result(CustomExceptionType.OK, dzEquipment);
    }

    @Override
    public Result<EquipmentListDo> list(String sub, SelectEquipmentVo data) {
        String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getTableName();
        data.setTableKey(tableKey);
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode() && byUserName.getUseOrgCode().equals(FinalCode.DZ_USE_ORG_CODE)) {
            byUserName.setUseOrgCode(null);
        }
        PageHelper.startPage(data.getPage(), data.getLimit());
        data.setEquipmentType(data.getEquipmentType());
        data.setUseOrgCode(byUserName.getUseOrgCode());
        List<EquipmentListDo> list = equipmentMapper.list(data);
        PageInfo<EquipmentListDo> info = new PageInfo<>(list);
        List<EquipmentListDo> dataList = info.getList();

        //????????????
        for (EquipmentListDo equipmentListDo:dataList) {
            String runState=null;//????????????
            String connectState=null;//????????????
            String operatorMode=null;//????????????
            if(equipmentListDo.getEquipmentType().intValue()==2){//??????
                runState=cachingApi.convertTcp("B562",equipmentListDo.getB562());
                connectState=cachingApi.convertTcp("B561",equipmentListDo.getB561());
                operatorMode=cachingApi.convertTcp("B565",equipmentListDo.getB565());
            }else  if(equipmentListDo.getEquipmentType().intValue()==3){//?????????
                runState=cachingApi.convertTcp("A563",equipmentListDo.getA563());
                connectState=cachingApi.convertTcp("A561",equipmentListDo.getA561());
                operatorMode=cachingApi.convertTcp("A562",equipmentListDo.getA562());
            }else  if(equipmentListDo.getEquipmentType().intValue()==8){//?????????
                runState=cachingApi.convertTcp("H562",equipmentListDo.getH562());
                connectState=cachingApi.convertTcp("H561",equipmentListDo.getH561());
                operatorMode=cachingApi.convertTcp("H566",equipmentListDo.getH566());
            }else  if(equipmentListDo.getEquipmentType().intValue()==9){//?????????
                runState=cachingApi.convertTcp("K562",equipmentListDo.getK562());
                connectState=cachingApi.convertTcp("K561",equipmentListDo.getK561());
                operatorMode=cachingApi.convertTcp("K566",equipmentListDo.getK566());
            }
            equipmentListDo.setRunStatus(runState);
            equipmentListDo.setConnectState(connectState);
            equipmentListDo.setOperatorMode(operatorMode);
        }
        return new Result(CustomExceptionType.OK,dataList , info.getTotal());
    }

    @Override
    public Result<EquipmentDo> getById(String sub, Long id) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue() && byUserName.getUseOrgCode().equals(FinalCode.DZ_USE_ORG_CODE)) {
            byUserName.setUseOrgCode(null);
        }
        DzEquipment dzEquipment = equipmentMapper.getById(byUserName.getUseOrgCode(), id);
        if (dzEquipment == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_6);
        }
        return new Result(CustomExceptionType.OK, dzEquipment);
    }

    @Override
    public Result put(String sub, PutEquipmentVo putEquipmentVo) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue() && byUserName.getUseOrgCode().equals(FinalCode.DZ_USE_ORG_CODE)) {
            byUserName.setUseOrgCode(null);
        }
        //?????????????????????
        EquipmentDo dzEquipment = equipmentMapper.getById(byUserName.getUseOrgCode(), putEquipmentVo.getId());
        if (dzEquipment == null) {
            log.error("??????id?????????,id:{}", putEquipmentVo.getId());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_6);
        }

        //??????????????????
        if (dzEquipment.getLineId().longValue() != putEquipmentVo.getLineId().longValue()) {
            DzProductionLine dzProductionLine = dzProductionLineMapper.selectById(putEquipmentVo.getLineId());
            if (dzProductionLine == null) {
                log.error("???????????????id?????????");
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_71);
            }
            //??????????????????????????? ???????????????????????????????????????
            QueryWrapper<DzEquipment> wrapper = new QueryWrapper<>();
            wrapper.eq("line_id", putEquipmentVo.getLineId());
            wrapper.eq("equipment_no", putEquipmentVo.getEquipmentNo());
            wrapper.eq("equipment_type", putEquipmentVo.getEquipmentType());
            List<DzEquipment> dzEquipments = equipmentMapper.selectList(wrapper);
            if (dzEquipments.size() > 0) {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_72);
            }
            //??????????????????????????? ???????????????????????????
            QueryWrapper<DzEquipment> wrapper1 = new QueryWrapper<>();
            wrapper1.eq("line_id", putEquipmentVo.getLineId());
            wrapper1.eq("equipment_code", putEquipmentVo.getEquipmentCode());
            List<DzEquipment> dzEquipmentList = equipmentMapper.selectList(wrapper1);
            if (dzEquipmentList.size() > 0) {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_73);
            }
            //??????????????? ?????????????????????
            putEquipmentVo.setOrderNo(dzProductionLine.getOrderNo());
        } else {
            //??????????????? ??????????????????????????????
            putEquipmentVo.setOrderNo(dzEquipment.getOrderNo());
        }
        //????????????????????????
//        if (!putEquipmentVo.getEquipmentName().equals(dzEquipment.getEquipmentName())) {
//            List<DzEquipment> equipmentName = equipmentMapper.selectList(new QueryWrapper<DzEquipment>().eq("equipment_name", putEquipmentVo.getEquipmentName()));
//            if (equipmentName.size() > 0) {
//                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_27);
//            }
//        }
        //??????????????????????????????
        if (!putEquipmentVo.getEquipmentCode().equals(dzEquipment.getEquipmentCode())) {
            List<DzEquipment> equipmentCode = equipmentMapper.selectList(new QueryWrapper<DzEquipment>().eq("equipment_code", putEquipmentVo.getEquipmentCode()));
            if (equipmentCode.size() > 0) {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_26);
            }
        }
        //????????????????????????
        if (!putEquipmentVo.getEquipmentNo().equals(dzEquipment.getEquipmentNo())) {
            QueryWrapper<DzEquipment> eq = new QueryWrapper<DzEquipment>()
                    .eq("order_no", putEquipmentVo.getOrderNo())
                    .eq("line_id", putEquipmentVo.getLineId())
                    .eq("equipment_type", putEquipmentVo.getEquipmentType())
                    .eq("equipment_no", putEquipmentVo.getEquipmentNo());
            List<DzEquipment> dzEquipments = equipmentMapper.selectList(eq);
            if (dzEquipments.size() > 0) {
                log.error("?????????????????????????????????????????????????????????");
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_63);
            }
        }
        //??????????????????
        if (putEquipmentVo.getEquipmentType().intValue() != dzEquipment.getEquipmentType().intValue()) {
            //????????????????????????
            QueryWrapper<DzEquipment> eq = new QueryWrapper<DzEquipment>()
                    .eq("order_no", putEquipmentVo.getOrderNo())
                    .eq("line_id", putEquipmentVo.getLineId())
                    .eq("equipment_type", putEquipmentVo.getEquipmentType())
                    .eq("equipment_no", putEquipmentVo.getEquipmentNo());
            List<DzEquipment> dzEquipments = equipmentMapper.selectList(eq);
            if (dzEquipments.size() > 0) {
                log.error("???????????????????????????????????????????????????????????????{}????????????????????????{}???????????????", putEquipmentVo.getOrderNo(), putEquipmentVo.getLineId(), putEquipmentVo.getEquipmentType(), putEquipmentVo.getEquipmentNo());
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_63);
            }
        }
        dzEquipment.setDoorCode(putEquipmentVo.getDoorCode());
        dzEquipment.setIsShow(putEquipmentVo.getIsShow());
        dzEquipment.setEquipmentName(putEquipmentVo.getEquipmentName());
        dzEquipment.setPostscript(putEquipmentVo.getPostscript());
        dzEquipment.setStandardOperationRate(putEquipmentVo.getStandardOperationRate());
        BeanUtils.copyProperties(putEquipmentVo, dzEquipment);
        equipmentMapper.updateById(dzEquipment);
        //???????????????????????????
        DzLineShiftDay dzLineShiftDay = new DzLineShiftDay();
        dzLineShiftDay.setEquipmentNo(putEquipmentVo.getEquipmentNo());
        dzLineShiftDay.setEquipmentType(putEquipmentVo.getEquipmentType());
        dzLineShiftDayMapper.update(dzLineShiftDay, new QueryWrapper<DzLineShiftDay>().eq("eq_id", putEquipmentVo.getId()));
        return new Result(CustomExceptionType.OK, Message.OK_3);
    }

    @Override
    public Result<List<EquipmentDataDo>> listEquipmentData(String sub, Integer robotEquipmentCode, SelectEquipmentDataVo selectEquipmentDataVo) {
        String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getTableName();
        selectEquipmentDataVo.setTableKey(tableKey);
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue() && byUserName.getUseOrgCode().equals(FinalCode.DZ_USE_ORG_CODE)) {
            byUserName.setUseOrgCode(null);
        }
        selectEquipmentDataVo.setOrgCode(byUserName.getUseOrgCode());
        selectEquipmentDataVo.setEquipmentType(robotEquipmentCode);
        if (selectEquipmentDataVo.getPage() != -1) {
            PageHelper.startPage(selectEquipmentDataVo.getPage(), selectEquipmentDataVo.getLimit());
        }
        List<EquipmentDataDo> list = dzEquipmentProNumMapper.listEquipmentDataV2(selectEquipmentDataVo);
        PageInfo<EquipmentDataDo> info = new PageInfo<>(list);
        return new Result(CustomExceptionType.OK, info.getList(), info.getTotal());
    }

    @Transactional
    @Override
    public Result del(String sub, Long id) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue()) {
            DzEquipment dzEquipment = equipmentMapper.selectById(id);
            if (dzEquipment == null) {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_56);
            }
            //????????????????????????
            int delIndex = dzLineShiftDayMapper.delete(new QueryWrapper<DzLineShiftDay>().eq("eq_id", id));
            //????????????
            int i = equipmentMapper.deleteById(id);
            log.info("??????id:{},???????????????????????????{},????????????????????????:{}", id, i, delIndex);
            return new Result(CustomExceptionType.OK, Message.OK_2);
        } else {
            //???????????????????????????????????????
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_55);
        }
    }

    @Override
    public Result<List<EquipmentDo>> list(String sub, Integer type, PageLimit pageLimit, SelectEquipmentVo data) {
        String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getTableName();
        data.setTableKey(tableKey);
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode() && byUserName.getUseOrgCode().equals(FinalCode.DZ_USE_ORG_CODE)) {
            byUserName.setUseOrgCode(null);
        }
        if (pageLimit.getPage() != -1) {
            PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        }
        data.setEquipmentType(type);
        data.setUseOrgCode(byUserName.getUseOrgCode());
        List<EquipmentDo> list = equipmentMapper.equipmentList(data);
        PageInfo<EquipmentDo> info = new PageInfo<>(list);
        return new Result(CustomExceptionType.OK, info.getList(), info.getTotal());
    }

    @Override
    public Result<DzEquipment> getEquipmentByLineId(String sub, Long id) {
        QueryWrapper<DzEquipment> wrapper = new QueryWrapper();
        wrapper.select("id", "equipment_name", "equipment_type");
        wrapper.eq("line_id", id);
        List<DzEquipment> dzEquipments = equipmentMapper.selectList(wrapper);
        return new Result(CustomExceptionType.OK, dzEquipments, dzEquipments.size());
    }

    @Override
    public List<DzEquipment> listLingId(Long lineId) {
        QueryWrapper<DzEquipment> wrapper = new QueryWrapper();
        wrapper.eq("line_id", lineId);
        wrapper.select("equipment_no", "equipment_type", "equipment_name");
        return equipmentMapper.selectList(wrapper);
    }

    @Override
    public List<EquimentOrderLineId> getOrderLineEqId(List<String> equimentId) {
        return equipmentMapper.getOrderLineEqId(equimentId);
    }

    @Override
    public Result putIsShow(String sub, PutIsShowVo putIsShowVo) {
        DzEquipment dzEquipment = equipmentMapper.selectById(putIsShowVo.getId());
        dzEquipment.setIsShow(putIsShowVo.getIsShow());
        equipmentMapper.updateById(dzEquipment);
        return Result.ok(dzEquipment);
    }

    @Override
    public Result getDevcieLineId(String sub, String lineId) {
        List<DeviceMessage> deviceMessages = equipmentMapper.getDevcieLineId(Long.valueOf(lineId));
        return Result.OK(deviceMessages);
    }

    @Override
    public Result getEquipmentState(PageLimit pageLimit,String lineId) {
        PageHelper.startPage(pageLimit.getPage(),pageLimit.getLimit());
        List<EquipmentStateDo>list=equipmentMapper.getEquipmentState(lineId);
        PageInfo<EquipmentStateDo>info=new PageInfo<>(list);
        return Result.ok(info.getList(),info.getTotal());
    }

    @Override
    public Result putEquipmentDataState(PutEquipmentDataStateVo stateVo) {
        String name = stateVo.getName();
        stateVo.setName(humpToLine2(name));
        Boolean b=equipmentMapper.putEquipmentDataState(stateVo);
        return Result.ok(stateVo);
    }

    private static Pattern humpPattern = Pattern.compile("[A-Z]");
    /** ??????????????????,?????????????????? */
    public static String humpToLine2(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}
