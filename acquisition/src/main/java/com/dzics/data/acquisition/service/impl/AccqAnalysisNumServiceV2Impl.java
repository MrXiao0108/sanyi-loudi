package com.dzics.data.acquisition.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.enums.*;
import com.dzics.common.model.custom.*;
import com.dzics.common.model.entity.*;
import com.dzics.common.util.DateUtil;
import com.dzics.data.acquisition.model.PylseSignalValue;
import com.dzics.data.acquisition.service.*;
import com.dzics.data.acquisition.util.RedisUtil;
import com.dzics.data.acquisition.util.SnowflakeUtil;
import com.dzics.data.acquisition.util.TcpStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

/**
 * @author ZhangChengJun
 * Date 2021/1/20.
 * @since
 */
@Service
@Slf4j
public class AccqAnalysisNumServiceV2Impl implements AccqAnalysisNumService {
    @Autowired
    private MqService mqService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    TcpStringUtil tcpStringUtil;
    @Autowired
    private AccqLineShiftDayService accqLineShiftDayService;
    @Autowired
    private AccqDzEquipmentService accqDzEquipmentService;
    @Autowired
    private AccqDzEqProNumDetailsService accqDzEqProNumDetailsService;
    @Autowired
    private AcqDzEqProDataService accqDzEqProDataService;
    @Autowired
    private AccqDzEquipmentStateLog accqDzEquipmentStateLog;
    @Autowired
    private AccEqStopLogService accEqStopLogService;
    @Autowired
    @Lazy
    private CacheService cacheService;
    @Autowired
    private SnowflakeUtil snowflakeUtil;
    @Autowired
    private AccDetectorDataService dzDetectorDataService;

    @Autowired
    private AccEquipmentAlarmRecordService alarmRecordService;
    @Autowired
    private AccDayShutDownTimesService accDayShutDownTimesService;


