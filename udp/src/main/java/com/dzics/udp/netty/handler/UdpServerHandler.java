package com.dzics.udp.netty.handler;

import com.alibaba.fastjson.JSONObject;
import com.dzics.udp.model.Response;
import com.dzics.udp.mq.RabbitmqService;
import com.google.common.primitives.Bytes;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UDP业务处理handler
 *
 * @author ZhangChengJun
 * Date 2021/3/19.
 */
@ChannelHandler.Sharable
@Component
@Slf4j
public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    @Autowired
    @Qualifier("businessGroup")
    private EventExecutorGroup businessGroup;

    @Autowired
    private RabbitmqService rabbitmqService;

    private static byte[] cacheBuffer;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        ByteBuf buf = packet.content();
        if (buf == null) {
            return;
        }
        int len = buf.readableBytes();
        if (len <= 0) {
            return;
        }
        if (cacheBuffer == null) {
            cacheBuffer = new byte[len];
            buf.readBytes(cacheBuffer);
        } else {
//            生成新临时 byte 数组
            byte[] tempBuffer = new byte[len];
            buf.readBytes(tempBuffer);
//            缓存的buffer 增加 当前读取的 tempBuffer
            byte[] newCaheBuffe = new byte[cacheBuffer.length + len];
            System.arraycopy(cacheBuffer, 0, newCaheBuffe, 0, cacheBuffer.length);
            System.arraycopy(tempBuffer, 0, newCaheBuffe, cacheBuffer.length, tempBuffer.length);
            cacheBuffer = newCaheBuffe;
        }
//        校验消息
        Map<String, Object> map = valCheckout(cacheBuffer);
        if (map != null) {
            List<Byte> cache = (List<Byte>) map.get("cache");
            cacheBuffer = Bytes.toArray(cache);
            List<List<Byte>> sendByte = (List<List<Byte>>) map.get("send");
            for (List<Byte> byteList : sendByte) {
                List<Byte> subList = byteList.subList(4, byteList.size());
                String sendStr = new String(Bytes.toArray(subList), "UTF-8");
                businessGroup.execute(() -> {
                    try {
                        rabbitmqService.sendJsonString(sendStr);
//                        Response resp = Response.success("ok");
//                        send2client(ctx.channel(), resp, packet.sender());
                    } catch (Throwable e) {
                        log.error("UP-UDP........处理数据错误..........", e);
                    }
                });
            }

        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("UP-UDP.............数据接收处理错误................：", cause);
    }


    public Map<String, Object> valCheckout(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        //        数组总长度
        int bytesLength = bytes.length;
        List<Byte> endbyte = new ArrayList<>();
        if (bytesLength < 6) {
            map.put("cache", Bytes.asList(bytes));
            return map;
        }
        List<List<Byte>> byteList = new ArrayList<>();
        int falgAddIndex = -1;
        int endSub = 0;
        for (int i = 0; i < bytesLength; i++) {
//            j 变量为i的下一个变量，为了获取连续两个 35 开始标识位
            int j = i + 1;
            if (j < bytesLength) {
                if (bytes[i] == (byte) 35) {
                    if (bytes[i] == bytes[j]) {
//                        获取到标识位置开始下标 读取 4 位
                        if ((j + 4) < bytesLength) {
//                            j增加读取4位的下标要 小于数组长度
                            int anInt = ByteUtil.getInt(bytes, j);
//                            查看剩余长度是否 > 表示长度
                            falgAddIndex = falgAddIndex + 1;
                            int offset = anInt + j;
                            if (offset < bytesLength) {
                                endSub = offset;
                                List<Byte> byteAdd = new ArrayList<>(anInt - 1);
//                            填充值   从j +5 位置开始读取
                                for (int k = j + 1; k < offset; k++) {
                                    byteAdd.add(bytes[k]);
                                }
                                byte[] bytesSum = Bytes.toArray(byteAdd);
                                byte sum = ByteUtil.sum(bytesSum, 0, bytesSum.length - 1);
                                byte aByte = bytes[offset];
                                if (sum == aByte) {
                                    byteList.add(falgAddIndex, byteAdd);
                                }
                                i = offset + 1;
                            }
                        }
                    }
                }
            }
        }
        for (int i = endSub + 1; i < bytes.length; i++) {
            endbyte.add(bytes[i]);
        }
        map.put("cache", endbyte);
        map.put("send", byteList);
        return map;
    }

    void send2client(Channel channel, Response resp, InetSocketAddress address) {
        try {
            byte[] bytes = sendUdpServer(JSONObject.toJSONString(resp));
            channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(bytes), address));
            log.debug("回复消息：message:{}",JSONObject.toJSONString(resp));
//          channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(JSONObject.toJSONString(resp.encode()).getBytes()), address));
        } catch (Throwable e) {
            log.error("UDP........发送数据给客户端出错............", e);
        }
    }


    public byte[] sendUdpServer( String message) {
        try {
//            头部byte
            byte[] byteStart = "##".getBytes();
//            发送内容byte
            byte[] messageBytes = message.getBytes();
//            内容长度+ 数据长度四字节
            int length = messageBytes.length + 5;
            byte[] leBytes = ByteUtil.tolh(length);
//           内容 byte
            byte[] concat = Bytes.concat(leBytes, messageBytes);

            byte sum = ByteUtil.sum(concat, 0, concat.length - 1);
            byte[] sumByte = new byte[1];
            sumByte[0] = sum;
            byte[] bytesEnd = "!!".getBytes();
            byte[] linefeed = {13};
//            发送 byte
            byte[] concatSendByte = Bytes.concat(byteStart, leBytes, messageBytes, sumByte, bytesEnd, linefeed);
            return concatSendByte;
        } catch (Throwable e) {
            return null;
        }
    }

}
