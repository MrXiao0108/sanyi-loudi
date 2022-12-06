package com.dzics.business.service.kanban;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.business.service.BusTimeAnalysisService;
import com.dzics.business.service.cache.DzDetectionTemplCache;
import com.dzics.business.util.RedisUtil;
import com.dzics.common.dao.DzEquipmentMapper;
import com.dzics.common.enums.EquiTypeEnum;
import com.dzics.common.enums.WorkState;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.constant.MomProgressStatus;
import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.model.entity.DzProductionLine;
import com.dzics.common.model.entity.MonOrder;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.timeanalysis.*;
import com.dzics.common.util.DateUtil;
import com.dzics.common.util.RedisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Classname TimeAnalysisKanBan
 * @Description 看板时间相关方法
 * @Date 2022/6/17 15:18
 * @Created by NeverEnd
 */
@Service
@Slf4j
public class TimeAnalysisKanBan {
    @Autowired
    public RedisUtil redisUtil;
    @Autowired
    public DzDetectionTemplCache dzDetectionTemplCache;
    @Autowired
    public DzEquipmentMapper dzEquipmentMapper;
    @Autowired
    private BusTimeAnalysisService busTimeAnalysisService;
    @Autowired
    private  DateUtil dateUtil;

    /**
     * 获取当前日期数据
     * @param orderNoLineNo
     * @return
     */
    public Result getCurrentDate(GetOrderNoLineNo orderNoLineNo) {
        orderNoLineNo.setCacheTime(orderNoLineNo.getCacheTime()+((int) (Math.random() * 10)));
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

    /**
     * 设备用时分析包含 作业 待机 故障 关机
     * @return
     */
    public Result getDeviceTimeAnalysis(GetOrderNoLineNo orderNoLineNo) {
        orderNoLineNo.setCacheTime(orderNoLineNo.getCacheTime()+((int) (Math.random() * 10)));
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
//                        detailsData.setDuration(duration);
                        x.add(dateStr);
                        x.add(duration);
                    } else {
//                        detailsData.setDuration(duration);
                        x.add(resetTime);
                        x.add(duration);
                    }
                    detailsData.setValue(x);
                    DeviceParseData parseData = deviceParseDataX.get(index);
                    parseData.setDeviceName(equipmentName);
                    parseData.setDeviceType(deviceType);
                    parseData.setDoorCode(doorCode);
                    parseData.setEquipmentNo(equipmentNo);
                    DzEquipment dzEquipment = dzEquipmentMapper.selectOne(new QueryWrapper<DzEquipment>().eq("order_no", orderNoLineNo.getOrderNo())
                            .eq("line_no", orderNoLineNo.getLineNo()).eq("equipment_no", equipmentNo));
                    parseData.setStandardOperationRate(dzEquipment.getStandardOperationRate());
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


}
