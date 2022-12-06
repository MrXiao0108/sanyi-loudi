package com.dzics.business.service.kanban;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.business.service.cache.DzDetectionTemplCache;
import com.dzics.business.util.RedisUtil;
import com.dzics.common.dao.*;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.custom.LineNumberTotal;
import com.dzics.common.model.custom.MachiningNumTotal;
import com.dzics.common.model.dto.check.DayCheckModel;
import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.model.entity.DzProductionLine;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.common.model.response.*;
import com.dzics.common.model.response.charts.WorkShiftSum;
import com.dzics.common.model.response.charts.loudi.WorkShiftLouDi;
import com.dzics.common.service.DzEquipmentProNumService;
import com.dzics.common.service.DzEquipmentService;
import com.dzics.common.util.DateUtil;
import com.dzics.common.util.RedisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Classname YieldAnalysisKanBan
 * @Description 产量分析
 * @Date 2022/6/17 15:36
 * @Created by NeverEnd
 */
@Service
@Slf4j
public class YieldAnalysisKanBan {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DzEquipmentService dzEquipmentService;
    @Autowired
    private DzDetectionTemplCache cacheService;
    @Autowired
    private DzEquipmentProNumService proNumService;
    @Autowired
    private DzProductionLineMapper dzProductionLineMapper;
    @Autowired
    private DzEquipmentProNumSignalMapper dzEquipmentProNumSignalMapper;
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    public DzEquipmentProNumMapper dzEquipmentProNumMapper;
    @Autowired
    private DzWorkpieceDataMapper workpieceDataMapper;
    @Autowired
    public DzProductionPlanDayMapper dzProductionPlanDayMapper;

