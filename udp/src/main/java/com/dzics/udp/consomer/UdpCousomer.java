package com.dzics.udp.consomer;

import com.alibaba.fastjson.JSONObject;
import com.dzics.udp.model.SendPlcModel;
import com.dzics.udp.mq.RabbitmqService;
import com.dzics.udp.netty.handler.ByteUtil;
import com.google.common.primitives.Bytes;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 富华装箱消费
 *
 * @author ZhangChengJun
 * Date 2021/3/11.
 * @since
 */
@Component
@Slf4j
public class UdpCousomer {


    @Value("${dzics.udp.queue.repertory}")
    private String query;
    /**
     * 装箱数据队列
     *
     * @param mess
     * @param deliveryTag
     * @param channel
     * @throws Throwable
     */
    @RabbitListener(queues = "${dzics.udp.queue.repertory}")
    public void dzEncasementRecordEquipment(@Payload String mess, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        try {
            log.debug("lower-收到上层应用:{},消息:{},触发二维码上发", query, mess);
            SendPlcModel sendPlcModel = JSONObject.parseObject(mess, SendPlcModel.class);
            String ip = sendPlcModel.getIp();
            Integer port = sendPlcModel.getPort();
            String message = sendPlcModel.getMessage();
            boolean b = sendUdpServer(ip, port, message);
            if (b) {
                log.info("lower-发送给底层设备触发二维码上发成功：ip:{},port:{},message：{}", ip, port, mess);
            } else {
                log.info("lower-发送给底层设备触发二维码上发失败：ip:{},port:{},message：{}", ip, port, mess);
            }
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("lower-消费数据处理失败: {},队列的消息：{}", query, mess, e);
            channel.basicReject(deliveryTag, false);
        }
    }


    public boolean sendUdpServer(String ip, int port, String message) {
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
            log.info("lower-发送给底设备ip：{},port:{},concatSendByte:{}", ip, port, concatSendByte);
//            Map<String, Object> map = valCheckout(concatSendByte);
            DatagramPacket dpSend = new DatagramPacket(concatSendByte, concatSendByte.length, InetAddress.getByName(ip), port);
            DatagramSocket ds = new DatagramSocket();
            ds.send(dpSend);
            ds.close();
            return true;
        } catch (Throwable e) {
            log.error("lower-发送给底层设备触发二维码上发失败:ip：{},port:{},message:{}", ip, port, message, e);
            return false;
        }
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


}
