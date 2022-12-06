package com.dzics.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.business.service.BusinessProductionPlanDayService;
import com.dzics.business.service.cache.DzDetectionTemplCache;
import com.dzics.common.dao.*;
import com.dzics.common.enums.Message;
import com.dzics.common.enums.UserIdentityEnum;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.entity.*;
import com.dzics.common.model.constant.FinalCode;
import com.dzics.common.model.request.charts.ActivationVo;
import com.dzics.common.model.request.mom.LineProductionDataVO;
import com.dzics.common.model.request.plan.SelectProductionPlanRecordVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.RunDataModel;
import com.dzics.common.model.response.charts.ActivationDetailsDo;
import com.dzics.common.model.response.charts.ActivationDo;
import com.dzics.common.model.response.mom.LineProductionDataDo;
import com.dzics.common.model.response.plan.PlanRecordDetailsListDo;
import com.dzics.common.model.response.plan.SelectProductionPlanRecordDo;
import com.dzics.common.service.DzProductionLineService;
import com.dzics.common.service.SysUserServiceDao;
import com.dzics.common.util.PageLimit;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BusinessProductionPlanDayServiceImpl implements BusinessProductionPlanDayService {
    @Autowired
    DzProductionPlanDayMapper dzProductionPlanDayMapper;
    @Autowired
    DzProductionPlanMapper dzProductionPlanMapper;
    @Autowired
    DzEquipmentProNumMapper dzEquipmentProNumMapper;
    @Autowired
    SysUserServiceDao sysUserServiceDao;
    @Autowired
    DzProductionLineMapper dzProductionLineMapper;
    @Autowired
    DzDetectionTemplCache dzDetectionTemplCache;
    @Autowired
    SysDepartMapper sysDepartMapper;
    @Autowired
    DzProductMapper dzProductMapper;
    @Autowired
    DzProductionLineService dzProductionLineService;
    @Override
    public Result list(String sub, PageLimit pageLimit, SelectProductionPlanRecordVo selectProductionPlanRecordVo) {
        String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getPlanDay();
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue() && byUserName.getUseOrgCode().equals(FinalCode.DZ_USE_ORG_CODE)) {
            byUserName.setUseOrgCode(null);
        }
        selectProductionPlanRecordVo.setPlanDayTable(tableKey);
        selectProductionPlanRecordVo.setOrgCode(byUserName.getUseOrgCode());
        PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        List<SelectProductionPlanRecordDo> list = dzProductionPlanDayMapper.list(selectProductionPlanRecordVo);
        PageInfo<SelectProductionPlanRecordDo> info = new PageInfo<>(list);
        return new Result(CustomExceptionType.OK, info.getList(), info.getTotal());
    }

    @Override
    public Result detailsList(Long planId, String detectorTime) {
        String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getTableName();
        DzProductionPlan dzProductionPlan = dzProductionPlanMapper.selectById(planId);
        if (dzProductionPlan == null) {
            log.error("生产计划id不存在:{}", planId);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_65);
        }
        Long lineId = dzProductionPlan.getLineId();
        SysDepart sysDepart=sysDepartMapper.selectByLineId(lineId);
        DzProductionLine dzProductionLine = dzProductionLineMapper.selectById(lineId);
        if (dzProductionLine == null || dzProductionLine.getStatisticsEquimentId() == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR35);
        }
        List<PlanRecordDetailsListDo> list = dzEquipmentProNumMapper.detailsList(lineId, detectorTime, tableKey, dzProductionLine.getStatisticsEquimentId());
        for (int i = 0; i < list.size(); i++) {
            PlanRecordDetailsListDo planRecordDetailsListDo = list.get(i);
            if(planRecordDetailsListDo.getProductName()==null){
                //产品名称为空 则用产品类型(底层传递的类型 其实就是产品名称)去查询
                String productType = planRecordDetailsListDo.getProductType();
                List<DzProduct> dzProducts = dzProductMapper.selectList(new QueryWrapper<DzProduct>().eq("product_name", productType).eq("depart_id", sysDepart.getId()));
                if(dzProducts.size()>0){
                    String productNo = dzProducts.get(0).getProductNo();
                    String productName = dzProducts.get(0).getProductName();
                    list.get(i).setProductNo(productNo);
                    list.get(i).setProductName(productName);
                }else{
                    list.get(i).setProductNo(FinalCode.DZ_PRODUCT_NO);
                    list.get(i).setProductName(FinalCode.DZ_PRODUCT_NAME);
                }
                list.get(i).setDepartName(sysDepart.getDepartName());
            }

        }
        return new Result(CustomExceptionType.OK, list, list.size());
    }

    @Override
    public Result activation(String sub, ActivationVo activationVo) {
        String planDay = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getPlanDay();
        List<ActivationDo> list = new ArrayList<>();
        QueryWrapper<DzProductionLine> wrapper = new QueryWrapper<>();
        wrapper.select("id", "line_name");
        wrapper.eq("order_id", activationVo.getOrderId());
        List<DzProductionLine> dzProductionLines = dzProductionLineMapper.selectList(wrapper);
        //填充产线数据
        for (DzProductionLine dzProductionLine : dzProductionLines) {
            ActivationDo activationDo = new ActivationDo();
            activationDo.setShow(activationVo.getLineList().contains(dzProductionLine.getId()));
            activationDo.setLineId(dzProductionLine.getId());
            activationDo.setLineName(dzProductionLine.getLineName());
            List<ActivationDetailsDo> data = dzProductionPlanDayMapper.getActivation(dzProductionLine.getId(), activationVo.getStartTime(), activationVo.getEndTime(), planDay);
            List<BigDecimal> activationData = new ArrayList<>();
            List<String> dateData = new ArrayList<>();
            for (ActivationDetailsDo activationDetailsDo : data) {
                BigDecimal bigDecimal = activationDetailsDo.getActivationData().setScale(2, BigDecimal.ROUND_HALF_UP);
                if(bigDecimal.compareTo(BigDecimal.ZERO)!=0){
                    activationData.add(bigDecimal);
                    dateData.add(activationDetailsDo.getDateData());
                }
            }
            activationDo.setActivationData(activationData);
            activationDo.setDateData(dateData);
            list.add(activationDo);
        }
        return new Result(CustomExceptionType.OK, list, list.size());
    }

    @Override
    public Result getLineProductionData(LineProductionDataVO lineProductionDataVO) {
        if(CollectionUtils.isEmpty(lineProductionDataVO.getIds())){
            return Result.error(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
        }
        //根据id查询产线
        List<DzProductionLine> lineList = dzProductionLineService.list(new QueryWrapper<DzProductionLine>().in("id", lineProductionDataVO.getIds()));
        List<LineProductionDataDo>list=new ArrayList<>();
        //产线生产计划
        for (DzProductionLine dzProductionLine:lineList) {
            LineProductionDataDo line=new LineProductionDataDo();
            line.setLineName(dzProductionLine.getLineName());
            line.setLineId(dzProductionLine.getId());
            list.add(line);
        }
        List<DzProductionPlan> dzProductionPlans = dzProductionPlanMapper.selectList(new QueryWrapper<DzProductionPlan>().in("line_id", lineProductionDataVO.getIds()).eq("plan_type", 0));
        for (DzProductionPlan dzProductionPlan:dzProductionPlans) {
            for (LineProductionDataDo lineProductionDataDo:list) {
                if(lineProductionDataDo.getLineId().intValue()==dzProductionPlan.getLineId().intValue()){
                    lineProductionDataDo.setPlanId(lineProductionDataDo.getLineId());
                }
            }
        }
        List<Long> collect = dzProductionPlans.stream().map(p -> p.getId()).collect(Collectors.toList());
        String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getPlanDay();
        List<Map<String,Object>>mapList=dzProductionPlanDayMapper.getLineProductionData(tableKey,collect,lineProductionDataVO.getStartTime(),lineProductionDataVO.getEndTime());
        for (Map<String,Object> map:mapList) {
            String dataSum = map.get("completedQuantity").toString();
            String planId = map.get("planId").toString();
            for (LineProductionDataDo lineProductionDataDo:list) {
                if(planId.equals(lineProductionDataDo.getPlanId().toString())){
                    lineProductionDataDo.setDataSum(dataSum);
                }
            }
        }
        return Result.ok(list);
    }
}
