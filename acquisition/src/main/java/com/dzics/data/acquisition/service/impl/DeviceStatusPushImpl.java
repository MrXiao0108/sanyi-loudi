package com.dzics.data.acquisition.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.dao.DzProductDetectionTemplateMapper;
import com.dzics.common.dao.DzWaitCheckResMapper;
import com.dzics.common.dao.DzWorkStationManagementMapper;
import com.dzics.common.enums.DeviceSocketSendStatus;
import com.dzics.common.enums.EquiTypeEnum;
import com.dzics.common.model.custom.MachiningMessageStatus;
import com.dzics.common.model.custom.OrderIdLineId;
import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.custom.ReqWorkQrCodeOrder;
import com.dzics.common.model.entity.*;
import com.dzics.common.model.constant.LogType;
import com.dzics.common.model.constant.SysConfigDepart;
import com.dzics.common.model.frid.FridJson;
import com.dzics.common.model.frid.FridTypeCar;
import com.dzics.common.model.qrCode.QrCodeType;
import com.dzics.common.model.response.*;
import com.dzics.common.model.response.productiontask.ProDetection;
import com.dzics.common.model.response.productiontask.stationbg.ResponseWorkStationBg;
import com.dzics.common.service.DzProductDetectionTemplateService;
import com.dzics.common.service.DzWorkingFlowService;
import com.dzics.common.util.DateUtil;
import com.dzics.common.util.RedisKey;
import com.dzics.data.acquisition.config.MapConfig;
import com.dzics.data.acquisition.model.EqMentStatus;
import com.dzics.data.acquisition.model.SocketDowmSum;
import com.dzics.data.acquisition.netty.server.SocketIoHandler;
import com.dzics.data.acquisition.netty.SocketMessageType;
import com.dzics.data.acquisition.netty.SocketServerTemplate;
import com.dzics.data.acquisition.service.*;
import com.dzics.data.acquisition.util.RedisUtil;
import com.dzics.data.acquisition.util.SnowflakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.regex.Matcher;

/**
 * @author ZhangChengJun
 * Date 2021/4/7.
 * @since
 */
@Service
@Slf4j
public class DeviceStatusPushImpl implements DeviceStatusPush {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MapConfig mapConfig;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private SocketServerTemplate socketServerTemplate;
    @Autowired
    private DzProductDetectionTemplateService dzProductDetectionTemplateService;
    @Qualifier("localRabbitTemplate")
    @Autowired
    private RabbitTemplate localRabbitTemplate;
    @Autowired
    private DzWorkingFlowService dzWorkingFlowService;
    @Autowired
    private LineDataService lineDataService;
    @Value("${accq.realTime.equipment.routing}")
    private String directRealTimeEquipmentRouting;
    @Value("${accq.realTime.equipment.exchange}")
    private String directRealTimeEquipmentExchange;
    @Autowired
    private SocketIoHandler socketIoHandler;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    DzProductDetectionTemplateMapper dzProductDetectionTemplateMapper;
    @Autowired
    AccDetectorDataService accDetectorDataService;
    @Autowired
    private SnowflakeUtil snowflakeUtil;
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private AccqDzProductService dzProductService;
    @Autowired
    private DzWaitCheckResMapper checkResMapper;
    @Autowired
    private DzWorkStationManagementMapper stationManagementService;

    @Override
    public void sendReatimLogs(SysRealTimeLogs b) {
        String deviceType = b.getDeviceType();
        Integer messageType = b.getMessageType();
        String message = b.getMessage();
        String clientId = b.getClientId();
        Date timestampTime = b.getTimestampTime();
        String orderCode = b.getOrderCode();
        String lineNo = b.getLineNo();
        if (messageType != null && 2 == messageType) {
//            是AGV 时 ,是否存在单独订阅AGV日志的事件
            String agvDeviceType = String.valueOf(EquiTypeEnum.AVG.getCode());
            if (agvDeviceType.equals(deviceType)) {
                String eventKeyType = getEvent(SocketMessageType.DEVICE_WARN_LOG, orderCode, lineNo, deviceType);
                if (getIsSendEvend(eventKeyType)) {
                    extracted(deviceType, messageType, message, clientId, timestampTime, eventKeyType, SocketMessageType.DEVICE_WARN_LOG);
                }
            } else {
//       告警日志 不区分告警类型 订阅事件发送
                String eventKey = getEvent(SocketMessageType.DEVICE_WARN_LOG, orderCode, lineNo);
                if (getIsSendEvend(eventKey)) {
                    extracted(deviceType, messageType, message, clientId, timestampTime, eventKey, SocketMessageType.DEVICE_WARN_LOG);
                }
            }
        } else {
//       是AGV 时 ,是否存在单独订阅AGV日志的事件
            String agvDeviceType = String.valueOf(EquiTypeEnum.AVG.getCode());
            if (agvDeviceType.equals(deviceType)) {
                String eventKeyType = getEvent(SocketMessageType.DEVICE_LOG, orderCode, lineNo, deviceType);
                if (getIsSendEvend(eventKeyType)) {
                    extracted(deviceType, messageType, message, clientId, timestampTime, eventKeyType, SocketMessageType.DEVICE_LOG);
                }
            } else {
//       实时日志
                String eventKey = getEvent(SocketMessageType.DEVICE_LOG, orderCode, lineNo);
                if (getIsSendEvend(eventKey)) {
                    extracted(deviceType, messageType, message, clientId, timestampTime, eventKey, SocketMessageType.DEVICE_LOG);
                }
            }

        }
    }

