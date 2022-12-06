package com.dzics.data.acquisition.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dzics.common.enums.DeviceSocketSendStatus;
import com.dzics.common.enums.EquiTypeEnum;
import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.entity.SysRealTimeLogs;
import com.dzics.common.model.constant.LogType;
import com.dzics.common.model.request.dzcheck.DzOrderCheck;
import com.dzics.common.model.response.JCEquimentBase;
import com.dzics.common.model.response.ReatimLogRes;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.SysRealTimeLogsService;
import com.dzics.data.acquisition.model.EqMentStatus;
import com.dzics.data.acquisition.service.AccRealTimeLogsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/4/6.
 * @since
 */
@Slf4j
@Service
public class AccRealTimeLogsServiceImpl implements AccRealTimeLogsService {
    @Autowired
    private SysRealTimeLogsService realTimeLogsService;

    @Override
    public SysRealTimeLogs saveRealTimeLog(RabbitmqMessage rabbitmqMessage) {
        String clientId = rabbitmqMessage.getClientId();
        if (LogType.logType.equals(clientId)) {
            String jsonLogEntity = rabbitmqMessage.getMessage();
            SysRealTimeLogs sysRealTimeLogs = JSONObject.parseObject(jsonLogEntity, SysRealTimeLogs.class);
            boolean save = realTimeLogsService.save(sysRealTimeLogs);
            return sysRealTimeLogs;
        } else if (LogType.logType_MA.equals(clientId)) {
            String jsonLogEntity = rabbitmqMessage.getMessage();
            SysRealTimeLogs sysRealTimeLogs = JSONObject.parseObject(jsonLogEntity, SysRealTimeLogs.class);
            String message = sysRealTimeLogs.getMessage();
            DzOrderCheck dzOrderCheck = JSONObject.parseObject(message, DzOrderCheck.class);
            String vehicleNumber = dzOrderCheck.getBasketType();
            sysRealTimeLogs.setMessage("收到小车 " + vehicleNumber + " 物料信息 待确认");
            boolean save = realTimeLogsService.save(sysRealTimeLogs);
            return sysRealTimeLogs;
        } else {
            String message = rabbitmqMessage.getMessage();
            if (!StringUtils.isEmpty(message)) {
                String[] split = message.split("\\|");
                if (split.length < 2) {
                    return null;
                }
//            指令信息
                String cmd = split[0];
                if (cmd.equals(EqMentStatus.CMD_ROB_RUN_INFO)) {
                    String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(split[1], "[");
                    deviceItemValue = deviceItemValue.substring(0, deviceItemValue.length() - 1);
                    String[] msg = deviceItemValue.split("TT");
                    if (msg.length < 2) {
                        return null;
                    }
                    String msgType = msg[0];
                    String messageSave = msg[1];
                    SysRealTimeLogs realTimeLogs = new SysRealTimeLogs();
                    realTimeLogs.setMessageId(rabbitmqMessage.getMessageId());
                    realTimeLogs.setQueueName(rabbitmqMessage.getQueueName());
                    realTimeLogs.setClientId(rabbitmqMessage.getClientId());
                    realTimeLogs.setOrderCode(rabbitmqMessage.getOrderCode());
                    realTimeLogs.setLineNo(rabbitmqMessage.getLineNo());
                    realTimeLogs.setDeviceType(rabbitmqMessage.getDeviceType());
                    realTimeLogs.setDeviceCode(rabbitmqMessage.getDeviceCode());
                    realTimeLogs.setMessageType(Integer.valueOf(msgType));
                    realTimeLogs.setMessage(messageSave);
                    realTimeLogs.setTimestampTime(new Date());
                    realTimeLogsService.save(realTimeLogs);
                    return realTimeLogs;
                }

            }
        }
        return null;
    }

    @Override
    public void saveRealTimeLog(List<RabbitmqMessage> rabbitmqMessageList) {
        for (RabbitmqMessage rabbitmqMessage : rabbitmqMessageList) {
            String message = rabbitmqMessage.getMessage();
            if (!StringUtils.isEmpty(message)) {
                String[] split = message.split("\\|");
                if (split.length < 2) {
                    continue;
                }
//            指令信息
                String cmd = split[0];
                if (cmd.equals(EqMentStatus.CMD_ROB_RUN_INFO)) {
                    String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(split[1], "[");
                    deviceItemValue = deviceItemValue.substring(0, deviceItemValue.length() - 1);
                    String[] msg = deviceItemValue.split("TT");
                    if (msg.length < 2) {
                        continue;
                    }
                    String msgType = msg[0];
                    String messageSave = msg[1];
                    SysRealTimeLogs realTimeLogs = new SysRealTimeLogs();
                    realTimeLogs.setMessageId(rabbitmqMessage.getMessageId());
                    realTimeLogs.setQueueName(rabbitmqMessage.getQueueName());
                    realTimeLogs.setClientId(rabbitmqMessage.getClientId());
                    realTimeLogs.setOrderCode(rabbitmqMessage.getOrderCode());
                    realTimeLogs.setLineNo(rabbitmqMessage.getLineNo());
                    realTimeLogs.setDeviceType(rabbitmqMessage.getDeviceType());
                    realTimeLogs.setDeviceCode(rabbitmqMessage.getDeviceCode());
                    realTimeLogs.setMessageType(Integer.valueOf(msgType));
                    realTimeLogs.setMessage(messageSave);
                    realTimeLogs.setTimestampTime(new Date());
                    realTimeLogsService.save(realTimeLogs);
                }

            }
        }
    }

    @Override
    public Result getLogDebug(String orderNo, String lineNo, String deviceType) {
        List<ReatimLogRes> list = realTimeLogsService.getReatimeLogsType(orderNo, lineNo, 1, deviceType, 10);
        JCEquimentBase<List<ReatimLogRes>> jcEquimentBase = new JCEquimentBase<>();
        jcEquimentBase.setData(list);
        if (StringUtils.isEmpty(deviceType)) {
            jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_REAL_TIME_LOG.getInfo());
        } else {
            jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_REAL_TIME_LOG_AGV.getInfo());
        }
        return Result.ok(jcEquimentBase);
    }

    @Override
    public Result getLogWarn(String orderNo, String lineNo, String deviceType) {
        List<ReatimLogRes> list = realTimeLogsService.getReatimeLogsType(orderNo, lineNo, 2, deviceType, 20);
        JCEquimentBase<List<ReatimLogRes>> jcEquimentBase = new JCEquimentBase<>();
        jcEquimentBase.setData(list);
        if (StringUtils.isEmpty(deviceType)) {
            jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_ALARM_LOG.getInfo());
        } else {
            jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_ALARM_LOG_AGV.getInfo());
        }
        return Result.ok(jcEquimentBase);
    }

}
