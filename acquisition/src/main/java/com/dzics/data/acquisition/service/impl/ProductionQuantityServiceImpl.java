package com.dzics.data.acquisition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.dao.*;
import com.dzics.common.enums.DeviceSocketSendStatus;
import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.model.entity.DzProductionLine;
import com.dzics.common.model.entity.DzToolCompensationData;
import com.dzics.common.model.constant.FinalCode;
import com.dzics.common.model.response.*;
import com.dzics.common.util.RedisKey;
import com.dzics.data.acquisition.service.CacheService;
import com.dzics.data.acquisition.service.ProductionQuantityService;
import com.dzics.data.acquisition.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ProductionQuantityServiceImpl implements ProductionQuantityService {

    @Autowired
    DzProductionLineMapper dzProductionLineMapper;
    @Autowired
    CacheService cacheService;
    @Autowired
    DzEquipmentProNumMapper dzEquipmentProNumMapper;
    @Autowired
    DzEquipmentDowntimeRecordMapper dzEquipmentDowntimeRecordMapper;
    @Autowired
    DzEquipmentMapper dzEquipmentMapper;
    @Autowired
    DzProductionPlanDayMapper dzProductionPlanDayMapper;
    @Autowired
    DzToolCompensationDataMapper dzToolCompensationDataMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Result getOutputByLineId(Long lineId) {
        String tableKey = ((RunDataModel) cacheService.systemRunModel(null).getData()).getTableName();
        DzProductionLine dzProductionLine = dzProductionLineMapper.selectById(lineId);
        GetOutputByLineIdDo getOutputByLineIdDo = new GetOutputByLineIdDo();
        getOutputByLineIdDo.setDateList(getFiveDate());
        if (dzProductionLine != null && dzProductionLine.getStatisticsEquimentId() != null) {
            //获取产线产量记数设备id
            Long eqId = dzProductionLine.getStatisticsEquimentId();
            //查询五日内生产数据
            List<Long> data = dzEquipmentProNumMapper.getOutputByEqId(eqId, tableKey);

            getOutputByLineIdDo.setList(data);
        } else {
            log.warn("查询产线绑定设备五日数据异常，产线id：{}", lineId);
            if (dzProductionLine != null) {
                log.warn("查询产线绑定设备五日数据异常，产线绑定设备id：{}", dzProductionLine.getStatisticsEquimentId());
            }
            Long[] list = {0L, 0L, 0L, 0L, 0L};
            List<Long> longs = Arrays.asList(list);
            getOutputByLineIdDo.setList(longs);
        }
        JCEquimentBase jcEquimentBase = new JCEquimentBase();
        jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_PITPUT_IN_FIVE_DAYS_ANALYSIS.getInfo());
        jcEquimentBase.setData(getOutputByLineIdDo);
        return Result.ok(jcEquimentBase);
    }

    @Override
    public Result getEquipmentAvailable(Long lineId) {
        EquipmentAvailableDo equipmentAvailableDo = new EquipmentAvailableDo();

        DzEquipment[] yName = new DzEquipment[3];
        List<DzEquipment> dataList = getLineIdIsShowAcc(lineId);
        for (DzEquipment dzEquipment : dataList) {
            if ("01".equals(dzEquipment.getEquipmentNo())) {
                yName[0] = dzEquipment;
            } else if ("A1".equals(dzEquipment.getEquipmentNo())) {
                yName[1] = dzEquipment;
            } else if ("A2".equals(dzEquipment.getEquipmentNo())) {
                yName[2] = dzEquipment;
            }
        }
        List<String> eqName = new ArrayList<>();
        List<String> timeRun = new ArrayList<>();
        List<String> stopTime = new ArrayList<>();
        LocalDate startTime = LocalDate.now();
        LocalDate endTime = LocalDate.now().plusDays(1);
        for (DzEquipment dzEquipment : yName) {
            eqName.add(dzEquipment.getNickName());
            String equipmentNo = dzEquipment.getEquipmentNo();
            Integer equipmentType = dzEquipment.getEquipmentType();

            BigDecimal until = new BigDecimal(startTime.until(endTime, ChronoUnit.DAYS) * 24);
            //           判断需不需要减去今天还没有经过的时间
            if (endTime.compareTo(LocalDate.now().plusDays(1)) == 0) {
                until = until.subtract(new BigDecimal(24));
                LocalTime now = LocalTime.now();
                int hour = now.getHour();
                int mis = now.getMinute() * (60) + now.getSecond();
                BigDecimal minute = new BigDecimal(mis).divide(new BigDecimal(3600), 2, BigDecimal.ROUND_HALF_UP).add(new BigDecimal(hour));
                until = until.add(minute);
            }
            Long timeStop = dzEquipmentDowntimeRecordMapper.getTimeDuration(dzEquipment.getLineNo(), dzEquipment.getOrderNo(), equipmentNo, equipmentType, startTime, endTime);
            if (timeStop == null) {
                timeStop = 0L;
            }
            BigDecimal bigDecimal = new BigDecimal(timeStop);
//            停机小时
            BigDecimal divide = bigDecimal.divide(new BigDecimal(3600000), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal subtract = until.subtract(divide);
            timeRun.add(subtract.toString());
            stopTime.add(divide.toString());
        }
        equipmentAvailableDo.setEqName(eqName);
        equipmentAvailableDo.setTimeRun(timeRun);
        equipmentAvailableDo.setStopTime(stopTime);
        JCEquimentBase jcEquimentBase = new JCEquimentBase();
        jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_DEVICE_CURRENT_DAILY_HOURS.getInfo());
        jcEquimentBase.setData(equipmentAvailableDo);
        return Result.ok(jcEquimentBase);
    }

    private List<DzEquipment> getLineIdIsShowAcc(Long lineId) {
        List<DzEquipment> list = redisUtil.lGet(RedisKey.getLineIdIsShowAcc + lineId, 0, -1);
        if (CollectionUtils.isNotEmpty(list)) {
            return list;
        }
        QueryWrapper<DzEquipment> wrapper = new QueryWrapper();
        wrapper.eq("line_id", lineId);
        wrapper.select("equipment_no", "equipment_type", "equipment_name", "nick_name");
        List<DzEquipment> dataList = dzEquipmentMapper.selectList(wrapper);
        int timeCahce = (int) (Math.random() * 700)+60;
        redisUtil.lSet(RedisKey.getLineIdIsShowAcc + lineId, dataList, timeCahce);
        return dataList;
    }

    @Override
    public Result getProductionPlanFiveDay(Long lineId) {
        String tableKey = ((RunDataModel) cacheService.systemRunModel(null).getData()).getPlanDay();
        List<BigDecimal> list = dzProductionPlanDayMapper.getProductionPlanFiveDay(lineId, tableKey);
        ProductionPlanFiveDayDo res = new ProductionPlanFiveDayDo();
        res.setList(list);
        res.setDateList(getFiveDate());
        JCEquimentBase jcEquimentBase = new JCEquimentBase();
        jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_FIVE_DAY_YIELD.getInfo());
        jcEquimentBase.setData(list);
        return Result.ok(jcEquimentBase);
    }

    @Override
    public Result getPlanAnalysis(Long lineId) {
//        完成率
        List<BigDecimal> percentageComplete = new ArrayList<>();
//       产出率
        List<BigDecimal> outputRate = new ArrayList<>();
//        合格率
        List<BigDecimal> passRate = new ArrayList<>();
        String tableKey = ((RunDataModel) cacheService.systemRunModel(null).getData()).getPlanDay();

        List<Map<String, BigDecimal>> list = dzProductionPlanDayMapper.getPlanAnalysis(lineId, tableKey);
        for (Map<String, BigDecimal> data : list) {
            percentageComplete.add(data.get("percentageComplete"));
            outputRate.add(data.get("outputRate"));
            passRate.add(data.get("passRate"));
        }
        PlanAnalysisDo planAnalysisDo = new PlanAnalysisDo();
        planAnalysisDo.setPercentageComplete(percentageComplete);
        planAnalysisDo.setOutputRate(outputRate);
        planAnalysisDo.setPassRate(passRate);
        planAnalysisDo.setDateList(getFiveDate());
        JCEquimentBase jcEquimentBase = new JCEquimentBase();
        jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_FIVE_DAY_PLAN_ANALYSIS.getInfo());
        jcEquimentBase.setData(planAnalysisDo);
        return Result.ok(jcEquimentBase);
    }

    @Override
    public Result getToolInfoData(String orderNo, String lineNo) {
        List<Long> eqIdList = dzToolCompensationDataMapper.getEqIds();
        if (eqIdList.size() == 0) {
            return null;
        }
        //查询机床
        QueryWrapper<DzEquipment> wrapper = new QueryWrapper();
        wrapper.eq("order_no", orderNo);
        wrapper.eq("line_no", lineNo);
        wrapper.eq("equipment_type", FinalCode.TOOL_EQUIPMENT_CODE);
//        wrapper.in("equipment_no",FinalCode.DZ_TOOL_CODE_A2);
        List<DzEquipment> dzEquipments = dzEquipmentMapper.selectList(wrapper);
        List<GetToolInfoDataDo> dataList = new ArrayList<>();
        QueryWrapper<DzToolCompensationData> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("tool_no");
        List<DzToolCompensationData> dzToolCompensationData = dzToolCompensationDataMapper.selectList(queryWrapper);
        for (DzEquipment dzEquipment : dzEquipments) {
            GetToolInfoDataDo getToolInfoDataDo = new GetToolInfoDataDo();
            getToolInfoDataDo.setEquipmentId(dzEquipment.getId());
            getToolInfoDataDo.setEquipmentName(dzEquipment.getEquipmentName());
            getToolInfoDataDo.setEquipmentNo(dzEquipment.getEquipmentNo());
            //设备绑定的刀具信息集合
            List<ToolDataDo> toolDataDos = new ArrayList<>();
            for (DzToolCompensationData data : dzToolCompensationData) {
                if (data.getEquipmentId() != null && data.getEquipmentId().intValue() == dzEquipment.getId().intValue()) {
                    ToolDataDo toolDataDo = new ToolDataDo();
                    toolDataDo.setToolLife(data.getToolLife());
                    toolDataDo.setToolLifeCounter(data.getToolLifeCounter());
                    if (data.getToolNo() != null) {
                        if (data.getToolNo().intValue() < 10) {
                            toolDataDo.setToolNo("T0" + data.getToolNo().toString());
                        } else {
                            toolDataDo.setToolNo("T" + data.getToolNo().toString());
                        }
                    }
                    toolDataDos.add(toolDataDo);
                }
            }
            //填充设备绑定的刀具信息集合
            getToolInfoDataDo.setToolDataList(toolDataDos);
            //填充数据到设备集合里面
            dataList.add(getToolInfoDataDo);
        }

        JCEquimentBase jcEquimentBase = new JCEquimentBase();
        jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_MACHINE_TOOL_INFORMATION.getInfo());
        jcEquimentBase.setData(dataList);
        return Result.ok(jcEquimentBase);
    }

    //获取五日内日期
    public List<String> getFiveDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate date5 = LocalDate.now();
        LocalDate date4 = date5.plusDays(-1);
        LocalDate date3 = date4.plusDays(-1);
        LocalDate date2 = date3.plusDays(-1);
        LocalDate date1 = date2.plusDays(-1);
        List<String> list = new ArrayList<>();
        list.add(date1.format(formatter));
        list.add(date2.format(formatter));
        list.add(date3.format(formatter));
        list.add(date4.format(formatter));
        list.add(date5.format(formatter));
        return list;
    }


}
