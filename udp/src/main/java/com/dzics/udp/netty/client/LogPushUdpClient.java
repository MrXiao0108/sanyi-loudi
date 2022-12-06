package com.dzics.udp.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.nio.charset.Charset;

/**
 * udp客户端
 *
 * @author ZhangChengJun
 * Date 2021/3/18.
 * @since
 */
public class LogPushUdpClient {
    private final Bootstrap bootstrap;
    public final NioEventLoopGroup workerGroup;
    public static Channel channel;
    private static final Charset ASCII = Charset.forName("ASCII");

    public void start() throws Exception {
        try {
            channel = bootstrap.bind(1234).sync().channel();
            channel.closeFuture().await(1000);
        } finally {
//        	workerGroup.shutdownGracefully();
        }
    }

    public Channel getChannel() {
        return channel;
    }

    public static LogPushUdpClient getInstance() {
        return logPushUdpClient.INSTANCE;
    }

    private static final class logPushUdpClient {
        static final LogPushUdpClient INSTANCE = new LogPushUdpClient();
    }

    private LogPushUdpClient() {
        bootstrap = new Bootstrap();
        workerGroup = new NioEventLoopGroup();
        bootstrap.group(workerGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    protected void initChannel(NioDatagramChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
//				pipeline.addLast(new StringDecoder(ASCII))
//                .addLast(new StringEncoder(ASCII))
                        pipeline.addLast(new LogPushUdpClientHandler());
                    }
                });
    }

}
