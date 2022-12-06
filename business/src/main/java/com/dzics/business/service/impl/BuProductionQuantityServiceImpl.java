package com.dzics.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.business.model.response.proddetection.HeaderClom;
import com.dzics.business.model.response.proddetection.ProDetection;
import com.dzics.business.service.BuProductionQuantityService;
import com.dzics.business.service.BusTimeAnalysisService;
import com.dzics.business.service.BusinessProductionPlanService;
import com.dzics.business.service.cache.DzDetectionTemplCache;
import com.dzics.business.util.RedisUtil;
import com.dzics.common.dao.*;
import com.dzics.common.enums.DeviceSocketSendStatus;
import com.dzics.common.enums.EquiTypeEnum;
import com.dzics.common.enums.Message;
import com.dzics.common.enums.WorkState;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.constant.FinalCode;
import com.dzics.common.model.constant.MomProgressStatus;
import com.dzics.common.model.constant.SysConfigDepart;
import com.dzics.common.model.custom.LineNumberTotal;
import com.dzics.common.model.custom.MachiningNumTotal;
import com.dzics.common.model.custom.SocketProQuantity;
import com.dzics.common.model.custom.SocketUtilization;
import com.dzics.common.model.dto.check.DayCheckModel;
import com.dzics.common.model.entity.*;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.common.model.response.*;
import com.dzics.common.model.response.charts.WorkShiftSum;
import com.dzics.common.model.response.charts.loudi.WorkShiftLouDi;
import com.dzics.common.model.response.homepage.QualifiedAndOutputDo;
import com.dzics.common.model.response.timeanalysis.*;
import com.dzics.common.service.*;
import com.dzics.common.util.DateUtil;
import com.dzics.common.util.RedisKey;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BuProductionQuantityServiceImpl implements BuProductionQuantityService {
    @Autowired
    public DzProductionLineMapper dzProductionLineMapper;
    @Autowired
    public DzDetectionTemplCache dzDetectionTemplCache;
    @Autowired
    public DzEquipmentProNumMapper dzEquipmentProNumMapper;
    @Autowired
    public DzEquipmentDowntimeRecordMapper dzEquipmentDowntimeRecordMapper;
    @Autowired
    public DzEquipmentMapper dzEquipmentMapper;
    @Autowired
    public DzProductionPlanDayMapper dzProductionPlanDayMapper;
    @Autowired
    public DzToolCompensationDataMapper dzToolCompensationDataMapper;
    @Autowired
    public DzEquipmentService dzEquipmentService;
    @Autowired
    public DzEquipmentProNumService proNumService;
    @Autowired
    public RedisUtil redisUtil;
    @Autowired
    DzWorkpieceDataMapper dzWorkpieceDataMapper;
    @Autowired
    private DzWorkpieceDataMapper workpieceDataMapper;
    @Autowired
    private DzWorkpieceDataService workpieceDataService;
    @Autowired
    private DzProductDetectionTemplateService dzProductDetectionTemplateService;
    @Autowired
    DateUtil dateUtil;
    @Autowired
    DzEquipmentProNumSignalMapper dzEquipmentProNumSignalMapper;
    @Autowired
    DzDetectionTemplCache cacheService;
    @Autowired
    DzProductionPlanMapper dzProductionPlanMapper;
    @Autowired
    DzLineShiftDayMapper dzLineShiftDayMapper;
    @Autowired
    DzEquipmentRunTimeMapper dzEquipmentRunTimeMapper;
    @Autowired
    private BusTimeAnalysisService busTimeAnalysisService;
    @Autowired
    private MomOrderService momOrderService;
    @Autowired
    DzEquipmentProNumSignalService dzEquipmentProNumSignalService;
    @Autowired
    BusinessProductionPlanService businessProductionPlanService;
    @Autowired
    DzProductionPlanDaySignalService dzProductionPlanDayService;
    @Autowired
    DzProductionPlanService dzProductionPlanService;
    @Autowired
    DzProductionLineService productionLineService;


    @Override
    public Result getOutputByLineId(GetOrderNoLineNo getOrderNoLineNo) {
        String key = RedisKey.BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_OUTPUT_BY_LINE_ID + getOrderNoLineNo.getOrderNo() + getOrderNoLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Result o = (Result) redisUtil.get(key);
                o.setRef(false);
                return o;
            }
            DzProductionLine lineIdByOrderNoLineNo = dzDetectionTemplCache.getLineIdByOrderNoLineNo(getOrderNoLineNo);
            if (lineIdByOrderNoLineNo == null) {
                return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
            }
            String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getTableName();
            DzProductionLine dzProductionLine = dzProductionLineMapper.selectById(lineIdByOrderNoLineNo.getId());
            GetOutputByLineIdDo getOutputByLineIdDo = new GetOutputByLineIdDo();
            getOutputByLineIdDo.setDateList(getFiveDate());
            if (dzProductionLine != null && dzProductionLine.getStatisticsEquimentId() != null) {
                //获取产线产量记数设备id
                Long eqId = dzProductionLine.getStatisticsEquimentId();
                //查询五日内生产数据
                List<Long> data = dzEquipmentProNumMapper.getOutputByEqId(eqId, tableKey);

                getOutputByLineIdDo.setList(data);
            } else {
                log.warn("查询产线绑定设备五日数据异常，产线id：{}", lineIdByOrderNoLineNo.getId());
                if (dzProductionLine != null) {
                    log.warn("查询产线绑定设备五日数据异常，产线绑定设备id：{}", dzProductionLine.getStatisticsEquimentId());
                }
                Long[] list = {0L, 0L, 0L, 0L, 0L};
                List<Long> longs = Arrays.asList(list);
                getOutputByLineIdDo.setList(longs);
            }
            Result ok = Result.ok(getOutputByLineIdDo);
            redisUtil.set(key, ok, getOrderNoLineNo.getCacheTime());
            return ok;
        } catch (Exception e) {
            log.error("根据产线获取绑定设备的五日内产量异常:{}", e.getMessage(), e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
    }

    @Override
    public Result getEquipmentAvailable(GetOrderNoLineNo getOrderNoLineNo) {
        String key = RedisKey.BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_EQUIPMENT_AVAILABLE + getOrderNoLineNo.getOrderNo() + getOrderNoLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Result o = (Result) redisUtil.get(key);
                o.setRef(false);
                return o;
            }
            DzProductionLine lineIdByOrderNoLineNo = dzDetectionTemplCache.getLineIdByOrderNoLineNo(getOrderNoLineNo);
            if (lineIdByOrderNoLineNo == null) {
                return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
            }
            EquipmentAvailableDo equipmentAvailableDo = new EquipmentAvailableDo();
            List<DzEquipment> yName = getLineIdIsShow(lineIdByOrderNoLineNo.getId(), FinalCode.IS_SHOW);
            List<String> eqName = new ArrayList<>();
            List<String> timeRun = new ArrayList<>();
            List<String> stopTime = new ArrayList<>();
            long todayPastTime = dateUtil.getStartDate();//当日凌晨12点的时间戳
            for (DzEquipment dzEquipment : yName) {
                eqName.add(dzEquipment.getNickName());
                String equipmentNo = dzEquipment.getEquipmentNo();
                Integer equipmentType = dzEquipment.getEquipmentType();
                Long runTimeData = busTimeAnalysisService.getEquipmentAvailable(dzEquipment.getId());
//                List<Map<String, Object>> runTimeList = dzEquipmentRunTimeMapper.getRunTime(getOrderNoLineNo.getLineNo(), getOrderNoLineNo.getOrderNo(), equipmentNo, equipmentType, localDate);
//                for (Map<String, Object> map : runTimeList) {
//                    Long duration = (Long) map.get("duration");
//                    if (duration == null || duration.longValue() == 0) {
////                    当前时间戳
//                        Long resetTime = System.currentTimeMillis();
////                    启动时间
//                        Long stop = ((Date) map.get("stopTime")).getTime();
//                        duration = resetTime.longValue() - stop.longValue();
//                    }
//                    runTimeData += duration;
//                }

                BigDecimal bigDecimal = new BigDecimal(runTimeData);
//               运行小时
                BigDecimal divide = bigDecimal.divide(new BigDecimal(3600000), 2, BigDecimal.ROUND_HALF_UP);
//                当日已过时间
                BigDecimal dd = new BigDecimal(todayPastTime).divide(new BigDecimal(3600000), 2, BigDecimal.ROUND_HALF_UP);
                timeRun.add(divide.toString());//运行时间
                //当前停机时间等于 当日已过时间-当前运行时间
                BigDecimal subtract = dd.subtract(divide);
                stopTime.add(subtract.toString());//停机时间
            }
            equipmentAvailableDo.setEqName(eqName);
            equipmentAvailableDo.setTimeRun(timeRun);
            equipmentAvailableDo.setStopTime(stopTime);
            Result ok = Result.ok(equipmentAvailableDo);
            redisUtil.set(key, ok, getOrderNoLineNo.getCacheTime());
            return ok;
        } catch (Exception e) {
            log.error("根据产线查询所有设备当日用时分析(旧)异常:{}", e.getMessage(), e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
    }

    @Override
    public Result getProductionPlanFiveDay(GetOrderNoLineNo getOrderNoLineNo) {
        String key = RedisKey.BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_PRODUCTION_PLAN_FIVE_DAY + getOrderNoLineNo.getOrderNo() + getOrderNoLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Result o = (Result) redisUtil.get(key);
                o.setRef(false);
                return o;
            }
            DzProductionLine lineIdByOrderNoLineNo = dzDetectionTemplCache.getLineIdByOrderNoLineNo(getOrderNoLineNo);
            if (lineIdByOrderNoLineNo == null) {
                return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
            }
            String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getPlanDay();
            List<BigDecimal> list = dzProductionPlanDayMapper.getProductionPlanFiveDay(lineIdByOrderNoLineNo.getId(), tableKey);
            ProductionPlanFiveDayDo res = new ProductionPlanFiveDayDo();
            res.setList(list);
            res.setDateList(getFiveDate());
            Result ok = Result.ok(list);
            redisUtil.set(key, ok, getOrderNoLineNo.getCacheTime());
            return ok;
        } catch (Exception e) {
            log.error("根据产线id查询近五日稼动率异常:{}", e.getMessage(), e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }

    }

    @Override
    public Result getPlanAnalysis(GetOrderNoLineNo getOrderNoLineNo) {
        String key = RedisKey.BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_PLAN_ANALYSIS + getOrderNoLineNo.getOrderNo() + getOrderNoLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Result o = (Result) redisUtil.get(key);
                o.setRef(false);
                return o;
            }

            DzProductionLine lineIdByOrderNoLineNo = dzDetectionTemplCache.getLineIdByOrderNoLineNo(getOrderNoLineNo);
            if (lineIdByOrderNoLineNo == null) {
                return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
            }
//        完成率
            List<BigDecimal> percentageComplete = new ArrayList<>();
//       产出率
            List<BigDecimal> outputRate = new ArrayList<>();
