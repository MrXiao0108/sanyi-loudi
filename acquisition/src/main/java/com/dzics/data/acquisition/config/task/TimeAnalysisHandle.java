package com.dzics.data.acquisition.config.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.enums.EquiTypeEnum;
import com.dzics.common.enums.WorkState;
import com.dzics.common.model.entity.*;
import com.dzics.common.service.DzTimeChangeHandleService;
import com.dzics.data.acquisition.constant.PushEnumType;
import com.dzics.data.acquisition.model.PushKanbanBase;
import com.dzics.data.acquisition.model.SocketDowmSum;
import com.dzics.data.acquisition.service.AccDayShutDownTimesService;
import com.dzics.data.acquisition.service.AccDeviceTimeAnalysisService;
import com.dzics.data.acquisition.service.AccqDzEquipmentService;
import com.dzics.data.acquisition.service.CacheService;
import com.dzics.data.acquisition.service.mq.RabbitmqService;
import com.dzics.data.acquisition.util.SnowflakeUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.dataflow.job.DataflowJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ZhangChengJun
 * Date 2021/11/17.
 * @since
 */
@Slf4j
@Service
public class TimeAnalysisHandle implements DataflowJob<DzTimeChangeHandle> {
    @Autowired
    private DzTimeChangeHandleService dzTimeChangeHandleService;
    @Autowired
    private AccDeviceTimeAnalysisService accDeviceTimeAnalysisService;
    @Autowired
    private SnowflakeUtil snowflakeUtil;
    @Autowired
    private AccDayShutDownTimesService accDayShutDownTimesService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private AccqDzEquipmentService accqDzEquipmentService;
    @Autowired
    private RabbitmqService rabbitmqService;

    @Override
    public List<DzTimeChangeHandle> fetchData(ShardingContext shardingContext) {
        log.debug("??????????????????????????????????????????:shardingParameter{}", shardingContext);
        PageHelper.startPage(1, 10);
        QueryWrapper<DzTimeChangeHandle> wp = new QueryWrapper<>();
        wp.eq("equipment_no", shardingContext.getShardingParameter());
        wp.orderByAsc("key_sort");
        List<DzTimeChangeHandle> handles = dzTimeChangeHandleService.list(wp);
        PageInfo<DzTimeChangeHandle> dzTimeChangeHandlePageInfo = new PageInfo<>(handles);
        List<DzTimeChangeHandle> changeHandles = dzTimeChangeHandlePageInfo.getList();
        List<String> collect = changeHandles.stream().map(s -> s.getChangeId()).collect(Collectors.toList());
        dzTimeChangeHandleService.removeByIds(collect);
//        changeHandles.sort(Comparator.comparing(DzTimeChangeHandle::getKeySort).reversed());
        return changeHandles;
    }

    @Override
    public synchronized void processData(ShardingContext shardingContext, List<DzTimeChangeHandle> list) {
        for (DzTimeChangeHandle changeHandle : list) {
            Integer workState = changeHandle.getWorkState();
            Date upLocalDate = changeHandle.getUpLocalDate();
            LocalDate localDate = changeHandle.getDetectionDate();
            Date nowDate = changeHandle.getDetectionTime();
            Long deviceId = changeHandle.getDeviceId();
//           START ????????????
            if (workState == null) {
                splitDateDayTimeIns(localDate, nowDate, upLocalDate, deviceId);
                continue;
            }
//           END ????????????
            LocalTime localTime = changeHandle.getDetectionLocalTime();
            Integer equipmentType = changeHandle.getEquipmentType();
            String equipmentNo = changeHandle.getEquipmentNo();
            String orderNo = changeHandle.getOrderNo();
            String lineNo = changeHandle.getLineNo();
//          START ??????????????????????????????????????????????????????????????????
            DzEquipmentTimeAnalysis timeAnalysis = accDeviceTimeAnalysisService.getResetTimeIsNull(deviceId);
            long duration = 0;
            if (timeAnalysis == null) {
//                    ????????????
                DzEquipmentTimeAnalysis dzEquipmentTimeAnalysis = saveTimeAnalysis(localDate, localTime, nowDate, deviceId, orderNo, lineNo, equipmentNo, equipmentType, workState);
                accDeviceTimeAnalysisService.saveTimeAnlysis(dzEquipmentTimeAnalysis);
            } else {
//                    ?????? ?????? ????????????  ????????????????????????????????????????????????????????????
                long stopTime = timeAnalysis.getStopTime().getTime();
                long restTime = nowDate.getTime();
                duration = restTime - stopTime;
                timeAnalysis.setDuration(duration);
                timeAnalysis.setResetTime(nowDate);
                timeAnalysis.setResetDayTime(localTime);
                timeAnalysis.setResetHour(localTime.getHour());
                accDeviceTimeAnalysisService.updateByIdTimeAnalysis(timeAnalysis);
                DzEquipmentTimeAnalysis dzEquipmentTimeAnalysis = saveTimeAnalysis(localDate, localTime, nowDate, deviceId, orderNo, lineNo, equipmentNo, equipmentType, workState);
                accDeviceTimeAnalysisService.saveTimeAnlysis(dzEquipmentTimeAnalysis);
            }
//          END ??????????????????????????????????????????????????????????????????

//          START ????????????????????????????????????????????????????????????????????????
            if (EquiTypeEnum.MEN.getCode() == Integer.valueOf(equipmentType)) {
                if (WorkState.OPEN == workState.intValue()) {
//                     ??????????????? 10 ????????? ????????????1 ?????????????????????????????????
                    boolean b = updateDeviceStopSum(orderNo, lineNo, equipmentNo, equipmentType, localDate, deviceId);
                }
                if (timeAnalysis != null && timeAnalysis.getWorkState().intValue() == WorkState.OPEN) {
                    boolean b = updateTimeSum(orderNo, lineNo, equipmentNo, equipmentType, duration, deviceId);
                }
            } else {
                if (WorkState.SHUTDOWN == workState.intValue()) {
                    boolean b = updateDeviceStopSum(orderNo, lineNo, equipmentNo, equipmentType, localDate, deviceId);
                }
                if (timeAnalysis != null && timeAnalysis.getWorkState().intValue() == WorkState.SHUTDOWN) {
                    boolean b = updateTimeSum(orderNo, lineNo, equipmentNo, equipmentType, duration, deviceId);
                }
            }
//          END ????????????????????????????????????????????????????????????????????????
        }

    }

