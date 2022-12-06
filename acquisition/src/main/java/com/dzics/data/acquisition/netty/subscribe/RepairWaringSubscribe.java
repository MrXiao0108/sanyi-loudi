package com.dzics.data.acquisition.netty.subscribe;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.common.model.response.Result;
import com.dzics.data.acquisition.netty.SocketMessageType;
import com.dzics.data.acquisition.netty.server.SocketIoHandler;
import com.dzics.data.acquisition.util.SubscribeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Classname RepairWaringSubscribe
 * @Description 维修报警订阅
 * @Date 2022/6/24 9:13
 * @Created by NeverEnd
 */
@Component
@Slf4j
public class RepairWaringSubscribe {
    private final SocketIoHandler socketIoHandler;

    public RepairWaringSubscribe(SocketIoHandler socketIoHandler) {
        this.socketIoHandler = socketIoHandler;
    }

    /**
     * 维修报警推送  订阅
     *
     */
    @OnEvent(value = SocketMessageType.WARING_ALERT)
    public void getWaringAlert(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        long a = System.currentTimeMillis();
        log.info("IP:{},开始订阅 [ 维修报警 ]->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        boolean check = SubscribeUtil.checkOutParms(data);
        if (check) {
            String orderNo = data.getOrderNo();
            String lineNo = data.getLineNo();
            client.sendEvent(SocketMessageType.WARING_ALERT, Result.ok(false));
            socketIoHandler.addEvent(client, SocketMessageType.WARING_ALERT + orderNo + lineNo, "维修报警");
            long b = System.currentTimeMillis();
            log.info("订阅设备日志,订单号:{},产线号:{},耗时:{}毫秒", orderNo, lineNo, (b - a));
        } else {
            log.warn("IP:{},socket 参数传递错误：{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        }
    }

    /**
     * 取消维修报警推送  订阅
     *
     */
    @OnEvent(value = SocketMessageType.UN_WARING_ALERT)
    public void unGetWaringAlert(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        log.info("IP:{},取消 [ 维修报警 ] 日志:->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        String orderNo = data.getOrderNo();
        String lineNo = data.getLineNo();
       socketIoHandler.unEvent(client, SocketMessageType.WARING_ALERT + orderNo + lineNo, "维修报警");
    }


}
