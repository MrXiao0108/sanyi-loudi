package com.dzics.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.dao.*;
import com.dzics.common.model.entity.*;
import com.dzics.common.model.constant.FinalCode;
import com.dzics.business.service.BusinessEquipmentDowntimeRecordService;
import com.dzics.common.enums.UserIdentityEnum;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.request.GetByEquipmentNoVo;
import com.dzics.common.model.request.charts.RobotDataChartsListVo;
import com.dzics.common.model.response.GetByEquipmentNoDo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.charts.OperationDo;
import com.dzics.common.model.response.charts.OperationDoAll;
import com.dzics.common.service.SysUserServiceDao;
import com.dzics.common.util.DateUtil;
import com.dzics.common.util.PageLimit;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusinessEquipmentDowntimeRecordServiceImpl implements BusinessEquipmentDowntimeRecordService {
    @Autowired
    DzEquipmentDowntimeRecordMapper dzEquipmentDowntimeRecordMapper;
    @Autowired
    DzEquipmentMapper dzEquipmentMapper;
    @Autowired
    SysUserServiceDao sysUserServiceDao;
    @Autowired
    DzOrderMapper dzOrderMapper;
    @Autowired
    DzProductionLineMapper dzProductionLineMapper;
    @Resource
    DzEquipmentTimeAnalysisMapper dzEquipmentTimeAnalysisMapper;
    @Autowired
    DateUtil dateUtil;
    @Override
    public Result getByEquipmentNo(String sub, GetByEquipmentNoVo getByEquipmentNoVo, PageLimit pageLimit) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue() && byUserName.getUseOrgCode().equals(FinalCode.DZ_USE_ORG_CODE)) {
            byUserName.setUseOrgCode(null);
        }
        getByEquipmentNoVo.setOrgCode(byUserName.getUseOrgCode());
        PageHelper.startPage(pageLimit.getPage(),pageLimit.getLimit());
        List<GetByEquipmentNoDo> list = dzEquipmentDowntimeRecordMapper.getByEquipmentNo(getByEquipmentNoVo);
        PageInfo<GetByEquipmentNoDo>info=new PageInfo(list);
        List<GetByEquipmentNoDo> dataList = info.getList();
        if(dataList.size()==0){
            return new Result(CustomExceptionType.OK, info.getList(), info.getTotal());
        }
        List<Long> collect = dataList.stream().map(p -> p.getGroupId()).collect(Collectors.toList());
        QueryWrapper<DzEquipmentTimeAnalysis> wrapper = new QueryWrapper();
        wrapper.in("group_id",collect);
        wrapper.orderByAsc("stop_time");
        List<DzEquipmentTimeAnalysis> dzEquipmentTimeAnalyses = dzEquipmentTimeAnalysisMapper.selectList(wrapper);
        for (GetByEquipmentNoDo getByEquipmentNoDo:dataList) {
            List<DzEquipmentTimeAnalysis>dataArray=new ArrayList<>();
            for (DzEquipmentTimeAnalysis dzEquipmentTimeAnalysis:dzEquipmentTimeAnalyses) {
                if(getByEquipmentNoDo.getGroupId().longValue()==dzEquipmentTimeAnalysis.getGroupId().longValue()){
                    dataArray.add(dzEquipmentTimeAnalysis);
                }
            }
            if(dataArray.size()>0){
                getByEquipmentNoDo.setStopTime(dataArray.get(0).getStopTime());
                getByEquipmentNoDo.setResetTime(dataArray.get(dataArray.size()-1).getResetTime());
            }
        }
        return new Result(CustomExceptionType.OK, dataList, info.getTotal());
    }

    /**
     * 设备运行率=设备运行时间/（设备运行时间+设备停机时间）*100%
     *
     * @param sub
     * @param robotDataChartsListVo
     * @return
     */
    @Override
    public Result operation(String sub, RobotDataChartsListVo robotDataChartsListVo) {
        QueryWrapper<DzEquipment> eq = new QueryWrapper<>();
        eq.eq("line_id", robotDataChartsListVo.getLineId());
        eq.in("equipment_type", 2, 3);
        eq.select("id", "equipment_type", "equipment_no", "equipment_name");
        List<DzEquipment> dzEquipments = dzEquipmentMapper.selectList(eq);
        //查询设备运行率
        List<OperationDo> dd = new ArrayList<>();
        for (DzEquipment dzEquipment : dzEquipments) {
            OperationDo operationDo = new OperationDo();
            operationDo.setEquipmentName(dzEquipment.getEquipmentName());
            operationDo.setEquipmentId(dzEquipment.getId());
            if (robotDataChartsListVo.getEquipmentIdList().contains(dzEquipment.getId())) {
                operationDo.setShow(true);
            } else {
                operationDo.setShow(false);
            }
            List<BigDecimal> data = dzEquipmentDowntimeRecordMapper.operation(dzEquipment.getId(), robotDataChartsListVo.getStartTime(), robotDataChartsListVo.getEndTime());
            List<BigDecimal> res = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                BigDecimal bigDecimal = data.get(i).setScale(2, BigDecimal.ROUND_HALF_UP);
                res.add(bigDecimal);
            }
            operationDo.setEquipmentData(res);
            dd.add(operationDo);
        }
        List<String> date = dzEquipmentDowntimeRecordMapper.selectDate(robotDataChartsListVo.getStartTime(), robotDataChartsListVo.getEndTime());
        OperationDoAll operationDoAll = new OperationDoAll();
        operationDoAll.setDate(date);
        operationDoAll.setOperationDos(dd);
        return new Result(CustomExceptionType.OK, operationDoAll);
    }

    @Override
    public Long getTimeDuration(String lineNo,String orderNo, String equipmentNo, Integer equipmentType, LocalDate startTime, LocalDate endTime) {
        return dzEquipmentDowntimeRecordMapper.getTimeDuration(lineNo,orderNo,equipmentNo, equipmentType, startTime, endTime);
    }
}
