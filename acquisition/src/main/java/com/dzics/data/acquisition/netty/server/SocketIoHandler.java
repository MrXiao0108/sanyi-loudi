package com.dzics.data.acquisition.netty.server;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.common.model.response.*;
import com.dzics.common.model.response.detection.QrCodeDetector;
import com.dzics.data.acquisition.netty.SocketMessageType;
import com.dzics.data.acquisition.service.*;
import com.dzics.data.acquisition.util.SubscribeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author ZhangChengJun
 * Date 2021/3/24.
 */
@Component
@Slf4j
public class SocketIoHandler {

    /**
     * 存储客户端连接后触发的事件 map
     */
    private static final ConcurrentHashMap<String, ConcurrentSkipListSet<UUID>> connectType = new ConcurrentHashMap<>();

    /**
     * 存储客户端 触发事件时的 连接UUID 和 事件类型
     */
    private static final ConcurrentHashMap<String, ConcurrentSkipListSet<String>> connectTypeUUID = new ConcurrentHashMap<>();


    /**
     * 监听客户端连接
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {
        log.info("....客户端IP:{} 连接....", client.getRemoteAddress());
    }

    /**
     * 监听客户端断开
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        log.info("....客户端IP:{} 断开连接....", client.getRemoteAddress());
        UUID sessionId = client.getSessionId();
        String id = sessionId.toString();
//        根据sessid 获取连接的所有事件类型
        ConcurrentSkipListSet<String> skipListSet = connectTypeUUID.get(id);
        if (skipListSet != null && !skipListSet.isEmpty()) {
            for (String type : skipListSet) {
                ConcurrentSkipListSet<UUID> concurrentSkipListSet = connectType.get(type);
                if (concurrentSkipListSet == null || concurrentSkipListSet.isEmpty()) {
                    continue;
                }
                concurrentSkipListSet.remove(sessionId);
                connectType.put(type, concurrentSkipListSet);
            }
        }
        connectTypeUUID.remove(id);
    }

    public ConcurrentSkipListSet<UUID> getConnectType(String eventType) {
        return connectType.get(eventType);
    }


    /**
     * 订阅事件
     */
    public void addEvent(SocketIOClient client, String hostManagerNumberState, String subscriptionType) {
        UUID sessionId = client.getSessionId();
        String id = sessionId.toString();
//        ------------事件->连接存储   处理 发送事件 对应 的客户端------------------------
        ConcurrentSkipListSet<UUID> concurrentSkipListSet = connectType.get(hostManagerNumberState);
        if (concurrentSkipListSet == null || concurrentSkipListSet.isEmpty()) {
            concurrentSkipListSet = new ConcurrentSkipListSet<>();
        }
        concurrentSkipListSet.add(sessionId);
        connectType.put(hostManagerNumberState, concurrentSkipListSet);
//      ---------------- 事件对应的客户端连接更新完必-------------------------------------------
//      ---------------------------------------------------------------------------------------
//      ---------------- 连接对应的时间 存储更新开始 ------------------------------------------
        ConcurrentSkipListSet<String> uidTypeList = connectTypeUUID.get(id);
        if (uidTypeList == null || uidTypeList.isEmpty()) {
            uidTypeList = new ConcurrentSkipListSet<>();
        }
        uidTypeList.add(hostManagerNumberState);
        connectTypeUUID.put(sessionId.toString(), uidTypeList);
        log.info("IP:{},完成订阅:[ {} ]事件成功：addEventType: {}", client.getRemoteAddress(), subscriptionType, hostManagerNumberState);
//         ---------------- 连接对应的时间 存储更新结束 ------------------------------------------
    }

    /**
     * 取消订阅事件
     */
    public void unEvent(SocketIOClient client, String hostManagerNumberState, String subscriptionType) {
        UUID sessionId = client.getSessionId();
        String sid = sessionId.toString();
//        根据sessid 获取连接的事件
//        ---------------删除处理客户端id 存储的事件 开始 -----------
        ConcurrentSkipListSet<String> uidTypeList = connectTypeUUID.get(sid);
        if (uidTypeList != null && !uidTypeList.isEmpty()) {
            uidTypeList.remove(hostManagerNumberState);
            connectTypeUUID.put(sid, uidTypeList);
        }
//        ---------------删除处理客户端id 存储的事件 结束 -------------

//        --------------- 删除事件对应的客户端 id 开始-----------------
        ConcurrentSkipListSet<UUID> concurrentSkipListSet = connectType.get(hostManagerNumberState);
        if (concurrentSkipListSet == null || concurrentSkipListSet.isEmpty()) {
            return;
        }
        concurrentSkipListSet.remove(sessionId);
        connectType.put(hostManagerNumberState, concurrentSkipListSet);
        log.info("IP：{}，取消: [ {} ] 事件成功：addEventType:{}", client.getRemoteAddress(), subscriptionType, hostManagerNumberState);
    }




}
