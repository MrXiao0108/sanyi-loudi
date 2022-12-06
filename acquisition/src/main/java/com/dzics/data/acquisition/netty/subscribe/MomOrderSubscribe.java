package com.dzics.data.acquisition.netty.subscribe;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.dzics.common.enums.DeviceSocketSendStatus;
import com.dzics.common.model.constant.MomProgressStatus;
import com.dzics.common.model.entity.MonOrder;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.common.model.response.JCEquimentBase;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.MomOrderService;
import com.dzics.data.acquisition.netty.SocketMessageType;
import com.dzics.data.acquisition.netty.server.SocketIoHandler;
import com.dzics.data.acquisition.util.SubscribeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Classname MomOrderSubscribe
 * @Description MOM订单状态订阅
 * @Date 2022/6/24 8:52
 * @Created by NeverEnd
 */
@Component
@Slf4j
public class MomOrderSubscribe {
    private final SocketIoHandler socketIoHandler;
    private final MomOrderService momOrderService;

    public MomOrderSubscribe(SocketIoHandler socketIoHandler, MomOrderService momOrderService) {
        this.socketIoHandler = socketIoHandler;
        this.momOrderService = momOrderService;
    }

    /**
     * MOM订单实时状态推送
     *
     */
    @OnEvent(value = SocketMessageType.GET_MOM_ORDER_STATE)
    public void getMomOrderState(SocketIOClient client, GetOrderNoLineNo data) {
        long a = System.currentTimeMillis();
        log.info("IP:{},开始订阅 [ 订单实时状态 ]->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        boolean check = SubscribeUtil.checkOutParms(data);
        if (check) {
            String orderNo = data.getOrderNo();
            String lineNo = data.getLineNo();
            if ("HT".equals(orderNo)) {
//                后台
                socketIoHandler.addEvent(client, SocketMessageType.GET_MOM_ORDER_STATE + orderNo + lineNo, "订单实时状态");
            } else {
                MonOrder order = momOrderService.getMomOrder(orderNo, lineNo, MomProgressStatus.LOADING);
                JCEquimentBase<MonOrder> jcEquimentBase = new JCEquimentBase<>();
                jcEquimentBase.setType(DeviceSocketSendStatus.GET_MOM_ORDER_STATE.getInfo());
                jcEquimentBase.setData(order);
                Result<JCEquimentBase<MonOrder>> ok = Result.ok(jcEquimentBase);
                client.sendEvent(SocketMessageType.GET_MOM_ORDER_STATE, ok);
                socketIoHandler.addEvent(client, SocketMessageType.GET_MOM_ORDER_STATE + orderNo + lineNo, "订单实时状态");
            }
            long b = System.currentTimeMillis();
            log.info("订阅订单实时状态,订单号:{},产线号:{},耗时:{}毫秒", orderNo, lineNo, (b - a));
        } else {
            log.warn("IP:{},socket 参数传递错误：{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        }

    }

    /**
     * MOM订单实时状态推送 取消
     *
     */
    @OnEvent(value = SocketMessageType.UN_GET_MOM_ORDER_STATE)
    public void unGetMomOrderState(SocketIOClient client,  GetOrderNoLineNo data) {
        log.info("IP:{},取消 [ 订单实时状态 ]数据:->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        String orderNo = data.getOrderNo();
        String lineNo = data.getLineNo();
        socketIoHandler.unEvent(client, SocketMessageType.GET_MOM_ORDER_STATE + orderNo + lineNo, "订单实时状态");
    }
}
