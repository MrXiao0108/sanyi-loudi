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
 * @Classname ShutdownSumSubscribe
 * @Description 停机次数订阅
 * @Date 2022/6/24 9:00
 * @Created by NeverEnd
 */
@Component
@Slf4j
public class ShutdownSumSubscribe {
    private final SocketIoHandler socketIoHandler;

    public ShutdownSumSubscribe(SocketIoHandler socketIoHandler) {
        this.socketIoHandler = socketIoHandler;
    }

    /**
     * 订阅停机次数
     */
    @OnEvent(value = SocketMessageType.SHUT_DOWN_TIMES)
    public void getShutDownTimes(SocketIOClient client, GetOrderNoLineNo data) {
        long a = System.currentTimeMillis();
        log.info("IP:{},开始订阅 [ 停机次数 ]->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        boolean check = SubscribeUtil.checkOutParms(data);
        if (check) {
            String orderNo = data.getOrderNo();
            String lineNo = data.getLineNo();
            socketIoHandler.addEvent(client, SocketMessageType.SHUT_DOWN_TIMES + orderNo + lineNo, "停机次数");
            long b = System.currentTimeMillis();
            log.info("订阅停机次数,订单号:{},产线号:{},耗时:{}毫秒", orderNo, lineNo, (b - a));
        } else {
            log.warn("IP:{},socket 参数传递错误：{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        }

    }

    /**
     * 取消订阅停机次数
     */
    @OnEvent(value = SocketMessageType.GET_SHUT_DOWN_TIMES)
    public void unGetShutDownTimes(SocketIOClient client, GetOrderNoLineNo data) {
        log.info("IP:{},取消 [ 订阅停机次数 ]:->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        String orderNo = data.getOrderNo();
        String lineNo = data.getLineNo();
        socketIoHandler.unEvent(client, SocketMessageType.SHUT_DOWN_TIMES + orderNo + lineNo, "停机次数");
    }

}
