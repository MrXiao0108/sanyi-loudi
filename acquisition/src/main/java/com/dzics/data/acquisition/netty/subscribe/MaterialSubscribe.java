package com.dzics.data.acquisition.netty.subscribe;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.dzics.common.enums.DeviceSocketSendStatus;
import com.dzics.common.model.entity.MomReceiveMaterial;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.common.model.response.JCEquimentBase;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.MomReceiveMaterialService;
import com.dzics.data.acquisition.netty.SocketMessageType;
import com.dzics.data.acquisition.netty.server.SocketIoHandler;
import com.dzics.data.acquisition.util.SubscribeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Classname MaterialSubscribe
 * @Description 接收物料信息订阅  接收FRID信息订阅
 * @Date 2022/6/24 9:21
 * @Created by NeverEnd
 */
@Component
@Slf4j
public class MaterialSubscribe {
    private final SocketIoHandler socketIoHandler;
    private final MomReceiveMaterialService momReceiveMaterialService;
    public MaterialSubscribe(SocketIoHandler socketIoHandler, MomReceiveMaterialService momReceiveMaterialService) {
        this.socketIoHandler = socketIoHandler;
        this.momReceiveMaterialService = momReceiveMaterialService;
    }

    /**
     * 接收物料信息订阅
     */
    @OnEvent(value = SocketMessageType.GET_Material_Information)
    public void getMaterialInformation(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        long a = System.currentTimeMillis();
        log.info("IP:{},开始订阅 [ 接收物料信息 ]->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        boolean check = SubscribeUtil.checkOutParms(data);
        if (check) {
            String orderNo = data.getOrderNo();
            String lineNo = data.getLineNo();
            socketIoHandler.addEvent(client, SocketMessageType.GET_Material_Information + orderNo + lineNo, "接收物料信息");
            long b = System.currentTimeMillis();
            log.info("接收物料信息订阅,订单号:{},产线号:{},耗时:{}毫秒", orderNo, lineNo, (b - a));
        } else {
            log.warn("IP:{},socket 参数传递错误：{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        }

    }

    /**
     * 接收物料信息 取消
     */
    @OnEvent(value = SocketMessageType.UN_GET_Material_Information)
    public void unGetMaterialInformation(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        log.info("IP:{},取消 [ 接收物料信息 ]:->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        if (SubscribeUtil.checkOutParms(data)) {
            String orderNo = data.getOrderNo();
            String lineNo = data.getLineNo();
            socketIoHandler.unEvent(client, SocketMessageType.GET_Material_Information + orderNo + lineNo, "接收物料信息");
        } else {
            log.info("取消 [ 接收物料信息 ] IP:{},socket 参数传递错误：{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        }

    }


    /**
     * 获取未确认物料数据
     */
    @OnEvent(value = SocketMessageType.GET_Material_Information_No_Chceck)
    public void getMaterialInformationNOCheck(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        long a = System.currentTimeMillis();
        log.info("IP:{},开始订阅 [ 获取未确认物料信息 ]->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        boolean b = SubscribeUtil.checkOutParms(data);
        if (b) {
            String orderNo = data.getOrderNo();
            String lineNo = data.getLineNo();
            List<MomReceiveMaterial> dzOrderChecks = momReceiveMaterialService.listNoCheck(orderNo, lineNo);
            JCEquimentBase<List<MomReceiveMaterial>> jcEquimentBase = new JCEquimentBase<>();
            jcEquimentBase.setData(dzOrderChecks);
            jcEquimentBase.setType(DeviceSocketSendStatus.Material_Information.getInfo());
            client.sendEvent(SocketMessageType.GET_Material_Information_No_Chceck, Result.ok(jcEquimentBase));
            long c = System.currentTimeMillis();
            log.info("获取未确认物料数据,订单号:{},产线号:{},耗时:{}毫秒", data.getOrderNo(), data.getLineNo(), (c - a));
        } else {
            log.warn("获取未确认物料数据 IP:{},socket 参数传递错误：{}", client.getRemoteAddress(), data);
        }

    }


    /**
     * 接收FRID信息订阅
     */
    @OnEvent(value = SocketMessageType.GET_Material_SEARCH_FRID)
    public void getMaterialFrid(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        long a = System.currentTimeMillis();
        log.info("IP:{},开始订阅 [ 扫码FRID ]->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        boolean b = SubscribeUtil.checkOutParms(data);
        if (b) {
            String orderNo = data.getOrderNo();
            String lineNo = data.getLineNo();
            socketIoHandler.addEvent(client, SocketMessageType.GET_Material_SEARCH_FRID + orderNo + lineNo, "扫码FRID");
            long c = System.currentTimeMillis();
            log.info("接收FRID信息订阅,订单号:{},产线号:{},耗时:{}毫秒", orderNo, lineNo, (c - a));
        } else {
            log.warn("扫码FRID IP:{},socket 参数传递错误：{}", client.getRemoteAddress(), data);
        }

    }

    /**
     * 接收FRID信息 取消
     */
    @OnEvent(value = SocketMessageType.UN_GET_Material_SEARCH_FRID)
    public void unGetMaterialFrid(SocketIOClient client, AckRequest ackRequest, GetOrderNoLineNo data) {
        log.info("IP:{},取消 [ 扫码FRID ]:->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        boolean b = SubscribeUtil.checkOutParms(data);
        if (b) {
            String orderNo = data.getOrderNo();
            String lineNo = data.getLineNo();
            socketIoHandler.unEvent(client, SocketMessageType.GET_Material_SEARCH_FRID + orderNo + lineNo, "扫码FIRD");
        } else {
            log.warn("取消扫码FRID IP:{},socket 参数传递错误：{}", client.getRemoteAddress(), data);
        }

    }


}
