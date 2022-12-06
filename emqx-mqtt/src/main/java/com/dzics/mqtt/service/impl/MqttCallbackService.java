package com.dzics.mqtt.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * @author ZhangChengJun
 * Date 2021/7/4.
 * @since
 */
@Slf4j
public class MqttCallbackService implements MqttCallbackExtended {
    private MqttServiceImpl mqttService;

    public MqttCallbackService(MqttServiceImpl mqttService) {
        this.mqttService = mqttService;
    }

    /**
     * 在这里处理接收到的消息。
     *
     * @param topic       消息主题
     * @param mqttMessage 消息对象
     * @throws Exception
     */
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        // 订阅之后的消息
        String payload = new String(mqttMessage.getPayload());
        log.info("主题：{}，内容: {}", topic, payload);
    }

    /**
     * 接收到已经发布的 QoS 1 或 QoS 2 消息的传递令牌时调用。
     *
     * @param iMqttDeliveryToken 消息的传递令牌
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        String[] topics = iMqttDeliveryToken.getTopics();
//        log.info("发送完成---> topics：{} ， isComplete: {}", topics,iMqttDeliveryToken.isComplete());
    }

    /**
     * 此方法在客户端连接断开之后调用
     *
     * @param throwable
     */
    @Override
    public void connectionLost(Throwable throwable) {
        log.error("错误信息：{}, 连接断开 ，可以重新链接...", throwable.getMessage());
    }

    /**
     * 此方法在客户端连接成功之后调用
     *
     * @param reconnect
     * @param serverURI
     */
    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        log.info("连接成功：reconnect：{},s:{}", reconnect, serverURI);
    }
}
