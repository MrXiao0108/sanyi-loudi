package com.dzics.data.acquisition.netty;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.dzics.data.acquisition.netty.server.SocketIoHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 消息发送模板
 *
 * @author ZhangChengJun
 * Date 2021/3/24.
 * @since
 */
@Component
@Slf4j
public class SocketServerTemplate<T> {

    @Autowired
    private SocketIOServer socketioserver;
    @Autowired
    private SocketIoHandler socketIoHandler;

    public void sendMessage(String event, String eventKey, T data) {
        ConcurrentSkipListSet<UUID> connectType = socketIoHandler.getConnectType(eventKey);
        if (connectType != null) {
            connectType.forEach(x -> {
                SocketIOClient client = socketioserver.getClient(x);
                if (client != null) {
                    try {
                        client.sendEvent(event, data);
                    } catch (Throwable throwable) {
                        log.debug("发送数据到事件：{}，客户端id:{}  异常:{}", event, x, throwable);
                    }
                } else {
                    log.warn("根据客户端id：{} 无法获取 SocketIOClient ", x);
                }
            });
        }
    }
}
