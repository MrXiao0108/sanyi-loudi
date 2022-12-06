package com.dzics.mqtt.task;

import com.dzics.mqtt.service.MqttService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SignalTask {
    @Autowired
    private MqttService mqttService;
    /**
     * 心跳信号发送
     */
//    @Scheduled(fixedDelay = 300000, initialDelay = 300000)
    public void signal() {
        log.info("心跳发送开始。。。。。。");
        mqttService.sendSignal();
        log.info("心跳发送完成。。。。。。");
    }

}