    /**
     * ????????????
     *
     * @param orderNo  ??????
     * @param lineNo   ??????
     * @param eqNo     ????????????
     * @param eqType   ????????????
     * @param noLoDate ?????????????????????
     * @param deviceId ??????ID
     * @return
     */
    public boolean updateDeviceStopSum(String orderNo, String lineNo, String eqNo, Integer eqType, LocalDate noLoDate, Long deviceId) {
        String eqTyStr = String.valueOf(eqType);
        Long downSum = cacheService.upDownSum(lineNo, eqNo, eqTyStr, orderNo);
        if (downSum == null) {
            log.warn("??????????????????????????????NULL???orderNo???{},lineNum:{},deviceNum:{},deviceType:{}", orderNo, lineNo, eqNo, eqTyStr);
            return false;
        }
        DzDayShutDownTimes dayDown = cacheService.getDayShoutDownTime(lineNo, eqNo, eqType, orderNo, noLoDate);
        if (dayDown == null) {
            String orgCode = cacheService.getDeviceOrgCode(lineNo, eqNo, eqTyStr, orderNo);
            dayDown = new DzDayShutDownTimes();
            dayDown.setWorkDate(noLoDate);
            dayDown.setOrderNo(orderNo);
            dayDown.setLineNo(lineNo);
            dayDown.setEquipmentNo(eqNo);
            dayDown.setEquipmentType(eqType);
            dayDown.setDownSum(1L);
            dayDown.setOrgCode(orgCode);
            dayDown.setDelFlag(false);
            dayDown.setCreateBy("");
            dayDown.setCreateTime(new Date());
            accDayShutDownTimesService.saveDzDayShutDownTimes(dayDown);
        } else {
            dayDown.setDownSum(dayDown.getDownSum().longValue() + 1);
            cacheService.updateByIdDzDayShutDownTimes(dayDown);
        }
        downSum = downSum + 1;
        DzEquipment dzE = new DzEquipment();
        dzE.setId(deviceId);
        dzE.setEquipmentNo(eqNo);
        dzE.setLineNo(lineNo);
        dzE.setEquipmentType(eqType);
        dzE.setOrderNo(orderNo);
        dzE.setDownSum(downSum);
        dzE.setDayDownSum(dayDown.getDownSum());
        int up = accqDzEquipmentService.updateByLineNoAndEqNoDownTime(dzE);
        Long upDateDownSum = cacheService.upDateDownSum(lineNo, eqNo, eqTyStr, orderNo, downSum);
//        ??????????????????????????????MQ
        SocketDowmSum dowmSum = new SocketDowmSum();
        dowmSum.setDownSum(dayDown.getDownSum());
        dowmSum.setEquimentId(String.valueOf(deviceId));
        dowmSum.setLineNo(lineNo);
        dowmSum.setOrderNo(orderNo);
        PushKanbanBase kanbanBase = new PushKanbanBase();
        kanbanBase.setData(dowmSum);
        kanbanBase.setType(PushEnumType.DOWN_SUM);
        rabbitmqService.sendDeviceDownSum(kanbanBase);
        return true;
    }