    /**
     * 处理生产数据
     *
     * @param cmd
     */
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public DzEquipmentProNum analysisNum(RabbitmqMessage cmd) {
        Map<String, Object> map = tcpStringUtil.analysisCmdV2(cmd);
        if (CollectionUtils.isNotEmpty(map)) {
//            底层设备上传时间时间
            Long senDate = (Long) map.get(CmdStateClassification.DATA_STATE_TIME.getCode());
//            分类唯一属性值
            DzTcpDateID tcpDateId = (DzTcpDateID) map.get(CmdStateClassification.TCP_ID.getCode());
            if (tcpDateId == null) {
                log.error("分类唯一属性值不存在：DzTcpDateID：{}", tcpDateId);
                return null;
            }
            String lineNum = tcpDateId.getProductionLineNumber();
            String deviceType = tcpDateId.getDeviceType();
            String deviceNum = tcpDateId.getDeviceNumber();
            String orderNumber = tcpDateId.getOrderNumber();
//            成品数量
            List<CmdTcp> cpData = (List<CmdTcp>) map.get(CmdStateClassification.CP_DATA.getCode());
//            合格数量
            List<CmdTcp> okData = (List<CmdTcp>) map.get(CmdStateClassification.Ok_DATA.getCode());
//            毛坯数量
            List<CmdTcp> mpData = (List<CmdTcp>) map.get(CmdStateClassification.MP_DATA.getCode());

            List<CmdTcp> workPiece = (List<CmdTcp>) map.get(CmdStateClassification.TCP_ROB_WORK_PIECE.getCode());
//            定义当前时间 当前日期
            Date nowDate = new Date(senDate);
            LocalTime localTime = DateUtil.dataToLocalTime(nowDate).withNano(0);
            int hour = localTime.getHour();
            LocalDate nowLocalDate = DateUtil.dataToLocalDate(nowDate);
//            数据系统编码
            String orgCode = cacheService.getDeviceOrgCode(lineNum, deviceNum, deviceType, orderNumber);
//           上次设备数据
            UpValueDevice upValueDevice = cacheService.getUpValueDevice(lineNum, deviceNum, deviceType, orderNumber);
//           定义当前设备数据
            UpValueDevice nowValueDevice = null;
//         查看当前班次记录是否存在，存在则返回当前班次数据
            DzEquipmentProNum dzEqProNum = null;
            DzEquipmentProNumDetails details = null;
//            默认跟新班次生产数据
            boolean falg = true;
//            数据设值是否更新
            if (CollectionUtils.isNotEmpty(cpData) || CollectionUtils.isNotEmpty(mpData) || CollectionUtils.isNotEmpty(okData)) {
//            当前班次定义
                DzLineShiftDay lineShiftDays = cacheService.getLingShifuDay(lineNum, deviceNum, deviceType, orderNumber, nowLocalDate, localTime);
                if (lineShiftDays != null) {
                    //                    TODO 如果有批次号,型号区分需要二次修改额外处理
////                    查看当前班次记录是否存在，存在则返回当前班次数据
                    dzEqProNum = cacheService.getDzDayEqProNum(lineShiftDays.getId(), "", "", "", hour);
                    if (dzEqProNum == null) {
                        dzEqProNum = new DzEquipmentProNum();
                        dzEqProNum.setDayId(lineShiftDays.getId());
                        dzEqProNum.setProductType("");
                        dzEqProNum.setWorkData(nowLocalDate);
                        dzEqProNum.setWorkMouth(nowLocalDate.getYear() + "-" + (nowLocalDate.getMonth().getValue() > 10 ? nowLocalDate.getMonth().getValue() : "0" + nowLocalDate.getMonth().getValue()));
                        dzEqProNum.setWorkYear(nowLocalDate.getYear());
                        dzEqProNum.setOrderNo(orderNumber);
                        dzEqProNum.setLineNo(lineNum);
                        dzEqProNum.setBatchNumber("");
                        dzEqProNum.setModelNumber("");
                        dzEqProNum.setRoughNum(0L);
                        dzEqProNum.setQualifiedNum(0L);
                        dzEqProNum.setNowNum(0L);
                        dzEqProNum.setTotalNum(0L);
                        dzEqProNum.setBadnessNum(0L);
                        dzEqProNum.setOrgCode(orgCode);
                        dzEqProNum.setDelFlag(false);
                        dzEqProNum.setWorkHour(hour);
                        falg = false;
                    }
                } else {
//                    当前班次数据不存在时，这里返回，相当于现在存储的是有班次之前的数量值，一直到有班次是，中间的差额数量会计算到下次有班次的的记录中。
//                    如果当前班次数量不存在时间过长，或生产数量一个周期则数据会错误。
                    log.warn("当前排班数据不存在丢弃数据：lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, cmd);
                    return null;
                }
            } else {
                log.debug("生产数据未发生变化：lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, cmd);
                return null;
            }
            details = new DzEquipmentProNumDetails();
            nowValueDevice = new UpValueDevice();
            details.setWorkNum(0L);
            details.setRoughNum(0L);
            details.setQualifiedNum(0L);
            if (CollectionUtils.isNotEmpty(cpData)) {
//                成品数量本次有值
                log.debug("本次数据有成品数量 :lineNum:{},deviceNum:{},deviceType:{},cpData:{}", lineNum, deviceNum, deviceType, cpData);
//              上次班次成品的值
                Long proNumNowNum = dzEqProNum.getNowNum();
//                上次总数值
                Long upTotalNum = upValueDevice.getTotalNum();
                Long workNum = upValueDevice.getWorkNum();
//                当前值
                Long wkN = Long.valueOf(cpData.get(0).getDeviceItemValue());
//               增加的值
                Long now = calculateQuantity(wkN, workNum);
//                累计总数值
                Long histyTotal = upTotalNum + now;
//                当前班次累计值
                Long proMowNum = proNumNowNum + now;
                dzEqProNum.setNowNum(proMowNum);
                dzEqProNum.setTotalNum(histyTotal);
//                重新设置
                nowValueDevice.setTotalNum(histyTotal);
                nowValueDevice.setWorkNum(wkN);
                details.setWorkNum(wkN);
            } else {
                nowValueDevice.setTotalNum(upValueDevice.getTotalNum());
                nowValueDevice.setWorkNum(upValueDevice.getWorkNum());
            }
            if (CollectionUtils.isNotEmpty(mpData)) {
                log.debug("本次数据有毛坯数量 :lineNum:{},deviceNum:{},deviceType:{},mpData:{}", lineNum, deviceNum, deviceType, mpData);
//              上次班次毛坯的值
                Long proNumRoughNum = dzEqProNum.getRoughNum();
//              上次发送毛坯总数值
                Long upTotalRoughNum = upValueDevice.getTotalRoughNum();
                Long roughNum = upValueDevice.getRoughNum();
//                当前值
                Long wkN = Long.valueOf(mpData.get(0).getDeviceItemValue());
//                本次需要增加的值
                Long now = calculateQuantity(wkN, roughNum);
//                累计总数值
                Long histyTotal = upTotalRoughNum + now;
//                当前班次累计值
                Long nowNumRoughNum = proNumRoughNum + now;
                dzEqProNum.setRoughNum(nowNumRoughNum);
//                重新设置
                nowValueDevice.setTotalRoughNum(histyTotal);
                nowValueDevice.setRoughNum(wkN);
                details.setRoughNum(wkN);
            } else {
                nowValueDevice.setTotalRoughNum(upValueDevice.getTotalRoughNum());
                nowValueDevice.setRoughNum(upValueDevice.getRoughNum());
            }
            if (CollectionUtils.isNotEmpty(okData)) {
                log.debug("本次数据有合格数量 :lineNum:{},deviceNum:{},deviceType:{},okData:{}", lineNum, deviceNum, deviceType, okData);
//                当前班次值
                Long proQualifiedNum = dzEqProNum.getQualifiedNum();
//                历史总数
                Long totalQualifiedNum = upValueDevice.getTotalQualifiedNum();
                Long qualifiedNum = upValueDevice.getQualifiedNum();
//                当前值
                Long wkN = Long.valueOf(okData.get(0).getDeviceItemValue());
//               增加的值
                long now = calculateQuantity(wkN, qualifiedNum);
//                累计总数值
                Long histyTotal = totalQualifiedNum + now;
//                当前班次累计值
                Long nowqnum = proQualifiedNum + now;
//                设置班次新增后值
                dzEqProNum.setQualifiedNum(nowqnum);
//                重新设置
                nowValueDevice.setTotalQualifiedNum(histyTotal);
                nowValueDevice.setQualifiedNum(wkN);
                details.setQualifiedNum(wkN);
            } else {
                nowValueDevice.setTotalQualifiedNum(upValueDevice.getTotalQualifiedNum());
                nowValueDevice.setQualifiedNum(upValueDevice.getQualifiedNum());
            }
            if (falg) {
                dzEqProNum.setBadnessNum(dzEqProNum.getRoughNum() - dzEqProNum.getQualifiedNum());
                cacheService.updateDzEqProNum(dzEqProNum);
            } else {
                dzEqProNum.setBadnessNum(dzEqProNum.getRoughNum() - dzEqProNum.getQualifiedNum());
                Long typeLingEqNo = cacheService.getTypeLingEqNoId(deviceNum, lineNum, deviceType, orderNumber);
                dzEqProNum.setEquimentId(typeLingEqNo);
                cacheService.saveDzEqProNum(dzEqProNum);
            }
            details.setDeviceType(deviceType);
            details.setLineNo(lineNum);
            details.setOrderNo(orderNumber);
            details.setEquipmentNo(deviceNum);
            details.setTotalNum(nowValueDevice.getTotalNum());
            details.setTotalQualifiedNum(nowValueDevice.getTotalQualifiedNum());
            details.setTotalRoughNum(nowValueDevice.getTotalRoughNum());
            details.setDelFlag(false);
            details.setOrgCode(orgCode);
//          存储记录详情
            accqDzEqProNumDetailsService.saveDataDetails(details);
//          缓存当前数据记录值
            cacheService.saveUpValueDevice(lineNum, deviceNum, deviceType, orderNumber, nowValueDevice);
            log.debug("保存当前数据详情& 设置当前生产数量缓存 :lineNum:{},deviceNum:{},deviceType:{}", lineNum, deviceNum, deviceType);
            if (CollectionUtils.isNotEmpty(workPiece)) {
                dzEqProNum.setCmdTcpList(workPiece);
            }
            return dzEqProNum;
        } else {
            log.warn("处理生成数据解析Map为为空：data：{}", map);
        }
        return null;
    }


    /**
     * @param cmd 处理运行状态数据
     */
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public DzEquipment analysisNumRunState(RabbitmqMessage cmd) {
        Map<String, Object> map = tcpStringUtil.analysisCmdV2(cmd);
        if (CollectionUtils.isNotEmpty(map)) {
//            底层设备上传时间时间
            Long senDate = (Long) map.get(CmdStateClassification.DATA_STATE_TIME.getCode());
//            分类唯一属性值
            DzTcpDateID tcpDateId = (DzTcpDateID) map.get(CmdStateClassification.TCP_ID.getCode());
            if (tcpDateId == null) {
                log.error("分类唯一属性值不存在：DzTcpDateID：{}", tcpDateId);
                return null;
            }
            String lineNum = tcpDateId.getProductionLineNumber();
            String deviceTypeStr = tcpDateId.getDeviceType();
            Integer deviceType = Integer.valueOf(deviceTypeStr);
            String deviceNum = tcpDateId.getDeviceNumber();
            String orderNumber = tcpDateId.getOrderNumber();
//            停机记录
            List<CmdTcp> cmdTcps = (List<CmdTcp>) map.get(CmdStateClassification.RUN_STATE.getCode());
            if (CollectionUtils.isNotEmpty(cmdTcps)) {
//              当前运行状态
                CmdTcp nowDzDq = cmdTcps.get(0);
                Integer nowDeviceItemValue = Integer.valueOf(nowDzDq.getDeviceItemValue());
//
                CmdTcp upCmdTcp = new CmdTcp();
                BeanUtils.copyProperties(nowDzDq, upCmdTcp);
                upCmdTcp = cacheService.getUpRunState(lineNum, deviceNum, deviceTypeStr, orderNumber, nowDzDq);
//              是否生成停机记录 根据设备类型判断停止指令
                Integer upDeviceItemValue = Integer.valueOf(upCmdTcp.getDeviceItemValue());
                if (upDeviceItemValue.intValue() != nowDeviceItemValue.intValue()) {
                    DzEquipment upDzDqState = cacheService.getTypeLingEqNo(deviceNum, lineNum, deviceTypeStr, orderNumber);
                    DzEquipment downEq = new DzEquipment();
                    downEq.setId(upDzDqState.getId());
                    downEq.setEquipmentNo(deviceNum);
                    downEq.setLineNo(lineNum);
                    downEq.setEquipmentType(deviceType);
                    downEq.setOrderNo(orderNumber);
//            定义时间
                    Date nowDate = new Date(senDate);
                    LocalDate nowLocalDate = DateUtil.dataToLocalDate(nowDate);
//            数据系统编码
                    String orgCode = cacheService.getDeviceOrgCode(lineNum, deviceNum, deviceTypeStr, orderNumber);
//            停机次数定义
                    Long dowmSum = 0L;
//            停机记录定义
                    DzEquipmentDowntimeRecord nowDowntimeRecord = null;
//          更新停机记录定义
                    DzEquipmentDowntimeRecord updateDowntimeRecord = null;
//            上次停机停机次数
                    Long upDownSum = cacheService.upDownSum(lineNum, deviceNum, deviceTypeStr, orderNumber);
                    Long upDownSumTime = cacheService.upDownSumTime(lineNum, deviceNum, deviceTypeStr, orderNumber);
                    if (upDownSum == null) {
                        log.warn("生成停机次数时设备不存在：lineNum:{},deviceNum:{},deviceType:{}", lineNum, deviceNum, deviceType);
                        return null;
                    }
                    if (upDownSumTime == null) {
                        log.warn("生成停机时间时设备不存在：lineNum:{},deviceNum:{},deviceType:{}", lineNum, deviceNum, deviceType);
                        return null;
                    }
//           根据设备类型生成停机或更新记录
                    if (deviceType.intValue() == EquiTypeEnum.JC.getCode()) {
                        if (nowDeviceItemValue.intValue() == RunStateEnum.CNC_STOP.getCode()) {
//                            查询是否存在之前触发的停机记录且为设置 重启时间
                            DzEquipmentDowntimeRecord lineNoEqNoTypeNoResetIsDzeq = accEqStopLogService.getLineNoEqNoTypeNoResetIsDzeq(orderNumber, lineNum, deviceTypeStr, deviceNum);
                            if (lineNoEqNoTypeNoResetIsDzeq == null) {
//                              生成设备停止记录
                                dowmSum = (Long) upDownSum + 1;
                                nowDowntimeRecord = new DzEquipmentDowntimeRecord();
                                nowDowntimeRecord.setOrderNo(orderNumber);
                                nowDowntimeRecord.setLineNo(lineNum);
                                nowDowntimeRecord.setOrgCode(orgCode);
                                nowDowntimeRecord.setEquipmentNo(deviceNum);
                                nowDowntimeRecord.setEquipmentType(deviceType);
                                nowDowntimeRecord.setStopTime(nowDate);
                                nowDowntimeRecord.setStopData(nowLocalDate);
                                nowDowntimeRecord.setDelFlag(false);
                                log.debug("设置设备停止记录:lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, nowDowntimeRecord);
                            }
                        } else {
//                                   更新停止记录 中的结束时间
                            updateDowntimeRecord = new DzEquipmentDowntimeRecord();
                            updateDowntimeRecord.setOrderNo(orderNumber);
                            updateDowntimeRecord.setLineNo(lineNum);
                            updateDowntimeRecord.setEquipmentNo(deviceNum);
                            updateDowntimeRecord.setEquipmentType(deviceType);
                            updateDowntimeRecord.setResetTime(nowDate);
                            log.debug("设置停止记录中的结束时间:lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, updateDowntimeRecord);
                        }
                    } else {
//                            机器人
                        if (nowDeviceItemValue.intValue() == EquiTypeCommonEnum.PRO.getCode()) {
//                          更新停止记录 中的结束时间
                            updateDowntimeRecord = new DzEquipmentDowntimeRecord();
                            updateDowntimeRecord.setOrderNo(orderNumber);
                            updateDowntimeRecord.setLineNo(lineNum);
                            updateDowntimeRecord.setEquipmentNo(deviceNum);
                            updateDowntimeRecord.setEquipmentType(deviceType);
                            updateDowntimeRecord.setResetTime(nowDate);
                            log.debug("设置停止记录中的结束时间:lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, updateDowntimeRecord);
                        } else {
//                            查询是否存在之前触发的停机记录且未设置重启时间
                            DzEquipmentDowntimeRecord lineNoEqNoTypeNoResetIsDzeq = accEqStopLogService.getLineNoEqNoTypeNoResetIsDzeq(orderNumber, lineNum, deviceTypeStr, deviceNum);
                            if (lineNoEqNoTypeNoResetIsDzeq == null) {
                                dowmSum = (Long) upDownSum + 1;
                                nowDowntimeRecord = new DzEquipmentDowntimeRecord();
                                nowDowntimeRecord.setOrderNo(orderNumber);
                                nowDowntimeRecord.setLineNo(lineNum);
                                nowDowntimeRecord.setOrgCode(orgCode);
                                nowDowntimeRecord.setEquipmentNo(deviceNum);
                                nowDowntimeRecord.setEquipmentType(deviceType);
                                nowDowntimeRecord.setStopTime(nowDate);
                                nowDowntimeRecord.setStopData(nowLocalDate);
                                nowDowntimeRecord.setDelFlag(false);
                                log.debug("设置设备停止记录:lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, nowDowntimeRecord);
                            }
                        }
                    }
//
                    if (nowDowntimeRecord != null) {
                        accEqStopLogService.saveDownTimeRecord(nowDowntimeRecord);
//              更新设备停机次数
                        downEq.setDownSum(dowmSum);
                        int up = accqDzEquipmentService.updateByLineNoAndEqNoDownTime(downEq);
//              START 生成每日停机记录
                        DzDayShutDownTimes dzDayShutDownTimes = cacheService.getDayShoutDownTime(lineNum, deviceNum, deviceType, orderNumber, nowLocalDate);
                        if (dzDayShutDownTimes == null) {
                            dzDayShutDownTimes = new DzDayShutDownTimes();
                            dzDayShutDownTimes.setWorkDate(nowLocalDate);
                            dzDayShutDownTimes.setOrderNo(orderNumber);
                            dzDayShutDownTimes.setLineNo(lineNum);
                            dzDayShutDownTimes.setEquipmentNo(deviceNum);
                            dzDayShutDownTimes.setEquipmentType(deviceType);
                            dzDayShutDownTimes.setDownSum(1L);
                            dzDayShutDownTimes.setOrgCode(orgCode);
                            dzDayShutDownTimes.setDelFlag(false);
                            dzDayShutDownTimes.setCreateBy("");
                            dzDayShutDownTimes.setCreateTime(new Date());
                            accDayShutDownTimesService.saveDzDayShutDownTimes(dzDayShutDownTimes);
                        } else {
                            dzDayShutDownTimes.setDownSum(dzDayShutDownTimes.getDownSum().longValue() + 1);
                            cacheService.updateByIdDzDayShutDownTimes(dzDayShutDownTimes);
                        }
                        downEq.setDayDownSum(dzDayShutDownTimes.getDownSum());
//                        END
                        Long upDateDownSum = cacheService.upDateDownSum(lineNum, deviceNum, deviceTypeStr, orderNumber, dowmSum);
                        log.debug("保存设备停止记录:lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, nowDowntimeRecord);
                        CmdTcp runState = cacheService.upDateUpRunState(lineNum, deviceNum, deviceTypeStr, orderNumber, nowDzDq);
                        log.debug("更新设备状态:lineNum:{},deviceNum:{},deviceType:{}", lineNum, deviceNum, deviceType);
                        downEq.setDowntimeRecord(nowDowntimeRecord);
                    }

                    if (updateDowntimeRecord != null) {
//                        上次停机记录的开始停机时间
                        UpdateDownTimeDate upStopTimeX = accEqStopLogService.getLineNoEqNoTypeNoResetIsNo(updateDowntimeRecord);

                        if (upStopTimeX != null) {
                            Date upStopTime = upStopTimeX.getUpStopTime();
                            updateDowntimeRecord.setId(upStopTimeX.getId());
//                       恢复时间减去 - 停机时间 = 停机时长
                            long resTime = (updateDowntimeRecord.getResetTime().getTime() - upStopTime.getTime());
                            updateDowntimeRecord.setDuration(resTime);
                            downEq.setDownTime(upDownSumTime + resTime);
                            LocalDate uplocalDate = upStopTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            if (nowLocalDate.compareTo(uplocalDate) != 0) {
//                            当前日期和上次停机的日期不为同一天
                                Date dateEnd = getnowEndTime(nowDate);
                                long resTimeSplit = (dateEnd.getTime() - upStopTime.getTime());
                                updateDowntimeRecord.setDuration(resTimeSplit);
                                updateDowntimeRecord.setResetTime(dateEnd);
//                                设置切割后的时间保存
                                long resTimeSplitNext = (nowDate.getTime() - dateEnd.getTime());
                                DzEquipmentDowntimeRecord splDown = new DzEquipmentDowntimeRecord();
                                splDown.setOrderNo(orderNumber);
                                splDown.setLineNo(lineNum);
                                splDown.setOrgCode(orgCode);
                                splDown.setEquipmentNo(deviceNum);
                                splDown.setEquipmentType(deviceType);
                                splDown.setStopTime(dateEnd);
                                splDown.setStopData(nowLocalDate);
                                splDown.setDelFlag(false);
                                splDown.setResetTime(nowDate);
                                splDown.setDuration(resTimeSplitNext);
                                accEqStopLogService.saveDownTimeRecord(splDown);
                            }
                            accEqStopLogService.updateLineNoEqNoTypeNo(updateDowntimeRecord);
                            int up = accqDzEquipmentService.updateByLineNoAndEqNoDownTime(downEq);
                            Long upDateDownTime = cacheService.upDateDownTime(lineNum, deviceNum, deviceTypeStr, orderNumber, downEq.getDownTime());
                            log.debug("更新设置停止记录中的结束时间:lineNum:{},deviceNum:{},deviceType:{}", lineNum, deviceNum, deviceType);
                            CmdTcp runState = cacheService.upDateUpRunState(lineNum, deviceNum, deviceTypeStr, orderNumber, nowDzDq);
                            log.debug("更新设备状态:lineNum:{},deviceNum:{},deviceType:{}", lineNum, deviceNum, deviceType);
                            downEq.setUpdateDowntimeRecord(updateDowntimeRecord);
                        } else {
                            CmdTcp runState = cacheService.upDateUpRunState(lineNum, deviceNum, deviceTypeStr, orderNumber, nowDzDq);
                            log.error("上次停机记录的不存在 无法更新设备表的停机时长cmd:{},updateDowntimeRecord:{}", cmd, updateDowntimeRecord);
                        }
                    }
                    if (nowDowntimeRecord != null) {
                        return downEq;
                    } else {
                        return null;
                    }
                } else {
                    log.debug("设备运行状态距离上次无变化：lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, nowDzDq);
                    return null;
                }
            } else {
                log.debug("处理运行状态数指令List为为空：data：{}", map);
                return null;
            }
        } else {
            log.warn("处理运行告警状态数据解析Map为为空：data：{}", map);
            return null;
        }

    }


    public static Date getnowEndTime(Date nowData) {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.setTime(nowData);
        todayEnd.set(Calendar.HOUR_OF_DAY, 00);
        todayEnd.set(Calendar.MINUTE, 00);
        todayEnd.set(Calendar.SECOND, 00);
        todayEnd.set(Calendar.MILLISECOND, 00);
        return todayEnd.getTime();
    }


    /**
     * @param nowQuantity 当前数量
     * @param upQuantity  上次数量
     * @return 返回增加的数量值
     */
    @Override
    public long calculateQuantity(Long nowQuantity, Long upQuantity) {
        long increment = 0;
        if (upQuantity.compareTo(0L) == 0) {
            return increment;
        }
        if (nowQuantity.compareTo(upQuantity) == -1) {
            increment = nowQuantity;
        } else if (nowQuantity.compareTo(upQuantity) == 1) {
            increment = nowQuantity - upQuantity;
        }
        return increment;
    }


    @Deprecated
    @Override
    public PylseSignalValue getSingValue(CmdTcp nowDzDq) {
        String deviceItemValue = nowDzDq.getDeviceItemValue();
        String[] sig = deviceItemValue.split(",");
        if (sig.length != 8) {
            log.error("脉冲信号值长度错误：data:{}", nowDzDq);
            return null;
        }
        PylseSignalValue signalValue = new PylseSignalValue();
        signalValue.setProductType(sig[0]);
        signalValue.setBatchCode(sig[1]);
        signalValue.setProductCode(sig[2]);
        signalValue.setQuantity1(Integer.valueOf(sig[3]));
        signalValue.setQuantity2(Integer.valueOf(sig[4]));
        signalValue.setQuantity3(Integer.valueOf(sig[5]));
        signalValue.setQuantity4(Integer.valueOf(sig[6]));
        signalValue.setSigFlag(Integer.valueOf(sig[7]));
        return signalValue;
    }


    /**
     * {"MessageId":"5541cab4-a440-497c-b048-f80929f84251","QueueName":"dzics-dev-gather-v1-checkout-equipment",
     * "ClientId":"DZROBOT","OrderCode":"DZ-1882","LineNo":"1","DeviceType":"2","DeviceCode":"01",
     * "Message":"A809|[9f709fea3d994f9c8ea0154ee00c4f65,
     * 1030,
     * 4,
     * 0
     * ,4
     * ,8952.071,0,
     * 7415.005,0,
     * 3467.137,0,
     * 68.565,1]",
     * "Timestamp":"2021-02-08 17:21:37.4722"}
     * 产品条码，产品ID，工位编号，机床编号，总状态(0=NG,1=OK,100)，检测项数量，检测值1，检测1状态，检测值2，检测2状态，检测值3，检测3状态，检测值4，检测4状态
     * A809|[4c1e55c02ca04480975b9b0d4bb4a79f,1030,2,0,4,5835.125,1,6922.305,1,1744.856,0,8869.579,0]
     *
     * @param cmd
     */
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public String queueCheckoutEquipment(RabbitmqMessage cmd) {
        log.debug("检测设备数据：{}", cmd);
        Map<String, Object> map = tcpStringUtil.analysisCmdV2(cmd);
        if (CollectionUtils.isNotEmpty(map)) {
//            底层设备上传时间时间
            Long senDate = (Long) map.get(CmdStateClassification.DATA_STATE_TIME.getCode());
//            分类唯一属性值
            DzTcpDateID tcpDateId = (DzTcpDateID) map.get(CmdStateClassification.TCP_ID.getCode());
            if (tcpDateId == null) {
                log.error("分类唯一属性值不存在：DzTcpDateID：{}", tcpDateId);
                return null;
            }
            String lineNum = tcpDateId.getProductionLineNumber();
            String deviceType = tcpDateId.getDeviceType();
            String deviceNum = tcpDateId.getDeviceNumber();
            String orderNumber = tcpDateId.getOrderNumber();
            List<CmdTcp> cmdTcps = (List<CmdTcp>) map.get(CmdStateClassification.TCP_CHECK_EQMENT.getCode());
            if (CollectionUtils.isNotEmpty(cmdTcps)) {
                CmdTcp cmdTcp = cmdTcps.get(0);
                String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(cmdTcp.getDeviceItemValue(), "[]");
                String tcpValue = cmdTcp.getTcpValue();
                if (!StringUtils.isEmpty(deviceItemValue) && !StringUtils.isEmpty(tcpValue)) {
                    String[] split = deviceItemValue.split(",");
                    if (split.length > 6) {
                        String producBarcode = split[0];
                        String productNo = split[1];
                        String workNumber = split[2];
                        String machineNumber = split[3];
                        Integer allState = Integer.valueOf(split[4]);
                        String itemNumber = split[5];
                        Date dateCj = new Date(senDate);
                        //            数据系统编码
                        String orgCode = cacheService.getDeviceOrgCode(lineNum, deviceNum, deviceType, orderNumber);
                        long groupKey = snowflakeUtil.nextId();
                        List<DzDetectorData> dataList = new ArrayList<>();
                        Date createTime = new Date();
                        List<ProductTemp> tempId = cacheService.getDzProDetectIonTemp(productNo);
                        if (CollectionUtils.isNotEmpty(tempId)) {
                            int flag = 6;
                            boolean falsIsAllState = false;
                            for (int i = 6; i < split.length; i = i + 2) {
                                DzDetectorData dzDetectorData = new DzDetectorData();
                                BigDecimal dataVal = new BigDecimal(split[i]);
                                ProductTemp productTemp = tempId.get(flag - 6);
                                dzDetectorData.setDetectionId(productTemp.getDetectionId());
                                dzDetectorData.setProducBarcode(producBarcode);
                                dzDetectorData.setWorkNumber(workNumber);
                                dzDetectorData.setAllState(allState);
                                dzDetectorData.setMachineNumber(machineNumber);
                                dzDetectorData.setAllStateUse(allState);
                                if (allState.intValue() == CheckEqState.PD.getCode()) {
                                    dzDetectorData.setAllStateUse(CheckEqState.OK.getCode());
                                    BigDecimal dataValJs = dataVal.add(productTemp.getCompensationValue());
                                    BigDecimal standardValue = productTemp.getStandardValue();
                                    if (dataValJs.compareTo(standardValue) != 0) {
                                        dzDetectorData.setIsQualified(CheckEqState.OK.getCode());
                                    } else {
                                        falsIsAllState = true;
                                        dzDetectorData.setIsQualified(CheckEqState.NG.getCode());
                                    }
                                } else {
                                    dzDetectorData.setIsQualified(Integer.valueOf(split[i + 1]));
                                }
                                dzDetectorData.setItemNumber(itemNumber);
                                dzDetectorData.setProductNo(productNo);
                                dzDetectorData.setEquipmentNo(deviceNum);
                                dzDetectorData.setOrderNo(orderNumber);
                                dzDetectorData.setDataVal(dataVal);
                                dzDetectorData.setDetectorTime(dateCj);
                                dzDetectorData.setGroupKey(groupKey + "");
                                dzDetectorData.setOrgCode(orgCode);
                                dzDetectorData.setDelFlag(false);
                                dzDetectorData.setCreateTime(createTime);
                                dataList.add(dzDetectorData);
                                flag++;
                            }
                            if (falsIsAllState) {
                                for (DzDetectorData dzDetectorData : dataList) {
                                    dzDetectorData.setAllStateUse(CheckEqState.NG.getCode());
                                }
                            }
                            boolean b = dzDetectorDataService.saveDataList(dataList);
                            if (b) {
                                return productNo;
                            }
                        } else {
                            log.error("产品检测项未配置：product_no:{}", productNo);
                        }

                    } else {
                        log.error("检测数据数组长度小于5：split：{}", split.toString());
                    }
                } else {
                    log.warn("指令数据信息无内容：device_item_value：{}，tcp_value：{}", deviceItemValue, tcpValue);
                }
            } else {
                log.warn("检测数据指令值无数据：cmdTcps：{}", cmdTcps);
            }
        } else {
            log.warn("处理检测设备数据解析Map为为空：data：{}", map);
        }

        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public DzEquipment analysisNumAlarmState(RabbitmqMessage rabbitmqMessage) {
        Map<String, Object> map = tcpStringUtil.analysisCmdV2(rabbitmqMessage);
        if (CollectionUtils.isNotEmpty(map)) {
//            底层设备上传时间时间
            Long senDate = (Long) map.get(CmdStateClassification.DATA_STATE_TIME.getCode());
//            分类唯一属性值
            DzTcpDateID tcpDateId = (DzTcpDateID) map.get(CmdStateClassification.TCP_ID.getCode());
            if (tcpDateId == null) {
                log.error("分类唯一属性值不存在：DzTcpDateID：{}", tcpDateId);
                return null;
            }
            String lineNum = tcpDateId.getProductionLineNumber();
            String deviceTypeStr = tcpDateId.getDeviceType();
            Integer deviceType = Integer.valueOf(deviceTypeStr);
            String deviceNum = tcpDateId.getDeviceNumber();
            String orderNumber = tcpDateId.getOrderNumber();
//            生成设备告警记录
            List<CmdTcp> alarmRecpRd = (List<CmdTcp>) map.get(CmdStateClassification.ALARM_RECPRD.getCode());
            if (CollectionUtils.isNotEmpty(alarmRecpRd)) {
                DzEquipment downEq = new DzEquipment();
                downEq.setEquipmentNo(deviceNum);
                downEq.setLineNo(lineNum);
                downEq.setEquipmentType(deviceType);
                downEq.setOrderNo(orderNumber);
//              当前告警状态
                CmdTcp nowDzDq = alarmRecpRd.get(0);
                Integer nowDeviceItemValue = Integer.valueOf(nowDzDq.getDeviceItemValue());
                CmdTcp upCmdTcp = cacheService.getUpAlarmRecpRd(lineNum, deviceNum, deviceTypeStr, orderNumber, nowDzDq);
//              是否生成告警记录 根据设备类型判断停止指令
                Integer upDeviceItemValue = Integer.valueOf(upCmdTcp.getDeviceItemValue());
                if (upDeviceItemValue.intValue() != nowDeviceItemValue.intValue()) {
//            定义时间
                    Date nowDate = new Date(senDate);
                    LocalDate nowLocalDate = DateUtil.dataToLocalDate(nowDate);
//            数据系统编码
                    String orgCode = cacheService.getDeviceOrgCode(lineNum, deviceNum, deviceTypeStr, orderNumber);

//            告警记录定义
                    DzEquipmentAlarmRecord nowDowntimeRecord = null;
//          更新告警记录定义
                    DzEquipmentAlarmRecord updateDowntimeRecord = null;
//           根据设备类型生成告警或更新记录
                    if (deviceType.intValue() == EquiTypeEnum.JC.getCode()) {
                        if (nowDeviceItemValue.intValue() == AlarmStatusEnum.CNC_ALARM_STATUS_DANGER.getCode()) {
//                  生成设备停止记录
                            nowDowntimeRecord = new DzEquipmentAlarmRecord();
                            nowDowntimeRecord.setOrderNo(orderNumber);
                            nowDowntimeRecord.setLineNo(lineNum);
                            nowDowntimeRecord.setOrgCode(orgCode);
                            nowDowntimeRecord.setEquipmentNo(deviceNum);
                            nowDowntimeRecord.setEquipmentType(deviceType);
                            nowDowntimeRecord.setStopTime(nowDate);
                            nowDowntimeRecord.setStopData(nowLocalDate);
                            nowDowntimeRecord.setDelFlag(false);
                            log.debug("设置设备告警记录:lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, nowDowntimeRecord);
                        } else if (nowDeviceItemValue.intValue() == AlarmStatusEnum.CNC_ALARM_STATUS_AUTHER.getCode()) {
//                                   更新停止记录 中的结束时间
                            updateDowntimeRecord = new DzEquipmentAlarmRecord();
                            updateDowntimeRecord.setOrderNo(orderNumber);
                            updateDowntimeRecord.setLineNo(lineNum);
                            updateDowntimeRecord.setEquipmentNo(deviceNum);
                            updateDowntimeRecord.setEquipmentType(deviceType);
                            updateDowntimeRecord.setResetTime(nowDate);
                            log.debug("设置告警记录中的结束时间:lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, updateDowntimeRecord);
                        }
                    } else if (deviceType.intValue() == EquiTypeEnum.JQR.getCode()) {
//                            其他
                        if (nowDeviceItemValue.intValue() == AlarmStatusEnum.ROBOT_AlARM_STATUS_DANGER.getCode()) {
                            nowDowntimeRecord = new DzEquipmentAlarmRecord();
                            nowDowntimeRecord.setOrderNo(orderNumber);
                            nowDowntimeRecord.setLineNo(lineNum);
                            nowDowntimeRecord.setOrgCode(orgCode);
                            nowDowntimeRecord.setEquipmentNo(deviceNum);
                            nowDowntimeRecord.setEquipmentType(deviceType);
                            nowDowntimeRecord.setStopTime(nowDate);
                            nowDowntimeRecord.setStopData(nowLocalDate);
                            nowDowntimeRecord.setDelFlag(false);
                            log.debug("设置设备告警记录:lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, nowDowntimeRecord);
                        } else if (nowDeviceItemValue.intValue() == AlarmStatusEnum.ROBOT_AlARM_STATUS_AUTHER.getCode()) {
                            //                                   更新停止记录 中的结束时间
                            updateDowntimeRecord = new DzEquipmentAlarmRecord();
                            updateDowntimeRecord.setOrderNo(orderNumber);
                            updateDowntimeRecord.setLineNo(lineNum);
                            updateDowntimeRecord.setEquipmentNo(deviceNum);
                            updateDowntimeRecord.setEquipmentType(deviceType);
                            updateDowntimeRecord.setResetTime(nowDate);
                            log.debug("设置停止告警中的结束时间:lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, updateDowntimeRecord);
                        }

                    }
//
                    if (nowDowntimeRecord != null) {
                        alarmRecordService.saveDownTimeRecord(nowDowntimeRecord);
                        log.debug("保存设备告警记录:lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, nowDowntimeRecord);
                    }

                    if (updateDowntimeRecord != null) {
//                        上次停机记录的开始停机时间
                        Date upStopTime = alarmRecordService.getLineNoEqNoTypeNoResetIsNo(updateDowntimeRecord);
                        if (upStopTime != null) {
//                       恢复时间减去 - 停机时间 = 停机时长
                            long resTime = (updateDowntimeRecord.getResetTime().getTime() - upStopTime.getTime());
                            updateDowntimeRecord.setDuration(resTime);
                            LocalDate uplocalDate = upStopTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            if (nowLocalDate.compareTo(uplocalDate) != 0) {
//                            当前日期和上次停机的日期不为同一天
                                Date dateEnd = getnowEndTime(nowDate);
                                long resTimeSplit = (dateEnd.getTime() - upStopTime.getTime());
                                updateDowntimeRecord.setDuration(resTimeSplit);
                                updateDowntimeRecord.setResetTime(dateEnd);
//                                设置切割后的时间保存
                                long resTimeSplitNext = (nowDate.getTime() - dateEnd.getTime());
                                DzEquipmentAlarmRecord splDown = new DzEquipmentAlarmRecord();
                                splDown.setOrderNo(orderNumber);
                                splDown.setLineNo(lineNum);
                                splDown.setOrgCode(orgCode);
                                splDown.setEquipmentNo(deviceNum);
                                splDown.setEquipmentType(deviceType);
                                splDown.setStopTime(dateEnd);
                                splDown.setStopData(nowLocalDate);
                                splDown.setDelFlag(false);
                                splDown.setResetTime(nowDate);
                                splDown.setDuration(resTimeSplitNext);
                                alarmRecordService.saveDownTimeRecord(splDown);
                            }
                            alarmRecordService.updateLineNoEqNoTypeNo(updateDowntimeRecord);
                            log.debug("更新设置告警记录中的结束时间:lineNum:{},deviceNum:{},deviceType:{}", lineNum, deviceNum, deviceType);
                        } else {
                            log.error("上次告警记录的不存在 无法更新设备表的告警时长");
                        }
                    }
                    CmdTcp runState = cacheService.upDateUpAlremState(lineNum, deviceNum, deviceTypeStr, orderNumber, nowDzDq);
                    log.debug("更新告警状态缓存记录:lineNum:{},deviceNum:{},deviceType:{}", lineNum, deviceNum, deviceType);
                    if (updateDowntimeRecord != null) {
                        return downEq;
                    }
                } else {
                    log.debug("设备告警状态距离上次无变化：lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, nowDzDq);
                }
            } else {
                log.debug("处理告警状态数指令alarmRecpRd为为空：data：{}", map);
            }
        } else {
            log.warn("处理告警状态数据解析Map为为空：data：{}", map);
        }
        return null;
    }
}
