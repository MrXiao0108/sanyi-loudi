package com.dzics.data.acquisition.netty.subscribe;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.common.model.response.Result;
import com.dzics.data.acquisition.netty.SocketMessageType;
import com.dzics.data.acquisition.netty.server.SocketIoHandler;
import com.dzics.data.acquisition.service.AccStorageLocationService;
import com.dzics.data.acquisition.util.SubscribeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Classname DevcieStatusSubscribe
 * @Description 设备状态订阅
 * @Date 2022/6/24 8:40
 * @Created by NeverEnd
 */
@Component
@Slf4j
public class DevcieStatusSubscribe {

    private final AccStorageLocationService accqDzEquipmentService;
    private final SocketIoHandler socketIoHandler;

    public DevcieStatusSubscribe(AccStorageLocationService accqDzEquipmentService, SocketIoHandler socketIoHandler) {
        this.accqDzEquipmentService = accqDzEquipmentService;
        this.socketIoHandler = socketIoHandler;
    }

    /**
     * 设备状态订阅
     */
    @OnEvent(value = SocketMessageType.DEVICE_STATUS)
    public void getDeviceStatus(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        long a = System.currentTimeMillis();
        log.info("IP：{}，开始订阅 [ 设备状态 ]->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        boolean check = SubscribeUtil.checkOutParms(data);
        if (check) {
            String orderNo = data.getOrderNo();
            String lineNo = data.getLineNo();
//        设备状态
            Result equimentState = accqDzEquipmentService.getEquimentStateX(lineNo, orderNo);
            client.sendEvent(SocketMessageType.DEVICE_STATUS, equimentState);
            socketIoHandler.addEvent(client, SocketMessageType.DEVICE_STATUS + orderNo + lineNo, "设备状态");
            long b = System.currentTimeMillis();
            log.info("设备状态订阅,订单号:{},产线号:{},耗时:{}毫秒", orderNo, lineNo, (b - a));
        } else {
            log.warn("IP:{},socket 参数传递错误：{}", client.getRemoteAddress(), data);
        }

    }

    /**
     * 取消设备状态订阅
     */
    @OnEvent(value = SocketMessageType.UN_DEVICE_STATUS)
    public void unGetDeviceStatus(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        log.info("IP:{},取消 [ 设备状态 ] 订阅:->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        String orderNo = data.getOrderNo();
        String lineNo = data.getLineNo();
        socketIoHandler.unEvent(client, SocketMessageType.DEVICE_STATUS + orderNo + lineNo, "设备状态");
    }


}
