package com.dzics.data.acquisition.netty.subscribe;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.data.acquisition.netty.SocketMessageType;
import com.dzics.data.acquisition.netty.server.SocketIoHandler;
import com.dzics.data.acquisition.util.SubscribeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Classname CodeOutputSubscribe
 * @Description 订阅二维码输入信息
 * @Date 2022/6/24 9:25
 * @Created by NeverEnd
 */
@Component
@Slf4j
public class CodeOutputSubscribe {
    private final SocketIoHandler socketIoHandler;

    public CodeOutputSubscribe(SocketIoHandler socketIoHandler) {
        this.socketIoHandler = socketIoHandler;
    }

    /**
     * 订阅二维码输入
     */
    @OnEvent(value = SocketMessageType.GET_QRCODE)
    public void getQrcode(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        long a = System.currentTimeMillis();
        log.info("IP:{},开始订阅 [ 二维码输入 ]->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        boolean b = SubscribeUtil.checkOutParms(data);
        if (b) {
            String orderNo = data.getOrderNo();
            String lineNo = data.getLineNo();
            socketIoHandler.addEvent(client, SocketMessageType.GET_QRCODE + orderNo + lineNo, "二维码输入");
            long c = System.currentTimeMillis();
            log.info("接收 二维码输入 信息订阅,订单号:{},产线号:{},耗时:{} 毫秒", orderNo, lineNo, (c - a));
        } else {
            log.warn("二维码输入 IP:{},socket 参数传递错误：{}", client.getRemoteAddress(), data);
        }
    }

    /**
     * 取消订阅二维码输入
     */
    @OnEvent(value = SocketMessageType.UN_GET_QRCODE)
    public void unGetQrCode(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        log.info("IP:{},取消 [ 二维码输入 ]:->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        boolean b = SubscribeUtil.checkOutParms(data);
        if (b) {
            String orderNo = data.getOrderNo();
            String lineNo = data.getLineNo();
            socketIoHandler.unEvent(client, SocketMessageType.GET_QRCODE + orderNo + lineNo, "二维码输入");
        } else {
            log.warn("取消 二维码输入 IP:{},socket 参数传递错误：{}", client.getRemoteAddress(), data);
        }

    }


}
