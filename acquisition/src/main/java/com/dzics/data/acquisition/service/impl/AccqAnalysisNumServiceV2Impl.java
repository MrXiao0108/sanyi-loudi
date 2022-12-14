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
     * ??????????????????
     *
     * @param cmd
     */
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public DzEquipmentProNum analysisNum(RabbitmqMessage cmd) {
        Map<String, Object> map = tcpStringUtil.analysisCmdV2(cmd);
        if (CollectionUtils.isNotEmpty(map)) {
//            ??????????????????????????????
            Long senDate = (Long) map.get(CmdStateClassification.DATA_STATE_TIME.getCode());
//            ?????????????????????
            DzTcpDateID tcpDateId = (DzTcpDateID) map.get(CmdStateClassification.TCP_ID.getCode());
            if (tcpDateId == null) {
                log.error("?????????????????????????????????DzTcpDateID???{}", tcpDateId);
                return null;
            }
            String lineNum = tcpDateId.getProductionLineNumber();
            String deviceType = tcpDateId.getDeviceType();
            String deviceNum = tcpDateId.getDeviceNumber();
            String orderNumber = tcpDateId.getOrderNumber();
//            ????????????
            List<CmdTcp> cpData = (List<CmdTcp>) map.get(CmdStateClassification.CP_DATA.getCode());
//            ????????????
            List<CmdTcp> okData = (List<CmdTcp>) map.get(CmdStateClassification.Ok_DATA.getCode());
//            ????????????
            List<CmdTcp> mpData = (List<CmdTcp>) map.get(CmdStateClassification.MP_DATA.getCode());

            List<CmdTcp> workPiece = (List<CmdTcp>) map.get(CmdStateClassification.TCP_ROB_WORK_PIECE.getCode());
//            ?????????????????? ????????????
            Date nowDate = new Date(senDate);
            LocalTime localTime = DateUtil.dataToLocalTime(nowDate).withNano(0);
            int hour = localTime.getHour();
            LocalDate nowLocalDate = DateUtil.dataToLocalDate(nowDate);
//            ??????????????????
            String orgCode = cacheService.getDeviceOrgCode(lineNum, deviceNum, deviceType, orderNumber);
//           ??????????????????
            UpValueDevice upValueDevice = cacheService.getUpValueDevice(lineNum, deviceNum, deviceType, orderNumber);
//           ????????????????????????
            UpValueDevice nowValueDevice = null;
//         ????????????????????????????????????????????????????????????????????????
            DzEquipmentProNum dzEqProNum = null;
            DzEquipmentProNumDetails details = null;
//            ??????????????????????????????
            boolean falg = true;
//            ????????????????????????
            if (CollectionUtils.isNotEmpty(cpData) || CollectionUtils.isNotEmpty(mpData) || CollectionUtils.isNotEmpty(okData)) {
//            ??????????????????
                DzLineShiftDay lineShiftDays = cacheService.getLingShifuDay(lineNum, deviceNum, deviceType, orderNumber, nowLocalDate, localTime);
                if (lineShiftDays != null) {
                    //                    TODO ??????????????????,??????????????????????????????????????????
////                    ????????????????????????????????????????????????????????????????????????
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
//                    ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
//                    ????????????????????????????????????????????????????????????????????????????????????????????????
                    log.warn("??????????????????????????????????????????lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, cmd);
                    return null;
                }
            } else {
                log.debug("??????????????????????????????lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, cmd);
                return null;
            }
            details = new DzEquipmentProNumDetails();
            nowValueDevice = new UpValueDevice();
            details.setWorkNum(0L);
            details.setRoughNum(0L);
            details.setQualifiedNum(0L);
            if (CollectionUtils.isNotEmpty(cpData)) {
//                ????????????????????????
                log.debug("??????????????????????????? :lineNum:{},deviceNum:{},deviceType:{},cpData:{}", lineNum, deviceNum, deviceType, cpData);
//              ????????????????????????
                Long proNumNowNum = dzEqProNum.getNowNum();
//                ???????????????
                Long upTotalNum = upValueDevice.getTotalNum();
                Long workNum = upValueDevice.getWorkNum();
//                ?????????
                Long wkN = Long.valueOf(cpData.get(0).getDeviceItemValue());
//               ????????????
                Long now = calculateQuantity(wkN, workNum);
//                ???????????????
                Long histyTotal = upTotalNum + now;
//                ?????????????????????
                Long proMowNum = proNumNowNum + now;
                dzEqProNum.setNowNum(proMowNum);
                dzEqProNum.setTotalNum(histyTotal);
//                ????????????
                nowValueDevice.setTotalNum(histyTotal);
                nowValueDevice.setWorkNum(wkN);
                details.setWorkNum(wkN);
            } else {
                nowValueDevice.setTotalNum(upValueDevice.getTotalNum());
                nowValueDevice.setWorkNum(upValueDevice.getWorkNum());
            }
            if (CollectionUtils.isNotEmpty(mpData)) {
                log.debug("??????????????????????????? :lineNum:{},deviceNum:{},deviceType:{},mpData:{}", lineNum, deviceNum, deviceType, mpData);
//              ????????????????????????
                Long proNumRoughNum = dzEqProNum.getRoughNum();
//              ???????????????????????????
                Long upTotalRoughNum = upValueDevice.getTotalRoughNum();
                Long roughNum = upValueDevice.getRoughNum();
//                ?????????
                Long wkN = Long.valueOf(mpData.get(0).getDeviceItemValue());
//                ????????????????????????
                Long now = calculateQuantity(wkN, roughNum);
//                ???????????????
                Long histyTotal = upTotalRoughNum + now;
//                ?????????????????????
                Long nowNumRoughNum = proNumRoughNum + now;
                dzEqProNum.setRoughNum(nowNumRoughNum);
//                ????????????
                nowValueDevice.setTotalRoughNum(histyTotal);
                nowValueDevice.setRoughNum(wkN);
                details.setRoughNum(wkN);
            } else {
                nowValueDevice.setTotalRoughNum(upValueDevice.getTotalRoughNum());
                nowValueDevice.setRoughNum(upValueDevice.getRoughNum());
            }
            if (CollectionUtils.isNotEmpty(okData)) {
                log.debug("??????????????????????????? :lineNum:{},deviceNum:{},deviceType:{},okData:{}", lineNum, deviceNum, deviceType, okData);
//                ???????????????
                Long proQualifiedNum = dzEqProNum.getQualifiedNum();
//                ????????????
                Long totalQualifiedNum = upValueDevice.getTotalQualifiedNum();
                Long qualifiedNum = upValueDevice.getQualifiedNum();
//                ?????????
                Long wkN = Long.valueOf(okData.get(0).getDeviceItemValue());
//               ????????????
                long now = calculateQuantity(wkN, qualifiedNum);
//                ???????????????
                Long histyTotal = totalQualifiedNum + now;
//                ?????????????????????
                Long nowqnum = proQualifiedNum + now;
//                ????????????????????????
                dzEqProNum.setQualifiedNum(nowqnum);
//                ????????????
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
//          ??????????????????
            accqDzEqProNumDetailsService.saveDataDetails(details);
//          ???????????????????????????
            cacheService.saveUpValueDevice(lineNum, deviceNum, deviceType, orderNumber, nowValueDevice);
            log.debug("????????????????????????& ?????????????????????????????? :lineNum:{},deviceNum:{},deviceType:{}", lineNum, deviceNum, deviceType);
            if (CollectionUtils.isNotEmpty(workPiece)) {
                dzEqProNum.setCmdTcpList(workPiece);
            }
            return dzEqProNum;
        } else {
            log.warn("????????????????????????Map????????????data???{}", map);
        }
        return null;
    }


    /**
     * @param cmd ????????????????????????
     */
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public DzEquipment analysisNumRunState(RabbitmqMessage cmd) {
        Map<String, Object> map = tcpStringUtil.analysisCmdV2(cmd);
        if (CollectionUtils.isNotEmpty(map)) {
//            ??????????????????????????????
            Long senDate = (Long) map.get(CmdStateClassification.DATA_STATE_TIME.getCode());
//            ?????????????????????
            DzTcpDateID tcpDateId = (DzTcpDateID) map.get(CmdStateClassification.TCP_ID.getCode());
            if (tcpDateId == null) {
                log.error("?????????????????????????????????DzTcpDateID???{}", tcpDateId);
                return null;
            }
            String lineNum = tcpDateId.getProductionLineNumber();
            String deviceTypeStr = tcpDateId.getDeviceType();
            Integer deviceType = Integer.valueOf(deviceTypeStr);
            String deviceNum = tcpDateId.getDeviceNumber();
            String orderNumber = tcpDateId.getOrderNumber();
//            ????????????
            List<CmdTcp> cmdTcps = (List<CmdTcp>) map.get(CmdStateClassification.RUN_STATE.getCode());
            if (CollectionUtils.isNotEmpty(cmdTcps)) {
//              ??????????????????
                CmdTcp nowDzDq = cmdTcps.get(0);
                Integer nowDeviceItemValue = Integer.valueOf(nowDzDq.getDeviceItemValue());
//
                CmdTcp upCmdTcp = new CmdTcp();
                BeanUtils.copyProperties(nowDzDq, upCmdTcp);
                upCmdTcp = cacheService.getUpRunState(lineNum, deviceNum, deviceTypeStr, orderNumber, nowDzDq);
//              ???????????????????????? ????????????????????????????????????
                Integer upDeviceItemValue = Integer.valueOf(upCmdTcp.getDeviceItemValue());
                if (upDeviceItemValue.intValue() != nowDeviceItemValue.intValue()) {
                    DzEquipment upDzDqState = cacheService.getTypeLingEqNo(deviceNum, lineNum, deviceTypeStr, orderNumber);
                    DzEquipment downEq = new DzEquipment();
                    downEq.setId(upDzDqState.getId());
                    downEq.setEquipmentNo(deviceNum);
                    downEq.setLineNo(lineNum);
                    downEq.setEquipmentType(deviceType);
                    downEq.setOrderNo(orderNumber);
//            ????????????
                    Date nowDate = new Date(senDate);
                    LocalDate nowLocalDate = DateUtil.dataToLocalDate(nowDate);
//            ??????????????????
                    String orgCode = cacheService.getDeviceOrgCode(lineNum, deviceNum, deviceTypeStr, orderNumber);
//            ??????????????????
                    Long dowmSum = 0L;
//            ??????????????????
                    DzEquipmentDowntimeRecord nowDowntimeRecord = null;
//          ????????????????????????
                    DzEquipmentDowntimeRecord updateDowntimeRecord = null;
//            ????????????????????????
                    Long upDownSum = cacheService.upDownSum(lineNum, deviceNum, deviceTypeStr, orderNumber);
                    Long upDownSumTime = cacheService.upDownSumTime(lineNum, deviceNum, deviceTypeStr, orderNumber);
                    if (upDownSum == null) {
                        log.warn("???????????????????????????????????????lineNum:{},deviceNum:{},deviceType:{}", lineNum, deviceNum, deviceType);
                        return null;
                    }
                    if (upDownSumTime == null) {
                        log.warn("???????????????????????????????????????lineNum:{},deviceNum:{},deviceType:{}", lineNum, deviceNum, deviceType);
                        return null;
                    }
//           ?????????????????????????????????????????????
                    if (deviceType.intValue() == EquiTypeEnum.JC.getCode()) {
                        if (nowDeviceItemValue.intValue() == RunStateEnum.CNC_STOP.getCode()) {
//                            ????????????????????????????????????????????????????????? ????????????
                            DzEquipmentDowntimeRecord lineNoEqNoTypeNoResetIsDzeq = accEqStopLogService.getLineNoEqNoTypeNoResetIsDzeq(orderNumber, lineNum, deviceTypeStr, deviceNum);
                            if (lineNoEqNoTypeNoResetIsDzeq == null) {
//                              ????????????????????????
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
                                log.debug("????????????????????????:lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, nowDowntimeRecord);
                            }
                        } else {
//                                   ?????????????????? ??????????????????
                            updateDowntimeRecord = new DzEquipmentDowntimeRecord();
                            updateDowntimeRecord.setOrderNo(orderNumber);
                            updateDowntimeRecord.setLineNo(lineNum);
                            updateDowntimeRecord.setEquipmentNo(deviceNum);
                            updateDowntimeRecord.setEquipmentType(deviceType);
                            updateDowntimeRecord.setResetTime(nowDate);
                            log.debug("????????????????????????????????????:lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, updateDowntimeRecord);
                        }
                    } else {
//                            ?????????
                        if (nowDeviceItemValue.intValue() == EquiTypeCommonEnum.PRO.getCode()) {
//                          ?????????????????? ??????????????????
                            updateDowntimeRecord = new DzEquipmentDowntimeRecord();
                            updateDowntimeRecord.setOrderNo(orderNumber);
                            updateDowntimeRecord.setLineNo(lineNum);
                            updateDowntimeRecord.setEquipmentNo(deviceNum);
                            updateDowntimeRecord.setEquipmentType(deviceType);
                            updateDowntimeRecord.setResetTime(nowDate);
                            log.debug("????????????????????????????????????:lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, updateDowntimeRecord);
                        } else {
//                            ?????????????????????????????????????????????????????????????????????
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
                                log.debug("????????????????????????:lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, nowDowntimeRecord);
                            }
                        }
                    }
//
                    if (nowDowntimeRecord != null) {
                        accEqStopLogService.saveDownTimeRecord(nowDowntimeRecord);
//              ????????????????????????
                        downEq.setDownSum(dowmSum);
                        int up = accqDzEquipmentService.updateByLineNoAndEqNoDownTime(downEq);
//              START ????????????????????????
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
                        log.debug("????????????????????????:lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, nowDowntimeRecord);
                        CmdTcp runState = cacheService.upDateUpRunState(lineNum, deviceNum, deviceTypeStr, orderNumber, nowDzDq);
                        log.debug("??????????????????:lineNum:{},deviceNum:{},deviceType:{}", lineNum, deviceNum, deviceType);
                        downEq.setDowntimeRecord(nowDowntimeRecord);
                    }

                    if (updateDowntimeRecord != null) {
//                        ???????????????????????????????????????
                        UpdateDownTimeDate upStopTimeX = accEqStopLogService.getLineNoEqNoTypeNoResetIsNo(updateDowntimeRecord);

                        if (upStopTimeX != null) {
                            Date upStopTime = upStopTimeX.getUpStopTime();
                            updateDowntimeRecord.setId(upStopTimeX.getId());
//                       ?????????????????? - ???????????? = ????????????
                            long resTime = (updateDowntimeRecord.getResetTime().getTime() - upStopTime.getTime());
                            updateDowntimeRecord.setDuration(resTime);
                            downEq.setDownTime(upDownSumTime + resTime);
                            LocalDate uplocalDate = upStopTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            if (nowLocalDate.compareTo(uplocalDate) != 0) {
//                            ???????????????????????????????????????????????????
                                Date dateEnd = getnowEndTime(nowDate);
                                long resTimeSplit = (dateEnd.getTime() - upStopTime.getTime());
                                updateDowntimeRecord.setDuration(resTimeSplit);
                                updateDowntimeRecord.setResetTime(dateEnd);
//                                ??????????????????????????????
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
                            log.debug("??????????????????????????????????????????:lineNum:{},deviceNum:{},deviceType:{}", lineNum, deviceNum, deviceType);
                            CmdTcp runState = cacheService.upDateUpRunState(lineNum, deviceNum, deviceTypeStr, orderNumber, nowDzDq);
                            log.debug("??????????????????:lineNum:{},deviceNum:{},deviceType:{}", lineNum, deviceNum, deviceType);
                            downEq.setUpdateDowntimeRecord(updateDowntimeRecord);
                        } else {
                            CmdTcp runState = cacheService.upDateUpRunState(lineNum, deviceNum, deviceTypeStr, orderNumber, nowDzDq);
                            log.error("?????????????????????????????? ????????????????????????????????????cmd:{},updateDowntimeRecord:{}", cmd, updateDowntimeRecord);
                        }
                    }
                    if (nowDowntimeRecord != null) {
                        return downEq;
                    } else {
                        return null;
                    }
                } else {
                    log.debug("??????????????????????????????????????????lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, nowDzDq);
                    return null;
                }
            } else {
                log.debug("???????????????????????????List????????????data???{}", map);
                return null;
            }
        } else {
            log.warn("????????????????????????????????????Map????????????data???{}", map);
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
     * @param nowQuantity ????????????
     * @param upQuantity  ????????????
     * @return ????????????????????????
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
            log.error("??????????????????????????????data:{}", nowDzDq);
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
     * ?????????????????????ID??????????????????????????????????????????(0=NG,1=OK,100)??????????????????????????????1?????????1??????????????????2?????????2??????????????????3?????????3??????????????????4?????????4??????
     * A809|[4c1e55c02ca04480975b9b0d4bb4a79f,1030,2,0,4,5835.125,1,6922.305,1,1744.856,0,8869.579,0]
     *
     * @param cmd
     */
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public String queueCheckoutEquipment(RabbitmqMessage cmd) {
        log.debug("?????????????????????{}", cmd);
        Map<String, Object> map = tcpStringUtil.analysisCmdV2(cmd);
        if (CollectionUtils.isNotEmpty(map)) {
//            ??????????????????????????????
            Long senDate = (Long) map.get(CmdStateClassification.DATA_STATE_TIME.getCode());
//            ?????????????????????
            DzTcpDateID tcpDateId = (DzTcpDateID) map.get(CmdStateClassification.TCP_ID.getCode());
            if (tcpDateId == null) {
                log.error("?????????????????????????????????DzTcpDateID???{}", tcpDateId);
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
                        //            ??????????????????
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
                            log.error("???????????????????????????product_no:{}", productNo);
                        }

                    } else {
                        log.error("??????????????????????????????5???split???{}", split.toString());
                    }
                } else {
                    log.warn("??????????????????????????????device_item_value???{}???tcp_value???{}", deviceItemValue, tcpValue);
                }
            } else {
                log.warn("?????????????????????????????????cmdTcps???{}", cmdTcps);
            }
        } else {
            log.warn("??????????????????????????????Map????????????data???{}", map);
        }

        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public DzEquipment analysisNumAlarmState(RabbitmqMessage rabbitmqMessage) {
        Map<String, Object> map = tcpStringUtil.analysisCmdV2(rabbitmqMessage);
        if (CollectionUtils.isNotEmpty(map)) {
//            ??????????????????????????????
            Long senDate = (Long) map.get(CmdStateClassification.DATA_STATE_TIME.getCode());
//            ?????????????????????
            DzTcpDateID tcpDateId = (DzTcpDateID) map.get(CmdStateClassification.TCP_ID.getCode());
            if (tcpDateId == null) {
                log.error("?????????????????????????????????DzTcpDateID???{}", tcpDateId);
                return null;
            }
            String lineNum = tcpDateId.getProductionLineNumber();
            String deviceTypeStr = tcpDateId.getDeviceType();
            Integer deviceType = Integer.valueOf(deviceTypeStr);
            String deviceNum = tcpDateId.getDeviceNumber();
            String orderNumber = tcpDateId.getOrderNumber();
//            ????????????????????????
            List<CmdTcp> alarmRecpRd = (List<CmdTcp>) map.get(CmdStateClassification.ALARM_RECPRD.getCode());
            if (CollectionUtils.isNotEmpty(alarmRecpRd)) {
                DzEquipment downEq = new DzEquipment();
                downEq.setEquipmentNo(deviceNum);
                downEq.setLineNo(lineNum);
                downEq.setEquipmentType(deviceType);
                downEq.setOrderNo(orderNumber);
//              ??????????????????
                CmdTcp nowDzDq = alarmRecpRd.get(0);
                Integer nowDeviceItemValue = Integer.valueOf(nowDzDq.getDeviceItemValue());
                CmdTcp upCmdTcp = cacheService.getUpAlarmRecpRd(lineNum, deviceNum, deviceTypeStr, orderNumber, nowDzDq);
//              ???????????????????????? ????????????????????????????????????
                Integer upDeviceItemValue = Integer.valueOf(upCmdTcp.getDeviceItemValue());
                if (upDeviceItemValue.intValue() != nowDeviceItemValue.intValue()) {
//            ????????????
                    Date nowDate = new Date(senDate);
                    LocalDate nowLocalDate = DateUtil.dataToLocalDate(nowDate);
//            ??????????????????
                    String orgCode = cacheService.getDeviceOrgCode(lineNum, deviceNum, deviceTypeStr, orderNumber);

//            ??????????????????
                    DzEquipmentAlarmRecord nowDowntimeRecord = null;
//          ????????????????????????
                    DzEquipmentAlarmRecord updateDowntimeRecord = null;
//           ?????????????????????????????????????????????
                    if (deviceType.intValue() == EquiTypeEnum.JC.getCode()) {
                        if (nowDeviceItemValue.intValue() == AlarmStatusEnum.CNC_ALARM_STATUS_DANGER.getCode()) {
//                  ????????????????????????
                            nowDowntimeRecord = new DzEquipmentAlarmRecord();
                            nowDowntimeRecord.setOrderNo(orderNumber);
                            nowDowntimeRecord.setLineNo(lineNum);
                            nowDowntimeRecord.setOrgCode(orgCode);
                            nowDowntimeRecord.setEquipmentNo(deviceNum);
                            nowDowntimeRecord.setEquipmentType(deviceType);
                            nowDowntimeRecord.setStopTime(nowDate);
                            nowDowntimeRecord.setStopData(nowLocalDate);
                            nowDowntimeRecord.setDelFlag(false);
                            log.debug("????????????????????????:lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, nowDowntimeRecord);
                        } else if (nowDeviceItemValue.intValue() == AlarmStatusEnum.CNC_ALARM_STATUS_AUTHER.getCode()) {
//                                   ?????????????????? ??????????????????
                            updateDowntimeRecord = new DzEquipmentAlarmRecord();
                            updateDowntimeRecord.setOrderNo(orderNumber);
                            updateDowntimeRecord.setLineNo(lineNum);
                            updateDowntimeRecord.setEquipmentNo(deviceNum);
                            updateDowntimeRecord.setEquipmentType(deviceType);
                            updateDowntimeRecord.setResetTime(nowDate);
                            log.debug("????????????????????????????????????:lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, updateDowntimeRecord);
                        }
                    } else if (deviceType.intValue() == EquiTypeEnum.JQR.getCode()) {
//                            ??????
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
                            log.debug("????????????????????????:lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, nowDowntimeRecord);
                        } else if (nowDeviceItemValue.intValue() == AlarmStatusEnum.ROBOT_AlARM_STATUS_AUTHER.getCode()) {
                            //                                   ?????????????????? ??????????????????
                            updateDowntimeRecord = new DzEquipmentAlarmRecord();
                            updateDowntimeRecord.setOrderNo(orderNumber);
                            updateDowntimeRecord.setLineNo(lineNum);
                            updateDowntimeRecord.setEquipmentNo(deviceNum);
                            updateDowntimeRecord.setEquipmentType(deviceType);
                            updateDowntimeRecord.setResetTime(nowDate);
                            log.debug("????????????????????????????????????:lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, updateDowntimeRecord);
                        }

                    }
//
                    if (nowDowntimeRecord != null) {
                        alarmRecordService.saveDownTimeRecord(nowDowntimeRecord);
                        log.debug("????????????????????????:lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, nowDowntimeRecord);
                    }

                    if (updateDowntimeRecord != null) {
//                        ???????????????????????????????????????
                        Date upStopTime = alarmRecordService.getLineNoEqNoTypeNoResetIsNo(updateDowntimeRecord);
                        if (upStopTime != null) {
//                       ?????????????????? - ???????????? = ????????????
                            long resTime = (updateDowntimeRecord.getResetTime().getTime() - upStopTime.getTime());
                            updateDowntimeRecord.setDuration(resTime);
                            LocalDate uplocalDate = upStopTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            if (nowLocalDate.compareTo(uplocalDate) != 0) {
//                            ???????????????????????????????????????????????????
                                Date dateEnd = getnowEndTime(nowDate);
                                long resTimeSplit = (dateEnd.getTime() - upStopTime.getTime());
                                updateDowntimeRecord.setDuration(resTimeSplit);
                                updateDowntimeRecord.setResetTime(dateEnd);
//                                ??????????????????????????????
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
                            log.debug("??????????????????????????????????????????:lineNum:{},deviceNum:{},deviceType:{}", lineNum, deviceNum, deviceType);
                        } else {
                            log.error("?????????????????????????????? ????????????????????????????????????");
                        }
                    }
                    CmdTcp runState = cacheService.upDateUpAlremState(lineNum, deviceNum, deviceTypeStr, orderNumber, nowDzDq);
                    log.debug("??????????????????????????????:lineNum:{},deviceNum:{},deviceType:{}", lineNum, deviceNum, deviceType);
                    if (updateDowntimeRecord != null) {
                        return downEq;
                    }
                } else {
                    log.debug("??????????????????????????????????????????lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, nowDzDq);
                }
            } else {
                log.debug("???????????????????????????alarmRecpRd????????????data???{}", map);
            }
        } else {
            log.warn("??????????????????????????????Map????????????data???{}", map);
        }
        return null;
    }
}
