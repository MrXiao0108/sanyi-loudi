package com.dzics.data.acquisition.service.impl;

import com.dzics.common.model.custom.CmdTcp;
import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.entity.DzDataCollection;
import com.dzics.common.model.entity.SysCommunicationLog;
import com.dzics.common.service.SysCommunicationLogService;
import com.dzics.common.util.DateUtil;
import com.dzics.data.acquisition.service.AccCommunicationLogService;
import com.dzics.data.acquisition.service.AccDataCollectionService;
import com.dzics.data.acquisition.service.CacheService;
import com.dzics.data.acquisition.service.MqService;
import com.dzics.data.acquisition.util.TcpStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ZhangChengJun
 * Date 2021/3/8.
 * @since
 */
@Slf4j
@Service
public class AccCommunicationLogServiceImpl implements AccCommunicationLogService {
    @Autowired
    private SysCommunicationLogService sysCommunicationLogService;

    @Autowired
    private AccDataCollectionService dataCollectionService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private TcpStringUtil tcpStringUtil;

    @Autowired
    private MqService mqService;

    @Async("asyncTaskExecutor")
    @Override
    public void saveRabbitmqMessage(RabbitmqMessage rabbitmqMessage, boolean update, boolean save) {
        Date date = DateUtil.stringDateToformatDate(rabbitmqMessage.getTimestamp());
        if (save){
            try {
                SysCommunicationLog communicationLog = new SysCommunicationLog();
                communicationLog.setMessageid(rabbitmqMessage.getMessageId());
                communicationLog.setQueuename(rabbitmqMessage.getQueueName());
                communicationLog.setClientid(rabbitmqMessage.getClientId());
                communicationLog.setOrdercode(rabbitmqMessage.getOrderCode());
                communicationLog.setLineno(rabbitmqMessage.getLineNo());
                communicationLog.setDevicetype(rabbitmqMessage.getDeviceType());
                communicationLog.setDevicecode(rabbitmqMessage.getDeviceCode());
                communicationLog.setMessage(rabbitmqMessage.getMessage());
                communicationLog.setCheck(rabbitmqMessage.getCheck());
                communicationLog.setTimestamp(date);
                sysCommunicationLogService.save(communicationLog);
            } catch (Throwable throwable) {
                log.error("保存上发指令数据错误：{}", throwable.getMessage(), throwable);
            }
        }

        if (update){
            try {
//       跟新采集数据指令数据
                String deviceType = rabbitmqMessage.getDeviceType();
                String lineNo = rabbitmqMessage.getLineNo();
                String orderCode = rabbitmqMessage.getOrderCode();
                String deviceCode = rabbitmqMessage.getDeviceCode();
                Long deviceId = cacheService.getDeviceId(orderCode, lineNo, deviceCode, deviceType);
                if (deviceId != null) {
                    DzDataCollection dzDataCollection = dataCollectionService.cacheDeviceId(deviceId);
                    String message = rabbitmqMessage.getMessage();
                    long time = date.getTime();
                    List<CmdTcp> cmdTcp = tcpStringUtil.getCmdTcp(message);
                    Map<String, Object> cmdMap = cmdTcpCurMap(cmdTcp, time);
                    DzDataCollection detectorItem = new DzDataCollection();
                    BeanUtils.populate(detectorItem, cmdMap);
                    if (dzDataCollection != null) {
                        detectorItem.setDeviceId(dzDataCollection.getDeviceId());
                        DzDataCollection b = dataCollectionService.updateDeviceId(detectorItem);
                    } else {
                        detectorItem.setDeviceId(deviceId);
                        detectorItem.setDelFlag(false);
                        boolean instert = dataCollectionService.instert(detectorItem);
                        log.warn("设备不存在指令数据,插入 deviceId: {},:detectorItem :{}", deviceId, detectorItem);
                    }
                } else {
                    log.warn("根据订单号：{},产线序号:{},设备编号: {},设备类型: {} 无法获取到设备ID : {}", orderCode, lineNo, deviceCode, deviceType,deviceId);
                }
            } catch (Throwable e) {
                log.error("跟新设备指令数据异常:{}", e.getMessage(), e);
            }
        }
    }


    private Map<String, Object> cmdTcpCurMap(List<CmdTcp> cmdTcp, long time) {
        Map<String, Object> map = new HashMap<>();
        for (CmdTcp tcp : cmdTcp) {
            String tcpValue = tcp.getTcpValue();
            String mpK = tcpValue.substring(0, 1).toLowerCase() + tcpValue.substring(1);
            map.put(mpK, tcp.getDeviceItemValue());
            map.put(MpKCmdBase.basK + mpK, time);
        }
        return map;
    }

    private class MpKCmdBase {
        public static final String basK = "d";
    }
}
