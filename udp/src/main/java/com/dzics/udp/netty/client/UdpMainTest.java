package com.dzics.udp.netty.client;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author ZhangChengJun
 * Date 2021/3/18.
 * @since
 */
@Slf4j
public class UdpMainTest {

    public static void main(String ip, int port) {
        try {
            DatagramSocket ds = new DatagramSocket();
            byte[] data = ("UDP 客户端" + 1).getBytes();
            DatagramPacket dpSend = new DatagramPacket(data, data.length, InetAddress.getByName(ip), port);
            ds.send(dpSend);
            ds.close();
        } catch (IOException e) {
            log.error("发送失败", e);
        }
    }

}
