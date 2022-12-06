package com.dzics.data.acquisition.config.mq.centermq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 菲仕设备状态转发  至  上发MES队列
 */
@Configuration
public class TopicEquipmentStateMqConfig {
    @Value("${equipment.status.change}")
    private String queue;
    @Value("${equipment.status.change.routing}")
    private String routingKey;
    @Value("${equipment.status.change.exchange}")
    private String topicExchange;

    @Bean(name = "queueEquipmentState")
    public Queue queueEquipmentState() {
        return new Queue(queue, true);
    }

    @Bean(name = "exchangeEquipmentState")
    public TopicExchange exchangeEquipmentState() {
        return new TopicExchange(topicExchange);
    }

    @Bean(name = "bindingEquipmentState")
    public Binding binding() {
        return BindingBuilder.bind(queueEquipmentState()).to(exchangeEquipmentState()).with(routingKey);
    }
}
