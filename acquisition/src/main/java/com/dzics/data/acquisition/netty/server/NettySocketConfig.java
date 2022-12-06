package com.dzics.data.acquisition.netty.server;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZhangChengJun
 * Date 2021/3/24.
 */
@Configuration
public class NettySocketConfig {
    @Value("${socketIo.port}")
    private int port;

    @Value("${socketIo.maxFramePayloadLength}")
    private int maxFramePayloadLength;

    @Value("${socketIo.maxHttpContentLength}")
    private int maxHttpContentLength;

    @Value("${socketIo.bossCount}")
    private int bossCount;

    @Value("${socketIo.workCount}")
    private int workCount;

    @Value("${socketIo.allowCustomRequests}")
    private boolean allowCustomRequests;

    @Value("${socketIo.upgradeTimeout}")
    private int upgradeTimeout;

    @Value("${socketIo.pingTimeout}")
    private int pingTimeout;

    @Value("${socketIo.pingInterval}")
    private int pingInterval;


    @Bean
    public SocketIOServer socketioserver() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setPort(port);
        config.setMaxFramePayloadLength(maxFramePayloadLength);
        config.setMaxHttpContentLength(maxHttpContentLength);
        config.setBossThreads(bossCount);
        config.setWorkerThreads(workCount);
        config.setAllowCustomRequests(allowCustomRequests);
        config.setUpgradeTimeout(upgradeTimeout);
        config.setPingTimeout(pingTimeout);
        config.setPingInterval(pingInterval);
        //该处进行身份验证
        config.setAuthorizationListener(handshakeData -> true);
        return new SocketIOServer(config);
    }

    //开启注解
    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
        return new SpringAnnotationScanner(socketServer);
    }

}
