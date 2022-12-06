package com.dzics.mqtt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 数据采集服务中控启动类
 *
 * @author ZhangChengJun
 * Date 2021/7/2.
 * @since
 */
@SpringBootApplication
@MapperScan(basePackages = "com.dzics.mqtt.dao")
@EnableCaching
@EnableScheduling
public class MqttApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqttApplication.class, args);
    }

}
