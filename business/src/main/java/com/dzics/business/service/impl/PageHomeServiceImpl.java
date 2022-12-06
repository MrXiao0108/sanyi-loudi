package com.dzics.business.service.impl;

import com.dzics.business.service.BuProductionQuantityService;
import com.dzics.business.service.BusinessDzProductionLineService;
import com.dzics.business.service.HomeLineDataService;
import com.dzics.business.service.PageHomeService;
import com.dzics.business.service.cache.DzDetectionTemplCache;
import com.dzics.common.dao.DzEquipmentMapper;
import com.dzics.common.dao.DzEquipmentProNumMapper;
import com.dzics.common.dao.DzProductMapper;
import com.dzics.common.dao.DzProductionLineMapper;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.custom.WorkNumberName;
import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.model.entity.DzProductionLine;
import com.dzics.common.model.constant.SysConfigDepart;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.RunDataModel;
import com.dzics.common.model.response.charts.WorkShiftSum;
import com.dzics.common.model.response.feishi.DayDataDo;
import com.dzics.common.model.response.homepage.GetEquipmentStateDo;
import com.dzics.common.model.response.homepage.HomeWorkShiftData;
import com.dzics.common.model.response.homepage.QualifiedAndOutputDo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PageHomeServiceImpl implements PageHomeService {
    @Autowired
    HomeLineDataService homeLineDataService;
    @Autowired
    DzProductionLineMapper dzProductionLineMapper;
    @Autowired
    DzEquipmentMapper dzEquipmentMapper;
    @Autowired
    DzDetectionTemplCache dzDetectionTemplCache;
    @Autowired
    DzEquipmentProNumMapper dzEquipmentProNumMapper;
    @Autowired
    DzProductMapper dzProductMapper;
    @Autowired
    private BuProductionQuantityService buProductionQuantityService;
    @Autowired
    private BusinessDzProductionLineService productionLineService;


    @Override
    public Result getOutputAndQualified(Long lineId) {
        QualifiedAndOutputDo qualifiedAndOutputDo = homeLineDataService.outputCapacity(lineId);
        return Result.ok(qualifiedAndOutputDo);
    }

    @Override
    @Deprecated
    public Result geDayAndMonthData(Long lineId) {
        String sysConfig = dzDetectionTemplCache.getIndexIsShowNg();
        if (SysConfigDepart.INDEX_IS_NG.equals(sysConfig)) {
            DzProductionLine dzProductionLine = productionLineService.getLineId(lineId);
            if (dzProductionLine == null || dzProductionLine.getStatisticsEquimentId() == null) {
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR17);
            }
            Long statisticsEquimentId = dzProductionLine.getStatisticsEquimentId();
            List<WorkShiftSum> dayWorkShiftSum = buProductionQuantityService.getWorkShiftSum(statisticsEquimentId);
            DayDataDo dayDataDo = homeLineDataService.monthData(lineId);
            HomeWorkShiftData homeWorkShiftData = new HomeWorkShiftData();
            homeWorkShiftData.setDayWorkShiftSum(dayWorkShiftSum);
            homeWorkShiftData.setMouthWorkShiftSum(dayDataDo);
            return Result.ok(homeWorkShiftData);
        } else {
            DzProductionLine dzProductionLine = productionLineService.getLineId(lineId);
            if (dzProductionLine == null || dzProductionLine.getStatisticsEquimentId() == null) {
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR17);
            }
            Long statisticsEquimentId = dzProductionLine.getStatisticsEquimentId();
            List<WorkShiftSum> dayWorkShiftSum = buProductionQuantityService.getWorkShiftSum(statisticsEquimentId);
            List<WorkShiftSum> mouthWorkShiftSum = buProductionQuantityService.getMouthWorkShiftSum(statisticsEquimentId);
            HomeWorkShiftData homeWorkShiftData = new HomeWorkShiftData();
            homeWorkShiftData.setDayWorkShiftSum(dayWorkShiftSum);
            homeWorkShiftData.setMouthWorkShiftSum(mouthWorkShiftSum);
            return Result.ok(homeWorkShiftData);
        }
    }

    @Override
    public Result geEquipmentState(Long lineId) {
        String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getTableName();

        DzProductionLine dzProductionLine = dzProductionLineMapper.selectById(lineId);
        if (dzProductionLine == null) {
            log.error("首页查询产线信息，产线不存在,id:{}", lineId);
            return Result.error(CustomExceptionType.OK_NO_DATA);
        }
        DzEquipment dzEquipment = dzEquipmentMapper.selectById(dzProductionLine.getStatisticsEquimentId());
        if (dzEquipment == null) {
            log.error("首页查询产线绑定设备信息，设备不存在,id:{}", dzProductionLine.getStatisticsEquimentId());
            return Result.error(CustomExceptionType.OK_NO_DATA);
        }
        GetEquipmentStateDo getEquipmentStateDo = new GetEquipmentStateDo();
        getEquipmentStateDo.setLineName(dzProductionLine.getLineName());
        getEquipmentStateDo.setEquipmentName(dzEquipment.getEquipmentName());
        getEquipmentStateDo.setConnectState(dzEquipment.getConnectState());
        getEquipmentStateDo.setRunStatus(dzEquipment.getRunStatus());
        getEquipmentStateDo.setAlarmStatus(dzEquipment.getAlarmStatus());
        getEquipmentStateDo.setDownNum(dzEquipment.getDownSum());
        PageHelper.startPage(1, 1);
        List<WorkNumberName> product = dzEquipmentProNumMapper.getProductName(tableKey, dzEquipment.getId());
        PageInfo<WorkNumberName> list = new PageInfo<>(product);
        if (product.size() > 0) {
            WorkNumberName workNumberName = list.getList().get(0);
            if (workNumberName.getProductName() != null) {
                if ("".equals(workNumberName.getProductName()) && "".equals(workNumberName.getModelNumber())) {
                    getEquipmentStateDo.setProductNo("默认编号");
                    getEquipmentStateDo.setProductName(workNumberName.getProductName());
                } else {
                    WorkNumberName dd = dzProductMapper.getProductType(workNumberName.getProductName());
                    getEquipmentStateDo.setProductNo(dd != null ? dd.getModelNumber() : "默认编号");
                    getEquipmentStateDo.setProductName(workNumberName.getProductName());
                }

            }
        } else {
            getEquipmentStateDo.setProductName("默认产品");
            getEquipmentStateDo.setProductNo("默认编号");
        }
        return Result.ok(getEquipmentStateDo);
    }

    @Override
    public Result geDayAndMonthDataV2(Long lineId) {
        DzProductionLine dzProductionLine = productionLineService.getLineId(lineId);
        if (dzProductionLine == null || dzProductionLine.getStatisticsEquimentId() == null) {
            throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR17);
        }
        Long statisticsEquimentId = dzProductionLine.getStatisticsEquimentId();
        List<WorkShiftSum> dayWorkShiftSum = buProductionQuantityService.getWorkShiftSum(statisticsEquimentId);
        DayDataDo dayDataDo = homeLineDataService.monthData(lineId);
        HomeWorkShiftData homeWorkShiftData = new HomeWorkShiftData();
        homeWorkShiftData.setDayWorkShiftSum(dayWorkShiftSum);
        homeWorkShiftData.setMouthWorkShiftSum(dayDataDo);
        homeWorkShiftData.setMouthValue(dayWorkShiftSum.get(0).getMouthValue());
        return Result.ok(homeWorkShiftData);
    }
}
