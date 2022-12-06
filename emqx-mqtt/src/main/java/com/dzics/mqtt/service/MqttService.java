package com.dzics.mqtt.service;

import org.eclipse.paho.client.mqttv3.MqttClient;

import java.util.List;
import java.util.Map;

/**
 * @author ZhangChengJun
 * Date 2021/7/4.
 * @since
 */
public interface MqttService {

    /**
     * 软件上线上报
     *
     * @param topic  主题
     * @param client
     */
    boolean sendOnLine(String topic, MqttClient client);

    /**
     * 软件心跳上报
     *
     * @return
     */
    boolean sendSignal();

    /**
     * 实时数据发布
     *
     * @param mapList
     * @param assetsEncoding
     * @return
     */
    boolean sendRealTimeData(List<Map<String, Object>> mapList, String devceType, Long deviceId, String assetsEncoding);


    boolean snedDataCmdJc(String msg);
}