    /**
     * 根据订单和产线序号查询该产线下（所有机器人和机床）的（日产、总产）
     *
     * @param orderNoLineNo
     * @return
     */
    public Result getDeviceProductionQuantity(GetOrderNoLineNo orderNoLineNo) {
        orderNoLineNo.setCacheTime(orderNoLineNo.getCacheTime() + ((int) (Math.random() * 10)));
        String key = RedisKey.BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_DEVICEPRODUCTION_QUANTITY + orderNoLineNo.getOrderNo() + orderNoLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Result o = (Result) redisUtil.get(key);
                o.setRef(false);
                return o;
            }
            LocalDate now = LocalDate.now();
            List<DzEquipment> dzEquipments = dzEquipmentService.getDeviceOrderNoLineNo(orderNoLineNo.getOrderNo(), orderNoLineNo.getLineNo());
            if (CollectionUtils.isNotEmpty(dzEquipments)) {
                List<String> collect = dzEquipments.stream().map(s -> s.getId().toString()).collect(Collectors.toList());
                List<MachiningNumTotal> machiningNumTotalsX = machiningNumTotals(now, collect);
                Result<List<MachiningNumTotal>> ok = Result.ok(machiningNumTotalsX);
                redisUtil.set(key, ok, orderNoLineNo.getCacheTime());
                return ok;
            }
            return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
        } catch (Exception e) {
            log.error("根据订单和产线序号查询设备日产总产异常:{}", e.getMessage(), e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
    }

    private List<MachiningNumTotal> machiningNumTotals(LocalDate now, List<String> collect) {
        String tableKey = ((RunDataModel) cacheService.systemRunModel(null).getData()).getTableName();
        List<MachiningNumTotal> machiningNumTotals = proNumService.getEqIdData(now, collect, tableKey);
        List<MachiningNumTotal> machiningNumTotalsX = new ArrayList<>();
        for (MachiningNumTotal machiningNumTotal : machiningNumTotals) {
            if (machiningNumTotal != null) {
                machiningNumTotalsX.add(machiningNumTotal);
            }
        }
        return machiningNumTotalsX;
    }

    /**
     * 获取产线绑定的计数设备的日产
     *
     * @return
     */
    public Result getLineSumQuantity(GetOrderNoLineNo orderNoLineNo) {
        orderNoLineNo.setCacheTime(orderNoLineNo.getCacheTime() + ((int) (Math.random() * 10)));
        String key = RedisKey.BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_LINE_SUM_QUANTITY + orderNoLineNo.getOrderNo() + orderNoLineNo.getLineNo();
        if (redisUtil.hasKey(key)) {
            Result o = (Result) redisUtil.get(key);
            o.setRef(false);
            return o;
        }
        LocalDate now = LocalDate.now();
        String tableKey = ((RunDataModel) cacheService.systemRunModel(null).getData()).getTableName();
        String orderNo = orderNoLineNo.getOrderNo();
        String lineNo = orderNoLineNo.getLineNo();
        Long eqId = null;
        Object eqIdCache = redisUtil.get(RedisKey.dzProductionLineMapperGetLineEqmentId + orderNo + lineNo);
        if (eqIdCache != null) {
            eqId = Long.valueOf(eqIdCache.toString());
        } else {
            eqId = dzProductionLineMapper.getLineEqmentId(orderNo, lineNo);
            int cahceTime = (int) ((Math.random() * 700) + 100);
            redisUtil.set(RedisKey.dzProductionLineMapperGetLineEqmentId + orderNo + lineNo, eqId, cahceTime);
        }
        String systemConfig = cacheService.getSystemConfigDepart();
        LineNumberTotal eqIdData = proNumService.getLineSumQuantity(now, eqId, tableKey, systemConfig);
        eqIdData.setDateStr(LocalDate.now().toString());
        Result ok = Result.ok(eqIdData);
        redisUtil.set(key, ok, orderNoLineNo.getCacheTime());
        return ok;
    }

    /**
     * 产线月生产 合格/不合格 数量
     * 产线月生产数据,只区分每月的合格不合格数量
     *
     * @param orderNoLineNo
     * @return
     */
    public Result getMonthData(GetOrderNoLineNo orderNoLineNo) {
        orderNoLineNo.setCacheTime(orderNoLineNo.getCacheTime() + ((int) (Math.random() * 10)));
        String key = RedisKey.BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_MONTH_DATA + orderNoLineNo.getOrderNo() + orderNoLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Result o = (Result) redisUtil.get(key);
                o.setRef(false);
                return o;
            }
            DzProductionLine dzProductionLine = cacheService.getLineIdByOrderNoLineNo(orderNoLineNo);
            if (dzProductionLine == null) {
                log.warn("以月为单位，查询本年生产数据失败，产线不存在。订单:{}， 产线序号:{}", orderNoLineNo.getOrderNo(), orderNoLineNo.getLineNo());
                return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
            }
            if (dzProductionLine.getStatisticsEquimentId() == null) {
                log.warn("产线未绑定记数设备,产线id:{}", dzProductionLine.getId());
                return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
            }
            //获取产线产量记数设备id
            Long eqId = dzProductionLine.getStatisticsEquimentId();
            String tableKey = ((RunDataModel) cacheService.systemRunModel(null).getData()).getTableName();
            List<Map<String, Object>> data = dzEquipmentProNumSignalMapper.getMonthData(tableKey, eqId);
            List<MonthData> monthData = new ArrayList<>();
            List<String> allMonth = dateUtil.getAllMonth();
            for (String str : allMonth) {
                MonthData month = new MonthData();
                month.setMonth(str);
                month.setQualified(0L);
                month.setRejects(0L);
                for (Map<String, Object> map : data) {
                    String workMouth = map.get("workMouth").toString();
                    if (workMouth.equals(str)) {
                        month.setQualified(Long.valueOf(map.get("qualified").toString()));
                        month.setRejects(Long.valueOf(map.get("rejects").toString()));
                        break;
                    }
                }
                monthData.add(month);
            }
            List<Long> ok = monthData.stream().map(p -> p.getQualified()).collect(Collectors.toList());
            List<Long> no = monthData.stream().map(p -> p.getRejects()).collect(Collectors.toList());
            GetMonthlyCapacityListDo getMonthlyCapacityListDo = new GetMonthlyCapacityListDo();
            getMonthlyCapacityListDo.setQualifiedList(ok);
            getMonthlyCapacityListDo.setBadnessList(no);
            Result res = Result.ok(getMonthlyCapacityListDo);
            redisUtil.set(key, res, orderNoLineNo.getCacheTime());
            return res;
        } catch (Exception e) {
            log.error("查询产线当年(以月为单位)生产(合格/不合格)数量异常:{}", e.getMessage(), e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }

    }

    /**
     * 订单产线号查询产线当日生产 合格/不合格数量，当日班次生产合格，不合格
     * 日产班次数据堆叠数据展示
     *
     * @return
     */
    public synchronized Result getMonthlyCapacityShift(GetOrderNoLineNo ol) {
        try {
            ol.setCacheTime(ol.getCacheTime() + ((int) (Math.random() * 10)));
            String lineNo = ol.getLineNo();
            String orderNo = ol.getOrderNo();
            String key = RedisKey.BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_MONTHLY_CAPACITY_SHIFT + orderNo + lineNo;
            if (redisUtil.hasKey(key)) {
                Result o = (Result) redisUtil.get(key);
                o.setRef(false);
                return o;
            }
            DzProductionLine line = cacheService.getLineIdByOrderNoLineNo(ol);
            if (line == null) {
                return Result.error(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
            }
            WorkShiftLouDi workShiftSumLouDi = getWorkShiftSumLouDi(line.getStatisticsEquimentId(), orderNo, lineNo);
            Result res = Result.ok(workShiftSumLouDi);
            redisUtil.set(key, res, ol.getCacheTime());
            return res;

        } catch (Exception e) {
            log.error("查询产线当月生产(合格/不合格)数量异常:{}", e.getMessage(), e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
    }

    private WorkShiftLouDi getWorkShiftSumLouDi(Long statisticsEquimentId, String orderNo, String lineNo) {
        List<WorkShiftSum> workShiftSumList = new ArrayList<>();
        String tableKey = ((RunDataModel) cacheService.systemRunModel(null).getData()).getTableName();
//     两个日期的所有日期
        List<String> mouthDate = dateUtil.getAllDay();
        LocalDate now = LocalDate.now();
        String month = now.toString().substring(0, 7);
        int monthValue = now.getMonthValue();
        List<Map<String, Object>> workShift = dzEquipmentProNumMapper.getWorkShift(tableKey, statisticsEquimentId, month);
        Map<String, List<Map<String, Object>>> shift = new HashMap<>();
        for (Map<String, Object> map : workShift) {
            String workName = map.get("workName").toString();
            List<Map<String, Object>> list = shift.get(workName);
            if (CollectionUtils.isNotEmpty(list)) {
                list.add(map);
            } else {
                list = new ArrayList<>();
                list.add(map);
                shift.put(workName, list);
            }
        }
        for (Map.Entry<String, List<Map<String, Object>>> stringListEntry : shift.entrySet()) {
            List<Map<String, Object>> value = stringListEntry.getValue();
            String key = stringListEntry.getKey();
            List<Object> longListOk = new ArrayList<>();
            List<Object> longListNg = new ArrayList<>();
            for (String s : mouthDate) {
                Map<String, Object> mapx = null;
                for (Map<String, Object> map : value) {
                    String string = map.get("workData").toString();
                    if (s.equals(string)) {
                        mapx = map;
                        break;
                    }
                }
                longListOk.add(mapx != null ? mapx.get("qualifiedNum") : 0);
                longListNg.add(mapx != null ? mapx.get("badnessNum") : 0);
            }
            WorkShiftSum shiftdataOK = new WorkShiftSum();
            shiftdataOK.setData(longListOk);
            shiftdataOK.setName(key + "OK");
            shiftdataOK.setStack(key);
            workShiftSumList.add(shiftdataOK);
            WorkShiftSum shiftdataNG = new WorkShiftSum();
            shiftdataNG.setData(longListNg);
            shiftdataNG.setName(key + "NG");
            shiftdataNG.setStack(key);
            workShiftSumList.add(shiftdataNG);
        }
        if (workShiftSumList.isEmpty()) {
            WorkShiftSum workShiftSum = new WorkShiftSum();
            workShiftSum.setMouthValue(Integer.toString(monthValue));
            workShiftSumList.add(workShiftSum);
        } else {
            workShiftSumList.get(0).setMouthValue(Integer.toString(monthValue));
        }
        List<DayCheckModel> checkModels = null;
        if("DZ-1955".equals(orderNo) || "DZ-1956".equals(orderNo)){
            checkModels = workpieceDataMapper.getDayWorkModelCjg(month,orderNo,lineNo);
        }else{
            checkModels = workpieceDataMapper.getDayWorkModel(month, orderNo, lineNo);
        }
        Map<String, List<String>> dayList = getStrings(checkModels, mouthDate);
        WorkShiftLouDi louDi = new WorkShiftLouDi();
        louDi.setWorkShiftSums(workShiftSumList);
        louDi.setX1(dayList.get("x1"));
        louDi.setX2(dayList.get("x2"));
        return louDi;
    }

    private List<WorkShiftSum> getWorkShiftSum(Long statisticsEquimentId) {
        List<WorkShiftSum> workShiftSumList = new ArrayList<>();
        String tableKey = ((RunDataModel) cacheService.systemRunModel(null).getData()).getTableName();
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int monthValue = now.getMonthValue();
        List<String> mouthDate = cacheService.getMouthDate(year, monthValue);
        String substring = LocalDate.now().toString().substring(0, 7);
        List<Map<String, Object>> workShift = dzEquipmentProNumMapper.getWorkShift(tableKey, statisticsEquimentId, substring);
        Map<String, List<Map<String, Object>>> shift = new HashMap<>();
        for (Map<String, Object> map : workShift) {
            String workName = map.get("workName").toString();
            List<Map<String, Object>> list = shift.get(workName);
            if (CollectionUtils.isNotEmpty(list)) {
                list.add(map);
            } else {
                list = new ArrayList<>();
                list.add(map);
                shift.put(workName, list);
            }
        }
        for (Map.Entry<String, List<Map<String, Object>>> stringListEntry : shift.entrySet()) {
            List<Map<String, Object>> value = stringListEntry.getValue();
            String key = stringListEntry.getKey();
            List<Object> longListOk = new ArrayList<>();
            List<Object> longListNg = new ArrayList<>();
            for (String s : mouthDate) {
                Map<String, Object> mapx = null;
                for (Map<String, Object> map : value) {
                    String string = map.get("workData").toString();
                    if (s.equals(string)) {
                        mapx = map;
                        break;
                    }
                }
                longListOk.add(mapx != null ? mapx.get("qualifiedNum") : 0);
                longListNg.add(mapx != null ? mapx.get("badnessNum") : 0);
            }
            WorkShiftSum shiftdataOK = new WorkShiftSum();
            shiftdataOK.setData(longListOk);
            shiftdataOK.setName(key + "OK");
            shiftdataOK.setStack(key);
            workShiftSumList.add(shiftdataOK);
            WorkShiftSum shiftdataNG = new WorkShiftSum();
            shiftdataNG.setData(longListNg);
            shiftdataNG.setName(key + "NG");
            shiftdataNG.setStack(key);
            workShiftSumList.add(shiftdataNG);
        }
        if (workShiftSumList.isEmpty()) {
            WorkShiftSum workShiftSum = new WorkShiftSum();
            workShiftSum.setMouthValue(Integer.toString(monthValue));
            workShiftSumList.add(workShiftSum);
        } else {
            workShiftSumList.get(0).setMouthValue(Integer.toString(monthValue));
        }
        return workShiftSumList;
    }

    private Map<String, List<String>> getStrings(List<DayCheckModel> checkModels, List<String> mouthDate) {
        Map<String, List<DayCheckModel>> dayMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(checkModels)) {
            for (DayCheckModel checkModel : checkModels) {
                String dayDate = checkModel.getDayDate();
                List<DayCheckModel> dayCheckModels = dayMap.get(dayDate);
                if (CollectionUtils.isNotEmpty(dayCheckModels)) {
                    dayCheckModels.add(checkModel);
                } else {
                    dayCheckModels = new ArrayList<>();
                    dayCheckModels.add(checkModel);
                    dayMap.put(dayDate, dayCheckModels);
                }
            }
        }
        List<String> dayListX1 = new ArrayList<>();
        List<String> dayListX2 = new ArrayList<>();
        for (String s : mouthDate) {
            String[] split = s.split("-");
            dayListX1.add(split[2]);
            List<DayCheckModel> dayCheckModels = dayMap.get(s);
            if (CollectionUtils.isNotEmpty(dayCheckModels)) {
                StringBuilder day = new StringBuilder();
                for (DayCheckModel checkModel : dayCheckModels) {
                    if (day.toString().length() > 0) {
                        day.append(",");
                    }
                    String modelName = checkModel.getModelName();
//                    如果首个字符是W则去除
                    if (modelName.startsWith("W")) {
                        modelName = modelName.substring(1);
                    }
                    day.append(modelName);
                }
                dayListX2.add(day.toString());
            } else {
                dayListX2.add("");
            }
        }
        Map<String, List<String>> map = new HashMap<>();
        map.put("x1", dayListX1);
        map.put("x2", dayListX2);
        return map;
    }

    /**
     * 根据订单产线号查询近五日产线计划分析
     * 参数：订单产线 功能：产线(计数设备) 完成率 产出率 合格率 折线图 计算方式：
     *
     * @return
     */
    public Result getPlanAnalysis(GetOrderNoLineNo getOrderNoLineNo) {
        getOrderNoLineNo.setCacheTime(getOrderNoLineNo.getCacheTime() + ((int) (Math.random() * 10)));
        String key = RedisKey.BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_PLAN_ANALYSIS + getOrderNoLineNo.getOrderNo() + getOrderNoLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Result o = (Result) redisUtil.get(key);
                o.setRef(false);
                return o;
            }

            DzProductionLine lineIdByOrderNoLineNo = cacheService.getLineIdByOrderNoLineNo(getOrderNoLineNo);
            if (lineIdByOrderNoLineNo == null) {
                return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
            }
//        完成率
            List<BigDecimal> percentageComplete = new ArrayList<>();
//       产出率
            List<BigDecimal> outputRate = new ArrayList<>();
//        合格率
            List<BigDecimal> passRate = new ArrayList<>();
            String tableKey = ((RunDataModel) cacheService.systemRunModel(null).getData()).getPlanDay();

            List<Map<String, BigDecimal>> list = dzProductionPlanDayMapper.getPlanAnalysis(lineIdByOrderNoLineNo.getId(), tableKey);
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
            Result ok = Result.ok(planAnalysisDo);
            redisUtil.set(key, ok, getOrderNoLineNo.getCacheTime());
            return ok;
        } catch (Exception e) {
            log.error("根据产线id查询近五日产线计划分析异常:{}", e.getMessage(), e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
    }


    private List<String> getFiveDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate date7 = LocalDate.now();
        LocalDate date6 = date7.plusDays(-1);
        LocalDate date5 = date6.plusDays(-1);
        LocalDate date4 = date5.plusDays(-1);
        LocalDate date3 = date4.plusDays(-1);
        List<String> list = new ArrayList<>();
        list.add(date3.format(formatter));
        list.add(date4.format(formatter));
        list.add(date5.format(formatter));
        list.add(date6.format(formatter));
        list.add(date7.format(formatter));
        return list;
    }

    /**
     * 获取五日内生产数量(NG和OK)
     * 产线的近五日产量,NG和OK
     */
    public Result getDataNgAndOk(GetOrderNoLineNo orderNoLineNo) {
        orderNoLineNo.setCacheTime(orderNoLineNo.getCacheTime() + ((int) (Math.random() * 10)));
        String key = RedisKey.GET_DATA_NG_AND_OK + orderNoLineNo.getOrderNo() + orderNoLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Result o = (Result) redisUtil.get(key);
                o.setRef(false);
                return o;
            }
            DzProductionLine lineIdByOrderNoLineNo = cacheService.getLineIdByOrderNoLineNo(orderNoLineNo);
            if (lineIdByOrderNoLineNo == null) {
                return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
            }
            String tableKey = ((RunDataModel) cacheService.systemRunModel(null).getData()).getTableName();
            DzProductionLine dzProductionLine = dzProductionLineMapper.selectById(lineIdByOrderNoLineNo.getId());
            GetOutputByLineIdDo getOutputByLineIdDo = new GetOutputByLineIdDo();
            getOutputByLineIdDo.setDateList(getFiveDate());
            if (dzProductionLine != null && dzProductionLine.getStatisticsEquimentId() != null) {
                //获取产线产量记数设备id
                Long eqId = dzProductionLine.getStatisticsEquimentId();
                //查询五日内生产数据 OK NG
                List<Map<String, Object>> data = dzEquipmentProNumMapper.getDataNgAndOk(eqId, tableKey);
                List<Long> ok = new ArrayList<>();
                List<Long> ng = new ArrayList<>();
                for (Map<String, Object> map : data) {
                    ok.add(Long.valueOf(map.get("ok").toString()));
                    ng.add(Long.valueOf(map.get("ng").toString()));
                }
                getOutputByLineIdDo.setList(ok);
                getOutputByLineIdDo.setNg(ng);
            } else {
                log.warn("查询产线绑定设备五日数据异常，产线id：{}", lineIdByOrderNoLineNo.getId());
                if (dzProductionLine != null) {
                    log.warn("查询产线绑定设备五日数据异常，产线绑定设备id：{}", dzProductionLine.getStatisticsEquimentId());
                }
                Long[] list = {0L, 0L, 0L, 0L, 0L};
                List<Long> longs = Arrays.asList(list);
                getOutputByLineIdDo.setList(longs);
                getOutputByLineIdDo.setNg(longs);
            }
            Result ok = Result.ok(getOutputByLineIdDo);
            redisUtil.set(key, ok, orderNoLineNo.getCacheTime());
            return ok;
        } catch (Exception e) {
            log.error("根据产线获取绑定设备的五日内产量异常:{}", e.getMessage(), e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
    }

}