    private void extracted(String deviceType, Integer messageType, String message, String clientId, Date timestampTime, String eventKey, String eventBaseKey) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = dateFormat.format(timestampTime);
        ReatimLogRes reatimLogRes = new ReatimLogRes();
        reatimLogRes.setMessage(message);
        reatimLogRes.setRealTime(format);
        reatimLogRes.setClientId(clientId);
        JCEquimentBase<ReatimLogRes> jcEquimentBase = new JCEquimentBase<>();
        jcEquimentBase.setData(reatimLogRes);
        if (messageType != null && 1 == messageType) {
            if (String.valueOf(EquiTypeEnum.AVG.getCode()).equals(deviceType)) {
                jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_REAL_TIME_LOG_AGV.getInfo());
            } else {
                jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_REAL_TIME_LOG.getInfo());
            }
        } else {
            if (String.valueOf(EquiTypeEnum.AVG.getCode()).equals(deviceType)) {
                jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_ALARM_LOG_AGV.getInfo());
            } else {
                jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_ALARM_LOG.getInfo());
            }

        }
        Result<JCEquimentBase<ReatimLogRes>> ok = Result.ok(jcEquimentBase);
        socketServerTemplate.sendMessage(eventBaseKey, eventKey, ok);
    }


    /**
     * 判断socket链接是否存在
     *
     * @param event
     * @return
     */
    private synchronized boolean getIsSendEvend(String event) {
        ConcurrentSkipListSet<UUID> connectType = socketIoHandler.getConnectType(event);
        if (connectType != null && connectType.size() > 0) {
            return true;
        }
        return false;
    }

    private String getEvent(String event, String orderCode, String lineNo) {
        if (orderCode.equals("-")) {
            orderCode = "";
        }
        if (lineNo.equals("-")) {
            lineNo = "";
        }
        return event + orderCode + lineNo;
    }

    private String getEvent(String event, String orderCode, String lineNo, String deviceType) {
        if (orderCode.equals("-")) {
            orderCode = "";
        }
        if (lineNo.equals("-")) {
            lineNo = "";
        }
        if (deviceType.equals("-")) {
            deviceType = "";
        }
        return event + orderCode + lineNo + deviceType;
    }

    @Override
    public boolean sendWorkpieceData(DzWorkpieceData dzWorkpieceData) {

        String eventKey = getEvent(SocketMessageType.TEST_ITEM_RECORD, dzWorkpieceData.getOrderNo(), dzWorkpieceData.getLineNo());
        boolean isSendEvend = getIsSendEvend(eventKey);
        if (isSendEvend) {
            NewestThreeDataDo newestThreeDataDo = new NewestThreeDataDo();
            newestThreeDataDo.setOnlyKey(dzWorkpieceData.getId());
            SimpleDateFormat aDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String mrz = "9999.999";
            newestThreeDataDo.setName(dzWorkpieceData.getName());
            newestThreeDataDo.setProducBarcode(dzWorkpieceData.getProducBarcode());
            newestThreeDataDo.setOutOk(dzWorkpieceData.getOutOk());
            newestThreeDataDo.setDetectorTime(aDate.format(dzWorkpieceData.getDetectorTime()));
            //1
            if ("".equals(dzWorkpieceData.getDetect01()) || null == dzWorkpieceData.getDetect01()) {
                newestThreeDataDo.setDetect01(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk01() != null && dzWorkpieceData.getOutOk01() == 0 ? dzWorkpieceData.getDetect01().toString() + "::" : dzWorkpieceData.getDetect01().toString();
                newestThreeDataDo.setDetect01(detect);
            }
            //2
            if ("".equals(dzWorkpieceData.getDetect02()) || null == dzWorkpieceData.getDetect02()) {
                newestThreeDataDo.setDetect02(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk02() != null && dzWorkpieceData.getOutOk02() == 0 ? dzWorkpieceData.getDetect02().toString() + "::" : dzWorkpieceData.getDetect02().toString();
                newestThreeDataDo.setDetect02(detect);
            }
            //3
            if ("".equals(dzWorkpieceData.getDetect03()) || null == dzWorkpieceData.getDetect03()) {
                newestThreeDataDo.setDetect03(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk03() != null && dzWorkpieceData.getOutOk03() == 0 ? dzWorkpieceData.getDetect03().toString() + "::" : dzWorkpieceData.getDetect03().toString();
                newestThreeDataDo.setDetect03(detect);
            }
            //4
            if ("".equals(dzWorkpieceData.getDetect04()) || null == dzWorkpieceData.getDetect04()) {
                newestThreeDataDo.setDetect04(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk04() != null && dzWorkpieceData.getOutOk04() == 0 ? dzWorkpieceData.getDetect04().toString() + "::" : dzWorkpieceData.getDetect04().toString();
                newestThreeDataDo.setDetect04(detect);
            }
            //5
            if ("".equals(dzWorkpieceData.getDetect05()) || null == dzWorkpieceData.getDetect05()) {
                newestThreeDataDo.setDetect05(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk05() != null && dzWorkpieceData.getOutOk05() == 0 ? dzWorkpieceData.getDetect05().toString() + "::" : dzWorkpieceData.getDetect05().toString();
                newestThreeDataDo.setDetect05(detect);
            }
            //6
            if ("".equals(dzWorkpieceData.getDetect06()) || null == dzWorkpieceData.getDetect06()) {
                newestThreeDataDo.setDetect06(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk06() != null && dzWorkpieceData.getOutOk06() == 0 ? dzWorkpieceData.getDetect06().toString() + "::" : dzWorkpieceData.getDetect06().toString();
                newestThreeDataDo.setDetect06(detect);
            }
            //7
            if ("".equals(dzWorkpieceData.getDetect07()) || null == dzWorkpieceData.getDetect07()) {
                newestThreeDataDo.setDetect07(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk07() != null && dzWorkpieceData.getOutOk07() == 0 ? dzWorkpieceData.getDetect07().toString() + "::" : dzWorkpieceData.getDetect07().toString();
                newestThreeDataDo.setDetect07(detect);
            }
            //8
            if ("".equals(dzWorkpieceData.getDetect08()) || null == dzWorkpieceData.getDetect08()) {
                newestThreeDataDo.setDetect08(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk08() != null && dzWorkpieceData.getOutOk08() == 0 ? dzWorkpieceData.getDetect08().toString() + "::" : dzWorkpieceData.getDetect08().toString();
                newestThreeDataDo.setDetect08(detect);
            }
            //9
            if ("".equals(dzWorkpieceData.getDetect09()) || null == dzWorkpieceData.getDetect09()) {
                newestThreeDataDo.setDetect09(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk09() != null && dzWorkpieceData.getOutOk09() == 0 ? dzWorkpieceData.getDetect09().toString() + "::" : dzWorkpieceData.getDetect09().toString();
                newestThreeDataDo.setDetect09(detect);
            }
            //10
            if ("".equals(dzWorkpieceData.getDetect10()) || null == dzWorkpieceData.getDetect10()) {
                newestThreeDataDo.setDetect10(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk10() != null && dzWorkpieceData.getOutOk10() == 0 ? dzWorkpieceData.getDetect10().toString() + "::" : dzWorkpieceData.getDetect10().toString();
                newestThreeDataDo.setDetect10(detect);
            }
            //11
            if ("".equals(dzWorkpieceData.getDetect11()) || null == dzWorkpieceData.getDetect11()) {
                newestThreeDataDo.setDetect11(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk11() != null && dzWorkpieceData.getOutOk11() == 0 ? dzWorkpieceData.getDetect11().toString() + "::" : dzWorkpieceData.getDetect11().toString();
                newestThreeDataDo.setDetect11(detect);
            }
            //12
            if ("".equals(dzWorkpieceData.getDetect12()) || null == dzWorkpieceData.getDetect12()) {
                newestThreeDataDo.setDetect12(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk12() != null && dzWorkpieceData.getOutOk12() == 0 ? dzWorkpieceData.getDetect12().toString() + "::" : dzWorkpieceData.getDetect12().toString();
                newestThreeDataDo.setDetect12(detect);
            }
            //13
            if ("".equals(dzWorkpieceData.getDetect13()) || null == dzWorkpieceData.getDetect13()) {
                newestThreeDataDo.setDetect13(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk13() != null && dzWorkpieceData.getOutOk13() == 0 ? dzWorkpieceData.getDetect13().toString() + "::" : dzWorkpieceData.getDetect13().toString();
                newestThreeDataDo.setDetect13(detect);
            }
            //14
            if ("".equals(dzWorkpieceData.getDetect14()) || null == dzWorkpieceData.getDetect14()) {
                newestThreeDataDo.setDetect14(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk14() != null && dzWorkpieceData.getOutOk14() == 0 ? dzWorkpieceData.getDetect14().toString() + "::" : dzWorkpieceData.getDetect14().toString();
                newestThreeDataDo.setDetect14(detect);
            }
            //15
            if ("".equals(dzWorkpieceData.getDetect15()) || null == dzWorkpieceData.getDetect15()) {
                newestThreeDataDo.setDetect15(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk15() != null && dzWorkpieceData.getOutOk15() == 0 ? dzWorkpieceData.getDetect15().toString() + "::" : dzWorkpieceData.getDetect15().toString();
                newestThreeDataDo.setDetect15(detect);
            }
            //16
            if ("".equals(dzWorkpieceData.getDetect16()) || null == dzWorkpieceData.getDetect16()) {
                newestThreeDataDo.setDetect16(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk16() != null && dzWorkpieceData.getOutOk16() == 0 ? dzWorkpieceData.getDetect16().toString() + "::" : dzWorkpieceData.getDetect16().toString();
                newestThreeDataDo.setDetect16(detect);
            }
            //17
            if ("".equals(dzWorkpieceData.getDetect17()) || null == dzWorkpieceData.getDetect17()) {
                newestThreeDataDo.setDetect17(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk17() != null && dzWorkpieceData.getOutOk17() == 0 ? dzWorkpieceData.getDetect17().toString() + "::" : dzWorkpieceData.getDetect17().toString();
                newestThreeDataDo.setDetect17(detect);
            }
            //18
            if ("".equals(dzWorkpieceData.getDetect18()) || null == dzWorkpieceData.getDetect18()) {
                newestThreeDataDo.setDetect18(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk18() != null && dzWorkpieceData.getOutOk18() == 0 ? dzWorkpieceData.getDetect18().toString() + "::" : dzWorkpieceData.getDetect18().toString();
                newestThreeDataDo.setDetect18(detect);
            }
            //19
            if ("".equals(dzWorkpieceData.getDetect19()) || null == dzWorkpieceData.getDetect19()) {
                newestThreeDataDo.setDetect19(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk19() != null && dzWorkpieceData.getOutOk19() == 0 ? dzWorkpieceData.getDetect19().toString() + "::" : dzWorkpieceData.getDetect19().toString();
                newestThreeDataDo.setDetect19(detect);
            }
            //20
            if ("".equals(dzWorkpieceData.getDetect20()) || null == dzWorkpieceData.getDetect20()) {
                newestThreeDataDo.setDetect20(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk20() != null && dzWorkpieceData.getOutOk20() == 0 ? dzWorkpieceData.getDetect20().toString() + "::" : dzWorkpieceData.getDetect20().toString();
                newestThreeDataDo.setDetect20(detect);
            }
            //21
            if ("".equals(dzWorkpieceData.getDetect21()) || null == dzWorkpieceData.getDetect21()) {
                newestThreeDataDo.setDetect21(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk21() != null && dzWorkpieceData.getOutOk21() == 0 ? dzWorkpieceData.getDetect21().toString() + "::" : dzWorkpieceData.getDetect21().toString();
                newestThreeDataDo.setDetect21(detect);
            }
            //22
            if ("".equals(dzWorkpieceData.getDetect22()) || null == dzWorkpieceData.getDetect22()) {
                newestThreeDataDo.setDetect22(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk22() != null && dzWorkpieceData.getOutOk22() == 0 ? dzWorkpieceData.getDetect22().toString() + "::" : dzWorkpieceData.getDetect22().toString();
                newestThreeDataDo.setDetect22(detect);
            }
            //23
            if ("".equals(dzWorkpieceData.getDetect23()) || null == dzWorkpieceData.getDetect23()) {
                newestThreeDataDo.setDetect23(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk23() != null && dzWorkpieceData.getOutOk23() == 0 ? dzWorkpieceData.getDetect23().toString() + "::" : dzWorkpieceData.getDetect23().toString();
                newestThreeDataDo.setDetect23(detect);
            }
            //24
            if ("".equals(dzWorkpieceData.getDetect24()) || null == dzWorkpieceData.getDetect24()) {
                newestThreeDataDo.setDetect24(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk24() != null && dzWorkpieceData.getOutOk24() == 0 ? dzWorkpieceData.getDetect24().toString() + "::" : dzWorkpieceData.getDetect24().toString();
                newestThreeDataDo.setDetect24(detect);
            }
            //25
            if ("".equals(dzWorkpieceData.getDetect25()) || null == dzWorkpieceData.getDetect25()) {
                newestThreeDataDo.setDetect25(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk25() != null && dzWorkpieceData.getOutOk25() == 0 ? dzWorkpieceData.getDetect25().toString() + "::" : dzWorkpieceData.getDetect25().toString();
                newestThreeDataDo.setDetect25(detect);
            }
            //26
            if ("".equals(dzWorkpieceData.getDetect26()) || null == dzWorkpieceData.getDetect26()) {
                newestThreeDataDo.setDetect26(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk26() != null && dzWorkpieceData.getOutOk26() == 0 ? dzWorkpieceData.getDetect26().toString() + "::" : dzWorkpieceData.getDetect26().toString();
                newestThreeDataDo.setDetect26(detect);
            }
            //27
            if ("".equals(dzWorkpieceData.getDetect27()) || null == dzWorkpieceData.getDetect27()) {
                newestThreeDataDo.setDetect27(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk27() != null && dzWorkpieceData.getOutOk27() == 0 ? dzWorkpieceData.getDetect27().toString() + "::" : dzWorkpieceData.getDetect27().toString();
                newestThreeDataDo.setDetect27(detect);
            }
            //28
            if ("".equals(dzWorkpieceData.getDetect28()) || null == dzWorkpieceData.getDetect28()) {
                newestThreeDataDo.setDetect28(mrz);
            } else {
                String detect = dzWorkpieceData.getOutOk28() != null && dzWorkpieceData.getOutOk28() == 0 ? dzWorkpieceData.getDetect28().toString() + "::" : dzWorkpieceData.getDetect28().toString();
                newestThreeDataDo.setDetect28(detect);
            }
            List<Map<String, Object>> templates = dzProductDetectionTemplateService.listProductNo(dzWorkpieceData.getProductNo(), dzWorkpieceData.getOrderNo(), dzWorkpieceData.getLineNo());
            if (CollectionUtils.isEmpty(templates)) {
                templates = dzProductDetectionTemplateService.getDefoutDetectionTemp();
            }
            ProDetection proDetection = new ProDetection();
            proDetection.setTableColumn(templates);
            proDetection.setTableData(newestThreeDataDo);
            JCEquimentBase jcEquimentBase = new JCEquimentBase();
            jcEquimentBase.setData(proDetection);
            jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_DATA_TREND_SINGLE.getInfo());
            Result<JCEquimentBase> ok = Result.ok(jcEquimentBase);
            socketServerTemplate.sendMessage(SocketMessageType.TEST_ITEM_RECORD, eventKey, ok);
            return true;
        }

        return false;
    }

    @Override
    public synchronized void sendStateEquiment(DzEquipment dzEquipment) {
        String eventKey = getEvent(SocketMessageType.DEVICE_STATUS, dzEquipment.getOrderNo(), dzEquipment.getLineNo());
        boolean isSendEvend = getIsSendEvend(eventKey);
        if (isSendEvend) {
            Result sendBase = getSendBase(dzEquipment, eventKey);
            socketServerTemplate.sendMessage(SocketMessageType.DEVICE_STATUS, eventKey, sendBase);
        }

    }

    @Override
    public void getWorkingFlow(ReqWorkQrCodeOrder qrCode) {
        //推送报工看板追溯数据
        String eventKey = getEvent(SocketMessageType.WORKPIECE_POSITION, qrCode.getOrderNo(), qrCode.getLineNo());
        boolean isSendEvend = getIsSendEvend(eventKey);
        if (isSendEvend) {
            List<String> code = Arrays.asList(qrCode.getQrCode());
            Long lineId = qrCode.getLineId();
            Long orderId = qrCode.getOrderId();
            List<ResponseWorkStationBg> workpiecePosition = dzWorkingFlowService.getPosition(code, orderId, lineId);
            if (CollectionUtils.isNotEmpty(workpiecePosition)) {
                ResponseWorkStationBg responseWorkStation = workpiecePosition.get(0);
                JCEquimentBase jcEquimentBase = new JCEquimentBase();
                jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_WORK_REPORT_INFORMATION_SINGLE.getInfo());
                jcEquimentBase.setData(responseWorkStation);
                Result<JCEquimentBase> ok = Result.ok(jcEquimentBase);
                socketServerTemplate.sendMessage(SocketMessageType.WORKPIECE_POSITION, eventKey, ok);
            }
        }

        //推送人工打磨台2数据追踪
        String eventKey1 = getEvent(SocketMessageType.get_maerbiao_record, qrCode.getOrderNo(), qrCode.getLineNo());
        boolean isSendEvend1 = getIsSendEvend(eventKey1);
        if(isSendEvend1){
            Long orderId = qrCode.getOrderId();
            if(orderId==11 || orderId==12 || orderId==13 || orderId==14 || orderId==15){
                QueryWrapper<DzWorkStationManagement>stationWrapper=new QueryWrapper<>();
                stationWrapper.eq("order_id",orderId).like("station_name","人工打磨").orderByDesc("sort_code");
                List<DzWorkStationManagement> managements = stationManagementService.selectList(stationWrapper);
                if(CollectionUtils.isEmpty(managements)){
                    if(log.isErrorEnabled()){
                        log.error("人工打磨检测看板实时监控数据推送异常,请检查后台配置，未查询到相关人工打磨台工位");
                        return;
                    }
                }
                DzWorkStationManagement dzWorkStationManagement = managements.get(0);
                if(!qrCode.getStationId().equals(dzWorkStationManagement.getStationId())){
                    return;
                }
                this.sendDetectionMonitor(qrCode.getOrderNo(), qrCode.getLineNo(), qrCode.getQrCode());
            }
        }
    }

    /**
     * 发送停机记录
     *
     * @param dowmSum
     */
    @Override
    public void senddeviceStopStatusPush(SocketDowmSum dowmSum) {
        String eventKey = getEvent(SocketMessageType.SHUT_DOWN_TIMES, dowmSum.getOrderNo(), dowmSum.getLineNo());
        boolean isSendEvend = getIsSendEvend(eventKey);
        if (isSendEvend) {
            Result sendBase = getSendBaseRunState(dowmSum);
            socketServerTemplate.sendMessage(SocketMessageType.SHUT_DOWN_TIMES, eventKey, sendBase);
        }


    }

    @Override
    public Result getSendBaseRunState(SocketDowmSum dz) {
        SocketDowmSum dowmSum = new SocketDowmSum();
        dowmSum.setDownSum(dz.getDownSum());
        dowmSum.setEquimentId(dz.getEquimentId());
        JCEquimentBase jcEquimentBase = new JCEquimentBase();
        jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_DOWN_NUMBER.getInfo());
        jcEquimentBase.setData(dowmSum);
        Result<JCEquimentBase> ok = Result.ok(jcEquimentBase);
        return ok;
    }

    @Override
    public void dzRefresh(String msg) {
        String eventKey = getEvent(SocketMessageType.GET_VERSION_PUSH_REFRESH, "", "");
        boolean isSendEvend = getIsSendEvend(eventKey);
        if (isSendEvend) {
            JCEquimentBase jcEquimentBase = new JCEquimentBase();
            jcEquimentBase.setData(msg);
            jcEquimentBase.setType(DeviceSocketSendStatus.REFRESH.getInfo());
            Result<JCEquimentBase> ok = Result.ok(jcEquimentBase);
            socketServerTemplate.sendMessage(SocketMessageType.GET_VERSION_PUSH_REFRESH, eventKey, ok);
        }

    }

    @Override
    public void sendRabbitmqRealTimeLogs(List<RabbitmqMessage> rabbitmqMessageList) {
        for (RabbitmqMessage rabbitmqMessage : rabbitmqMessageList) {
            Message message = MessageBuilder.withBody(JSONObject.toJSONString(rabbitmqMessage).getBytes(StandardCharsets.UTF_8))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding(StandardCharsets.UTF_8.name())
                    .setMessageId(UUID.randomUUID().toString()).build();
            localRabbitTemplate.send(directRealTimeEquipmentExchange, directRealTimeEquipmentRouting, message);
        }
    }

    @Override
    public boolean sendSanYiDetectionCurve(DzWorkpieceData dzWorkpieceData) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String eventKey = getEvent(SocketMessageType.GET_DETECTION_LINE_CHART, dzWorkpieceData.getOrderNo(), dzWorkpieceData.getLineNo());
        boolean isSendEvend = getIsSendEvend(eventKey);
        if (isSendEvend) {
            GetDetectionLineChartDo charts = lineDataService.charts(dzWorkpieceData);
            charts.setProductName(dzWorkpieceData.getName());
            JCEquimentBase jcEquimentBase = new JCEquimentBase();
            jcEquimentBase.setData(charts);
            jcEquimentBase.setType(DeviceSocketSendStatus.GET_DETECTION_LINE_CHART.getInfo());
            socketServerTemplate.sendMessage(SocketMessageType.GET_DETECTION_LINE_CHART, eventKey, Result.ok(jcEquimentBase));
            return true;
        }
        return false;

    }

    @Override
    public boolean sendSingleProbe(DzWorkpieceData dzWorkpieceData) {
        try {
            String eventKey = getEvent(SocketMessageType.GET_DETECTION_ONE, dzWorkpieceData.getOrderNo(), dzWorkpieceData.getLineNo());
            //缓存保存了检测项的名称 检测字段 和检测结果字段
            //根据产品查询检测项
            //根据检测项 查询指定检测项的值和 检测结果  检测时间
            QueryWrapper<DzProductDetectionTemplate> wrapper = new QueryWrapper<DzProductDetectionTemplate>()
                    .eq("product_no", dzWorkpieceData.getProductNo())
                    .eq("is_show", 0)
                    .eq("order_no", dzWorkpieceData.getOrderNo())
                    .eq("line_no", dzWorkpieceData.getLineNo());
            List<DzProductDetectionTemplate> templates = dzProductDetectionTemplateMapper.selectList(wrapper);
            if (templates.size() == 0) {
                log.error("产品检测项单项检测值推送，产品没有绑定检测项,产品编号:{}", dzWorkpieceData);
                return false;
            }
            DzProductDetectionTemplate dzProductDetectionTemplate = templates.get(0);
            String tableColCon = dzProductDetectionTemplate.getTableColCon();//检测项name
            String tableColVal = dzProductDetectionTemplate.getTableColVal();//检测项 表格字段值
            Object tableColValData = getFieldValueByName(tableColVal, dzWorkpieceData);
            Object outOkValData = dzWorkpieceData.getOutOk28();
            GetDetectionOneDo getDetectionOneDo = new GetDetectionOneDo();
            //1.产品编号
            getDetectionOneDo.setProducBarcode(dzWorkpieceData.getProducBarcode());
            getDetectionOneDo.setProductNo(dzWorkpieceData.getProductNo());
            //2.产品名称
            getDetectionOneDo.setProductName(dzWorkpieceData.getName());
            //3.检测时间
            getDetectionOneDo.setDetectorTime(dateUtil.dateFormatToStingYmdHms(dzWorkpieceData.getDetectorTime()));
            //3.产品检测项名称
            getDetectionOneDo.setTableColCon(tableColCon);
            //4.产品检测项值
            getDetectionOneDo.setDetectValue(tableColValData != null ? tableColValData.toString() : null);
            //5.产品检测结果
            getDetectionOneDo.setDetectOutOk(outOkValData != null ? Integer.valueOf(outOkValData.toString()) : null);
            JCEquimentBase jcEquimentBase = new JCEquimentBase();
            jcEquimentBase.setData(getDetectionOneDo);
            jcEquimentBase.setType(DeviceSocketSendStatus.GET_DETECTION_ONE.getInfo());
            socketServerTemplate.sendMessage(SocketMessageType.GET_DETECTION_ONE, eventKey, Result.ok(jcEquimentBase));
            return true;
        } catch (Exception e) {
            log.error("单项检测记录推送异常,dzWorkpieceData:{}", dzWorkpieceData, e);
            return false;
        }
    }

    @Override
    public boolean sendToolDetection(RabbitmqMessage rabbitmqMessage, Result result) {
        try {
            String eventKey = getEvent(SocketMessageType.TOOL_TEST_DATA, rabbitmqMessage.getOrderCode(), rabbitmqMessage.getLineNo());
            boolean isSendEvend = getIsSendEvend(eventKey);
            if (isSendEvend) {
                socketServerTemplate.sendMessage(SocketMessageType.TOOL_TEST_DATA, eventKey, result);
            }
            return true;
        } catch (Exception e) {
            log.error("单项检测记录推送异常,dzWorkpieceData:{}", result, e);
            return false;
        }
    }

    @Override
    public void sendSysRealTimeLogs(SysRealTimeLogs sysRealTimeLogs) {
        try {
            String js = JSONObject.toJSONString(sysRealTimeLogs);
            RabbitmqMessage rabbitmqMessage = new RabbitmqMessage();
            rabbitmqMessage.setMessage(js);
            rabbitmqMessage.setClientId(LogType.logType);
            String toJSONString = JSONObject.toJSONString(rabbitmqMessage);
            CorrelationData correlationData = new CorrelationData(snowflakeUtil.nextId() + "");
            MessageProperties messageProperties = new MessageProperties();
            Message message = new Message(toJSONString.getBytes("UTF-8"), messageProperties);
            correlationData.setReturnedMessage(message);
            localRabbitTemplate.send(directRealTimeEquipmentExchange, directRealTimeEquipmentRouting, message);
            log.debug("发送日志信息到队列有队列处理 {}", toJSONString);
        } catch (Throwable throwable) {
            log.error("发送日志信息到队列有队列处理异常:{}", throwable.getMessage(), throwable);
        }

    }

    @Override
    public void sendMomReceiveMaterial(MomReceiveMaterial dzOrderCheck) {
        String orderCode = dzOrderCheck.getOrderNo();
        String lineNo = dzOrderCheck.getLineNo();
        String eventKey = getEvent(SocketMessageType.GET_Material_Information, orderCode, lineNo);
        boolean isSendEvend = getIsSendEvend(eventKey);
        if (isSendEvend) {
            JCEquimentBase jcEquimentBase = new JCEquimentBase();
            jcEquimentBase.setData(dzOrderCheck);
            jcEquimentBase.setType(DeviceSocketSendStatus.Material_Information.getInfo());
            Result<JCEquimentBase> ok = Result.ok(jcEquimentBase);
            socketServerTemplate.sendMessage(SocketMessageType.GET_Material_Information, eventKey, ok);
        }
    }

    @Override
    public void sendMomOrderRef(MonOrder momOrder, int i) {
        try {
            if (true) {
                String eventKey = getEvent(SocketMessageType.GET_MOM_ORDER_STATE, "HT", "HT");
                boolean isSendEvend = getIsSendEvend(eventKey);
                if (isSendEvend) {
                    JCEquimentBase jcEquimentBase = new JCEquimentBase();
                    jcEquimentBase.setData(momOrder);
                    if (i == 1) {
                        jcEquimentBase.setType(DeviceSocketSendStatus.GET_MOM_ORDER_STATE.getInfo());
                    } else if (i == 2) {
                        jcEquimentBase.setType(DeviceSocketSendStatus.GET_MOM_ORDER_QUANTITY.getInfo());
                    }
                    socketServerTemplate.sendMessage(SocketMessageType.GET_MOM_ORDER_STATE, eventKey, Result.ok(jcEquimentBase));
                }
            }
            if (true) {
                OrderIdLineId lineId = cacheService.getOrderIdLineId(momOrder.getOrderId(), momOrder.getLineId());
                String eventKey = getEvent(SocketMessageType.GET_MOM_ORDER_STATE, lineId.getOrderNo(), lineId.getLienNo());
                boolean isSendEvend = getIsSendEvend(eventKey);
                if (isSendEvend) {
                    JCEquimentBase jcEquimentBase = new JCEquimentBase();
                    jcEquimentBase.setData(momOrder);
                    if (i == 1) {
                        jcEquimentBase.setType(DeviceSocketSendStatus.GET_MOM_ORDER_STATE.getInfo());
                    } else if (i == 2) {
                        jcEquimentBase.setType(DeviceSocketSendStatus.GET_MOM_ORDER_QUANTITY.getInfo());
                    }
                    socketServerTemplate.sendMessage(SocketMessageType.GET_MOM_ORDER_STATE, eventKey, Result.ok(jcEquimentBase));
                }
            }
        } catch (Throwable throwable) {
            log.error("发送订单状态到页面错误：{}", throwable.getMessage(), throwable);
        }
    }

    /**
     * Q,2,1004,A,MOM订单号,物料号,工序号,托盘编号,数量
     * <p>
     * Q,2,1004,A,物料号,工序号,托盘编号,数量,MOM订单号,订单号,产线号
     *
     * @param msg
     */
    @Override
    public void pushFrdiJson(String[] msg) {
        String dateStr = DateUtil.getDateStr(new Date());
        String q22 = msg[2]; // 指令类型
        String q3 = msg[3];  // A,B,C
        String materialNo = msg[4];  // 物料号
        String workNo = msg[5]; //工序号
        String palletNo = msg[6]; //托盘编号
        String prodCount = msg[7];   //   数量
        String momOrderNo = msg[8];     //  MOM订单号
        String orderNo = msg[9];  // 订单号
        String lineNo = msg[10]; // 产线号
        log.warn("扫码信息 指令类型 ：{},小车 ：{},物料号 ：{},工序号 ：{},托盘编号 ：{},数量 ：{},MOM订单号 ：{},订单号 ：{},产线号 ：{}",
                q22, q3, materialNo, workNo, palletNo, prodCount, momOrderNo, orderNo, lineNo);
        FridJson fridJson = new FridJson();
        fridJson.setMomOrderNo(momOrderNo);
        fridJson.setMaterialNo(materialNo);
        fridJson.setWorkNo(workNo);
        fridJson.setPalletNo(palletNo);
        fridJson.setProdCount(prodCount);
        fridJson.setScanningTime(dateStr);
        fridJson.setOrderNo(orderNo);
        fridJson.setLineNo(lineNo);
        FridTypeCar fridTypeCar = new FridTypeCar();
        fridTypeCar.setBasketType(q3);
        fridTypeCar.setFridAnalysis(fridJson);
        String eventKey = getEvent(SocketMessageType.GET_Material_SEARCH_FRID, orderNo, lineNo);
        boolean isSendEvend = getIsSendEvend(eventKey);
        if (isSendEvend) {
            JCEquimentBase jcEquimentBase = new JCEquimentBase();
            jcEquimentBase.setData(fridTypeCar);
            jcEquimentBase.setType(DeviceSocketSendStatus.FRID_ANYS.getInfo());
            Result<JCEquimentBase> ok = Result.ok(jcEquimentBase);
            socketServerTemplate.sendMessage(SocketMessageType.GET_Material_SEARCH_FRID, eventKey, ok);
        }
    }

    /**
     * Q,2,1005,A,原始信息
     * Q,2,1005,A,订单号,产线号,原始信息    新
     *
     * @param msg
     */
    @Override
    public void pushFridOld(String[] msg) {
        String cmdType = msg[2]; // 指令类型
        String carType = msg[3];  // A,B,C
        String orderNo = msg[4];  //订单号
        String lineNo = msg[5];   //产线号
        String lodMsg = msg[6];   //原始信息

        FridTypeCar fridTypeCar = new FridTypeCar();
        fridTypeCar.setBasketType(carType);
        fridTypeCar.setFridAnalysis(lodMsg);
        String eventKey = getEvent(SocketMessageType.GET_Material_SEARCH_FRID, orderNo, lineNo);
        boolean isSendEvend = getIsSendEvend(eventKey);
        if (isSendEvend) {
            JCEquimentBase jcEquimentBase = new JCEquimentBase();
            jcEquimentBase.setData(fridTypeCar);
            jcEquimentBase.setType(DeviceSocketSendStatus.FRID_OLD.getInfo());
            Result<JCEquimentBase> ok = Result.ok(jcEquimentBase);
            socketServerTemplate.sendMessage(SocketMessageType.GET_Material_SEARCH_FRID, eventKey, ok);
        }
    }

    @Override
    public void sendInputQrCode(String orderNo, String lineNo) {
        String eventKey = getEvent(SocketMessageType.GET_QRCODE, orderNo, lineNo);
        boolean isSendEvend = getIsSendEvend(eventKey);
        if (isSendEvend) {
            QrCodeType qrCode = new QrCodeType();
            qrCode.setSignal("1");
            JCEquimentBase base = new JCEquimentBase();
            base.setType(DeviceSocketSendStatus.QR_CODE_INPUT.getInfo());
            base.setData(qrCode);
            Result<JCEquimentBase> ok = Result.ok(base);
            socketServerTemplate.sendMessage(SocketMessageType.GET_QRCODE, eventKey, ok);
        }
    }

    @Override
    public boolean sendDetectionByMachine(DzWorkpieceData dzWorkpieceData) {
        String eventKey = getEvent(SocketMessageType.GET_DETECTION_BY_MACHINE, dzWorkpieceData.getOrderNo(), dzWorkpieceData.getLineNo());
        boolean isSendEvend = getIsSendEvend(eventKey);
        if (isSendEvend) {
            SelectTrendChartDo charts = lineDataService.getCharts(dzWorkpieceData);
            charts.setProductName(dzWorkpieceData.getName());
            JCEquimentBase jcEquimentBase = new JCEquimentBase();
            jcEquimentBase.setData(charts);
            jcEquimentBase.setType(DeviceSocketSendStatus.DETECTION_BY_MACHINE.getInfo());
            socketServerTemplate.sendMessage(SocketMessageType.GET_DETECTION_BY_MACHINE, eventKey, Result.ok(jcEquimentBase));
            return true;
        }
        return false;
    }

    @Override
    public void sendRabbitmqRealTimeLogsPush(List<RabbitmqMessage> rabbitmqMessageList) {
        for (RabbitmqMessage rabbitmqMessage : rabbitmqMessageList) {
            String message = rabbitmqMessage.getMessage();
            if (!StringUtils.isEmpty(message)) {
                String[] split = message.split("\\|");
                if (split.length < 2) {
                    break;
                }
//            指令信息
                String cmd = split[0];
                if (cmd.equals(EqMentStatus.CMD_ROB_RUN_INFO)) {
                    String deviceItemValue = org.apache.commons.lang3.StringUtils.strip(split[1], "[");
                    deviceItemValue = deviceItemValue.substring(0, deviceItemValue.length() - 1);
                    String[] msg = deviceItemValue.split("TT");
                    if (msg.length < 2) {
                        break;
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
                    this.sendReatimLogs(realTimeLogs);
                }

            }
        }
    }


    @Async("asyncTaskExecutor")
    @Override
    public void sendUploadQualityParam(DzWorkpieceData dzWorkpieceData) {
        String url = "";
        try {
            String orderNo = dzWorkpieceData.getOrderNo();
            String lineNo = dzWorkpieceData.getLineNo();
            log.info("发送检测记录到单岛进行报工：订单：{},产线：{}", orderNo, lineNo);
            Map<String, String> mapIps = mapConfig.getMaps();
            String plcIp = mapIps.get(orderNo + lineNo);
            if (CollectionUtils.isNotEmpty(mapIps) && !StringUtils.isEmpty(plcIp)) {
                url = "http://" + plcIp + ":8107/api/receive/data/detected/data";
            }
            ResponseEntity<Result> response = restTemplate.postForEntity(url, dzWorkpieceData, Result.class);
            Result body = response.getBody();
            Integer code = body.getCode();
            if (0 != code) {
                DzWaitCheckRes checkRes = new DzWaitCheckRes();
                checkRes.setUrl(url);
                checkRes.setReqParms(JSONObject.toJSONString(dzWorkpieceData));
                checkRes.setResParms(JSONObject.toJSONString(body));
                checkResMapper.insert(checkRes);
            }
            log.info("发送检测记录到单岛 订单：{}, IP: {} 返回结果：{}", orderNo, url, response.getBody());
        } catch (Throwable e) {
            DzWaitCheckRes checkRes = new DzWaitCheckRes();
            checkRes.setUrl(url);
            checkRes.setReqParms(JSONObject.toJSONString(dzWorkpieceData));
            checkRes.setResParms(e.getMessage());
            checkResMapper.insert(checkRes);
            log.error("发送检测数据 dzWorkpieceData: {} 据到sanymom,  发送错误：{}", JSONObject.toJSONString(dzWorkpieceData), e.getMessage(), e);
        }
    }

    @Override
    public boolean sendIntelligentDetection(DzWorkpieceData dzWorkpieceData) throws Exception {
        //TODO
        String eventKey = getEvent(SocketMessageType.get_intelligent_detection, dzWorkpieceData.getOrderNo(), dzWorkpieceData.getLineNo());
        boolean isSendEvend = getIsSendEvend(eventKey);
        if (isSendEvend) {
            JCEquimentBase jcEquimentBase = new JCEquimentBase();

            if (dzWorkpieceData != null) {
                Map<String, Object> result = dzProductService.getIntelligentDetection(dzWorkpieceData);
                jcEquimentBase.setData(result);
            }
            jcEquimentBase.setType(DeviceSocketSendStatus.Get_Intelligent_Detection.getInfo());
            socketServerTemplate.sendMessage(SocketMessageType.get_intelligent_detection, eventKey, Result.ok(jcEquimentBase));
            return true;
        }
        return false;
    }


    public Result getSendBase(DzEquipment dzEquipment, String eventKey) {
        String currentLocation = dzEquipment.getCurrentLocation();
        String db527 = dzEquipment.getB527();
        String db526 = dzEquipment.getB526();
        String b527 = "";
        if (!StringUtils.isEmpty(db527)) {
            b527 = org.apache.commons.lang3.StringUtils.strip(db527, "[]");
            b527 = b527.replaceAll(",", ":");
            b527 = b527.replaceAll(Matcher.quoteReplacement("$"), "\\/");
        }
        String b526 = "";
        if (!StringUtils.isEmpty(db526)) {
            b526 = org.apache.commons.lang3.StringUtils.strip(db526, "[]");
            b526 = b526.replaceAll(",", ":");
            b526 = b526.replaceAll(Matcher.quoteReplacement("$"), "\\/");
        }
        MachiningMessageStatus statusMessage = new MachiningMessageStatus();
        if (!StringUtils.isEmpty(currentLocation)) {
            String[] split = currentLocation.split(",");
            if (split != null && split.length >= 3) {
                String x = split[0];
                if (!x.equals("0.000")) {
                    statusMessage.setX(x);
                }
                String y = split[1];
                if (!y.equals("0.000")) {
                    statusMessage.setY(y);
                }
                String z = split[2];
                if (!z.equals("0.000")) {
                    statusMessage.setZ(z);
                }
            }
        }
        statusMessage.setEquipmentName(dzEquipment.getEquipmentName());
        statusMessage.setEquimentId(dzEquipment.getId().toString());
        statusMessage.setOperatorMode(dzEquipment.getOperatorMode());
        statusMessage.setConnectState(dzEquipment.getConnectState());
        statusMessage.setRunStatus(dzEquipment.getRunStatus());
        statusMessage.setEmergencyStatus(dzEquipment.getEmergencyStatus());
        statusMessage.setAlarmStatus(dzEquipment.getAlarmStatus());
        statusMessage.setSpeedRatio(dzEquipment.getSpeedRatio());
        statusMessage.setA541(dzEquipment.getA541());
        statusMessage.setB809(dzEquipment.getB809());
        statusMessage.setA812(dzEquipment.getA812());
        statusMessage.setB526(b526);
        statusMessage.setB527(b527);
        statusMessage.setGasFlow(dzEquipment.getGasFlow());
        statusMessage.setMachiningTime(dzEquipment.getMachiningTime());
        statusMessage.setHeadPositionUd(dzEquipment.getHeadPositionUd());
        statusMessage.setHeadPostionLr(dzEquipment.getHeadPostionLr());
        statusMessage.setSpeedOfMainShaft(dzEquipment.getSpeedOfMainShaft());
        statusMessage.setCleanTime(dzEquipment.getCleanTime());
        statusMessage.setFeedSpeed(dzEquipment.getFeedSpeed());
        String systemConfig = cacheService.getSystemConfigDepart();
        if (systemConfig.equals(SysConfigDepart.SANY)) {
            String orderNum = dzEquipment.getOrderNo();
            if (orderNum.equals("DZ-1955") || orderNum.equals("DZ-1956")) {
                setWorkState(statusMessage);
                String equipmentNo = dzEquipment.getEquipmentNo();
                if ("02".equals(equipmentNo)) {
                    MachiningMessageStatus status = (MachiningMessageStatus) redisUtil.get(RedisKey.socketIoHandler_accqDzEquipmentService_getEquimentStateX + orderNum + "A2");
                    if (status != null) {
                        status.setConnectState(statusMessage.getConnectState());
                        status.setRunStatus(statusMessage.getRunStatus());
                        JCEquimentBase jcEquimentBase = new JCEquimentBase();
                        jcEquimentBase.setData(status);
                        jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_STATE.getInfo());
                        Result<JCEquimentBase> ok = Result.ok(jcEquimentBase);
                        socketServerTemplate.sendMessage(SocketMessageType.DEVICE_STATUS, eventKey, ok);
                    }
                }
            }
        }
        JCEquimentBase jcEquimentBase = new JCEquimentBase();
        jcEquimentBase.setData(statusMessage);
        jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_STATE.getInfo());
        return Result.ok(jcEquimentBase);
    }

    public void setWorkState(MachiningMessageStatus status) {
        String runStatus = status.getRunStatus();
        String connectState = status.getConnectState();
        String alarmStatus = status.getAlarmStatus();
        status.setWorkStatus("关机");
        if (!StringUtils.isEmpty(runStatus)) {
            if (!StringUtils.isEmpty(connectState) && connectState.equals("联机")) {
                if (!StringUtils.isEmpty(alarmStatus) && "报警".equals(alarmStatus)) {
                    status.setWorkStatus("故障");
                    return;
                } else {
                    if (runStatus.equals("生产")) {
                        status.setWorkStatus("作业");
                        return;
                    } else {
                        status.setWorkStatus("待机");
                        return;
                    }
                }
            }
        }
        return;
    }

    /* 根据属性名获取属性值
     * */
    private static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(o, new Object[]{});
            return value;
        } catch (Exception e) {

            return null;
        }
    }

    @Override
    public boolean sendDetection(String orderNo, String lineNo, String Id) {
        String eventKey = getEvent(SocketMessageType.get_detection_record, orderNo, lineNo);
        boolean isSendEvend = getIsSendEvend(eventKey);
        if (isSendEvend) {
            Object o = redisUtil.get(RedisKey.MA_ER_BIAO_CHECK_HISTORY + orderNo + lineNo);
            Result<JCEquimentBase<ProDetection<Map<String, Object>>>> data = accDetectorDataService.getDetectionRecordMomSingle(Id, orderNo, lineNo);
            String productNo = String.valueOf(data.getData().getData().getTableData().get("productNo"));
            if (productNo.equals(o.toString())) {
                data.setRef(false);
            } else {
                data.setRef(true);
            }
            redisUtil.set(RedisKey.MA_ER_BIAO_CHECK_HISTORY + orderNo + lineNo, productNo);
            socketServerTemplate.sendMessage(SocketMessageType.get_detection_record, eventKey, data);
            return true;
        }
        return false;
    }

    @Override
    public boolean sendDetectionMonitor(String orderNo, String lineNo, String qrCode) {
        String eventKey = getEvent(SocketMessageType.get_maerbiao_record, orderNo, lineNo);
        boolean isSendEvend = getIsSendEvend(eventKey);
        if(isSendEvend){
            Result data = accDetectorDataService.getMaErBiaoDetectionMonitor(orderNo, lineNo, qrCode);
            data.setRef(true);
            socketServerTemplate.sendMessage(SocketMessageType.get_maerbiao_record, eventKey, data);
            return true;
        }
        return false;
    }


}
