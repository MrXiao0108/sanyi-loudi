package com.dzics.mqtt.framework;

import com.dzics.mqtt.config.MqttProperties;
import com.dzics.mqtt.model.entity.SysConfigMqtt;
import com.dzics.mqtt.service.impl.MqttCallbackService;
import com.dzics.mqtt.service.impl.MqttServiceImpl;
import com.dzics.mqtt.util.SSL;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.io.InputStream;

/**
 * mqtt连接客户端
 *
 * @author ZhangChengJun
 * Date 2021/7/2.
 * @since
 */
@Slf4j
public class DzicsMqttClient {
    private MqttClient client;
    private static volatile DzicsMqttClient instance = null;
    private SysConfigMqtt configMqtt;
    private MqttServiceImpl mqttService;

    public DzicsMqttClient(MqttServiceImpl mqttService) {
        log.info("MQTT客户端:{} 连接......", mqttService.getConfigMqtt().getClientId());
        this.mqttService = mqttService;
        this.configMqtt = mqttService.getConfigMqtt();
        connect();
    }


    public static DzicsMqttClient getInstance(MqttServiceImpl mqttService) {
        if (instance == null) {
            synchronized (DzicsMqttClient.class) {
                if (instance == null) {
                    instance = new DzicsMqttClient(mqttService);
                }
            }
        }
        return instance;
    }

    @SneakyThrows
    public void connect() {
        try {
            while (true) {
                if (client != null) {
                    if (!client.isConnected()) {
                        log.info("MQTT客户端:{} 连接......", mqttService.getConfigMqtt().getClientId());
                    } else {
                        mqttService.sendOnLine(configMqtt.getTopicSoft(), client);
                        log.info("***** connect success *****");
                        break;
                    }
                }
               /* configMqtt.setAccesskey("admin");
                configMqtt.setSecretkey("public");
                configMqtt.setServer("tcp://127.0.0.1:1883");
                configMqtt.setClientId("admin");*/
                MqttConnectOptions conOpt = new MqttConnectOptions();
                conOpt.setAutomaticReconnect(MqttProperties.automaticReconnect);
                conOpt.setCleanSession(MqttProperties.cleanSession);
                conOpt.setUserName(configMqtt.getAccesskey());
                conOpt.setPassword(configMqtt.getSecretkey().toCharArray());
                conOpt.setKeepAliveInterval(MqttProperties.keepAliveInterval);
                if (configMqtt.getServer().startsWith(MqttProperties.serverUrlType)) {
                    InputStream crt = this.getClass().getClassLoader().getResourceAsStream("cacert.cer");
                    conOpt.setSocketFactory(new SSL().getSSLSocktet(crt));
                    conOpt.setHttpsHostnameVerificationEnabled(false);
                }
                String tmpDir = System.getProperty("java.io.tmpdir");
                MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);
                client = new MqttClient(configMqtt.getServer(), configMqtt.getClientId(), dataStore);
                client.setCallback(new MqttCallbackService(mqttService));
                client.connect(conOpt);
                Thread.sleep(10000);
            }
        } catch (Throwable e) {
            log.error("mqtt客户端:{} 连接异常：{}", configMqtt.getClientId(), e.getMessage(), e);
            throw e;
        }

    }

    /**
     * 断开连接
     */
    public void disconnect() {
        try {
            client.disconnect();
            log.info("客户端：{} 主动断开连接", configMqtt.getClientId());
        } catch (MqttException e) {
            log.error("客户端：{},主动断开连接异常：{}", configMqtt.getClientId(), e.getMessage(), e);
        }
    }


    public boolean sendMessage(String topic, String message) {
        try {
            boolean connected = client.isConnected();
            if (!connected) {
                log.error("发送消息失败：topic:{} , message: {},isConnected: {}", topic, message, connected);
                return false;
            }
            this.client.publish(topic, message.getBytes("UTF-8"), 1, true);
            return true;
        } catch (Throwable e) {
            log.error("发送消息失败：topic:{} , message: {}", topic, message, e);
            return false;
        }
    }
}
