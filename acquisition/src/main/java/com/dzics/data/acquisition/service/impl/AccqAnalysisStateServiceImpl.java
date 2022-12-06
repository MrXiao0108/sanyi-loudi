package com.dzics.data.acquisition.service.impl;

import com.dzics.common.enums.DeviceSocketSendStatus;
import com.dzics.common.model.custom.CmdTcp;
import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.model.entity.DzEquipmentRunTime;
import com.dzics.common.model.entity.DzToolCompensationData;
import com.dzics.common.model.response.*;
import com.dzics.common.util.DateUtil;
import com.dzics.common.util.NumberUtils;
import com.dzics.data.acquisition.model.EqMentStatus;
import com.dzics.data.acquisition.service.AccqAnalysisStateService;
import com.dzics.data.acquisition.service.CacheService;
import com.dzics.data.acquisition.service.RedisToolInfoListService;
import com.dzics.data.acquisition.util.TcpStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/3/10.
 * @since
 */
@Slf4j
@Service
public class AccqAnalysisStateServiceImpl implements AccqAnalysisStateService {

    @Autowired
    TcpStringUtil tcpStringUtil;
    @Autowired
    @Lazy
    private CacheService cacheService;
    @Autowired
    private RedisToolInfoListService redisToolInfoListService;

    @Override
    public Result getEqToolInfoList(RabbitmqMessage rabbitmqMessage) {
        String timestamp = rabbitmqMessage.getTimestamp();
        String orderNumber = rabbitmqMessage.getOrderCode();
        String lineNum = rabbitmqMessage.getLineNo();
        String deviceNum = rabbitmqMessage.getDeviceCode();
        String deviceTypeStr = rabbitmqMessage.getDeviceType();
        String msg = rabbitmqMessage.getMessage();
        //查询设备
        DzEquipment upDzDqState = cacheService.getTypeLingEqNo(deviceNum, lineNum, deviceTypeStr, orderNumber);
        Long id = upDzDqState.getId();
        //解析数据
        List<DzToolCompensationData> data = new ArrayList<>();
        if (msg == null) {
            return null;
        }
        try {
            List<DzToolCompensationData> res = new ArrayList<>();
            String[] split1 = msg.split("#");
            for (String str : split1) {
                String[] split = str.split("\\|");
                String cmd = split[0];//B803 或  B804
                String cmdValue = split[1];
                String replace = cmdValue.replace("[", "");
                String[] valueList = replace.split("]");//得到的指令值
                if (EqMentStatus.CMD_CUTTING_TOOL_FILE.equals(cmd)) {//刀具寿命
                    data = getLife(id, valueList);
                } else if (EqMentStatus.CMD_CUTTING_TOOL_INFO.equals(cmd)) {//刀具信息
                    data = getGeometry(id, valueList);
                }
                if (data != null && data.size() > 0) {
                    res = redisToolInfoListService.updateCompensationDataList(data);
                } else {
                    return null;
                }
            }
            //封装数据返回
            GetToolInfoDataDo toolInfoDataDo = getToolInfoDataDo(upDzDqState, res);
            JCEquimentBase jcEquimentBase = new JCEquimentBase();
            jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_MACHINE_TOOL_INFORMATION.getInfo());
            jcEquimentBase.setData(toolInfoDataDo);
            return Result.ok(jcEquimentBase);
        } catch (Exception e) {
            log.error("解析刀具指令异常,异常指令：{}", msg);
        }

