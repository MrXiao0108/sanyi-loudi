package com.dzics.data.acquisition.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.dao.DzEquipmentMapper;
import com.dzics.common.dao.DzEquipmentProNumMapper;
import com.dzics.common.dao.DzOrderMapper;
import com.dzics.common.enums.DeviceSocketSendStatus;
import com.dzics.common.enums.EquiTypeEnum;
import com.dzics.common.model.custom.MachiningMessageStatus;
import com.dzics.common.model.custom.MachiningNumTotal;
import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.model.entity.SysRealTimeLogs;
import com.dzics.common.model.constant.DzUdpType;
import com.dzics.common.model.constant.LogClientType;
import com.dzics.common.model.constant.SysConfigDepart;
import com.dzics.common.model.response.JCEquimentBase;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.RunDataModel;
import com.dzics.common.model.response.equipmentstate.DzDataCollectionDo;
import com.dzics.common.service.DzEquipmentProNumService;
import com.dzics.common.service.DzEquipmentService;
import com.dzics.common.util.RedisKey;
import com.dzics.data.acquisition.model.machining.MachiningJC;
import com.dzics.data.acquisition.service.AccStorageLocationService;
import com.dzics.data.acquisition.service.CacheService;
import com.dzics.data.acquisition.service.HomeLineDataService;
import com.dzics.data.acquisition.util.RedisUtil;
import com.dzics.data.acquisition.util.SnowflakeUtil;
import com.dzics.data.acquisition.util.TcpStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author ZhangChengJun
 * Date 2021/3/20.
 * @since
 */
@Slf4j
@Service
public class AccStorageLocationServiceImpl implements AccStorageLocationService {
    @Autowired
    private DzEquipmentService equipmentService;
    @Autowired
    private DzEquipmentProNumService proNumService;
    @Autowired
    private CacheService cacheService;
    @Value("${accq.read.cmd.queue.equipment.realTime}")
    private String queueRealTimeEquipment;
    @Autowired
    private DzOrderMapper dzOrderMapper;
    @Autowired
    TcpStringUtil tcpStringUtil;

    @Autowired
    private HomeLineDataService homeLineDataService;

    @Autowired
    private DzEquipmentProNumMapper dzEquipmentProNumMapper;
    @Autowired
    private DzEquipmentMapper dzEquipmentMapper;

    @Autowired
    private SnowflakeUtil snowflakeUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Result getEquimentStateX(String lineNo, String orderNum) {
        String systemConfig = cacheService.getSystemConfigDepart();
        LocalDate now = LocalDate.now();
        JCEquimentBase jcEquimentBase = new JCEquimentBase();
        jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_DEVICE.getInfo());
        List<DzDataCollectionDo> dd = equipmentService.getMachiningMessageStatus(lineNo, orderNum, now);
        List<MachiningMessageStatus> dzEquipments = toMachiningMessageStatus(dd);