//        合格率
            List<BigDecimal> passRate = new ArrayList<>();
            String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getPlanDay();

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

    @Override
    public Result getToolInfoData(GetOrderNoLineNo getOrderNoLineNo) {
        String key = RedisKey.BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_TOOL_INFO_DATA + getOrderNoLineNo.getOrderNo() + getOrderNoLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Result o = (Result) redisUtil.get(RedisKey.BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_TOOL_INFO_DATA);
                o.setRef(false);
                return o;
            }
            DzProductionLine lineIdByOrderNoLineNo = dzDetectionTemplCache.getLineIdByOrderNoLineNo(getOrderNoLineNo);
            if (lineIdByOrderNoLineNo == null) {
                return null;
            }
            List<Long> eqIdList = dzToolCompensationDataMapper.getEqIds();
            if (eqIdList.size() == 0) {
                return null;
            }
            //查询机床
            QueryWrapper<DzEquipment> wrapper = new QueryWrapper();
            wrapper.eq("line_id", lineIdByOrderNoLineNo.getId());
            wrapper.eq("equipment_type", FinalCode.TOOL_EQUIPMENT_CODE);
            wrapper.in("equipment_no", FinalCode.DZ_TOOL_CODE_A2);
            List<DzEquipment> dzEquipments = dzEquipmentMapper.selectList(wrapper);
            List<GetToolInfoDataDo> dataList = new ArrayList<>();
            List<DzToolCompensationData> dzToolCompensationData = dzToolCompensationDataMapper.selectList(new QueryWrapper<>());
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
                        toolDataDo.setToolNo(data.getToolNo() != null ? "T" + data.getToolNo().toString() : null);
                        toolDataDos.add(toolDataDo);
                    }
                }
                //填充设备绑定的刀具信息集合
                getToolInfoDataDo.setToolDataList(toolDataDos);
                //填充数据到设备集合里面
                dataList.add(getToolInfoDataDo);
            }
            Result ok = Result.ok(dataList);
            redisUtil.set(key, ok, getOrderNoLineNo.getCacheTime());
            return ok;
        } catch (Exception e) {
            log.error("查询刀具信息数据异常:{}", e.getMessage(), e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
    }

    @Override
    public Result getMonthlyCapacity(GetOrderNoLineNo getOrderNoLineNo) {
        String key = RedisKey.BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_MONTHLY_CAPACITY + getOrderNoLineNo.getOrderNo() + getOrderNoLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Result o = (Result) redisUtil.get(key);
                o.setRef(false);
                return o;
            }
            DzProductionLine lineIdByOrderNoLineNo = dzDetectionTemplCache.getLineIdByOrderNoLineNo(getOrderNoLineNo);
            if (lineIdByOrderNoLineNo == null) {
                return Result.error(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
            }
            String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getTableName();
            List<GetMonthlyCapacityDo> data = dzEquipmentProNumMapper.getMonthlyCapacity(tableKey, lineIdByOrderNoLineNo.getStatisticsEquimentId());
            List<Long> ok = new ArrayList<>();
            List<Long> no = new ArrayList<>();
            for (GetMonthlyCapacityDo capacityDo : data) {
                ok.add(capacityDo.getQualified() != null ? capacityDo.getQualified() : 0L);
                no.add(capacityDo.getBadness() != null ? capacityDo.getBadness() : 0L);
            }
            GetMonthlyCapacityListDo getMonthlyCapacityListDo = new GetMonthlyCapacityListDo();
            getMonthlyCapacityListDo.setQualifiedList(ok);
            getMonthlyCapacityListDo.setBadnessList(no);
            Result res = Result.ok(getMonthlyCapacityListDo);
            redisUtil.set(key, res, getOrderNoLineNo.getCacheTime());
            return res;
        } catch (Exception e) {
            log.error("查询产线当月生产(合格/不合格)数量异常:{}", e.getMessage(), e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
    }

    @Override
    public synchronized Result getMonthlyCapacityShift(GetOrderNoLineNo ol) {
        String lineNo = ol.getLineNo();
        String orderNo = ol.getOrderNo();
        String key = RedisKey.BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_MONTHLY_CAPACITY_SHIFT + orderNo + lineNo;
        try {
            if (redisUtil.hasKey(key)) {
                Result o = (Result) redisUtil.get(key);
                o.setRef(false);
                return o;
            }
            DzProductionLine line = dzDetectionTemplCache.getLineIdByOrderNoLineNo(ol);
            if (line == null) {
                return Result.error(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
            }
//            ============================
            String orgCode = ol.getOrgCode();
            if (SysConfigDepart.SANY.equals(orgCode)) {
                WorkShiftLouDi workShiftSumLouDi = getWorkShiftSumLouDi(line.getStatisticsEquimentId(), orderNo, lineNo);
                Result res = Result.ok(workShiftSumLouDi);
                redisUtil.set(key, res, ol.getCacheTime());
                return res;
            } else {
                List<WorkShiftSum> workShiftSum = getWorkShiftSum(line.getStatisticsEquimentId());
                Result res = Result.ok(workShiftSum);
                redisUtil.set(key, res, ol.getCacheTime());
                return res;
            }
            //            ============================
        } catch (Exception e) {
            log.error("查询产线当月生产(合格/不合格)数量异常:{}", e.getMessage(), e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
    }

    private WorkShiftLouDi getWorkShiftSumLouDi(Long statisticsEquimentId, String orderNo, String lineNo) {
        List<WorkShiftSum> workShiftSumList = new ArrayList<>();
        String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getTableName();
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int monthValue = now.getMonthValue();
        List<String> mouthDate = dzDetectionTemplCache.getMouthDate(year, monthValue);
        String month = LocalDate.now().toString().substring(0, 7);
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
        List<DayCheckModel> checkModels = workpieceDataMapper.getDayWorkModel(month, orderNo, lineNo);
        Map<String, List<String>> dayList = getStrings(checkModels, mouthDate);
        WorkShiftLouDi louDi = new WorkShiftLouDi();
        louDi.setWorkShiftSums(workShiftSumList);
        louDi.setX1(dayList.get("x1"));
        louDi.setX2(dayList.get("x2"));
        return louDi;
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

    @Override
    public List<WorkShiftSum> getWorkShiftSum(Long statisticsEquimentId) {
        List<WorkShiftSum> workShiftSumList = new ArrayList<>();
        String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getTableName();
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int monthValue = now.getMonthValue();
        List<String> mouthDate = dzDetectionTemplCache.getMouthDate(year, monthValue);
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

    /**
     * 查询产品最新的4条检测数据
     *
     * @param getOrderNoLineNo
     * @return
     */
    @Override
    public Result inspectionData(GetOrderNoLineNo getOrderNoLineNo) {
        String key = RedisKey.GET_INSPECTION_DATA + getOrderNoLineNo.getOrderNo() + getOrderNoLineNo.getLineNo();
        if (redisUtil.hasKey(key)) {
            Result o = (Result) redisUtil.get(key);
            o.setRef(false);
            return o;
        } else {
            String orderNo = getOrderNoLineNo.getOrderNo();
            String lineNo = getOrderNoLineNo.getLineNo();
            List<String> infoList = workpieceDataService.getNewestThreeDataId(orderNo, lineNo, 10);
            JCEquimentBase jcEquimentBase = new JCEquimentBase();
            if (CollectionUtils.isNotEmpty(infoList)) {
                List<Map<String, Object>> list = workpieceDataService.newestThreeData(infoList);
                if (CollectionUtils.isNotEmpty(list)) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    for (Map<String, Object> objectMap : list) {
                        Object detectorTime = objectMap.get("detectorTime");
                        String format = simpleDateFormat.format(detectorTime);
                        objectMap.put("detectorTime", format);

                        if (objectMap.get("out_ok01") != null && objectMap.get("out_ok01").toString().equals("0")) {
                            objectMap.put("detect01", objectMap.get("detect01") + "::");
                        }
                        if (objectMap.get("out_ok02") != null && objectMap.get("out_ok02").toString().equals("0")) {
                            objectMap.put("detect02", objectMap.get("detect02") + "::");
                        }
                        if (objectMap.get("out_ok03") != null && objectMap.get("out_ok03").toString().equals("0")) {
                            objectMap.put("detect03", objectMap.get("detect03") + "::");
                        }
                        if (objectMap.get("out_ok04") != null && objectMap.get("out_ok04").toString().equals("0")) {
                            objectMap.put("detect04", objectMap.get("detect04") + "::");
                        }
                        if (objectMap.get("out_ok05") != null && objectMap.get("out_ok05").toString().equals("0")) {
                            objectMap.put("detect05", objectMap.get("detect05") + "::");
                        }
                        if (objectMap.get("out_ok06") != null && objectMap.get("out_ok06").toString().equals("0")) {
                            objectMap.put("detect06", objectMap.get("detect06") + "::");
                        }
                        if (objectMap.get("out_ok07") != null && objectMap.get("out_ok07").toString().equals("0")) {
                            objectMap.put("detect07", objectMap.get("detect07") + "::");
                        }
                        if (objectMap.get("out_ok08") != null && objectMap.get("out_ok08").toString().equals("0")) {
                            objectMap.put("detect08", objectMap.get("detect08") + "::");
                        }
                        if (objectMap.get("out_ok09") != null && objectMap.get("out_ok09").toString().equals("0")) {
                            objectMap.put("detect09", objectMap.get("detect09") + "::");
                        }
                        if (objectMap.get("out_ok10") != null && objectMap.get("out_ok10").toString().equals("0")) {
                            objectMap.put("detect10", objectMap.get("detect10") + "::");
                        }
                        if (objectMap.get("out_ok11") != null && objectMap.get("out_ok11").toString().equals("0")) {
                            objectMap.put("detect11", objectMap.get("detect11") + "::");
                        }
                        if (objectMap.get("out_ok12") != null && objectMap.get("out_ok12").toString().equals("0")) {
                            objectMap.put("detect12", objectMap.get("detect12") + "::");
                        }
                        if (objectMap.get("out_ok13") != null && objectMap.get("out_ok13").toString().equals("0")) {
                            objectMap.put("detect13", objectMap.get("detect13") + "::");
                        }
                        if (objectMap.get("out_ok14") != null && objectMap.get("out_ok14").toString().equals("0")) {
                            objectMap.put("detect14", objectMap.get("detect14") + "::");
                        }
                        if (objectMap.get("out_ok15") != null && objectMap.get("out_ok15").toString().equals("0")) {
                            objectMap.put("detect15", objectMap.get("detect15") + "::");
                        }
                        if (objectMap.get("out_ok16") != null && objectMap.get("out_ok16").toString().equals("0")) {
                            objectMap.put("detect16", objectMap.get("detect16") + "::");
                        }
                        if (objectMap.get("out_ok17") != null && objectMap.get("out_ok17").toString().equals("0")) {
                            objectMap.put("detect17", objectMap.get("detect17") + "::");
                        }
                        if (objectMap.get("out_ok18") != null && objectMap.get("out_ok18").toString().equals("0")) {
                            objectMap.put("detect18", objectMap.get("detect18") + "::");
                        }
                        if (objectMap.get("out_ok19") != null && objectMap.get("out_ok19").toString().equals("0")) {
                            objectMap.put("detect19", objectMap.get("detect19") + "::");
                        }
                        if (objectMap.get("out_ok20") != null && objectMap.get("out_ok20").toString().equals("0")) {
                            objectMap.put("detect20", objectMap.get("detect20") + "::");
                        }
                        if (objectMap.get("out_ok21") != null && objectMap.get("out_ok21").toString().equals("0")) {
                            objectMap.put("detect21", objectMap.get("detect21") + "::");
                        }
                        if (objectMap.get("out_ok22") != null && objectMap.get("out_ok22").toString().equals("0")) {
                            objectMap.put("detect22", objectMap.get("detect22") + "::");
                        }
                        if (objectMap.get("out_ok23") != null && objectMap.get("out_ok23").toString().equals("0")) {
                            objectMap.put("detect23", objectMap.get("detect23") + "::");
                        }
                        if (objectMap.get("out_ok24") != null && objectMap.get("out_ok24").toString().equals("0")) {
                            objectMap.put("detect24", objectMap.get("detect24") + "::");
                        }
                        if (objectMap.get("out_ok25") != null && objectMap.get("out_ok25").toString().equals("0")) {
                            objectMap.put("detect25", objectMap.get("detect25") + "::");
                        }
                        if (objectMap.get("out_ok26") != null && objectMap.get("out_ok26").toString().equals("0")) {
                            objectMap.put("detect26", objectMap.get("detect26") + "::");
                        }
                        if (objectMap.get("out_ok27") != null && objectMap.get("out_ok27").toString().equals("0")) {
                            objectMap.put("detect27", objectMap.get("detect27") + "::");
                        }
                        if (objectMap.get("out_ok28") != null && objectMap.get("out_ok28").toString().equals("0")) {
                            objectMap.put("detect28", objectMap.get("detect28") + "::");
                        }

                    }
                    String productNo = list.get(0).get("productNo").toString();
                    List<Map<String, Object>> templates = dzProductDetectionTemplateService.listProductNo(productNo, orderNo, lineNo);
                    List<HeaderClom> headList = new ArrayList<>();
                    for (Map<String, Object> map : templates) {
                        HeaderClom headerClom = new HeaderClom();
                        headerClom.setColName(map.get("colName").toString());
                        headerClom.setColData(map.get("colData").toString());
                        headList.add(headerClom);
                    }
                    ProDetection proDetection = new ProDetection();
                    proDetection.setTableColumn(headList);
                    proDetection.setTableData(list);
                    jcEquimentBase.setData(proDetection);
                }
            }
            jcEquimentBase.setType(DeviceSocketSendStatus.FOUR_PRODUCT_TEST_DATA.getInfo());
            Result<JCEquimentBase> ok = Result.ok(jcEquimentBase);
            redisUtil.set(key, ok, getOrderNoLineNo.getCacheTime());
            return ok;
        }


    }

    @Override
    public Result getDeviceproductionQuantity(GetOrderNoLineNo getOrderNoLineNo) {
        String key = RedisKey.BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_DEVICEPRODUCTION_QUANTITY + getOrderNoLineNo.getOrderNo() + getOrderNoLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Result o = (Result) redisUtil.get(key);
                o.setRef(false);
                return o;
            }
            LocalDate now = LocalDate.now();
            List<DzEquipment> dzEquipments = dzEquipmentService.getDeviceOrderNoLineNo(getOrderNoLineNo.getOrderNo(), getOrderNoLineNo.getLineNo());
            if (CollectionUtils.isNotEmpty(dzEquipments)) {
                List<String> collect = dzEquipments.stream().map(s -> s.getId().toString()).collect(Collectors.toList());
                List<MachiningNumTotal> machiningNumTotalsX = machiningNumTotals(now, collect);
                Result<List<MachiningNumTotal>> ok = Result.ok(machiningNumTotalsX);
                redisUtil.set(key, ok, getOrderNoLineNo.getCacheTime());
                return ok;
            }
            return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
        } catch (Exception e) {
            log.error("根据订单和产线序号查询设备日产总产异常:{}", e.getMessage(), e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
    }


    @Override
    public List<MachiningNumTotal> machiningNumTotals(LocalDate now, List<String> collect) {
        String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getTableName();
        List<MachiningNumTotal> machiningNumTotals = proNumService.getEqIdData(now, collect, tableKey);
        List<MachiningNumTotal> machiningNumTotalsX = new ArrayList<>();
        for (MachiningNumTotal machiningNumTotal : machiningNumTotals) {
            if (machiningNumTotal != null) {
                machiningNumTotalsX.add(machiningNumTotal);
            }
        }
        return machiningNumTotalsX;
    }

    @Override
    public Result getLineSumQuantity(GetOrderNoLineNo orderNoLineNo) {
        String key = RedisKey.BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_LINE_SUM_QUANTITY + orderNoLineNo.getOrderNo() + orderNoLineNo.getLineNo();
        if (redisUtil.hasKey(key)) {
            Result o = (Result) redisUtil.get(key);
            o.setRef(false);
            return o;
        }
        LocalDate now = LocalDate.now();
        String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getTableName();
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
     * 查询未绑二维码的检测数据(未绑定二维码的检测记录)
     *
     * @param getOrderNoLineNo
     * @return
     */
    @Override
    public Result unBoundQrCode(GetOrderNoLineNo getOrderNoLineNo) {
        String key = RedisKey.GET_UN_BOUND_QR_CODE + getOrderNoLineNo.getOrderNo() + getOrderNoLineNo.getLineNo();
        Result o = (Result) redisUtil.get(key);
        if (o != null) {
            o.setRef(false);
            return o;
        } else {
            PageHelper.startPage(1, 10);
            QueryWrapper<DzWorkpieceData> wrapper = new QueryWrapper<>();
            wrapper.eq("qr_code", FinalCode.UN_BOUND_QR_CODE);
            wrapper.eq("order_no", getOrderNoLineNo.getOrderNo());
            wrapper.orderByDesc("detector_time");
            wrapper.select("id");
            List<DzWorkpieceData> dzWorkpieceData = workpieceDataMapper.selectList(wrapper);
            PageInfo<DzWorkpieceData> info = new PageInfo<>(dzWorkpieceData);
            List<DzWorkpieceData> dataList = info.getList();
            JCEquimentBase jcEquimentBase = new JCEquimentBase();
            jcEquimentBase.setType(DeviceSocketSendStatus.UNBOUND_QR_CODE_DETECTION.getInfo());
            if (CollectionUtils.isNotEmpty(dataList)) {
                List<String> idList = dataList.stream().map(p -> p.getId()).collect(Collectors.toList());
//        原数据
                List<Map<String, Object>> list = workpieceDataMapper.notBoundQrCode(idList);
//
                if (!list.isEmpty()) {
                    //查询所有机床
                    QueryWrapper<DzEquipment> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("order_no", getOrderNoLineNo.getOrderNo());
                    queryWrapper.eq("line_no", getOrderNoLineNo.getLineNo());
                    queryWrapper.eq("equipment_type", FinalCode.TOOL_EQUIPMENT_CODE);//机床
                    queryWrapper.select("equipment_no", "equipment_name");
                    List<DzEquipment> dzEquipments = dzEquipmentMapper.selectList(queryWrapper);
                    Collections.reverse(list);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    for (Map<String, Object> objectMap : list) {
                        Object detectorTime = objectMap.get("detectorTime");
                        String format = simpleDateFormat.format(detectorTime);
                        objectMap.put("detectorTime", format);
                        objectMap.put("qrCode", null);
                        for (DzEquipment dzEquipment : dzEquipments) {
                            if (dzEquipment.getEquipmentNo().equals(objectMap.get("machine_number"))) {
                                objectMap.put("equipmentName", dzEquipment.getEquipmentName());
                            }
                        }
                        if (objectMap.get("out_ok01") != null && objectMap.get("out_ok01").toString().equals("0")) {
                            objectMap.put("detect01", objectMap.get("detect01") + "::");
                        }
                        if (objectMap.get("out_ok02") != null && objectMap.get("out_ok02").toString().equals("0")) {
                            objectMap.put("detect02", objectMap.get("detect02") + "::");
                        }
                        if (objectMap.get("out_ok03") != null && objectMap.get("out_ok03").toString().equals("0")) {
                            objectMap.put("detect03", objectMap.get("detect03") + "::");
                        }
                        if (objectMap.get("out_ok04") != null && objectMap.get("out_ok04").toString().equals("0")) {
                            objectMap.put("detect04", objectMap.get("detect04") + "::");
                        }
                        if (objectMap.get("out_ok05") != null && objectMap.get("out_ok05").toString().equals("0")) {
                            objectMap.put("detect05", objectMap.get("detect05") + "::");
                        }
                        if (objectMap.get("out_ok06") != null && objectMap.get("out_ok06").toString().equals("0")) {
                            objectMap.put("detect06", objectMap.get("detect06") + "::");
                        }
                        if (objectMap.get("out_ok07") != null && objectMap.get("out_ok07").toString().equals("0")) {
                            objectMap.put("detect07", objectMap.get("detect07") + "::");
                        }
                        if (objectMap.get("out_ok08") != null && objectMap.get("out_ok08").toString().equals("0")) {
                            objectMap.put("detect08", objectMap.get("detect08") + "::");
                        }
                        if (objectMap.get("out_ok09") != null && objectMap.get("out_ok09").toString().equals("0")) {
                            objectMap.put("detect09", objectMap.get("detect09") + "::");
                        }
                        if (objectMap.get("out_ok10") != null && objectMap.get("out_ok10").toString().equals("0")) {
                            objectMap.put("detect10", objectMap.get("detect10") + "::");
                        }
                        if (objectMap.get("out_ok11") != null && objectMap.get("out_ok11").toString().equals("0")) {
                            objectMap.put("detect11", objectMap.get("detect11") + "::");
                        }
                        if (objectMap.get("out_ok12") != null && objectMap.get("out_ok12").toString().equals("0")) {
                            objectMap.put("detect12", objectMap.get("detect12") + "::");
                        }
                        if (objectMap.get("out_ok13") != null && objectMap.get("out_ok13").toString().equals("0")) {
                            objectMap.put("detect13", objectMap.get("detect13") + "::");
                        }
                        if (objectMap.get("out_ok14") != null && objectMap.get("out_ok14").toString().equals("0")) {
                            objectMap.put("detect14", objectMap.get("detect14") + "::");
                        }
                        if (objectMap.get("out_ok15") != null && objectMap.get("out_ok15").toString().equals("0")) {
                            objectMap.put("detect15", objectMap.get("detect15") + "::");
                        }
                        if (objectMap.get("out_ok16") != null && objectMap.get("out_ok16").toString().equals("0")) {
                            objectMap.put("detect16", objectMap.get("detect16") + "::");
                        }
                        if (objectMap.get("out_ok17") != null && objectMap.get("out_ok17").toString().equals("0")) {
                            objectMap.put("detect17", objectMap.get("detect17") + "::");
                        }
                        if (objectMap.get("out_ok18") != null && objectMap.get("out_ok18").toString().equals("0")) {
                            objectMap.put("detect18", objectMap.get("detect18") + "::");
                        }
                        if (objectMap.get("out_ok19") != null && objectMap.get("out_ok19").toString().equals("0")) {
                            objectMap.put("detect19", objectMap.get("detect19") + "::");
                        }
                        if (objectMap.get("out_ok20") != null && objectMap.get("out_ok20").toString().equals("0")) {
                            objectMap.put("detect20", objectMap.get("detect20") + "::");
                        }
                        if (objectMap.get("out_ok21") != null && objectMap.get("out_ok21").toString().equals("0")) {
                            objectMap.put("detect21", objectMap.get("detect21") + "::");
                        }
                        if (objectMap.get("out_ok22") != null && objectMap.get("out_ok22").toString().equals("0")) {
                            objectMap.put("detect22", objectMap.get("detect22") + "::");
                        }
                        if (objectMap.get("out_ok23") != null && objectMap.get("out_ok23").toString().equals("0")) {
                            objectMap.put("detect23", objectMap.get("detect23") + "::");
                        }
                        if (objectMap.get("out_ok24") != null && objectMap.get("out_ok24").toString().equals("0")) {
                            objectMap.put("detect24", objectMap.get("detect24") + "::");
                        }
                        if (objectMap.get("out_ok25") != null && objectMap.get("out_ok25").toString().equals("0")) {
                            objectMap.put("detect25", objectMap.get("detect25") + "::");
                        }
                        if (objectMap.get("out_ok26") != null && objectMap.get("out_ok26").toString().equals("0")) {
                            objectMap.put("detect26", objectMap.get("detect26") + "::");
                        }
                        if (objectMap.get("out_ok27") != null && objectMap.get("out_ok27").toString().equals("0")) {
                            objectMap.put("detect27", objectMap.get("detect27") + "::");
                        }
                        if (objectMap.get("out_ok28") != null && objectMap.get("out_ok28").toString().equals("0")) {
                            objectMap.put("detect28", objectMap.get("detect28") + "::");
                        }

                    }
                    String productNo = list.get(0).get("productNo").toString();
                    List<Map<String, Object>> templates = dzProductDetectionTemplateService.listProductNo(productNo, getOrderNoLineNo.getOrderNo(), getOrderNoLineNo.getLineNo());
                    List<HeaderClom> headList = new ArrayList<>();
                    for (Map<String, Object> map : templates) {
                        HeaderClom headerClom = new HeaderClom();
                        headerClom.setColName(map.get("colName").toString());
                        headerClom.setColData(map.get("colData").toString());
                        headList.add(headerClom);
                    }
                    ProDetection proDetection = new ProDetection();
                    proDetection.setTableColumn(headList);
                    proDetection.setTableData(list);
                    jcEquimentBase.setData(proDetection);
                }
            } else {
                log.warn("未查询到未绑定二维码的检测记录");
            }
            Result<JCEquimentBase> ok = Result.ok(jcEquimentBase);
            redisUtil.set(key, ok, getOrderNoLineNo.getCacheTime());
            return ok;
        }

    }

    @Override
    public Result boundQrCode(GetOrderNoLineNo getOrderNoLineNo) {
        String key = RedisKey.GET_BUUND_QR_CODE + getOrderNoLineNo.getOrderNo() + getOrderNoLineNo.getLineNo();
        Result o = (Result) redisUtil.get(key);
        if (o != null) {
            o.setRef(false);
            return o;
        } else {
            PageHelper.startPage(1, 10);
            QueryWrapper<DzWorkpieceData> wrapper = new QueryWrapper<>();
            wrapper.ne("qr_code", FinalCode.UN_BOUND_QR_CODE);
            wrapper.eq("order_no", getOrderNoLineNo.getOrderNo());
            wrapper.orderByDesc("detector_time");
            wrapper.select("id");
            List<DzWorkpieceData> dzWorkpieceData = workpieceDataMapper.selectList(wrapper);
            PageInfo<DzWorkpieceData> info = new PageInfo<>(dzWorkpieceData);
            List<DzWorkpieceData> dataList = info.getList();
            JCEquimentBase jcEquimentBase = new JCEquimentBase();
            jcEquimentBase.setType(DeviceSocketSendStatus.QR_CODE_BOUND.getInfo());
            if (CollectionUtils.isNotEmpty(dataList)) {
                List<String> idList = dataList.stream().map(p -> p.getId()).collect(Collectors.toList());
//        原数据
                List<Map<String, Object>> list = workpieceDataMapper.notBoundQrCode(idList);

                if (!list.isEmpty()) {
                    Collections.reverse(list);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    for (Map<String, Object> objectMap : list) {
                        Object detectorTime = objectMap.get("detectorTime");
                        String format = simpleDateFormat.format(detectorTime);
                        objectMap.put("detectorTime", format);

                        if (objectMap.get("out_ok01") != null && objectMap.get("out_ok01").toString().equals("0")) {
                            objectMap.put("detect01", objectMap.get("detect01") + "::");
                        }
                        if (objectMap.get("out_ok02") != null && objectMap.get("out_ok02").toString().equals("0")) {
                            objectMap.put("detect02", objectMap.get("detect02") + "::");
                        }
                        if (objectMap.get("out_ok03") != null && objectMap.get("out_ok03").toString().equals("0")) {
                            objectMap.put("detect03", objectMap.get("detect03") + "::");
                        }
                        if (objectMap.get("out_ok04") != null && objectMap.get("out_ok04").toString().equals("0")) {
                            objectMap.put("detect04", objectMap.get("detect04") + "::");
                        }
                        if (objectMap.get("out_ok05") != null && objectMap.get("out_ok05").toString().equals("0")) {
                            objectMap.put("detect05", objectMap.get("detect05") + "::");
                        }
                        if (objectMap.get("out_ok06") != null && objectMap.get("out_ok06").toString().equals("0")) {
                            objectMap.put("detect06", objectMap.get("detect06") + "::");
                        }
                        if (objectMap.get("out_ok07") != null && objectMap.get("out_ok07").toString().equals("0")) {
                            objectMap.put("detect07", objectMap.get("detect07") + "::");
                        }
                        if (objectMap.get("out_ok08") != null && objectMap.get("out_ok08").toString().equals("0")) {
                            objectMap.put("detect08", objectMap.get("detect08") + "::");
                        }
                        if (objectMap.get("out_ok09") != null && objectMap.get("out_ok09").toString().equals("0")) {
                            objectMap.put("detect09", objectMap.get("detect09") + "::");
                        }
                        if (objectMap.get("out_ok10") != null && objectMap.get("out_ok10").toString().equals("0")) {
                            objectMap.put("detect10", objectMap.get("detect10") + "::");
                        }
                        if (objectMap.get("out_ok11") != null && objectMap.get("out_ok11").toString().equals("0")) {
                            objectMap.put("detect11", objectMap.get("detect11") + "::");
                        }
                        if (objectMap.get("out_ok12") != null && objectMap.get("out_ok12").toString().equals("0")) {
                            objectMap.put("detect12", objectMap.get("detect12") + "::");
                        }
                        if (objectMap.get("out_ok13") != null && objectMap.get("out_ok13").toString().equals("0")) {
                            objectMap.put("detect13", objectMap.get("detect13") + "::");
                        }
                        if (objectMap.get("out_ok14") != null && objectMap.get("out_ok14").toString().equals("0")) {
                            objectMap.put("detect14", objectMap.get("detect14") + "::");
                        }
                        if (objectMap.get("out_ok15") != null && objectMap.get("out_ok15").toString().equals("0")) {
                            objectMap.put("detect15", objectMap.get("detect15") + "::");
                        }
                        if (objectMap.get("out_ok16") != null && objectMap.get("out_ok16").toString().equals("0")) {
                            objectMap.put("detect16", objectMap.get("detect16") + "::");
                        }
                        if (objectMap.get("out_ok17") != null && objectMap.get("out_ok17").toString().equals("0")) {
                            objectMap.put("detect17", objectMap.get("detect17") + "::");
                        }
                        if (objectMap.get("out_ok18") != null && objectMap.get("out_ok18").toString().equals("0")) {
                            objectMap.put("detect18", objectMap.get("detect18") + "::");
                        }
                        if (objectMap.get("out_ok19") != null && objectMap.get("out_ok19").toString().equals("0")) {
                            objectMap.put("detect19", objectMap.get("detect19") + "::");
                        }
                        if (objectMap.get("out_ok20") != null && objectMap.get("out_ok20").toString().equals("0")) {
                            objectMap.put("detect20", objectMap.get("detect20") + "::");
                        }
                        if (objectMap.get("out_ok21") != null && objectMap.get("out_ok21").toString().equals("0")) {
                            objectMap.put("detect21", objectMap.get("detect21") + "::");
                        }
                        if (objectMap.get("out_ok22") != null && objectMap.get("out_ok22").toString().equals("0")) {
                            objectMap.put("detect22", objectMap.get("detect22") + "::");
                        }
                        if (objectMap.get("out_ok23") != null && objectMap.get("out_ok23").toString().equals("0")) {
                            objectMap.put("detect23", objectMap.get("detect23") + "::");
                        }
                        if (objectMap.get("out_ok24") != null && objectMap.get("out_ok24").toString().equals("0")) {
                            objectMap.put("detect24", objectMap.get("detect24") + "::");
                        }
                        if (objectMap.get("out_ok25") != null && objectMap.get("out_ok25").toString().equals("0")) {
                            objectMap.put("detect25", objectMap.get("detect25") + "::");
                        }
                        if (objectMap.get("out_ok26") != null && objectMap.get("out_ok26").toString().equals("0")) {
                            objectMap.put("detect26", objectMap.get("detect26") + "::");
                        }
                        if (objectMap.get("out_ok27") != null && objectMap.get("out_ok27").toString().equals("0")) {
                            objectMap.put("detect27", objectMap.get("detect27") + "::");
                        }
                        if (objectMap.get("out_ok28") != null && objectMap.get("out_ok28").toString().equals("0")) {
                            objectMap.put("detect28", objectMap.get("detect28") + "::");
                        }

                    }
                    String productNo = list.get(0).get("productNo").toString();
                    List<Map<String, Object>> templates = dzProductDetectionTemplateService.listProductNo(productNo, getOrderNoLineNo.getOrderNo(), getOrderNoLineNo.getLineNo());
                    List<HeaderClom> headList = new ArrayList<>();
                    for (Map<String, Object> map : templates) {
                        HeaderClom headerClom = new HeaderClom();
                        headerClom.setColName(map.get("colName").toString());
                        headerClom.setColData(map.get("colData").toString());
                        headList.add(headerClom);
                    }
                    ProDetection proDetection = new ProDetection();
                    proDetection.setTableColumn(headList);
                    proDetection.setTableData(list);
                    jcEquimentBase.setData(proDetection);
                }
            } else {
                log.warn("未查询到已经绑定二维码的检测记录");
            }
            Result<JCEquimentBase> ok = Result.ok(jcEquimentBase);
            redisUtil.set(key, ok, getOrderNoLineNo.getCacheTime());
            return ok;
        }

    }

    /**
     * 获取订单的下设备的 当日 投入 产出 不良品 数量
     *
     * @param orderNoLineNo
     * @return
     */
    @Override
    public Result getInputOutputDefectiveProducts(GetOrderNoLineNo orderNoLineNo) {
        String key = RedisKey.GET_INPUT_OUTPUT_DEFECTIVE_PRODUCTS + orderNoLineNo.getOrderNo() + orderNoLineNo.getLineNo();
        Result o = (Result) redisUtil.get(key);
        if (o != null) {
            o.setRef(false);
            return o;
        } else {
            LocalDate now = LocalDate.now();
            String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getTableName();
            List<SocketProQuantity> socketProQuantities = proNumService.getInputOutputDefectiveProducts(tableKey, now, orderNoLineNo.getOrderNo(), orderNoLineNo.getLineNo());
            Result<List<SocketProQuantity>> ok = Result.OK(socketProQuantities);
            redisUtil.set(key, ok, orderNoLineNo.getCacheTime());
            return ok;
        }
    }

    @Override
    public Result getSocketUtilization(GetOrderNoLineNo orderNoLineNo) {
        String key = RedisKey.GET_SOCKET_UTILIZATION + orderNoLineNo.getOrderNo() + orderNoLineNo.getLineNo();
        Result o = (Result) redisUtil.get(key);
        if (o != null) {
            o.setRef(false);
            return o;
        } else {
            try {
                List<SocketUtilization> socketUtilizations = dzEquipmentService.getSocketUtilizationList(orderNoLineNo.getOrderNo(), orderNoLineNo.getLineNo());
                Result<List<SocketUtilization>> ok = Result.OK(socketUtilizations);
                redisUtil.set(key, ok, orderNoLineNo.getCacheTime());
                return ok;
            } catch (Exception e) {
                Result result = new Result(CustomExceptionType.RedisNoGetError.getCode(), Message.ERR_131);
                redisUtil.set(key, result, orderNoLineNo.getCacheTime());
                return result;
            }

        }
    }

    @Override
    public Result getMonthData(GetOrderNoLineNo orderNoLineNo) {
        String key = RedisKey.BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_MONTH_DATA + orderNoLineNo.getOrderNo() + orderNoLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Result o = (Result) redisUtil.get(key);
                o.setRef(false);
                return o;
            }
            DzProductionLine dzProductionLine = dzDetectionTemplCache.getLineIdByOrderNoLineNo(orderNoLineNo);
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
            String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getTableName();
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
     * 产线月和班次生产 合格/不合格 数量,
     *
     * @param orderNoLineNo
     * @return
     */
    @Override
    public Result getMonthDataShift(GetOrderNoLineNo orderNoLineNo) {
        String key = RedisKey.BU_PRODUCTION_QUANTITY_SERVICE_IMPL_GET_MONTH_DATA_SHITF + orderNoLineNo.getOrderNo() + orderNoLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Result o = (Result) redisUtil.get(key);
                o.setRef(false);
                return o;
            }
            DzProductionLine dzProductionLine = dzDetectionTemplCache.getLineIdByOrderNoLineNo(orderNoLineNo);
            if (dzProductionLine == null) {
                log.warn("以月为单位，查询本年生产数据失败，产线不存在。订单:{}， 产线序号:{}", orderNoLineNo.getOrderNo(), orderNoLineNo.getLineNo());
                return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
            }
            Long statisticsEquimentId = dzProductionLine.getStatisticsEquimentId();
            if (statisticsEquimentId == null) {
                log.warn("产线未绑定记数设备,产线id:{}", dzProductionLine.getId());
                return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
            }
            List<WorkShiftSum> mouthWorkShiftSum = getMouthWorkShiftSum(statisticsEquimentId);
            Result res = Result.ok(mouthWorkShiftSum);
            redisUtil.set(key, res, orderNoLineNo.getCacheTime());
            return res;
        } catch (Exception e) {
            log.error("查询产线当年(以月为单位)生产(合格/不合格)数量异常:{}", e.getMessage(), e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }

    }

    @Override
    public List<WorkShiftSum> getMouthWorkShiftSum(Long eqId) {

        //获取产线产量记数设备id
        String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getTableName();
        List<String> allMonth = dateUtil.getAllMonth();
         /*   List<Map<String, Object>> data = dzEquipmentProNumSignalMapper.getMonthData(tableKey, eqId);
            List<Object> mouthOk = new ArrayList<>();
            List<Object> mouthNg = new ArrayList<>();
            for (String str : allMonth) {
                boolean falg = true;
                for (Map<String, Object> map : data) {
                    String workMouth = map.get("workMouth").toString();
                    if (workMouth.equals(str)) {
                        falg = false;
                        mouthOk.add(map.get("qualified"));
                        mouthNg.add(map.get("rejects"));
                        break;
                    }
                }
                if (falg) {
                    mouthOk.add(0);
                    mouthNg.add(0);
                }
            }
            WorkShiftSum workShiftSumOk = new WorkShiftSum();
            workShiftSumOk.setStack("月产");
            workShiftSumOk.setName("月产OK");
            workShiftSumOk.setData(mouthOk);
            WorkShiftSum workShiftSumNg = new WorkShiftSum();
            workShiftSumNg.setStack("月产");
            workShiftSumNg.setName("月产NG");
            workShiftSumNg.setData(mouthNg);
            shiftSums.add(workShiftSumOk);
            shiftSums.add(workShiftSumNg);*/
        String substring = LocalDate.now().toString().substring(0, 4);
        List<Map<String, Object>> dataShift = dzEquipmentProNumSignalMapper.getMonthDataShift(tableKey, eqId, Integer.valueOf(substring));
        List<WorkShiftSum> shiftSums = new ArrayList<>();
        Map<String, List<Map<String, Object>>> shift = new HashMap<>();
        for (Map<String, Object> map : dataShift) {
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
            for (String s : allMonth) {
                Map<String, Object> mapx = null;
                for (Map<String, Object> map : value) {
                    String string = map.get("workMouth").toString();
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
            shiftSums.add(shiftdataOK);
            WorkShiftSum shiftdataNG = new WorkShiftSum();
            shiftdataNG.setData(longListNg);
            shiftdataNG.setName(key + "NG");
            shiftdataNG.setStack(key);
            shiftSums.add(shiftdataNG);
        }
        return shiftSums;
    }

    @Override
    public Result getCurrentDate(GetOrderNoLineNo orderNoLineNo) {

        String orderNo = orderNoLineNo.getOrderNo();
        String lineNo = orderNoLineNo.getLineNo();
        String key = RedisKey.GET_CURRENT_DATE + orderNo + lineNo;
        try {
            if (redisUtil.hasKey(key)) {
                Result o = (Result) redisUtil.get(key);
                o.setRef(false);
                return o;
            }
            Calendar cal = Calendar.getInstance();
            int month = cal.get(Calendar.MONTH) + 1;
            Map<String, Object> map = new HashMap<>();
            map.put("month", month);
            map.put("dayClasses", 0);
            //获取当前产线的班次生产计划
            MonOrder orderLine = dzDetectionTemplCache.getNowOrder(orderNo, lineNo, MomProgressStatus.LOADING);
            if (orderLine != null) {
                String productAlias = orderLine.getProductAlias();
                DzProductionLine lineIdByOrderNoLineNo = dzDetectionTemplCache.getLineIdByOrderNoLineNo(orderNoLineNo);
                if (lineIdByOrderNoLineNo != null) {
                    String lineType = lineIdByOrderNoLineNo.getLineType();
                    BigDecimal jp = dzDetectionTemplCache.getProductNameFrequency(lineType, productAlias);
                    int hour = LocalTime.now().getHour();
                    if (hour < 8) {
                        double d = jp.doubleValue() * (hour + 5);
                        map.put("dayClasses", d);
                    } else {
                        int h = ((hour - 8) + 1);
                        double d = jp.doubleValue() * h;
                        map.put("dayClasses", d);
                    }
                }
            }
            Result ok = Result.ok(map);
            redisUtil.set(key, ok, orderNoLineNo.getCacheTime());
            return ok;
        } catch (Throwable e) {
            log.error("根据产线获取月份标准节拍异常:{}", e.getMessage(), e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
    }

    @Override
    public Result getDataNgAndOk(GetOrderNoLineNo orderNoLineNo) {
        String key = RedisKey.GET_DATA_NG_AND_OK + orderNoLineNo.getOrderNo() + orderNoLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Result o = (Result) redisUtil.get(key);
                o.setRef(false);
                return o;
            }
            DzProductionLine lineIdByOrderNoLineNo = dzDetectionTemplCache.getLineIdByOrderNoLineNo(orderNoLineNo);
            if (lineIdByOrderNoLineNo == null) {
                return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
            }
            String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getTableName();
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

    @Override
    public Result equipmentTimeAnalysis(GetOrderNoLineNo getOrderNoLineNo) {
        String key = RedisKey.GET_EQUIPMENT_TIME_ANALYSIS + getOrderNoLineNo.getOrderNo() + getOrderNoLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Result o = (Result) redisUtil.get(key);
                o.setRef(false);
                return o;
            }
            DzProductionLine lineIdByOrderNoLineNo = dzDetectionTemplCache.getLineIdByOrderNoLineNo(getOrderNoLineNo);
            if (lineIdByOrderNoLineNo == null) {
                return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
            }
            List<EquipmentTimeAnalysisDo> equipmentTimeAnalysisDos = new ArrayList<>();
            List<DzEquipment> yName = getLineIdIsShow(lineIdByOrderNoLineNo.getId(), FinalCode.IS_SHOW);
            LocalDate startTime = LocalDate.now();
            LocalDate endTime = LocalDate.now().plusDays(1);
            for (DzEquipment dzEquipment : yName) {
                EquipmentTimeAnalysisDo data = new EquipmentTimeAnalysisDo();
                data.setEqName(dzEquipment.getNickName());
                data.setEqNo(dzEquipment.getEquipmentNo());
                String equipmentNo = dzEquipment.getEquipmentNo();
                Integer equipmentType = dzEquipment.getEquipmentType();

                List<Map<String, Object>> runTimeList = dzEquipmentRunTimeMapper.getRunTime(getOrderNoLineNo.getLineNo(), getOrderNoLineNo.getOrderNo(), equipmentNo, equipmentType, startTime);
                Long runTimeData = 0L;
                for (Map<String, Object> map : runTimeList) {
                    Long duration = (Long) map.get("duration");
                    if (duration.longValue() == 0) {
//                    当前时间戳
                        Long resetTime = System.currentTimeMillis();
//                    启动时间
                        Long stop = ((Date) map.get("stopTime")).getTime();
                        duration = resetTime.longValue() - stop.longValue();
                    }
                    runTimeData += duration;
                }

                BigDecimal bigDecimal = new BigDecimal(runTimeData);
//            停机小时
                BigDecimal divide = bigDecimal.divide(new BigDecimal(3600000), 2, BigDecimal.ROUND_HALF_UP);
                data.setTimeRun(divide.toString());
                equipmentTimeAnalysisDos.add(data);
            }
            Result ok = Result.ok(equipmentTimeAnalysisDos);
            redisUtil.set(key, ok, getOrderNoLineNo.getCacheTime());
            return ok;
        } catch (Exception e) {
            log.error("根据产线查询所有设备当日用时分析(新)异常:{}", e.getMessage(), e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
    }

    @Override
    public Result getDeviceTimeAnalysis(GetOrderNoLineNo orderNoLineNo) {
        DeviceStateDetailsResp resp = new DeviceStateDetailsResp();
        String key = RedisKey.BuProductionQuantityService_getDeviceTimeAnalysis + orderNoLineNo.getOrderNo() + orderNoLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Result o = (Result) redisUtil.get(key);
                if (o != null) {
                    o.setRef(false);
                    return o;
                }
            }
            Calendar instance = Calendar.getInstance();
//            如果当前时间在 早上 08:00 之前 设置开始时间是前一天 08:00 之后
//            如果当前时间在 早上 08:00 之后 设置时间是当前日期 08:00 之后
            Calendar calenda = Calendar.getInstance();
            calenda.setTime(new Date());
            if (LocalTime.now().getHour() < 8) {
                calenda.add(Calendar.DATE, -1);
            }
            calenda.set(Calendar.HOUR_OF_DAY, 8);
            calenda.set(Calendar.MINUTE, 0);
            calenda.set(Calendar.MILLISECOND, 0);
            calenda.set(Calendar.SECOND, 0);
            Date time = calenda.getTime();
            DzProductionLine lineIdByOrderNoLineNo = dzDetectionTemplCache.getLineIdByOrderNoLineNo(orderNoLineNo);
            if (lineIdByOrderNoLineNo == null) {
                log.error("设备用时分析包含 作业 待机 故障 关机 错误,未查询到产线，orderNoLineNo：{}", orderNoLineNo);
                return Result.ok();
            }
            LocalDate localDate = DateUtil.getLocalDate(time);
            Map<Long, DzEquipment> deviceId = dzEquipmentMapper.getDeviceId(lineIdByOrderNoLineNo.getId());
            List<DeviceStateDetails> deviceStateDetails = new ArrayList<>();
            for (Map.Entry<Long, DzEquipment> mp : deviceId.entrySet()) {
                Long id = mp.getKey();
                List<DeviceStateDetails> bra = busTimeAnalysisService.getGeDeviceStateDetailsStopTime(localDate, time, id);
                if (CollectionUtils.isNotEmpty(bra)) {
                    for (DeviceStateDetails stateDetails : bra) {
                        stateDetails.setEquipmentName(mp.getValue().getNickName());
                        stateDetails.setEquipmentNo(mp.getValue().getEquipmentNo());
                        stateDetails.setDoorCode(mp.getValue().getDoorCode());
                        stateDetails.setDeviceType(String.valueOf(mp.getValue().getEquipmentType()));
                    }
                    deviceStateDetails.addAll(bra);
                }
            }
            List<DeviceStateDetailsData> respListX = new ArrayList<>();
            List<String> yx = new ArrayList<>();
            List<DeviceParseData> deviceParseDataX = new ArrayList<>();
            LinkedHashMap<String, Integer> mapX = new LinkedHashMap<>();
            List<DeviceStateDetails> detailsDoorNoHand = new ArrayList<>();
            List<DeviceStateDetails> deviceStateDetailsDef = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(deviceStateDetails)) {
                for (DeviceStateDetails deviceStateDetail : deviceStateDetails) {
                    String deviceType = deviceStateDetail.getDeviceType();
                    if (EquiTypeEnum.MEN.getCode() == Integer.valueOf(deviceType)) {
                        detailsDoorNoHand.add(deviceStateDetail);
                        continue;
                    } else {
                        deviceStateDetailsDef.add(deviceStateDetail);
                        continue;
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(deviceStateDetailsDef)) {
                LinkedHashMap<String, DeviceStateDetails> mxDef = new LinkedHashMap<>();
                for (DeviceStateDetails details : deviceStateDetailsDef) {
                    String groupId = details.getGroupId();
                    Long duration = details.getDuration();
                    if (duration != null && duration < 0) {
                        details.setResetTime(null);
                        continue;
                    }
                    String resetTime = details.getResetTime();
                    if (StringUtils.isEmpty(resetTime)) {
                        Calendar calendaX = Calendar.getInstance();
                        calendaX.setTime(new Date());
                        calendaX.set(Calendar.MILLISECOND, 0);
                        long nowDate = calendaX.getTime().getTime();
                        long stopTimeLong = details.getStopTime().getTime();
                        duration = nowDate - stopTimeLong;
                        if (duration < 0) {
                            continue;
                        }
                    }
                    if (mxDef.containsKey(groupId)) {
                        DeviceStateDetails dx = mxDef.get(groupId);
                        long timeK = dx.getStopTime().getTime();
                        long timeKn = details.getStopTime().getTime();
                        if (timeKn < timeK) {
                            dx.setStopTime(details.getStopTime());
                        } else if (timeKn > timeK) {
                            dx.setResetTime(details.getResetTime());
                        }
                        dx.setDuration(dx.getDuration() + details.getDuration());
                    } else {
                        mxDef.put(groupId, details);
                    }
                }
                List<DeviceStateDetails> detailsDoorDef = mxDef.values().stream().collect(Collectors.toList());
                for (int i = 0; i < detailsDoorDef.size(); i++) {
                    DeviceStateDetails details = detailsDoorDef.get(i);
                    String equipmentName = details.getEquipmentName();
                    String equipmentNo = details.getEquipmentNo();
                    String deviceType = details.getDeviceType();
                    String doorCode = details.getDoorCode();
                    Integer index = mapX.get(equipmentNo + "|" + doorCode);
                    if (index == null) {
                        int size = yx.size();
                        mapX.put(equipmentNo + "|" + doorCode, size);
                        index = size;
                        yx.add(index, equipmentName);
                        DeviceParseData parseData = new DeviceParseData();
                        deviceParseDataX.add(parseData);
                    }
                    DeviceStateDetailsData detailsData = new DeviceStateDetailsData();
                    String workState = details.getWorkState();
//                1作业 2：待机 3：故障 4：关机
                    String name = "";
                    if ("1".equals(workState)) {
                        name = "作业";
                    }
                    if ("0".equals(workState) || "2".equals(workState)) {
                        name = "待机";
                    }
                    if ("3".equals(workState)) {
                        name = "故障";
                    }
                    if ("4".equals(workState)) {
                        name = "关机";
                    }
                    detailsData.setName(name);
                    List<Object> x = new ArrayList<>(3);
                    x.add(index);
                    x.add(dateUtil.dateFormatToStingYmdHms(details.getStopTime()));
                    String resetTime = details.getResetTime();
                    Long duration = details.getDuration();
                    if (resetTime == null) {
                        Calendar calendaX = Calendar.getInstance();
                        calendaX.setTime(new Date());
                        calendaX.set(Calendar.MILLISECOND, 0);
                        Date nowDate = calendaX.getTime();
                        String dateStr = DateUtil.getDateStr(nowDate);
                        long stopTimeLong = details.getStopTime().getTime();
                        duration = nowDate.getTime() - stopTimeLong;
                        detailsData.setDuration(duration);
                        x.add(dateStr);
                    } else {
                        detailsData.setDuration(duration);
                        x.add(resetTime);
                    }
                    detailsData.setValue(x);
                    DeviceParseData parseData = deviceParseDataX.get(index);
                    parseData.setDeviceName(equipmentName);
                    parseData.setDeviceType(deviceType);
                    parseData.setDoorCode(doorCode);
                    parseData.setEquipmentNo(equipmentNo);
                    if ("1".equals(workState)) {
                        parseData.setOperationDuration(parseData.getOperationDuration() + duration);
                    }
                    if ("0".equals(workState) || "2".equals(workState)) {
                        parseData.setStandbyDuration(parseData.getStandbyDuration() + duration);
                    }
                    if ("3".equals(workState)) {
                        parseData.setFaultDuration(parseData.getFaultDuration() + duration);
                    }
                    if ("4".equals(workState)) {
                        parseData.setShutdownDuration(parseData.getShutdownDuration() + duration);
                    }
                    BigDecimal ch = BigDecimal.valueOf(parseData.getOperationDuration());
                    BigDecimal bh = BigDecimal.valueOf(parseData.getOperationDuration() + parseData.getStandbyDuration() + parseData.getFaultDuration());
                    BigDecimal divide = (bh.compareTo(new BigDecimal(0)) == 0 ? new BigDecimal(0) : ch.divide(bh, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100))).setScale(2, RoundingMode.HALF_UP);
                    parseData.setOperationRate(divide.toString());
                    respListX.add(detailsData);
                }
            }
//           安全门数据
            List<DeviceStateDetailsData> respListDoor = new ArrayList<>();
            List<DeviceParseDataBase> deviceParseDataDoor = new ArrayList<>();
            LinkedHashMap<String, Integer> mapDoor = new LinkedHashMap<>();

            if (CollectionUtils.isNotEmpty(detailsDoorNoHand)) {
//                放置最小的开始时间 和最大的结束时间
                LinkedHashMap<String, DeviceStateDetails> mx = new LinkedHashMap<>();
                for (DeviceStateDetails details : detailsDoorNoHand) {
                    String groupId = details.getGroupId();
                    if (mx.containsKey(groupId)) {
                        DeviceStateDetails dx = mx.get(groupId);
                        long timeK = dx.getStopTime().getTime();
                        long timeKn = details.getStopTime().getTime();
                        if (timeKn < timeK) {
                            dx.setStopTime(details.getStopTime());
                        } else if (timeKn > timeK) {
                            dx.setResetTime(details.getResetTime());
                        }
                        dx.setDuration(dx.getDuration() + details.getDuration());
                    } else {
                        mx.put(groupId, details);
                    }
                }
                List<DeviceStateDetails> detailsDoor = mx.values().stream().collect(Collectors.toList());
                for (int i = 0; i < detailsDoor.size(); i++) {
                    DeviceStateDetails details = detailsDoor.get(i);
                    String equipmentName = details.getEquipmentName();
                    String equipmentNo = details.getEquipmentNo();
                    String deviceType = details.getDeviceType();
                    String doorCode = details.getDoorCode();
                    Integer index = mapDoor.get(equipmentName);
                    if (index == null) {
                        index = getIndex(mapX, equipmentNo);
                        if (index == null) {
                            continue;
                        }
                        mapDoor.put(equipmentName, index);
                        DeviceParseDataBase parseData = new DeviceParseDataBase();
                        parseData.setDeviceName(equipmentName);
                        parseData.setDeviceType(deviceType);
                        parseData.setDoorCode(doorCode);
                        parseData.setEquipmentNo(equipmentNo);
                        deviceParseDataDoor.add(parseData);
                    }
                    DeviceStateDetailsData detailsData = new DeviceStateDetailsData();
                    String workState = details.getWorkState();
//                1作业 2：待机 3：故障 4：关机
                    String name = "";
                    if (WorkState.OPEN == Integer.valueOf(workState)) {
                        name = "开门";
                    }
                    if (WorkState.CLOSE == Integer.valueOf(workState)) {
                        name = "关门";
                    }
                    detailsData.setName(name);
                    List<Object> x = new ArrayList<>(3);
                    x.add(index);
                    x.add(details.getStopTime());
                    String resetTime = details.getResetTime();
                    Long duration = details.getDuration();
                    if (resetTime == null) {
                        Calendar calendaX = Calendar.getInstance();
                        calendaX.setTime(new Date());
                        calendaX.set(Calendar.MILLISECOND, 0);
                        Date nowDate = calendaX.getTime();
                        String dateStr = DateUtil.getDateStr(nowDate);
                        long stopTimeLong = details.getStopTime().getTime();
                        duration = nowDate.getTime() - stopTimeLong;
                        detailsData.setDuration(duration);
                        x.add(dateStr);
                    } else {
                        detailsData.setDuration(duration);
                        x.add(resetTime);
                    }
                    detailsData.setValue(x);
                    respListDoor.add(detailsData);
                }
            }
            resp.setData(respListX);
            resp.setYAxis(yx);
            resp.setDeviceBaseData(deviceParseDataX);
            resp.setDataDoor(respListDoor);
            resp.setDeviceBaseDataDoor(deviceParseDataDoor);
            Result<DeviceStateDetailsResp> ok = Result.OK(resp);
            redisUtil.set(key, ok, orderNoLineNo.getCacheTime());
            return ok;
        } catch (Throwable throwable) {
            log.error("设备用时分析包含 作业 待机 故障 关机 错误：{}", throwable.getMessage(), throwable);
            return Result.OK(resp);
        }

    }

    private Integer getIndex(LinkedHashMap<String, Integer> mapX, String doorCode) {
        for (Map.Entry<String, Integer> map : mapX.entrySet()) {
            String key = map.getKey();
            String[] split = key.split("\\|");
            if (split.length == 2) {
                if (split[1].equals(doorCode)) {
                    return map.getValue();
                }
            }
        }
        return null;
    }

    private synchronized List<DzEquipment> getLineIdIsShow(Long id, String isShow) {
        List<DzEquipment> list = redisUtil.lGet(RedisKey.getLineIdIsShow + id + isShow, 0, -1);
        if (CollectionUtils.isNotEmpty(list)) {
            return list;
        }
        QueryWrapper<DzEquipment> wrapper = new QueryWrapper();
        wrapper.eq("line_id", id);
        wrapper.eq("is_show", FinalCode.IS_SHOW);
        wrapper.select("equipment_no", "equipment_type", "equipment_name", "nick_name", "id");
        List<DzEquipment> yName = dzEquipmentMapper.selectList(wrapper);
        int timeCahce = (int) (Math.random() * 600) + 60;
        redisUtil.del(RedisKey.getLineIdIsShow + id + isShow);
        redisUtil.lSet(RedisKey.getLineIdIsShow + id + isShow, yName, timeCahce);
        return yName;
    }

    @Override
    public Result dailyPassRate(GetOrderNoLineNo orderNoLineNo) {
        String key = RedisKey.GET_DAILY_PASS_RATE + orderNoLineNo.getOrderNo() + orderNoLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Result o = (Result) redisUtil.get(key);
                if (o != null) {
                    o.setRef(false);
                    return o;
                }
            }
            DzProductionLine lineIdByOrderNoLineNo = dzDetectionTemplCache.getLineIdByOrderNoLineNo(orderNoLineNo);
            if (lineIdByOrderNoLineNo == null) {
                return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
            }
            Long equipmentId = lineIdByOrderNoLineNo.getStatisticsEquimentId();
            if (equipmentId == null) {
                log.warn("通用接口，查询当日生产合格率，该产线没有绑定记数设备,产线id：{}", lineIdByOrderNoLineNo.getId());
                return Result.error(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
            }
            String tableKey = ((RunDataModel) cacheService.systemRunModel(null).getData()).getTableName();
            String date = dateUtil.getDate();
            QualifiedAndOutputDo qualifiedAndOutputDo = dzEquipmentProNumMapper.dailyPassRate(tableKey, equipmentId, date);
            Result ok = Result.OK(qualifiedAndOutputDo);
            redisUtil.set(key, ok, orderNoLineNo.getCacheTime());
            return ok;
        } catch (Exception e) {
            log.error("通用接口，查询当日生产合格率异常:{}", e.getMessage(), e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }

    }

    @Override
    public Result shiftProductionDetails(GetOrderNoLineNo orderNoLineNo) {
        List<String> name = new ArrayList<>();
        List<Long> dataSum = new ArrayList<>();
        try {
            //产线日生产计划
            DzProductionLine lineIdByOrderNoLineNo = dzDetectionTemplCache.getLineIdByOrderNoLineNo(orderNoLineNo);
            if (lineIdByOrderNoLineNo == null) {
                return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
            }
            DzProductionPlan dzProductionPlan = dzProductionPlanMapper.selectOne(new QueryWrapper<DzProductionPlan>().eq("line_id", lineIdByOrderNoLineNo.getId()).eq("plan_type", 0));
            //计划生产数量
            Long planSum = dzProductionPlan != null ? dzProductionPlan.getPlannedQuantity() : 0;
            name.add("计划");
            dataSum.add(planSum);
            Long equipmentId = lineIdByOrderNoLineNo.getStatisticsEquimentId();
            if (equipmentId == null) {
                log.warn("通用接口，查询日生产计划和班次生产详情，该产线没有绑定记数设备,产线id：{}", lineIdByOrderNoLineNo.getId());
                return Result.error(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
            }
            //每天第一个班次的起始时间
            LocalTime ss = LocalTime.of(8, 0, 0);
            //当前时间
            LocalTime localTime = LocalTime.now();
            LocalDate nowDate = LocalDate.now();
            //判断当前班次是否跨天(当前时间小于第一个班次的起始时间)
            if (localTime.isBefore(ss)) {
                //跨天 则当前日期-1 才是当前班次的开始时间
                nowDate = nowDate.plusDays(-1);
            }
            //产线设备生产班次集合
            QueryWrapper<DzLineShiftDay> line_id = new QueryWrapper<DzLineShiftDay>()
                    .eq("eq_id", equipmentId)
                    .eq("work_data", nowDate)
                    .orderByAsc("sort_no");
            List<DzLineShiftDay> dzLineShiftDays = dzLineShiftDayMapper.selectList(line_id);
            if (dzLineShiftDays.size() == 0) {
                log.warn("通用接口，查询日生产计划和班次生产详情，未查询到设备班次,产线id:{}，设备id:{},日期:{}", lineIdByOrderNoLineNo.getId(), equipmentId, nowDate);
                return Result.error(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
            }
            String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getTableName();
            for (DzLineShiftDay dzLineShiftDay : dzLineShiftDays) {
                Long data = dzEquipmentProNumSignalMapper.shiftProductionDetails(tableKey, dzLineShiftDay.getId());
                name.add(dzLineShiftDay.getWorkName());
                dataSum.add(data);
            }
            ShiftProductionDetailsDo shiftProductionDetailsDo = new ShiftProductionDetailsDo();
            shiftProductionDetailsDo.setName(name);
            shiftProductionDetailsDo.setDataSum(dataSum);
            return Result.OK(shiftProductionDetailsDo);
        } catch (Exception e) {
            log.error("通用接口， 日生产计划，以及班次生产详情异常:{}", e.getMessage(), e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
    }

    @Override
    public Result dailyProductionDetails(GetOrderNoLineNo orderNoLineNo) {
        String key = RedisKey.MomOrderController_busMomOrderService_dailyProductionDetails + orderNoLineNo.getOrderNo() + orderNoLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Result o = (Result) redisUtil.get(key);
                if (o != null) {
                    o.setRef(false);
                    return  o;
                }
            }
            DzProductionLine lineIdByOrderNoLineNo = dzDetectionTemplCache.getLineIdByOrderNoLineNo(orderNoLineNo);
            if (lineIdByOrderNoLineNo == null) {
                return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
            }
            Long equipmentId = lineIdByOrderNoLineNo.getStatisticsEquimentId();
            if (equipmentId == null) {
                log.warn("通用接口，查询当日(24小时)生产明细，该产线没有绑定记数设备,产线id：{}", lineIdByOrderNoLineNo.getId());
                return Result.error(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
            }
            QueryWrapper<DzEquipmentProNumSignal> wrapper = new QueryWrapper<>();
            wrapper.eq("equiment_id", equipmentId);
            wrapper.eq("work_data", LocalDate.now());
            wrapper.groupBy("work_hour");
            wrapper.select("now_num", "work_hour");
            List<DzEquipmentProNumSignal> dzEquipmentProNumSignals = dzEquipmentProNumSignalMapper.selectList(wrapper);
            List<Long> list = new ArrayList<>();
            for (int i = 0; i < 24; i++) {
                Long data = 0L;
                for (DzEquipmentProNumSignal dzEquipmentProNumSignal : dzEquipmentProNumSignals) {
                    if (dzEquipmentProNumSignal.getWorkHour().intValue() == i) {
                        data = dzEquipmentProNumSignal.getNowNum();
                        break;
                    }
                }
                list.add(data);
            }
            Result<List<Long>> ok = Result.ok(list);
            redisUtil.set(key, ok, orderNoLineNo.getCacheTime());
            return ok;
        } catch (Exception e) {
            log.error("通用接口，查询当日(24小时)生产明细异常:{}", e.getMessage(), e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
    }

    @Override
    public Result productionDailyReport(GetOrderNoLineNo orderNoLineNo) {
        String key = RedisKey.MomOrderController_busMomOrderService_productionDailyReport + orderNoLineNo.getOrderNo() + orderNoLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Result o = (Result) redisUtil.get(key);
                if (o != null) {
                    o.setRef(false);
                    return o;
                }
            }
            DzProductionLine lineIdByOrderNoLineNo = dzDetectionTemplCache.getLineIdByOrderNoLineNo(orderNoLineNo);
            if (lineIdByOrderNoLineNo == null) {
                return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
            }
            Long equipmentId = lineIdByOrderNoLineNo.getStatisticsEquimentId();
            if (equipmentId == null) {
                log.warn("通用接口，查询当月每日生产数量异常，产线未绑定记数设备，产线id：{}", lineIdByOrderNoLineNo.getId());
                return Result.error(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
            }
            List<Long> mapList = dzEquipmentProNumSignalMapper.productionDailyReport(equipmentId);
            Result<List<Long>> ok = Result.ok(mapList);
            redisUtil.set(key, ok, orderNoLineNo.getCacheTime());
            return ok;
        } catch (Exception e) {
            log.error("通用接口，查询当日(24小时)生产明细异常:{}", e.getMessage(), e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }

    }

    @Override
    public Result dayAndHour(GetOrderNoLineNo orderNoLineNo) {
        try {
            DzProductionLine lineIdByOrderNoLineNo = dzDetectionTemplCache.getLineIdByOrderNoLineNo(orderNoLineNo);
            if (lineIdByOrderNoLineNo == null) {
                return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
            }
            List<DzEquipment> equipments = dzEquipmentMapper.selectList(new QueryWrapper<DzEquipment>().eq("line_id", lineIdByOrderNoLineNo.getId()));
            if (equipments.size() > 0) {
                List<Long> collect = equipments.stream().map(p -> p.getId()).collect(Collectors.toList());
                String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getTableName();
                List<Map<String, Object>> list = dzEquipmentProNumMapper.dayAndHour(tableKey, collect, dateUtil.getDate());
                List<Map<String, Object>> mapList = new ArrayList<>();
                //当前小时
                Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00")); //获取东八区时间
                int hour = c.get(Calendar.HOUR_OF_DAY); //获取当前小时
                for (DzEquipment equipment : equipments) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("equimentId", equipment.getId().toString());
                    data.put("equipmentNo", equipment.getEquipmentNo());
                    //设备日产
                    Long dayNum = 0L;
                    //设备时产
                    Long hourNum = 0L;
                    for (Map<String, Object> map : list) {
                        String equimentId = map.get("equimentId").toString();
                        Long nowNum = Long.valueOf(map.get("nowNum").toString());
                        Integer workHour = Integer.valueOf(map.get("workHour").toString());
                        //计算日产
                        if (equipment.getId().toString().equals(equimentId)) {
                            dayNum += nowNum;
                            //计算时产
                            if (hour == workHour.intValue()) {
                                hourNum += nowNum;
                            }
                        }
                    }
                    data.put("dayNum", dayNum);
                    data.put("hourNum", hourNum);
                    mapList.add(data);
                }
                return Result.ok(mapList);
            }
        } catch (Exception e) {
            log.error("通用接口，查询设备日产/时产异常:{}", e.getMessage(), e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
        return null;
    }

    @Override
    public Result robotRunTime(GetOrderNoLineNo orderNoLineNo) {
        try {
            DzProductionLine lineIdByOrderNoLineNo = dzDetectionTemplCache.getLineIdByOrderNoLineNo(orderNoLineNo);
            if (lineIdByOrderNoLineNo == null) {
                return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
            }
            List<EquipmentTimeAnalysisDo> equipmentTimeAnalysisDos = new ArrayList<>();
            QueryWrapper<DzEquipment> wrapper = new QueryWrapper();
            wrapper.eq("line_id", lineIdByOrderNoLineNo.getId());
            wrapper.eq("is_show", FinalCode.IS_SHOW);
            wrapper.eq("equipment_type", FinalCode.ROBOT_EQUIPMENT_CODE);
            wrapper.select("equipment_no", "id", "equipment_type");
            List<DzEquipment> yName = dzEquipmentMapper.selectList(wrapper);

            LocalDate startTime = LocalDate.now();
            for (DzEquipment dzEquipment : yName) {
                EquipmentTimeAnalysisDo data = new EquipmentTimeAnalysisDo();
                data.setEquimentId(dzEquipment.getId().toString());
                String equipmentNo = dzEquipment.getEquipmentNo();
                Integer equipmentType = dzEquipment.getEquipmentType();

                List<Map<String, Object>> runTimeList = dzEquipmentRunTimeMapper.getRunTime(orderNoLineNo.getLineNo(), orderNoLineNo.getOrderNo(), equipmentNo, equipmentType, startTime);
                Long runTimeData = 0L;
                for (Map<String, Object> map : runTimeList) {
                    Long duration = (Long) map.get("duration");
                    if (duration.longValue() == 0) {
//                    当前时间戳
                        Long resetTime = System.currentTimeMillis();
//                    启动时间
                        Long stop = ((Date) map.get("stopTime")).getTime();
                        duration = resetTime.longValue() - stop.longValue();
                    }
                    runTimeData += duration;
                }
                runTimeData = runTimeData / 1000;
                data.setTimeRun(runTimeData.toString());
                equipmentTimeAnalysisDos.add(data);
            }
            Result ok = Result.ok(equipmentTimeAnalysisDos);
            return ok;

        } catch (Exception e) {
            log.error("通用接口，查询机器人今日运行时间异常:{}", e.getMessage(), e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
    }

    @Override
    public Result allEquipmentDailyCapacity(GetOrderNoLineNo orderNoLineNo) {
        String key = RedisKey.BuProductionQuantityServiceImpl_allEquipmentDailyCapacity + orderNoLineNo.getOrderNo() + orderNoLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Result o = (Result) redisUtil.get(key);
                if (o != null) {
                    o.setRef(false);
                    return o;
                }
            }
            DzProductionLine lineIdByOrderNoLineNo = dzDetectionTemplCache.getLineIdByOrderNoLineNo(orderNoLineNo);
            if (lineIdByOrderNoLineNo == null) {
                return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
            }
            //查询产线设备列表
            QueryWrapper<DzEquipment> wrapper = new QueryWrapper();
            wrapper.eq("line_id", lineIdByOrderNoLineNo.getId());
            wrapper.select("equipment_no", "id", "equipment_type", "equipment_name");
            List<DzEquipment> equipments = dzEquipmentMapper.selectList(wrapper);
            //查询产线所有设备日产
            String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getTableName();
            List<Map<String, Object>> mapList = dzEquipmentProNumMapper.allEquipmentDailyCapacity(tableKey, orderNoLineNo.getOrderNo(), orderNoLineNo.getLineNo(), dateUtil.getDate());
            List<Map<String, String>> dataList = new ArrayList<>();
            for (DzEquipment dzEquipment : equipments) {
                Map<String, String> map = new HashMap<>();
                //全部转成字符串
                map.put("equimentId", dzEquipment.getId().toString());
                map.put("equimentNo", dzEquipment.getEquipmentNo());
                map.put("equimentName", dzEquipment.getEquipmentName());

                map.put("nowNum", "0");
                for (Map<String, Object> data : mapList) {
                    if (dzEquipment.getId().toString().equals(data.get("equimentId").toString())) {
                        map.put("nowNum", data.get("nowNum").toString());
                        break;
                    }
                }
                dataList.add(map);
            }
            Result<List<Map<String, String>>> ok = Result.ok(dataList);
            redisUtil.set(key, ok, orderNoLineNo.getCacheTime());
            return ok;
        } catch (Exception e) {
            log.error("通用接口，产线设备当日产量分析柱状图  订单：{}，异常信息：{} ", orderNoLineNo.getOrderNo(), e.getMessage());
        }
        return null;
    }

    @Override
    public Result allEquipmentDailyCapacityTwo(GetOrderNoLineNo orderNoLineNo) {
        String key = RedisKey.BuProductionQuantityServiceImpl_allEquipmentDailyCapacityTwo + orderNoLineNo.getOrderNo() + orderNoLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Result o = (Result) redisUtil.get(key);
                if (o != null) {
                    o.setRef(false);
                    return o;
                }
            }
            DzProductionLine lineIdByOrderNoLineNo = dzDetectionTemplCache.getLineIdByOrderNoLineNo(orderNoLineNo);
            if (lineIdByOrderNoLineNo == null) {
                return Result.error(CustomExceptionType.USER_IS_NULL, CustomResponseCode.ERR17);
            }
            //查询产线设备列表
            QueryWrapper<DzEquipment> wrapper = new QueryWrapper();
            wrapper.eq("line_id", lineIdByOrderNoLineNo.getId());
            wrapper.eq("standby_one", 1);
            wrapper.select("equipment_no", "id", "equipment_type", "equipment_name");
            List<DzEquipment> equipments = dzEquipmentMapper.selectList(wrapper);
            //查询产线所有设备日产
            String tableKey = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getTableName();
            List<Map<String, Object>> mapList = dzEquipmentProNumMapper.allEquipmentDailyCapacity(tableKey, orderNoLineNo.getOrderNo(), orderNoLineNo.getLineNo(), dateUtil.getDate());
            List<Map<String, String>> dataList = new ArrayList<>();
            for (DzEquipment dzEquipment : equipments) {
                Map<String, String> map = new HashMap<>();
                //全部转成字符串
                map.put("equimentId", dzEquipment.getId().toString());
                map.put("equimentNo", dzEquipment.getEquipmentNo());
                map.put("equimentName", dzEquipment.getEquipmentName());

                map.put("nowNum", "0");
                for (Map<String, Object> data : mapList) {
                    if (dzEquipment.getId().toString().equals(data.get("equimentId").toString())) {
                        Object nowNum = data.get("nowNum");
                        map.put("nowNum", nowNum != null ? nowNum.toString() : "0");
                        break;
                    }
                }
                dataList.add(map);
            }
            Result<List<Map<String, String>>> ok = Result.ok(dataList);
            redisUtil.set(key, ok, orderNoLineNo.getCacheTime());
            return ok;
        } catch (Exception e) {
            log.error("通用接口，产线设备当日产量分析柱状图  订单：{}，异常信息：{} ", orderNoLineNo.getOrderNo(), e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Result getLineProductionInfo(GetOrderNoLineNo orderNoLineNo) {

        try {
            DzProductionLine lineIdByOrderNoLineNo = dzDetectionTemplCache.getLineIdByOrderNoLineNo(orderNoLineNo);
            if (lineIdByOrderNoLineNo == null) {
                log.error("产线生产信息总览查询异常。产线不存在,订单号:{},产线号:{}", orderNoLineNo.getOrderNo(), orderNoLineNo.getLineNo());
                return Result.error(CustomExceptionType.SYSTEM_ERROR);
            }
            QueryWrapper<MonOrder> wp = new QueryWrapper<>();
            wp.eq("order_id", lineIdByOrderNoLineNo.getOrderId());
            wp.eq("line_id", lineIdByOrderNoLineNo.getId());
            wp.eq("ProgressStatus", "120");
            wp.eq("order_operation_result", 2);
            MonOrder one = momOrderService.getOne(wp);
            if (one == null) {
                log.warn("当前产线没有进行中的订单,订单号:{},产线号:{}", orderNoLineNo.getOrderNo(), orderNoLineNo.getLineNo());
                return Result.ok();
            }
            Map<String, Object> map = new HashMap<>();
            map.put("wipOrderNo", one.getWiporderno());
            map.put("orderStartTime", one.getRealityStartDate());
            map.put("scheduledProduction", one.getQuantity());
            map.put("practicalProduction", 100);
            map.put("productImg", "static/img/logo.9bcf4fec.png");
            return Result.OK(map);
        } catch (Exception e) {
            log.error("产线生产信息总览查询异常,订单号:{},产线号:{}", orderNoLineNo.getOrderNo(), orderNoLineNo.getLineNo());
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }

    }

    @Override
    public Result getMachineToolData(GetOrderNoLineNo orderNoLineNo) {
        try {
            //机床A
            QueryWrapper<DzEquipment> wrapperA = new QueryWrapper<>();
            wrapperA.eq("order_no", orderNoLineNo.getOrderNo());
            wrapperA.eq("line_no", orderNoLineNo.getLineNo());
            wrapperA.eq("equipment_type", 2);
            wrapperA.eq("equipment_no", "A1");
            wrapperA.select("id");
            DzEquipment dzEquipmentA = dzEquipmentService.getOne(wrapperA);
            //机床B
            QueryWrapper<DzEquipment> wrapperB = new QueryWrapper<>();
            wrapperB.eq("order_no", orderNoLineNo.getOrderNo());
            wrapperB.eq("line_no", orderNoLineNo.getLineNo());
            wrapperB.eq("equipment_type", 2);
            wrapperB.eq("equipment_no", "A2");
            wrapperB.select("id");
            DzEquipment dzEquipmentB = dzEquipmentService.getOne(wrapperB);

            List<Long> dayA = new ArrayList<>();
            List<Long> dayB = new ArrayList<>();
            List<Long> monthA = new ArrayList<>();
            List<Long> monthB = new ArrayList<>();
            String monthData = LocalDate.now().toString().substring(0, 7);
            Calendar cal = Calendar.getInstance();
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);
            List<String> mouthDate = dzDetectionTemplCache.getMouthDate(year, month);
            LocalDate now = LocalDate.now();
            QueryWrapper<DzEquipmentProNumSignal> wrapper = new QueryWrapper<>();
            wrapper.in("equiment_id", dzEquipmentA.getId(), dzEquipmentB.getId());
            wrapper.eq("work_mouth", monthData);
            wrapper.select("equiment_id", "now_num", "work_hour", "work_data");
            List<DzEquipmentProNumSignal> list = dzEquipmentProNumSignalService.list(wrapper);
            List<DzEquipmentProNumSignal> listA = new ArrayList<>();
            List<DzEquipmentProNumSignal> listB = new ArrayList<>();

            for (DzEquipmentProNumSignal signal : list) {
                //机床A的数据
                if (dzEquipmentA.getId().intValue() == signal.getEquimentId().intValue()) {
                    listA.add(signal);
                } else
                    //机床B的数据
                    if (dzEquipmentB.getId().intValue() == signal.getEquimentId().intValue()) {
                        listB.add(signal);
                    }
            }
            for (int i = 0; i < 24; i++) {
                long a = 0;
                long b = 0;
                for (DzEquipmentProNumSignal p : listA) {
                    if (now.toString().equals(p.getWorkData().toString()) && i == p.getWorkHour().intValue()) {
                        a += p.getNowNum();
                    }
                }
                for (DzEquipmentProNumSignal p : listB) {
                    if (now.toString().equals(p.getWorkData().toString()) && i == p.getWorkHour().intValue()) {
                        b += p.getNowNum();
                    }
                }
                dayA.add(a);
                dayB.add(b);
            }
            for (String date : mouthDate) {
                long a = 0;
                long b = 0;
                for (DzEquipmentProNumSignal p : listA) {
                    if (date.equals(String.valueOf(p.getWorkData()))) {
                        a += p.getNowNum();
                    }
                }
                for (DzEquipmentProNumSignal p : listB) {
                    if (date.equals(String.valueOf(p.getWorkData()))) {
                        b += p.getNowNum();
                    }
                }
                monthA.add(a);
                monthB.add(b);
            }
            Map<String, Object> map = new HashMap<>();
            map.put("dayA", dayA);
            map.put("dayB", dayB);
            map.put("monthA", monthA);
            map.put("monthB", monthB);
            return Result.OK(map);
        } catch (Exception e) {
            log.error("查询机床A和B 当月31天产量和当天24小时产量异常，请求信息：{}", orderNoLineNo, e);
            return Result.error(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
        }


    }

    @Override
    public Result achievingAndQualified(GetOrderNoLineNo orderNoLineNo) {
        DzProductionLine lineIdByOrderNoLineNo = dzDetectionTemplCache.getLineIdByOrderNoLineNo(orderNoLineNo);
        if (lineIdByOrderNoLineNo == null) {
            log.error("产线生产信息总览查询异常。产线不存在,订单号:{},产线号:{}", orderNoLineNo.getOrderNo(), orderNoLineNo.getLineNo());
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
        DzProductionPlan productionPlan = dzProductionPlanService.getOne(new QueryWrapper<DzProductionPlan>().eq("line_id", lineIdByOrderNoLineNo.getId()).eq("plan_type", 0));
        if (productionPlan == null) {
            log.error("查询产线达成率，合格率异常。产线生产计划不存在,订单号:{},产线号:{}", orderNoLineNo.getOrderNo(), orderNoLineNo.getLineNo());
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
        //        LocalDate now = LocalDate.of(2021,9,13);
        QueryWrapper<DzProductionPlanDaySignal> eq = new QueryWrapper<DzProductionPlanDaySignal>()
                .eq("plan_id", productionPlan.getId())
                .eq("detector_time", LocalDate.now());
        DzProductionPlanDaySignal one = dzProductionPlanDayService.getOne(eq);
        if (one == null) {
            log.error("查询产线达成率，合格率异常。产线当日生产计划不存在,订单号:{},产线号:{}", orderNoLineNo.getOrderNo(), orderNoLineNo.getLineNo());
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("percentageComplete", one.getPercentageComplete());
        map.put("passRate", one.getPassRate());
        return Result.OK(map);

    }

    @Override
    public Result getProductionLineNumberByDay(GetOrderNoLineNo orderNoLineNo) {
        String key = RedisKey.BuProductionQuantityServiceImpl_GetProductionLineNumberByDay + orderNoLineNo.getOrderNo() + orderNoLineNo.getLineNo();
        try {
            if (redisUtil.hasKey(key)) {
                Result o = (Result) redisUtil.get(key);
                if (o != null) {
                    o.setRef(false);
                    return o;
                }
            }
            DzProductionLine lineIdByOrderNoLineNo = dzDetectionTemplCache.getLineIdByOrderNoLineNo(orderNoLineNo);
            if (lineIdByOrderNoLineNo == null) {
                log.error("查询产线指定天数的生产数据(例如5天,7天)。产线不存在,订单号:{},产线号:{}", orderNoLineNo.getOrderNo(), orderNoLineNo.getLineNo());
                return Result.error(CustomExceptionType.SYSTEM_ERROR);
            }
            Long equimentId = lineIdByOrderNoLineNo.getStatisticsEquimentId();
            if (equimentId == null) {
                log.error("查询产线指定天数的生产数据(例如5天,7天)，产线计数设备不存在，订单号:{},产线号:{}", orderNoLineNo.getOrderNo(), orderNoLineNo.getLineNo());
                return Result.error(CustomExceptionType.SYSTEM_ERROR);
            }
            List<Long> dataList = new ArrayList<>();
            List<String> days = DateUtil.getDayByNumber(orderNoLineNo.getDayNumber());
            QueryWrapper<DzEquipmentProNumSignal> wrapper = new QueryWrapper<>();
            wrapper.eq("order_no", orderNoLineNo.getOrderNo());
            wrapper.eq("line_no", orderNoLineNo.getLineNo());
            wrapper.eq("equiment_id", equimentId);
            wrapper.ge("work_data", days.get(0));
            wrapper.le("work_data", days.get(days.size() - 1));
            wrapper.select("work_data", "now_num");
            List<DzEquipmentProNumSignal> list = dzEquipmentProNumSignalService.list(wrapper);
            for (String day : days) {
                Long number = 0L;
                for (DzEquipmentProNumSignal dzEquipmentProNumSignal : list) {
                    if (day.equals(dzEquipmentProNumSignal.getWorkData().toString())) {
                        number += dzEquipmentProNumSignal.getNowNum();
                    }
                }
                dataList.add(number);
            }
            Map<String, Object> map = new HashMap<>();
            List<String> newDay = DateUtil.yearMonthDayToMonthDay(days);
            map.put("dataList", dataList);
            map.put("dayList", newDay);
            Result<Map<String, Object>> ok = Result.OK(map);
            redisUtil.set(key, ok, orderNoLineNo.getCacheTime());
            return ok;
        } catch (Exception e) {
            log.error("查询产线指定天数的生产数据(例如5天,7天)异常,订单号:{},产线号:{}", orderNoLineNo.getOrderNo(), orderNoLineNo.getLineNo(), e);
            return Result.error(CustomExceptionType.SYSTEM_ERROR);
        }
    }


    //获取五日内日期
    public List<String> getFiveDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate date7 = LocalDate.now();
        LocalDate date6 = date7.plusDays(-1);
        LocalDate date5 = date6.plusDays(-1);
        LocalDate date4 = date5.plusDays(-1);
        LocalDate date3 = date4.plusDays(-1);
//        LocalDate date2 = date3.plusDays(-1);
//        LocalDate date1 = date2.plusDays(-1);
        List<String> list = new ArrayList<>();
//        list.add(date1.format(formatter));
//        list.add(date2.format(formatter));
        list.add(date3.format(formatter));
        list.add(date4.format(formatter));
        list.add(date5.format(formatter));
        list.add(date6.format(formatter));
        list.add(date7.format(formatter));
        return list;
    }
}
