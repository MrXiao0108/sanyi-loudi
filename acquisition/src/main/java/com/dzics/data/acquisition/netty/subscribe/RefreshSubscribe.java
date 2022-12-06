package com.dzics.data.acquisition.netty.subscribe;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.dzics.common.enums.DeviceSocketSendStatus;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.common.model.response.JCEquimentBase;
import com.dzics.common.model.response.Result;
import com.dzics.data.acquisition.netty.SocketMessageType;
import com.dzics.data.acquisition.netty.server.SocketIoHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Classname RefreshSubscribe
 * @Description 页面刷新订阅
 * @Date 2022/6/24 9:02
 * @Created by NeverEnd
 */
@Component
@Slf4j
public class RefreshSubscribe {
    private final SocketIoHandler socketIoHandler;

    public RefreshSubscribe(SocketIoHandler socketIoHandler) {
        this.socketIoHandler = socketIoHandler;
    }

    /**
     * 版本刷新订阅
     */
    @OnEvent(value = SocketMessageType.GET_VERSION_PUSH_REFRESH)
    public void getVersionPushRefresh(SocketIOClient client,GetOrderNoLineNo data) {
        log.info("IP:{},开始订阅 [ 版本刷新 ]", client.getRemoteAddress());
        JCEquimentBase<String> jcEquimentBase = new JCEquimentBase<>();
        jcEquimentBase.setData("OK");
        jcEquimentBase.setType(DeviceSocketSendStatus.REFRESH.getInfo());
        client.sendEvent(SocketMessageType.GET_VERSION_PUSH_REFRESH, Result.ok(jcEquimentBase));
        socketIoHandler.addEvent(client, SocketMessageType.GET_VERSION_PUSH_REFRESH, "版本刷新");
    }

    /**
     * 版本刷新订阅 取消
     *
     */
    @OnEvent(value = SocketMessageType.UN_GET_VERSION_PUSH_REFRESH)
    public void unGetVersionPushRefresh(SocketIOClient client, GetOrderNoLineNo data) {
        log.info("IP:{},取消 [ 订阅版本刷新 ]:->data:{}", client.getRemoteAddress(), data);
        socketIoHandler.unEvent(client, SocketMessageType.GET_VERSION_PUSH_REFRESH, "版本刷新");
    }

}
