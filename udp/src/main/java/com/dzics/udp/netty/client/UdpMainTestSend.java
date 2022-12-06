package com.dzics.udp.netty.client;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author ZhangChengJun
 * Date 2021/3/18.
 * @since
 */
@Slf4j
public class UdpMainTestSend {


    private static final String HOST = "127.0.0.1";

    private static final int PORT = 9999;

    public static void main(String[] args) throws Exception {
       logPushSendTest();
    }


    public static void logPushSendTest() throws Exception {
        LogPushUdpClient.getInstance().start();
        int i = 0;
        while (i < 10) {
            LogPushUdpClientHandler.sendMessage(new String("你好UdpServer"), new InetSocketAddress(HOST, PORT));
            log.info(i + " client send message is: 你好UdpServer");
            i++;
        }
    }


}
