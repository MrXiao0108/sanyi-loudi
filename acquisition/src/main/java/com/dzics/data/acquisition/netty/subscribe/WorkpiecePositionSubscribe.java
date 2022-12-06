package com.dzics.data.acquisition.netty.subscribe;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.dzics.common.dao.DzWorkingFlowMapper;
import com.dzics.common.enums.DeviceSocketSendStatus;
import com.dzics.common.exception.CustomException;
import com.dzics.common.model.custom.OrderIdLineId;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.common.model.response.JCEquimentBase;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.productiontask.stationbg.ResponseWorkStationBg;
import com.dzics.common.service.DzWorkingFlowService;
import com.dzics.data.acquisition.netty.SocketMessageType;
import com.dzics.data.acquisition.netty.server.SocketIoHandler;
import com.dzics.data.acquisition.service.CacheService;
import com.dzics.data.acquisition.util.SubscribeUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Classname WorkpiecePositionSubscribe
 * @Description 报工位置订阅
 * @Date 2022/6/24 8:57
 * @Created by NeverEnd
 */
@Slf4j
@Component
public class WorkpiecePositionSubscribe {
    private final SocketIoHandler socketIoHandler;
    private final CacheService cacheService;
    private final  DzWorkingFlowMapper dzWorkingFlowMapper;
    private final DzWorkingFlowService dzWorkingFlowService;

    public WorkpiecePositionSubscribe(SocketIoHandler socketIoHandler, CacheService cacheService, DzWorkingFlowMapper dzWorkingFlowMapper, DzWorkingFlowService dzWorkingFlowService) {
        this.socketIoHandler = socketIoHandler;
        this.cacheService = cacheService;
        this.dzWorkingFlowMapper = dzWorkingFlowMapper;
        this.dzWorkingFlowService = dzWorkingFlowService;
    }

    /**
     * 订阅工件位置数据
     *
     */
    @OnEvent(value = SocketMessageType.WORKPIECE_POSITION)
    public void getWorkpiecePosition(SocketIOClient client, GetOrderNoLineNo data) {
        long a = System.currentTimeMillis();
        log.info("IP:{},开始订阅 [ 工件位置数据 ]->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        boolean check = SubscribeUtil.checkOutParms(data);
        if (check) {
            String orderNo = data.getOrderNo();
            String lineNo = data.getLineNo();
            try {
                OrderIdLineId orderIdLineId = cacheService.getOrderNoLineNoId(orderNo, lineNo);
                if (orderIdLineId != null) {
                    Long lineId = orderIdLineId.getLineId();
                    Long orderId = orderIdLineId.getOrderId();
                    PageHelper.startPage(1, 30);
                    List<String> qrCodeX = dzWorkingFlowMapper.getWorkingFlowBigQrCode(orderId, lineId, null, null);
                    PageInfo<String> stringPageInfo = new PageInfo<>(qrCodeX);
                    List<String> qrCode = stringPageInfo.getList();
                    List<ResponseWorkStationBg> workpiecePosition = dzWorkingFlowService.getPosition(qrCode, orderId, lineId);
                    JCEquimentBase<List<ResponseWorkStationBg>> jcEquimentBase = new JCEquimentBase<>();
                    jcEquimentBase.setType(DeviceSocketSendStatus.DEVICE_SOCKET_SEND_WORK_REPORT_INFORMATION_MANY.getInfo());
                    jcEquimentBase.setData(workpiecePosition);
                    Result<JCEquimentBase<List<ResponseWorkStationBg>>> ok = Result.ok(jcEquimentBase);
                    client.sendEvent(SocketMessageType.WORKPIECE_POSITION, ok);
                } else {
                    log.error("根据订单号和产线号没有获取到 [订单ID 和产线ID] 无法获取报工记录：orderNo:{},lineNo:{},orderIdLineId:{}", orderNo, lineNo, orderIdLineId);
                }
            } catch (CustomException e) {
                log.error("IP:{},socket 发送报工数据错误:{}", client.getRemoteAddress(), e.getMessage());
            } catch (Throwable e) {
                log.error("IP:{},socket 发送报工数据错误:{}", client.getRemoteAddress(), e);
            }
            socketIoHandler.addEvent(client, SocketMessageType.WORKPIECE_POSITION + orderNo + lineNo, "工件位置");
            long b = System.currentTimeMillis();
            log.info("订阅工件位置数据,订单号:{},产线号:{},耗时:{}毫秒", orderNo, lineNo, (b - a));
        } else {
            log.warn("IP:{},socket 参数传递错误：{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        }

    }


    /**
     * 取消订阅工件位置数据
     *
     */
    @OnEvent(value = SocketMessageType.UN_WORKPIECE_POSITION)
    public void unGetWorkpiecePosition(SocketIOClient client, GetOrderNoLineNo data) {
        log.info("IP:{},取消 [ 订阅工件位置 ]数据:->data:{}", client.getRemoteAddress(), JSONObject.toJSONString(data));
        String orderNo = data.getOrderNo();
        String lineNo = data.getLineNo();
        socketIoHandler.unEvent(client, SocketMessageType.WORKPIECE_POSITION + orderNo + lineNo, "工件位置");
    }

}
