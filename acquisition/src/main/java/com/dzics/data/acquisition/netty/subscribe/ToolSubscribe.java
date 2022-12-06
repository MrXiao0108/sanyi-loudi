package com.dzics.data.acquisition.netty.subscribe;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.common.model.response.Result;
import com.dzics.data.acquisition.netty.SocketMessageType;
import com.dzics.data.acquisition.netty.server.SocketIoHandler;
import com.dzics.data.acquisition.service.ProductionQuantityService;
import com.dzics.data.acquisition.util.SubscribeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Classname ToolSubscribe
 * @Description 刀具检测订阅
 * @Date 2022/6/24 9:11
 * @Created by NeverEnd
 */
@Component
@Slf4j
public class ToolSubscribe {
    private final ProductionQuantityService productionQuantityService;
    private final SocketIoHandler socketIoHandler;

    public ToolSubscribe(ProductionQuantityService productionQuantityService, SocketIoHandler socketIoHandler) {
        this.productionQuantityService = productionQuantityService;
        this.socketIoHandler = socketIoHandler;
    }

    /**
     * 订阅刀具检测数据
     *
     */
    @OnEvent(value = SocketMessageType.TOOL_TEST_DATA)
    public void getToolTestData(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        long a = System.currentTimeMillis();
        log.info("IP:{},开始订阅 [ 刀具检测数据 ]->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        boolean check = SubscribeUtil.checkOutParms(data);
        if (check) {
            String orderNo = data.getOrderNo();
            String lineNo = data.getLineNo();
//        刀具检测
            Result getToolInfoData = productionQuantityService.getToolInfoData(data.getOrderNo(), data.getLineNo());
            client.sendEvent(SocketMessageType.TOOL_TEST_DATA, getToolInfoData);
            socketIoHandler.addEvent(client, SocketMessageType.TOOL_TEST_DATA + orderNo + lineNo, "刀具检测");
            long b = System.currentTimeMillis();
            log.info("订阅刀具检测数据,订单号:{},产线号:{},耗时:{}毫秒", orderNo, lineNo, (b - a));
        } else {
            log.warn("IP:{},socket 参数传递错误：{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        }

    }

    /**
     * 取消订阅刀具检测数据
     *
     */
    @OnEvent(value = SocketMessageType.UN_TOOL_TEST_DATA)
    public void unGetToolTestData(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        log.info("IP:{},取消 [ 订阅刀具检测 ] 数据:->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        String orderNo = data.getOrderNo();
        String lineNo = data.getLineNo();
        socketIoHandler.unEvent(client, SocketMessageType.TOOL_TEST_DATA + orderNo + lineNo, "刀具检测");
    }
}
