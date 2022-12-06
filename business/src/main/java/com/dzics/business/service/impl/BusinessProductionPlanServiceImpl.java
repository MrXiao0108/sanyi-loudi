package com.dzics.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.business.model.response.LineDataAnalysisX;
import com.dzics.business.model.vo.detectordata.ProcessingBeat;
import com.dzics.business.model.vo.detectordata.ResProcessingBeat;
import com.dzics.business.model.vo.plan.PlanAnalysisGraphical;
import com.dzics.business.model.vo.plan.PlanAnalysisGraphicalTime;
import com.dzics.business.service.BusTimeAnalysisService;
import com.dzics.business.service.BusinessEquipmentService;
import com.dzics.business.service.BusinessProductionPlanService;
import com.dzics.business.service.cache.DzDetectionTemplCache;
import com.dzics.business.util.RedisUtil;
import com.dzics.common.dao.*;
import com.dzics.common.enums.EquiTypeEnum;
import com.dzics.common.enums.Message;
import com.dzics.common.enums.UserIdentityEnum;
import com.dzics.common.enums.WorkState;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.constant.FinalCode;
import com.dzics.common.model.entity.*;
import com.dzics.common.model.request.charts.IntelligentDetectionVo;
import com.dzics.common.model.request.plan.SelectProductionPlanVo;
import com.dzics.common.model.response.EquimentOrderLineId;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.RunDataModel;
import com.dzics.common.model.response.plan.ProductionPlanDo;
import com.dzics.common.model.response.timeanalysis.*;
import com.dzics.common.service.*;
import com.dzics.common.util.DateUtil;
import com.dzics.common.util.PageLimit;
import com.dzics.common.util.RedisKey;
import com.dzics.common.util.StringToUpcase;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 产线日生产计划 Service
 */
