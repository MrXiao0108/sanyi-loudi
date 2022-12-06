package com.dzics.udp.netty.server;

import com.dzics.udp.exception.UdpException;
import com.dzics.udp.netty.handler.UdpServerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.internal.SystemPropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * netty服务（UDP）
 *
 * @author ZhangChengJun
 * Date 2021/3/19.
 * @since
 */
@Configuration
public class NettyUdpServer implements ApplicationListener<ApplicationStartedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyUdpServer.class);

    @Value("${netty.udp.port}")
    private int port;

    @Resource
    private UdpServerHandler udpServerHandler;

    private EventLoopGroup group = null;

    @Override
    public void onApplicationEvent(@NonNull ApplicationStartedEvent event) {

        try {
            Bootstrap b = new Bootstrap();
            String osName = SystemPropertyUtil.get("os.name").toLowerCase();
            if ("linux".equals(osName)) {
                group = new EpollEventLoopGroup();
                b.group(group)
                        .channel(EpollDatagramChannel.class);
            } else {
                group = new NioEventLoopGroup();
                b.group(group)
                        .channel(NioDatagramChannel.class);
            }
            b.option(ChannelOption.SO_BROADCAST, true)
                    .handler(udpServerHandler);

            ChannelFuture channelFuture = b.bind(port).sync();
            if (channelFuture.isSuccess()) {
                LOGGER.info("UDP服务启动完必,port={}", this.port);
            }

        } catch (InterruptedException e) {
            throw new UdpException("UDP服务启动失败", e);
        }

    }

    /**
     * 销毁资源
     */
    @PreDestroy
    public void destroy() {
        if (group != null) {
            group.shutdownGracefully();
        }
        LOGGER.info("UDP服务关闭成功");
    }

}