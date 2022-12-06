package com.dzics.data.acquisition.netty.server;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZhangChengJun
 * Date 2021/3/24.
 * @since
 */
@Configuration
@Slf4j
public class SocketIoRun implements ApplicationListener<ApplicationStartedEvent> {

    @Autowired
    private SocketIOServer socketIoServer;

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.info(".... socket.io 启动 ....");
        socketIoServer.start();
    }
}