        return null;
    }

    @Override
    public DzEquipment analysisNumStatePush(RabbitmqMessage cmd) {
        List<CmdTcp> cmdTcps = tcpStringUtil.getCmdTcp(cmd.getMessage());
        String orderNumber = cmd.getOrderCode();
        String lineNum = cmd.getLineNo();
        String deviceTypeStr = cmd.getDeviceType();
        Integer deviceType = Integer.valueOf(deviceTypeStr);
        String deviceNum = cmd.getDeviceCode();
        DzEquipment upDzDqState = cacheService.getTypeLingEqNoPush(deviceNum, lineNum, deviceTypeStr, orderNumber);
        if (upDzDqState == null) {
            log.warn("设备不存在 结束执行:lineNum:{},deviceNum:{},deviceType:{},data:{}", lineNum, deviceNum, deviceType, upDzDqState);
            return null;
        }
        boolean falg = false;
        boolean falgLocation = false;
        List<String> logs = new ArrayList<>();
        List<String> logsWar = new ArrayList<>();
        String deviceName = upDzDqState.getEquipmentName();
        for (CmdTcp commData : cmdTcps) {
//                指令
            String tcpValue = commData.getTcpValue();
//                指令值
            String deviceItemValue = commData.getDeviceItemValue();
//                解释翻译后内容 例如 1 设备正常正常 [31.454,45.464] 位置信息
            String tcpDescription = commData.getTcpDescription();
            switch (tcpValue) {
                case EqMentStatus.CMD_CNC_CUTTING_TIME:
                    if (upDzDqState.getB526() == null || !tcpDescription.equals(upDzDqState.getB526())) {
                        falg = true;
                        upDzDqState.setB526(tcpDescription);
                    }
                    continue;
                case EqMentStatus.TCP_ROB_WORK_PIECE:
                    if (upDzDqState.getA812() == null || !tcpDescription.equals(upDzDqState.getA812())) {
                        falg = true;
                        upDzDqState.setA812(tcpDescription);
                    }
                    continue;
                case EqMentStatus.CMD_CNC_RUN_TIME:
                    if (upDzDqState.getA541() == null || !tcpDescription.equals(upDzDqState.getA541())) {
                        falg = true;
                        upDzDqState.setA541(tcpDescription);
                    }
                    continue;
                case EqMentStatus.CMD_CNC_TOOL_NO:
                    if (upDzDqState.getB809() == null || !tcpDescription.equals(upDzDqState.getB809())) {
                        falg = true;
                        upDzDqState.setB809(tcpDescription);
                    }
                    continue;
//                清零状态
                case EqMentStatus.TCP_CL_CO_ST:
                    boolean numeric = NumberUtils.isNumeric(tcpValue, deviceItemValue);
                    if (numeric) {
                        Integer cleaState = Integer.valueOf(deviceItemValue);
                        if (upDzDqState.getClearCountStatusValue() == null || upDzDqState.getClearCountStatusValue().intValue() != cleaState.intValue()) {
                            falg = true;
                            upDzDqState.setClearCountStatus(tcpDescription);
                            upDzDqState.setClearCountStatusValue(cleaState);
                        }
                    }
                    continue;
                    //连接状态，如联机、脱机、虚拟机
                case EqMentStatus.TCP_CL_ST_CNC:
                case EqMentStatus.TCP_CL_ST_ROB:
                case EqMentStatus.TCP_CL_ST_CHJ:
                case EqMentStatus.TCP_CL_ST_JZJ:
                    boolean numeric1 = NumberUtils.isNumeric(tcpValue, deviceItemValue);
                    if (numeric1) {
                        Integer connectState = Integer.valueOf(deviceItemValue);
                        if (upDzDqState.getConnectStateValue() == null || upDzDqState.getConnectStateValue().intValue() != connectState.intValue()) {
                            falg = true;
                            logs.add(deviceName + deviceNum + ":" + tcpDescription);
                            upDzDqState.setConnectState(tcpDescription);
                            upDzDqState.setConnectStateValue(connectState);
                        }
                    }
                    continue;
                    //操作模式，自动/手动
                case EqMentStatus.TCP_OPE_MODE_CNC:
                case EqMentStatus.TCP_OPE_MODE_ROB:
                case EqMentStatus.TCP_OPE_MODE_CHJ:
                case EqMentStatus.TCP_OPE_MODE_JZJ:
                    boolean numeric2 = NumberUtils.isNumeric(tcpValue, deviceItemValue);
                    if (numeric2) {
                        Integer operatorMode = Integer.valueOf(deviceItemValue);
                        if (upDzDqState.getOperatorModeValue() == null || upDzDqState.getOperatorModeValue().intValue() != operatorMode.intValue()) {
                            falg = true;
                            logs.add(deviceName + deviceNum + ":操作模式切换为" + tcpDescription);
                            upDzDqState.setOperatorMode(tcpDescription);
                            upDzDqState.setOperatorModeValue(operatorMode);
                        }
                    }
                    continue;
                    //绝对坐标
                case EqMentStatus.TCP_ABS_POS_ROB:
                case EqMentStatus.TCP_ABS_POS_CNC:
                    if (upDzDqState.getCurrentLocation() == null || !tcpDescription.equals(upDzDqState.getCurrentLocation())) {
                        falgLocation = true;
                        upDzDqState.setCurrentLocation(tcpDescription);
                    }
                    continue;
                    // 机器人运行状态
                case EqMentStatus.TCP_RUN_STATE_CNS:
                case EqMentStatus.TCP_RUN_STATE_ROB:
                case EqMentStatus.TCP_RUN_STATE_CHJ:
                case EqMentStatus.TCP_RUN_STATE_JZJ:
                    boolean numeric3 = NumberUtils.isNumeric(tcpValue, deviceItemValue);
                    if (numeric3) {
                        Integer runStatus = Integer.valueOf(deviceItemValue);
                        if (upDzDqState.getRunStatusValue() == null || upDzDqState.getRunStatusValue().intValue() != runStatus.intValue()) {
                            falg = true;
                            logs.add(deviceName + deviceNum + ":" + tcpDescription);
                            upDzDqState.setRunStatus(tcpDescription);
                            upDzDqState.setRunStatusValue(runStatus);
                        }
                    }
                    continue;
                    //待机状态
                case EqMentStatus.CMD_ROB_WAIT_STATUS:
                    upDzDqState.setA567(tcpDescription);
                    continue;
//                    工作状态
                case EqMentStatus.TCP_WORK_STATE_CNS:
                case EqMentStatus.TCP_WORK_STATE_CHJ:
                case EqMentStatus.TCP_WORK_STATE_JZJ:
                    boolean numericGz = NumberUtils.isNumeric(tcpValue, deviceItemValue);
                    if (numericGz) {
                        Integer status = Integer.valueOf(deviceItemValue);
                        if (upDzDqState.getWorkStatusValue() == null || upDzDqState.getWorkStatusValue().intValue() != status.intValue()) {
                            falg = true;
                            logs.add(deviceName + deviceNum + ":" + tcpDescription);
                            upDzDqState.setWorkStatus(tcpDescription);
                            upDzDqState.setWorkStatusValue(status);
                        }
                    }
                    continue;
                    //急停状态
                case EqMentStatus.TCP_EMERGENCY_STATUS_CNS:
                case EqMentStatus.TCP_EMERGENCY_STATUS_ROB:
                case EqMentStatus.TCP_EMERGENCY_STATUS_CHJ:
                case EqMentStatus.TCP_EMERGENCY_STATUS_JZJ:
                    boolean numeric8 = NumberUtils.isNumeric(tcpValue, deviceItemValue);
                    if (numeric8) {
                        Integer status = Integer.valueOf(deviceItemValue);
                        if (upDzDqState.getEquipmentStatusValue() == null || upDzDqState.getEquipmentStatusValue().intValue() != status.intValue()) {
                            falg = true;
                            logs.add(deviceName + deviceNum + ":" + tcpDescription);
                            upDzDqState.setEquipmentStatus(tcpDescription);
                            upDzDqState.setEquipmentStatusValue(status);
                            upDzDqState.setEmergencyStatus(tcpDescription);
                            upDzDqState.setEmergencyStatusValue(status);
                        }

                    }
                    continue;
                    //告警状态
                case EqMentStatus.TCP_ALARM_STATUS_CNC:
                case EqMentStatus.TCP_ALARM_STATUS_ROB:
                case EqMentStatus.TCP_ALARM_STATUS_CHJ:
                case EqMentStatus.TCP_ALARM_STATUS_JZJ:
                    boolean numeric4 = NumberUtils.isNumeric(tcpValue, deviceItemValue);
                    if (numeric4) {
                        Integer alarmStatus = Integer.valueOf(deviceItemValue);
                        if (upDzDqState.getAlarmStatusValue() == null || upDzDqState.getAlarmStatusValue().intValue() != alarmStatus.intValue()) {
                            falg = true;
                            logsWar.add(deviceName + deviceNum + ":" + tcpDescription);
                            upDzDqState.setAlarmStatus(tcpDescription);
                            upDzDqState.setAlarmStatusValue(alarmStatus);
                        }
                    }
                    continue;
                case EqMentStatus.CMD_ROB_PROCESS_TIME:
                    if (upDzDqState.getMachiningTime() == null || !upDzDqState.getMachiningTime().equals(deviceItemValue)) {
                        falg = true;
                        upDzDqState.setMachiningTime(deviceItemValue);
                    }
                    continue;
                case EqMentStatus.CMD_ROB_SPEED_RATIO:
                    if (upDzDqState.getSpeedRatio() == null || !upDzDqState.getSpeedRatio().equals(deviceItemValue)) {
                        falg = true;
                        upDzDqState.setSpeedRatio(deviceItemValue);
                    }
                    continue;
                case EqMentStatus.TCP_HEAD_POSITION_UD_JZJ:
                    if (upDzDqState.getHeadPositionUd() == null || !upDzDqState.getHeadPositionUd().equals(tcpDescription)) {
                        falg = true;
                        upDzDqState.setHeadPositionUd(tcpDescription);
                    }
                    continue;
                case EqMentStatus.TCP_HEAD_POSITION_LR_JZJ:
                    if (upDzDqState.getHeadPostionLr() == null || !upDzDqState.getHeadPostionLr().equals(tcpDescription)) {
                        falg = true;
                        upDzDqState.setHeadPostionLr(tcpDescription);
                    }
                    continue;
                case EqMentStatus.TCP_CHJ_SPEED:
                    if (upDzDqState.getMovementSpeed() == null || !upDzDqState.getMovementSpeed().equals(tcpDescription)) {
                        falg = true;
                        upDzDqState.setMovementSpeed(tcpDescription);
                    }
                    continue;
                case EqMentStatus.TCP_CHJ_WORKPIECE_SPEED:
                    if (upDzDqState.getWorkpieceSpeed() == null || !upDzDqState.getWorkpieceSpeed().equals(tcpDescription)) {
                        falg = true;
                        upDzDqState.setWorkpieceSpeed(tcpDescription);
                    }
                    continue;
                case EqMentStatus.TCP_CHJ_COOL_TEMP:
                    if (upDzDqState.getCoolantTemperature() == null || !upDzDqState.getCoolantTemperature().equals(tcpDescription)) {
                        falg = true;
                        upDzDqState.setCoolantTemperature(tcpDescription);
                    }
                    continue;
                case EqMentStatus.TCP_CHJ_COOL_PRESS:
                    if (upDzDqState.getCoolantPressure() == null || !upDzDqState.getCoolantPressure().equals(tcpDescription)) {
                        falg = true;
                        upDzDqState.setCoolantPressure(tcpDescription);
                    }
                    continue;
                case EqMentStatus.TCP_CHJ_COOL_FLOW:
                    if (upDzDqState.getCoolantFlow() == null || !upDzDqState.getCoolantFlow().equals(tcpDescription)) {
                        falg = true;
                        upDzDqState.setCoolantFlow(tcpDescription);
                    }
                    continue;
                case EqMentStatus.TCO_CNC_JIE_PAI:
                    if (upDzDqState.getCleanTime() == null || !upDzDqState.getCleanTime().equals(tcpDescription)) {
                        falg = true;
                        upDzDqState.setB527(tcpDescription);
                        upDzDqState.setCleanTime(tcpDescription);
                    }
                    continue;
                case EqMentStatus.CMD_CNC_SPINDLE_SPEED:
                    if (upDzDqState.getSpeedOfMainShaft() == null || !upDzDqState.getSpeedOfMainShaft().equals(tcpDescription)) {
                        upDzDqState.setSpeedOfMainShaft(tcpDescription);
                        falg = true;
                    }
                    continue;
                case EqMentStatus.CMD_CNC_GAS_FLOW:
                    if (upDzDqState.getGasFlow() == null || !upDzDqState.getGasFlow().equals(tcpDescription)) {
                        upDzDqState.setGasFlow(tcpDescription);
                        falg = true;
                    }
                case EqMentStatus.CMD_CNC_FEED_SPEED:
                    if (upDzDqState.getFeedSpeed() == null || !upDzDqState.getFeedSpeed().equals(tcpDescription)) {
                        upDzDqState.setFeedSpeed(tcpDescription);
                        falg = true;
                    }
                    continue;
                default:
                    log.debug("未识别运行状态类型跳过:lineNum:{},deviceNum:{},deviceType:{},tcpValue:{}", lineNum, deviceNum, deviceType, tcpValue);
            }
        }
        if (falg) {
            DzEquipment dzEquipment = cacheService.updateByLineNoAndEqNoPush(upDzDqState);
            log.debug("更新设备状态:lineNum:{},deviceNum:{},deviceType:{}", lineNum, deviceNum, deviceType);
        } else {
            log.debug("设备状态未变化:lineNum:{},deviceNum:{},deviceType:{}", lineNum, deviceNum, deviceType);
        }
        if ("待机".equals(upDzDqState.getA567())) {
            upDzDqState.setRunStatus(upDzDqState.getA567());
        }
        if (falg || falgLocation) {
            upDzDqState.setLogs(logs);
            upDzDqState.setLogsWar(logsWar);
            return upDzDqState;
        }
        return null;
    }


    /**
     * 解析刀具寿命
     *
     * @return strings    01,    1,      100,   200,    2
     * 刀具号 刀具组   寿命 使用次数  类型
     */
    public List<DzToolCompensationData> getLife(Long eqId, String[] strList) {
        List<DzToolCompensationData> list = redisToolInfoListService.getCompensationDataList(eqId);
        for (String str : strList) {
            String[] strings = str.split(",");
            Integer toolNo = Integer.valueOf(strings[0]);//刀具号
            Integer groupNo = Integer.valueOf(strings[1]);//刀组号
            for (DzToolCompensationData data : list) {
                //寻找刀具 刀组和设备一样的记录
                if (data.getToolNo().intValue() == toolNo.intValue()
                        && groupNo.intValue() == data.getGroupNo().intValue()) {
                    data.setToolLife(Integer.valueOf(strings[2]));//寿命
                    data.setToolLifeCounter(Integer.valueOf(strings[3]));//使用量
                    data.setToolLifeCounterType(Integer.valueOf(strings[4]));//寿命记数类型
                }
            }
        }
        return list;
    }

    /**
     * 解析刀具信息数据
     * 01,         11.001,     11.002,      12.003,      13.004,        1.101,      1.210,      2.121,      3.112,         1
     * 刀具号			X轴（形状） Y轴（形状）  Z轴（形状）  半径（形状）   X轴（磨损） Y轴（磨损） Z轴（磨损）  半径（磨损）  方向
     *
     * @return
     */
    public List<DzToolCompensationData> getGeometry(Long eqId, String[] strList) {
        List<DzToolCompensationData> list = redisToolInfoListService.getCompensationDataList(eqId);
        for (String str : strList) {
            String[] strings = str.split(",");
            Integer toolNo = Integer.valueOf(strings[0]);//刀具号
            for (DzToolCompensationData data : list) {
                //寻找刀具 刀组和设备一样的记录
                if (data.getToolNo().intValue() == toolNo.intValue()) {
                    data.setToolGeometryX(new BigDecimal(strings[1]));
                    data.setToolGeometryY(new BigDecimal(strings[2]));
                    data.setToolGeometryZ(new BigDecimal(strings[3]));
                    data.setToolGeometryRadius(new BigDecimal(strings[4]));
                    data.setToolWearX(new BigDecimal(strings[5]));
                    data.setToolWearY(new BigDecimal(strings[6]));
                    data.setToolWearZ(new BigDecimal(strings[7]));
                    data.setToolWearRadius(new BigDecimal(strings[8]));
                    data.setToolNoseDirection(Integer.valueOf(strings[9]));

                }


            }
        }
        return list;
    }

    /**
     * 封装刀具信息返回
     */
    public GetToolInfoDataDo getToolInfoDataDo(DzEquipment equipment, List<DzToolCompensationData> res) {
        GetToolInfoDataDo getToolInfoDataDo = new GetToolInfoDataDo();
        getToolInfoDataDo.setEquipmentId(equipment.getId());
        getToolInfoDataDo.setEquipmentName(equipment.getEquipmentName());
        getToolInfoDataDo.setEquipmentNo(equipment.getEquipmentNo());
        //设备绑定的刀具信息集合
        List<ToolDataDo> toolDataDos = new ArrayList<>();
        for (DzToolCompensationData data : res) {
            if (data.getEquipmentId() != null && data.getEquipmentId().intValue() == equipment.getId().intValue()) {
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
        return getToolInfoDataDo;
    }

    /**
     * 计算运行时间 并插入数据库
     *
     * @param runTimeRecord
     * @param senDate
     */
    public void insertDzEquipmentRunTime(DzEquipmentRunTime runTimeRecord, Long senDate) {
        Date stopTime = new Date(senDate);
        List<RunTimeDo> list = new ArrayList<>();
        List<RunTimeDo> runTimeDos = new ArrayList<>();
        try {
            runTimeDos = figureRunTime(list, runTimeRecord.getStopTime(), stopTime);
        } catch (Exception e) {
            log.error("计算设备运行时间异常，起始时间:{},结束时间{}", runTimeRecord.getStopTime(), stopTime);
        }
        if (runTimeDos.size() > 1) {
            //运行时间跨天了  分开计算运行记录
            for (int i = 0; i < runTimeDos.size(); i++) {
                RunTimeDo runTimeDo = runTimeDos.get(i);
                if (i == 0) {
                    runTimeRecord.setResetTime(runTimeDo.getStopTime()); //停止运行时间
                    runTimeRecord.setDuration(runTimeDo.getSumTime());//运行时长毫秒
                    //更改库
                    cacheService.updateRunTimeRecord(runTimeRecord);
                } else {

                    runTimeRecord.setOrderNo(runTimeRecord.getOrderNo());
                    runTimeRecord.setLineNo(runTimeRecord.getLineNo());
                    runTimeRecord.setEquipmentNo(runTimeRecord.getEquipmentNo());
                    runTimeRecord.setEquipmentType(runTimeRecord.getEquipmentType());
                    runTimeRecord.setStopTime(runTimeDo.getRunTime());//运行开始时间
                    runTimeRecord.setResetTime(runTimeDo.getStopTime());//运行停止时间
                    runTimeRecord.setDuration(runTimeDo.getSumTime());//运行时长毫秒
                    runTimeRecord.setStopData(DateUtil.dataToLocalDate(runTimeDo.getRunTime()));//运行日期
                    runTimeRecord.setOrgCode(runTimeRecord.getOrgCode());
                    //插库
                    cacheService.insertRunTimeRecord(runTimeRecord);
                }
            }

        } else {
            //计算本次运行的时间=停止运行时间-开始运行时间
            Long duration = senDate - runTimeRecord.getStopTime().getTime();
            runTimeRecord.setResetTime(stopTime); //停止运行时间
            runTimeRecord.setDuration(duration);//运行时长毫秒
            //更改库
            cacheService.updateRunTimeRecord(runTimeRecord);
        }
    }

    /**
     * 计算设备跨天运行时间
     *
     * @param list
     * @param runTime
     * @param stopTime
     * @return
     * @throws ParseException
     */
    private List<RunTimeDo> figureRunTime(List<RunTimeDo> list, Date runTime, Date stopTime) throws ParseException {
        if (stopTime.getTime() > runTime.getTime()) {
            return list;
        }
        boolean sameDay = DateUtils.isSameDay(runTime, stopTime);
        if (sameDay) {
            long sumTime = stopTime.getTime() - runTime.getTime();
            RunTimeDo runTimeDo = new RunTimeDo();
            runTimeDo.setRunTime(runTime);
            runTimeDo.setStopTime(stopTime);
            runTimeDo.setSumTime(sumTime);
            list.add(runTimeDo);
            return list;
        } else {
            DateUtil dateUtil = new DateUtil();
            //第二天日期
            Date date = dateUtil.dayjiaday(runTime, 1);
            String year = String.format("%tY", date);
            String mon = String.format("%tm", date);
            String day = String.format("%td", date);
            String dateStr = year + "-" + mon + "-" + day + " 00:00:00";
            //拼接成第二天的0点时间戳
            Date date1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dateStr);
            //填充第一天的阶段的运行时间
            long sumTime = date1.getTime() - runTime.getTime();
            RunTimeDo runTimeDo = new RunTimeDo();
            runTimeDo.setRunTime(runTime);
            runTimeDo.setStopTime(date1);
            runTimeDo.setSumTime(sumTime);
            list.add(runTimeDo);
            return figureRunTime(list, date1, stopTime);
        }
    }
}
