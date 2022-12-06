package com.dzics.queueforwarding.mq.cousomer;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 日志队列数据处理
 *
 * @author ZhangChengJun
 * Date 2021/10/8.
 * @since
 */
@Configuration
public class DirectRealTimeLogs {

    /**
     * 实时日志信息队列
     */
    @Value("${accq.read.cmd.queue.equipment.realTime}")
    private String queueRealTimeEquipment;
    @Value("${accq.realTime.equipment.routing}")
    private String directRealTimeEquipmentRouting;
    @Value("${accq.realTime.equipment.exchange}")
    private String directRealTimeEquipmentExchange;


    @Value("${accq.read.cmd.queue.equipment.realTime.dead}")
    private String queueRealTimeEquipmentDead;
    @Value("${accq.realTime.equipment.routing.dead}")
    private String directRealTimeEquipmentRoutingDead;

    @Bean("directExchangeBaseRealTime")
    DirectExchange directExchangeBaseRealTime() {
        return new DirectExchange(directRealTimeEquipmentExchange, true, false);
    }

    @Bean(name = "directQueueRealTimeDead")
    public Queue directQueueRealTimeDead() {
        return new Queue(queueRealTimeEquipmentDead, true);
    }

    @Bean("bindingDirectBaseRealTimeDead")
    Binding bindingDirectBaseRealTimeDead() {
        return BindingBuilder.bind(directQueueRealTimeDead()).to(directExchangeBaseRealTime()).with(directRealTimeEquipmentRoutingDead);
    }


    @Bean(name = "directQueueRealTime")
    public Queue directQueueRealTime() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 3000);
        args.put("x-dead-letter-exchange", directRealTimeEquipmentExchange);
        args.put("x-dead-letter-routing-key", directRealTimeEquipmentRoutingDead);
        return new Queue(queueRealTimeEquipment, true, false, false, args);
    }

    @Bean("bindingDirectBaseRealTime")
    Binding bindingDirectBaseRealTime() {
        return BindingBuilder.bind(directQueueRealTime()).to(directExchangeBaseRealTime()).with(directRealTimeEquipmentRouting);
    }

//============================================================================================

}