    /**
     * ????????????  ????????????
     *
     * @param orderNo
     * @param lineNo
     * @param eqNo
     * @param eqType
     * @param duration
     * @param deviceId
     * @return
     */
    public boolean updateTimeSum(String orderNo, String lineNo, String eqNo, Integer eqType, long duration, Long deviceId) {
        String eqTyStr = String.valueOf(eqType);
        Long downSumTime = cacheService.upDownSumTime(lineNo, eqNo, eqTyStr, orderNo);
        if (downSumTime == null) {
            log.warn("?????????????????????????????????NULL???orderNo???{},lineNum:{},deviceNum:{},deviceType:{}", orderNo, lineNo, eqNo, eqTyStr);
            return false;
        }
        downSumTime = downSumTime + duration;
        DzEquipment dzE = new DzEquipment();
        dzE.setId(deviceId);
        dzE.setEquipmentNo(eqNo);
        dzE.setLineNo(lineNo);
        dzE.setEquipmentType(eqType);
        dzE.setOrderNo(orderNo);
        dzE.setDownTime(downSumTime);
        int up = accqDzEquipmentService.updateByLineNoAndEqNoDownTime(dzE);
        Long upDateDownTime = cacheService.upDateDownTime(lineNo, eqNo, eqTyStr, orderNo, downSumTime);
        return true;
    }

    public void splitDateDayTimeIns(LocalDate localDate, Date nowDate, Date upLocalDate, Long deviceId) {
//        ??????????????????
        int hoursNow = nowDate.getHours();
        int hoursUp = upLocalDate.getHours();
        Calendar instanceEnd = Calendar.getInstance();
        if (hoursNow != (hoursUp + 1)) {
            instanceEnd.setTime(nowDate);
        } else {
            instanceEnd.setTime(upLocalDate);
        }
        instanceEnd.set(Calendar.MINUTE, 00);
        instanceEnd.set(Calendar.SECOND, 00);
        instanceEnd.set(Calendar.MILLISECOND, 0);
        instanceEnd.add(Calendar.HOUR_OF_DAY, 1);
        Date endOfDay = instanceEnd.getTime();
        LocalTime resetDayTime = DateToLocalTime(endOfDay);

        Calendar instanceStart = Calendar.getInstance();
        instanceStart.setTime(nowDate);
        instanceStart.set(Calendar.MINUTE, 00);
        instanceStart.set(Calendar.SECOND, 00);
        instanceStart.set(Calendar.MILLISECOND, 0);
        Date startOfDay = instanceStart.getTime();
        LocalTime localTime = DateToLocalTime(startOfDay);
//        ????????????????????????????????????
        List<DzEquipmentTimeAnalysis> analysisList = accDeviceTimeAnalysisService.getRestTimeIsNullDeviceId(deviceId);
        for (DzEquipmentTimeAnalysis timeAnalysis : analysisList) {
            long endOfDayTime = endOfDay.getTime();
            long stopTime = timeAnalysis.getStopTime().getTime();
            if (timeAnalysis.getGroupId() == null) {
                timeAnalysis.setGroupId(snowflakeUtil.nextId());
            }
            timeAnalysis.setDuration(endOfDayTime - stopTime);
            timeAnalysis.setResetTime(endOfDay);
            timeAnalysis.setResetDayTime(resetDayTime);
            timeAnalysis.setResetHour(resetDayTime.getHour());
        }
        accDeviceTimeAnalysisService.updateByIdList(analysisList);
        List<DzEquipmentTimeAnalysis> inst = analysisList.stream().collect(Collectors.toList());
        for (DzEquipmentTimeAnalysis timeAnalysis : inst) {
            timeAnalysis.setId(null);
            timeAnalysis.setResetTime(null);
            timeAnalysis.setResetDayTime(null);
            timeAnalysis.setResetHour(null);
            timeAnalysis.setDuration(0L);
            timeAnalysis.setCreateTime(null);
            timeAnalysis.setUpdateTime(null);
            timeAnalysis.setStopTime(startOfDay);
            timeAnalysis.setStopDayTime(localTime);
            timeAnalysis.setStopHour(localTime.getHour());
            timeAnalysis.setStopData(localDate);
        }
        accDeviceTimeAnalysisService.insTimeAnalysis(inst);
    }


    public LocalTime DateToLocalTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        LocalTime localTime = localDateTime.toLocalTime();
        return localTime;
    }

    public Date getPreciseTime(Date date) {
        // ????????????
        Calendar c = Calendar.getInstance();
        // ????????????
        c.setTime(date);
        // ??????????????????0
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public Date localDateToUdate(LocalDate localDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        Date date = Date.from(instant);
        return getPreciseTime(date);
    }

    public DzEquipmentTimeAnalysis saveTimeAnalysis(LocalDate now, LocalTime localTime, Date date, Long deviceId, String orderNo, String lineNo, String equipmentNo, Integer equipmentType, int workState) {
//        ???????????????????????????
        DzEquipmentTimeAnalysis analysis = new DzEquipmentTimeAnalysis();
        analysis.setDeviceId(deviceId);
        analysis.setOrderNo(orderNo);
        analysis.setLineNo(lineNo);
        analysis.setEquipmentNo(equipmentNo);
        analysis.setEquipmentType(equipmentType);
        analysis.setWorkState(workState);
        analysis.setGroupId(snowflakeUtil.nextId());
        analysis.setStopTime(date);
        analysis.setStopData(now);
        analysis.setStopDayTime(localTime);
        analysis.setStopHour(localTime.getHour());
        analysis.setOrgCode("A00");
        analysis.setDelFlag(false);
        analysis.setCreateBy("SYS-A00");
        return analysis;
    }
}