        if (CollectionUtils.isNotEmpty(dzEquipments)) {
            if (systemConfig.equals(SysConfigDepart.SANY)) {
                if (orderNum.equals("DZ-1955") || orderNum.equals("DZ-1956")) {
                    getMach(dzEquipments, orderNum);
                }
            }
            MachiningJC machiningjc = new MachiningJC();
            machiningjc.setMachiningMessageStatus(dzEquipments);
            jcEquimentBase.setData(machiningjc);
        }
        Result<JCEquimentBase> ok = Result.ok(jcEquimentBase);
        return ok;
    }

    public void getMach(List<MachiningMessageStatus> statuses, String orderNo) {
        MachiningMessageStatus machiningMessageStatus = null;
        for (MachiningMessageStatus status : statuses) {
            if ("02".equals(status.getEquipmentNo())) {
                machiningMessageStatus = status;
            }
            String runStatus = status.getRunStatus();
            String connectState = status.getConnectState();
            String alarmStatus = status.getAlarmStatus();
            status.setWorkStatus("关机");
            if (!StringUtils.isEmpty(runStatus)) {
                if (!StringUtils.isEmpty(connectState) && connectState.equals("联机")) {
                    if (!StringUtils.isEmpty(alarmStatus) && "报警".equals(alarmStatus)) {
                        status.setWorkStatus("故障");
                        continue;
                    } else {
                        if (runStatus.equals("生产")) {
                            status.setWorkStatus("作业");
                            continue;
                        } else {
                            status.setWorkStatus("待机");
                            continue;
                        }
                    }
                }
            }
        }
//        设置摩擦焊机状态
        for (MachiningMessageStatus status : statuses) {
            String equipmentNo = status.getEquipmentNo();
            if ("A2".equals(equipmentNo)) {
                status.setConnectState(machiningMessageStatus.getConnectState());
                status.setRunStatus(machiningMessageStatus.getRunStatus());
                redisUtil.set(RedisKey.socketIoHandler_accqDzEquipmentService_getEquimentStateX + orderNo + "A2", status);
            }
        }
    }

    @Override
    public List<MachiningNumTotal> machiningNumTotals(LocalDate now, List<String> collect) {
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
     * 生成除去传送带等设备的日志信息
     *
     * @param dzEquipment
     * @return
     */
    @Override
    public List<RabbitmqMessage> createRealTimeLogsDevice(DzEquipment dzEquipment) {
        List<RabbitmqMessage> rabbitmqMessageList = new ArrayList<>();
        Date date = new Date();
        SimpleDateFormat formatterMMss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String format = formatterMMss.format(date);
        String lineNo = dzEquipment.getLineNo();
        String orderNo = dzEquipment.getOrderNo();
        List<String> logs = dzEquipment.getLogs();
        List<String> logsWar = dzEquipment.getLogsWar();
        if (CollectionUtils.isNotEmpty(logs)) {
            for (String log : logs) {
                String message = "A813|[" + 1 + "TT" + log + "]";
                RabbitmqMessage rabbitmqMessage = new RabbitmqMessage();
                rabbitmqMessage.setMessage(message);
                rabbitmqMessage.setDeviceType(dzEquipment.getEquipmentType().toString());
                rabbitmqMessage.setOrderCode(orderNo);
                rabbitmqMessage.setClientId(LogClientType.ACC_DEVICE_STATES);
                rabbitmqMessage.setDeviceCode(dzEquipment.getEquipmentNo());
                rabbitmqMessage.setQueueName(queueRealTimeEquipment);
                rabbitmqMessage.setTimestamp(format);
                rabbitmqMessage.setLineNo(lineNo);
                rabbitmqMessage.setMessageId(String.valueOf(snowflakeUtil.nextId()));
                rabbitmqMessage.setCheck(false);
                rabbitmqMessageList.add(rabbitmqMessage);
            }
        }
        if (CollectionUtils.isNotEmpty(logsWar)) {
            for (String log : logsWar) {
                String message = "A813|[" + 2 + "TT" + log + "]";
                RabbitmqMessage rabbitmqMessage = new RabbitmqMessage();
                rabbitmqMessage.setMessage(message);
                rabbitmqMessage.setDeviceType(dzEquipment.getEquipmentType().toString());
                rabbitmqMessage.setOrderCode(orderNo);
                rabbitmqMessage.setClientId(LogClientType.ACC_DEVICE_STATES);
                rabbitmqMessage.setDeviceCode(dzEquipment.getEquipmentNo());
                rabbitmqMessage.setQueueName(queueRealTimeEquipment);
                rabbitmqMessage.setTimestamp(format);
                rabbitmqMessage.setLineNo(lineNo);
                rabbitmqMessage.setMessageId(String.valueOf(snowflakeUtil.nextId()));
                rabbitmqMessage.setCheck(false);
                rabbitmqMessageList.add(rabbitmqMessage);
            }
        }
        return rabbitmqMessageList;
    }

    /**
     * Q,2,A,2   逗号分割
     * 第一位 Q通用前缀,
     * 第二位 1 发送获取二维码请求, 2 表示 触发来料信号
     * 第三位表示 车辆编号 有 A,B,C 三两小车
     * 第四位表示请求ID:
     *
     * @param msg
     * @return
     */
    @Override
    public SysRealTimeLogs getDzRealTimelogs(String[] msg) {
        if (msg.length == 7) {
            String q2 = msg[1]; // 类型
            String q22 = msg[2]; // 指令类型
            String q3 = msg[3];  // A,B,C
            String q4 = msg[4];  // 消息ID
            String orderNo = msg[5];
            String lineNo = msg[6];
            SysRealTimeLogs timeLogs = new SysRealTimeLogs();
            timeLogs.setMessageId(UUID.randomUUID().toString().replaceAll("-", ""));
            timeLogs.setQueueName(queueRealTimeEquipment);
            timeLogs.setClientId(LogClientType.UDP_AGV);
            timeLogs.setOrderCode(orderNo);
            timeLogs.setLineNo(lineNo);
            timeLogs.setDeviceType(String.valueOf(EquiTypeEnum.AVG.getCode()));
            timeLogs.setDeviceCode(q3);
            timeLogs.setMessageType(1);
            if (q22.equals(DzUdpType.udpTypeAgvSinal)) {
                timeLogs.setMessage("机器人回复收到 " + q3 + " 号小车来料信号");
            } else if (q22.equals(DzUdpType.undpAgvConfirm)) {
                timeLogs.setMessage("机器人回复收到 " + q3 + " 号小车确认物料信息");
            }
            timeLogs.setTimestampTime(new Date());
            return timeLogs;
        }
        return null;
    }

    @Override
    public SysRealTimeLogs getDzRealTimeRoblogs(String[] msg) {
//        Q,4,1100,小车,orderNo,lineNo,校验结果信息
        int length = msg.length;
        if (length >= 7) {
            String cmdType = msg[2]; // 指令类型1100
            String carNumber = msg[3];  // A,B,C
            String orderNo = msg[4];  // 消息ID
            String lineNo = msg[5];
            String message = msg[6];
            if (length > 7) {
                for (int i = 7; i < length; i++) {
                    message = message + msg[i];
                }
            }
            SysRealTimeLogs timeLogs = new SysRealTimeLogs();
            timeLogs.setMessageId(UUID.randomUUID().toString().replaceAll("-", ""));
            timeLogs.setQueueName(queueRealTimeEquipment);
            timeLogs.setClientId(LogClientType.ROB_AGV);
            timeLogs.setOrderCode(orderNo);
            timeLogs.setLineNo(lineNo);
            timeLogs.setDeviceType(String.valueOf(EquiTypeEnum.AVG.getCode()));
            timeLogs.setDeviceCode(carNumber);
            timeLogs.setMessageType(1);
            timeLogs.setMessage("小车" + carNumber + message);
            timeLogs.setTimestampTime(new Date());
            return timeLogs;
        } else {
            log.error("接收到机器人日志格式解析失败，根据逗号分割后长度错误: {}", msg);
        }
        return null;
    }

    public List<MachiningMessageStatus> toMachiningMessageStatus(List<DzDataCollectionDo> dataList) {
        List<MachiningMessageStatus> list = new ArrayList<>();
        for (DzDataCollectionDo data : dataList) {
            MachiningMessageStatus machiningMessageStatus = new MachiningMessageStatus();
            machiningMessageStatus.setEquimentId(String.valueOf(data.getEquipmentId()));
            machiningMessageStatus.setB527(tcpParse("B527", data.getB527()));
            machiningMessageStatus.setB526(tcpParse("B526", data.getB526()));
            machiningMessageStatus.setA812(tcpParse("A812", data.getA812()));
            machiningMessageStatus.setB809(tcpParse("B809", data.getB809()));
            machiningMessageStatus.setA541(tcpParse("A541", data.getA541()));
            machiningMessageStatus.setEquipmentNo(data.getEquipmentNo());
            machiningMessageStatus.setEquipmentType(data.getEquipmentType());
            machiningMessageStatus.setEquipmentName(data.getEquipmentName());
            machiningMessageStatus.setCurrentLocation(data.getCurrentLocation());
            machiningMessageStatus.setDownSum(data.getDownSum());
            machiningMessageStatus.setGasFlow(data.getB814());
            machiningMessageStatus.setX("0");
            machiningMessageStatus.setY("0");
            machiningMessageStatus.setZ("0");
            String[] split=null;
//            machiningMessageStatus.setHistoryOk();
//            machiningMessageStatus.setHistoryNg();
//            machiningMessageStatus.setDayOk();
//            machiningMessageStatus.setDayNg();
//            machiningMessageStatus.setOk();
//            machiningMessageStatus.setNg();

            if (data.getEquipmentType().intValue() == 2) {//机床
                machiningMessageStatus.setFeedSpeed(tcpParse("B541", data.getB541()));
                machiningMessageStatus.setOperatorMode(tcpParse("B565", data.getB565()));
                machiningMessageStatus.setConnectState(tcpParse("B561", data.getB561()));
                machiningMessageStatus.setRunStatus(tcpParse("B562", data.getB562()));
                machiningMessageStatus.setEmergencyStatus(tcpParse("B568", data.getB568()));
                machiningMessageStatus.setAlarmStatus(tcpParse("B569", data.getB569()));
                machiningMessageStatus.setMachiningTime(tcpParse("B527", data.getB527()));
                machiningMessageStatus.setSpeedOfMainShaft(tcpParse("B551", data.getB551()));
                machiningMessageStatus.setCleanTime(tcpParse("B527", data.getB527()));//清洗时长
                String str = tcpParse("B501", data.getB501());
                if(!StringUtils.isEmpty(str)){
                    split = str.split(",");
                }

            } else if (data.getEquipmentType().intValue() == 3) {//机器人
                machiningMessageStatus.setA567(tcpParse("A567",data.getA567()));
                machiningMessageStatus.setOperatorMode(tcpParse("A562", data.getA562()));
                machiningMessageStatus.setConnectState(tcpParse("A561", data.getA561()));
                machiningMessageStatus.setRunStatus(tcpParse("A563", data.getA563()));
                //如果机器人是待机  则运行状态改为待机状态
                if("1".equals(data.getA567())){
                    machiningMessageStatus.setRunStatus(machiningMessageStatus.getA567());
                }
                machiningMessageStatus.setEmergencyStatus(tcpParse("A565", data.getA565()));
                machiningMessageStatus.setAlarmStatus(tcpParse("A566", data.getA566()));
                machiningMessageStatus.setSpeedRatio(tcpParse("A521", data.getA521()));
                machiningMessageStatus.setMachiningTime(tcpParse("A802", data.getA802()));
                String str = tcpParse("A502", data.getA502());
                if(!StringUtils.isEmpty(str)){
                    split = str.split(",");
                }

            } else if (data.getEquipmentType().intValue() == 8) {//淬火机
                machiningMessageStatus.setOperatorMode(tcpParse("H566", data.getH566()));
                machiningMessageStatus.setConnectState(tcpParse("H561", data.getH561()));
                machiningMessageStatus.setRunStatus(tcpParse("H562", data.getH562()));
                machiningMessageStatus.setWorkStatus(tcpParse("H563", data.getH563()));
                machiningMessageStatus.setMovementSpeed(tcpParse("H706", data.getH706()));//淬火机 移动速度  mm/s
                machiningMessageStatus.setWorkpieceSpeed(tcpParse("H707", data.getH707()));//工件转速 Rad/min
                machiningMessageStatus.setCoolantTemperature(tcpParse("H801", data.getH801()));//冷却液温度 ℃
                machiningMessageStatus.setCoolantPressure(tcpParse("H804", data.getH804()));//冷却液压力 MPa
                machiningMessageStatus.setCoolantFlow(tcpParse("H805", data.getH805()));//冷却液流量 L/s

            } else if (data.getEquipmentType().intValue() == 9) {//校直机
                machiningMessageStatus.setOperatorMode(tcpParse("K566", data.getK566()));
                machiningMessageStatus.setConnectState(tcpParse("K561", data.getK561()));
                machiningMessageStatus.setRunStatus(tcpParse("K562", data.getK562()));
                machiningMessageStatus.setWorkStatus(tcpParse("K563", data.getK563()));
                machiningMessageStatus.setHeadPositionUd(tcpParse("K803", data.getK803()));//压头上下位置
                machiningMessageStatus.setHeadPostionLr(tcpParse("K804", data.getK804()));//压头左右位置

            }

            if(split!=null&&split.length>=3){
                machiningMessageStatus.setX(split[0]);
                machiningMessageStatus.setY(split[1]);
                machiningMessageStatus.setZ(split[2]);
            }
            list.add(machiningMessageStatus);
        }
        return list;
    }

    public String tcpParse(String tcpName, String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        return tcpStringUtil.getCmdNameV2(tcpName, value);
    }

}