@Service
@Slf4j
public class BusinessProductionPlanServiceImpl implements BusinessProductionPlanService {
    @Autowired
    private DzProductionPlanMapper dzProductionPlanMapper;
    @Autowired
    private SysUserServiceDao sysUserServiceDao;
    @Autowired
    private DzProductionPlanDayService productionPlanDayService;
    @Autowired
    private BusinessEquipmentService businessEquipmentService;
    @Autowired
    private DzDetectionTemplCache dzDetectionTemplCache;
    @Autowired
    private AverageBeatService averageBeatService;
    @Autowired
    private DzEquipmentRunTimeMapper dzEquipmentRunTimeMapper;
    @Autowired
    private DzProductionLineService dzProductionLineService;
    @Autowired
    private DzOrderService dzOrderService;
    @Autowired
    private BusTimeAnalysisService busTimeAnalysisService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SysDictItemService sysDictItemService;
    @Autowired
    private SysDictMapper sysDictMapper;
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private DzWorkpieceDataMapper dzWorkpieceDataMapper;
    @Autowired
    private DzProductService dzProductService;
    @Autowired
    private DzProductDetectionTemplateService dzProductDetectionTemplateService;
    @Autowired
    private MomOrderMapper momOrderMapper;
    @Autowired
    private DzEquipmentMapper equipmentMapper;
    @Override
    public Result<ProductionPlanDo> list(String sub, PageLimit pageLimit, SelectProductionPlanVo selectProductionPlanVo) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        if (byUserName.getUserIdentity().intValue() == UserIdentityEnum.DZ.getCode().intValue() && byUserName.getUseOrgCode().equals(FinalCode.DZ_USE_ORG_CODE)) {
            byUserName.setUseOrgCode(null);
        }
        selectProductionPlanVo.setOrgCode(byUserName.getUseOrgCode());
        PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        List<ProductionPlanDo> list = dzProductionPlanMapper.list(selectProductionPlanVo);
        PageInfo<ProductionPlanDo> info = new PageInfo<>(list);
        return new Result(CustomExceptionType.OK, info.getList(), info.getTotal());
    }

    @Transactional
    @Override
    public Result<ProductionPlanDo> put(String sub, ProductionPlanDo productionPlanDo) {
        if (productionPlanDo == null || productionPlanDo.getId() == null || productionPlanDo.getPlannedQuantity() == null) {
            log.error("参数为空:{}", productionPlanDo.toString());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_64);
        }
        DzProductionPlan dzProductionPlan = dzProductionPlanMapper.selectById(productionPlanDo.getId());
        if (dzProductionPlan == null) {
            log.error("日生产计划id不存在:{}", productionPlanDo.getId());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_65);
        }
        dzProductionPlan.setPlannedQuantity(productionPlanDo.getPlannedQuantity());
        dzProductionPlanMapper.updateById(dzProductionPlan);
        SysDictItem one = sysDictItemService.getOne(new QueryWrapper<SysDictItem>().eq("dict_code", FinalCode.ClASSES_CODE).eq("item_text", dzProductionPlan.getLineId()));
        if (one != null) {
            one.setItemValue(String.valueOf(productionPlanDo.getDayClasses()));
            sysDictItemService.updateById(one);
        } else {
            SysDict sysDict = sysDictMapper.selectOne(new QueryWrapper<SysDict>().eq("dict_code", FinalCode.ClASSES_CODE));
            one = new SysDictItem();
            one.setDictId(sysDict.getId());
            one.setDictCode(sysDict.getDictCode());
            one.setItemText(String.valueOf(dzProductionPlan.getLineId()));
            one.setItemValue(String.valueOf(productionPlanDo.getDayClasses()));
            sysDictItemService.save(one);
        }
        //修改生产记录表
        return new Result(CustomExceptionType.OK, dzProductionPlan);
    }

    @Override
    public Result<?> planAnalysisGraphical(String sub, PlanAnalysisGraphical graphical) {
        String planDay = ((RunDataModel) dzDetectionTemplCache.systemRunModel(null).getData()).getPlanDay();
        List<Map<String, Object>> maps = productionPlanDayService.planAnalysisGraphical(Long.valueOf(graphical.getLineId()), graphical.getStartTime(), graphical.getEndTime(), planDay);
        LineDataAnalysisX lineDataAnalysis = new LineDataAnalysisX();
        if (CollectionUtils.isNotEmpty(maps)) {
            List<String> percentageCompletes = new ArrayList<>();
            List<String> outputRates = new ArrayList<>();
            List<String> passRates = new ArrayList<>();
            List<String> detectorTimes = new ArrayList<>();
            for (Map<String, Object> map : maps) {
                String percentageComplete = new BigDecimal(map.get("percentageComplete").toString()).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                String outputRate = new BigDecimal(map.get("outputRate").toString()).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                String passRate = new BigDecimal(map.get("passRate").toString()).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                String detectorTime = map.get("detectorTime").toString();
                percentageCompletes.add(percentageComplete);
                outputRates.add(outputRate);
                passRates.add(passRate);
                detectorTimes.add(detectorTime);
            }
            lineDataAnalysis.setPercentageComplete(percentageCompletes);
            lineDataAnalysis.setOutputRate(outputRates);
            lineDataAnalysis.setPassRate(passRates);
            lineDataAnalysis.setDetectorTime(detectorTimes);
        }
        return Result.ok(lineDataAnalysis);
    }

    @Override
    public Result equipmentTimeAnalysis(String sub, PlanAnalysisGraphical graphical) {
        Map<String, Object> map = new HashMap<>();
        String lineId = graphical.getLineId();
        String orderId = graphical.getOrderId();
        List<DzEquipment> yName = businessEquipmentService.listLingId(Long.valueOf(lineId));
        List<String> eqName = new ArrayList<>();
        List<String> timeRun = new ArrayList<>();
        List<String> stopTime = new ArrayList<>();
        LocalDate startTime = graphical.getStartTime();
        LocalDate endTime = graphical.getEndTime().plusDays(1);
        DzProductionLine line = dzProductionLineService.getById(lineId);
        DzOrder order = dzOrderService.getById(orderId);
        String lineNo = line.getLineNo();
        String orderNo = order.getOrderNo();
        for (DzEquipment dzEquipment : yName) {
            eqName.add(dzEquipment.getEquipmentName());
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

            //            没有结束当前在运行的实时间
            Long time1 = dzEquipmentRunTimeMapper.getDayRunTimeIsRestNnullSumOderNo(orderNo, lineNo, equipmentNo, equipmentType, startTime, endTime);
            if (time1 == null) {
                time1 = 0L;
            }
            Long time2 = dzEquipmentRunTimeMapper.getDayRunTimeSumOrderLine(orderNo, lineNo, equipmentNo, equipmentType, startTime, endTime);
            if (time2 == null) {
                time2 = 0L;
            }
//            运行时间
            time1 = time1 + time2;

            BigDecimal bigDecimal = new BigDecimal(time1);
//            停机小时
            BigDecimal divide = bigDecimal.divide(new BigDecimal(3600000), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal subtract = until.subtract(divide);
            timeRun.add(subtract.toString());
            stopTime.add(divide.toString());
        }
        map.put("eqName", eqName);
        map.put("timeRun", stopTime);
        map.put("stopTime", timeRun);
        return new Result<Map>(CustomExceptionType.OK, map);
    }

    @Override
    public Result getProcessingBeat(String sub, ProcessingBeat processingBeat) {
        ResProcessingBeat resProcessingBeat = new ResProcessingBeat();
        List<EquimentOrderLineId> orderLineIds = businessEquipmentService.getOrderLineEqId(processingBeat.getEquipmentIdList());
        List<String> x = new ArrayList<>();
        List<String> name = new ArrayList<>();
        for (EquimentOrderLineId orderLineId : orderLineIds) {
            Long id = orderLineId.getId();
            List<Long> listTime = redisUtil.lGet(RedisKey.FREQUENCY_MIN + id, 0, -1);
            String deviceIdAverageBeat = averageBeatService.getDeviceIdAverageBeat(listTime, id);
            x.add(deviceIdAverageBeat);
            name.add(orderLineId.getEquipmentName());
        }
        resProcessingBeat.setXAxis(x);
        resProcessingBeat.setYName(name);
        return Result.OK(resProcessingBeat);
    }

    @Override
    public Result equipmentTimeAnalysisV2(String sub, PlanAnalysisGraphicalTime graphical) {
        Date startTimeBef = graphical.getStartTime();
        Calendar calenda = Calendar.getInstance();
        calenda.setTime(startTimeBef);
        calenda.set(Calendar.HOUR_OF_DAY, 8);
        calenda.set(Calendar.MINUTE, 0);
        calenda.set(Calendar.MILLISECOND, 0);
        calenda.set(Calendar.SECOND, 0);
        Date timeAft = calenda.getTime();
        Calendar calendaEnd = Calendar.getInstance();
        calendaEnd.setTime(timeAft);
        calendaEnd.add(Calendar.DATE, 1);
        Date calendaEndAft = calendaEnd.getTime();
//        时间转日期 localDate
        LocalDate localDate = DateUtil.getLocalDate(timeAft);

        Map<Long, DzEquipment> deviceId = equipmentMapper.getDeviceId(Long.valueOf(graphical.getLineId()));
        List<DeviceStateDetails> deviceStateDetails = new ArrayList<>();
        for (Map.Entry<Long, DzEquipment> mp : deviceId.entrySet()) {
            Long id = mp.getKey();
            List<DeviceStateDetails> bra = busTimeAnalysisService.getDeviceStateDetails(localDate,timeAft, calendaEndAft,id);
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
                if ("2".equals(workState)) {
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
                if ("2".equals(workState)) {
                    parseData.setStandbyDuration(parseData.getStandbyDuration() + duration);
                }
                if ("3".equals(workState)) {
                    parseData.setFaultDuration(parseData.getFaultDuration() + duration);
                }
                if ("4".equals(workState)) {
                    parseData.setShutdownDuration(parseData.getShutdownDuration() + duration);
                }
                BigDecimal ch = new BigDecimal(parseData.getOperationDuration());
                BigDecimal bh = new BigDecimal(parseData.getOperationDuration() + parseData.getStandbyDuration() + parseData.getFaultDuration());
                BigDecimal divide = (bh.compareTo(new BigDecimal(0)) == 0 ? new BigDecimal(0) : ch.divide(bh, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100))).setScale(2, BigDecimal.ROUND_HALF_UP);
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
        DeviceStateDetailsResp resp = new DeviceStateDetailsResp();
        resp.setData(respListX);
        resp.setYAxis(yx);
        resp.setDeviceBaseData(deviceParseDataX);
        resp.setDataDoor(respListDoor);
        resp.setDeviceBaseDataDoor(deviceParseDataDoor);
        return Result.OK(resp);
    }



    private Integer getIndex(LinkedHashMap<String, Integer> mapX, String doorCode) {
        for (Map.Entry<String, Integer> map : mapX.entrySet()) {
            String key = map.getKey();
            String[] split = key.split("\\|");
            if (split.length == 2){
                if (split[1].equals(doorCode)) {
                    return map.getValue();
                }
            }
        }
        return null;
    }
    @Override
    public Result intelligentDetection(String sub, IntelligentDetectionVo intelligentDetectionVo) throws ParseException {
        Map<String,Object>map=new HashMap<>();
        DzProductionLine dzProductionLine = dzProductionLineService.getById(intelligentDetectionVo.getLineId());
        if(dzProductionLine==null){
            log.error("智能检测系统查询异常,产线不存在:{}",intelligentDetectionVo.getLineId());
            return  new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR.getCode(),Message.ERR_28);
        }
        Date startDate=null;
        Date endDate=null;
        //如果日期是当前
        LocalDate now = LocalDate.now();
        try {
            if(now.toString().equals(intelligentDetectionVo.getNewDate().toString())){
                Date localDateTime = DateUtil.getNowDate(now, "08:00:00");
                if(new Date().after(localDateTime)){
                    startDate = DateUtil.getNowDate(now, "08:00:00");
                    endDate = DateUtil.getNowDate(now.plusDays(1), "08:00:00");
                }else{
                    startDate = DateUtil.getNowDate(now.plusDays(-1), "08:00:00");
                    endDate = DateUtil.getNowDate(now, "08:00:00");
                }
            }else {
                startDate = DateUtil.getNowDate(intelligentDetectionVo.getNewDate(), "08:00:00");
                endDate = DateUtil.getNowDate(intelligentDetectionVo.getNewDate().plusDays(1), "08:00:00");
            }
        }catch (Exception e){
            log.error("智能检测系统查询，时间转换异常:{}",intelligentDetectionVo.getNewDate(),e);
            return  new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
        }

        List<DzWorkpieceData> dzWorkpieceDataList=dzWorkpieceDataMapper.getOneWorkpieceData(dzProductionLine.getOrderNo(),
                dzProductionLine.getLineNo(),intelligentDetectionVo.getProductName(),intelligentDetectionVo.getProducBarcode(),
                intelligentDetectionVo.getNewDate(),startDate,endDate,1);
        if(dzWorkpieceDataList.size()==0){
            map.put("testItemResult",new ArrayList<>());
            map.put("dzWorkpieceData",null);
            map.put("dzWorkpieceList",new ArrayList<>());
            return Result.ok(map);
        }
        DzWorkpieceData dzWorkpieceData = dzWorkpieceDataList.get(0);
        List<DzWorkpieceData> resDzWorkpieceData=dzWorkpieceDataMapper.getOneWorkpieceData(dzProductionLine.getOrderNo(),dzProductionLine.getLineNo(),dzWorkpieceData.getName(),null,intelligentDetectionVo.getNewDate(),startDate,endDate,-1);

        String productNo = dzWorkpieceData.getProductNo();
        DzProduct product_no = dzProductService.getOne(new QueryWrapper<DzProduct>().eq("product_no", productNo));
        if(product_no==null){
            log.error("产品编号不存在：{}",productNo);
            map.put("testItemResult",new ArrayList<>());
            map.put("dzWorkpieceData",null);
            map.put("dzWorkpieceList",new ArrayList<>());
            return Result.ok(map);
        }
        Map<String,Object>product=new HashMap<>();
        //生产订单号
        String wipOrderNo=momOrderMapper.getMomOrderByProducBarcode(dzWorkpieceData.getProducBarcode().split("_")[1]);
        if(wipOrderNo==null){
            wipOrderNo="未知订单";
        }
        product.put("wipOrderNo",wipOrderNo);
        //二维码
        product.put("producBarcode",dzWorkpieceData.getProducBarcode());
        //物料编号
        product.put("syProductNo",product_no.getSyProductNo());
        //检测时间
        product.put("detectorTime",dzWorkpieceData.getDetectorTime());
        //检测结果
        product.put("outOk",dzWorkpieceData.getOutOk());
        //产品名称
        product.put("productName",dzWorkpieceData.getName());

        product.put("img",product_no.getPicture());
        //查询检测项---------------------------------
        QueryWrapper<DzProductDetectionTemplate> wrapper = new QueryWrapper<DzProductDetectionTemplate>()
                .eq("product_no", productNo)
                .eq("is_show", 0)
                .eq("order_no", dzProductionLine.getOrderNo())
                .eq("line_no", dzProductionLine.getLineNo());
        List<DzProductDetectionTemplate> dzProductDetectionTemplates = dzProductDetectionTemplateService.list(wrapper);
        Collections.reverse(resDzWorkpieceData);
        List<Map<String,Object>> list=toIntelligentDetection(dzProductDetectionTemplates,resDzWorkpieceData,intelligentDetectionVo.getProducBarcode());

        //最新一条检测记录各项检测值 表格数据
        DzWorkpieceData dzWorkpiece = resDzWorkpieceData.get(resDzWorkpieceData.size() - 1);
        List<Map<String,Object>>testItemResult=new ArrayList<>();
        for (DzProductDetectionTemplate dzProductDetectionTemplate:dzProductDetectionTemplates) {

            Map<String,Object>testItem=new HashMap<>();
            testItem.put("testItemName",dzProductDetectionTemplate.getTableColCon());
//            testItem.put("standardValue",dzProductDetectionTemplate.getStandardValue());
            //第一个对象表示检测值
            testItem.put("detectValue",reflect(dzWorkpiece,dzProductDetectionTemplate.getTableColVal()));
            testItem.put("outOk",reflect(dzWorkpiece,"outOk"+dzProductDetectionTemplate.getTableColVal().substring(6)));
            testItemResult.add(testItem);

            Map<String,Object>testItem2=new HashMap<>();
            testItem2.put("testItemName",dzProductDetectionTemplate.getTableColCon());
//            testItem2.put("standardValue",dzProductDetectionTemplate.getStandardValue());
            //第二个对象表示标准值
            testItem2.put("detectValue",dzProductDetectionTemplate.getStandardValue());
            testItem2.put("outOk",reflect(dzWorkpiece,"outOk"+dzProductDetectionTemplate.getTableColVal().substring(6)));
            testItemResult.add(testItem2);
        }
        map.put("testItemResult",testItemResult);
        //最新检测记录详情
        map.put("dzWorkpieceData",product);
        //当日8-20点所有检测项 检测详情
        map.put("dzWorkpieceList",list);
        return Result.ok(map);
    }

    /**
     * 根据检测项集合  检测数据  封装检测数据
     * @param dzProductDetectionTemplates
     * @param dzWorkpieceDataList
     */
    public List<Map<String,Object>> toIntelligentDetection(List<DzProductDetectionTemplate> dzProductDetectionTemplates,List<DzWorkpieceData> dzWorkpieceDataList,String code){
        List<Map<String,Object>>list=new ArrayList<>();
        for (DzProductDetectionTemplate dzProductDetectionTemplate:dzProductDetectionTemplates) {
            Map<String,Object>resList=new HashMap<>();
            //检测项  标准值 上限值 下限值
            Map<String,Object>jxcValue=new HashMap<>();
            jxcValue.put("standardValue",dzProductDetectionTemplate.getStandardValue());
            jxcValue.put("upperValue",dzProductDetectionTemplate.getUpperValue());
            jxcValue.put("lowerValue",dzProductDetectionTemplate.getLowerValue());
            resList.put("table",jxcValue);
            resList.put("title",dzProductDetectionTemplate.getTableColCon());

            //折线图数据
            List<Object>data=new ArrayList<>();
            for (int i = 0; i <dzWorkpieceDataList.size() ; i++) {
                DzWorkpieceData dzWorkpieceData=dzWorkpieceDataList.get(i);
                data.add(reflect(dzWorkpieceData,dzProductDetectionTemplate.getTableColVal()));
                if(!StringUtil.isEmpty(code)&&code.equals(dzWorkpieceData.getProducBarcode())){
                    resList.put("index",i);
                }
            }
            resList.put("data",data);
            list.add(resList);
        }

        return list;
    }

    public Object reflect(DzWorkpieceData obj,String str){

        try {
            Class quantityServiceImpl = obj.getClass();
            String method = StringToUpcase.toUpperCase(str);
            Method m = quantityServiceImpl.getDeclaredMethod("get"+method);
            Object invoke = m.invoke(obj);
            return invoke;
        } catch (NoSuchMethodException e) {
            log.error("获取Bean中的方法失败：{}", e);
        } catch (IllegalAccessException e) {
            log.error("执行方法参数异常：{}", e);
        } catch (InvocationTargetException e) {
            log.error("构造方法异常:{}", e);
        }
        return null;
    }


}
