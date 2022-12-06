package com.dzics.data.acquisition.netty.subscribe;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.common.model.response.Result;
import com.dzics.data.acquisition.netty.SocketMessageType;
import com.dzics.data.acquisition.netty.server.SocketIoHandler;
import com.dzics.data.acquisition.service.AccRealTimeLogsService;
import com.dzics.data.acquisition.util.SubscribeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * @Classname LogsSubScribe
 * @Description 日志订阅
 * @Date 2022/6/24 8:44
 * @Created by NeverEnd
 */
@Slf4j
@Component
public class LogsSubScribe {
    private final AccRealTimeLogsService accRealTimeLogsService;
    private final SocketIoHandler socketIoHandler;

    public LogsSubScribe(AccRealTimeLogsService accRealTimeLogsService, SocketIoHandler socketIoHandler) {
        this.accRealTimeLogsService = accRealTimeLogsService;
        this.socketIoHandler = socketIoHandler;
    }

    /**
     * 订阅设备日志
     */
    @OnEvent(value = SocketMessageType.DEVICE_LOG)
    public void getDeviceLogs(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        long a = System.currentTimeMillis();
        log.info("IP:{},开始订阅 [ 设备日志 ]->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        boolean check = SubscribeUtil.checkOutParms(data);
        if (check) {
            String orderNo = data.getOrderNo();
            String lineNo = data.getLineNo();
            String deviceType = data.getDeviceType();
            Result debug = accRealTimeLogsService.getLogDebug(orderNo, lineNo, deviceType);
            client.sendEvent(SocketMessageType.DEVICE_LOG, debug);
            socketIoHandler.addEvent(client, SocketMessageType.DEVICE_LOG + orderNo + lineNo + deviceType, "设备日志");
            long b = System.currentTimeMillis();
            log.info("订阅设备日志,订单号:{},产线号:{},耗时:{}毫秒", orderNo, lineNo, (b - a));
        } else {
            log.warn("IP:{},socket 参数传递错误：{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        }
    }

    /**
     * 取消订阅设备日志
     */
    @OnEvent(value = SocketMessageType.UN_DEVICE_LOG)
    public void unGetDeviceLogs(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        log.info("IP:{},取消 [ 订阅设备 ] 日志:->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        String orderNo = data.getOrderNo();
        String lineNo = data.getLineNo();
        String deviceType = data.getDeviceType();
        socketIoHandler.unEvent(client, SocketMessageType.DEVICE_LOG + orderNo + lineNo + deviceType, "设备日志");
    }


    /**
     * 订阅设备告警日志
     *
     */
    @OnEvent(value = SocketMessageType.DEVICE_WARN_LOG)
    public void getDeviceWarnLogs(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        long a = System.currentTimeMillis();
        log.info("IP:{},开始订阅 [ 设备日志 ]->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        boolean check = SubscribeUtil.checkOutParms(data);
        if (check) {
            String orderNo = data.getOrderNo();
            String lineNo = data.getLineNo();
            String deviceType = data.getDeviceType();
            Result warn = accRealTimeLogsService.getLogWarn(orderNo, lineNo, deviceType);
            client.sendEvent(SocketMessageType.DEVICE_WARN_LOG, warn);
            socketIoHandler.addEvent(client, SocketMessageType.DEVICE_WARN_LOG + orderNo + lineNo + deviceType, "设备日志");
            long b = System.currentTimeMillis();
            log.info("订阅设备日志,订单号:{},产线号:{},耗时:{}毫秒", orderNo, lineNo, (b - a));
        } else {
            log.warn("IP:{},socket 参数传递错误：{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        }
    }

    /**
     * 取消订阅设告警日志
     *
     */
    @OnEvent(value = SocketMessageType.UN_DEVICE_WARN_LOG)
    public void unGetDeviceWarnLogs(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        log.info("IP:{},取消 [ 订阅设备 ] 日志:->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        String orderNo = data.getOrderNo();
        String lineNo = data.getLineNo();
        String deviceType = data.getDeviceType();
        socketIoHandler.unEvent(client, SocketMessageType.DEVICE_WARN_LOG + orderNo + lineNo + deviceType, "设备日志");
    }

}
