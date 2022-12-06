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
 * 业务端发送数据到UDP端队列配置
 *
 * @author ZhangChengJun
 * Date 2021/9/30.
 * @since
 */
@Configuration
public class DirectUdpSendLowerConfig {

    //============================================================================================
    /**
     * 业务端发送数据到UDP端队列配置
     * 例如：华培下发指令 到 udp 准备获取二维码
     * 例如：三一发送订单数据到UDP
     */
    @Value("${accq.product.qrode.lower.query}")
    private String qrodeLowerQuery;
    @Value("${accq.product.qrode.lower.routing}")
    private String qrodeLowerRouting;
    @Value("${accq.product.qrode.lower.exchange}")
    private String qrodeLowerExchange;

    @Value("${accq.product.qrode.lower.query.dead}")
    private String qrodeLowerQueryDead;
    @Value("${accq.product.qrode.lower.routing.dead}")
    private String qrodeLowerRoutingDead;

    @Bean(name = "directExchangeBaseQrodeLower")
    DirectExchange directExchangeBaseQrodeLower() {
        return new DirectExchange(qrodeLowerExchange, true, false);
    }

    @Bean(name = "directQueueQrodeLowerDead")
    public Queue directQueueQrodeLowerDead() {
        return new Queue(qrodeLowerQueryDead, true);
    }

    @Bean(name = "bindingDirectBaseQrodeLowerDead")
    Binding bindingDirectBaseQrodeLowerDead() {
        return BindingBuilder.bind(directQueueQrodeLowerDead()).to(directExchangeBaseQrodeLower()).with(qrodeLowerRoutingDead);
    }

    @Bean(name = "directQueueQrodeLower")
    public Queue directQueueQrodeLower() {
        Map<String,Object> args = new HashMap<>();
        args.put("x-message-ttl", 3000);
        args.put("x-dead-letter-exchange", qrodeLowerExchange);
        args.put("x-dead-letter-routing-key", qrodeLowerRoutingDead);
        return new Queue(qrodeLowerQuery, true,false,false,args);
    }

    @Bean(name = "bindingDirectBaseQrodeLower")
    Binding bindingDirectBaseQrodeLower() {
        return BindingBuilder.bind(directQueueQrodeLower()).to(directExchangeBaseQrodeLower()).with(qrodeLowerRouting);
    }
//===================================================================================
    /**
     * 华培绑定检测数据二维码队列消息底层上发校验吗
     */
    @Value("${accq.product.qrode.up.udp.query}")
    private String qrodeupUdpJyQuery;
    @Value("${accq.product.qrode.up.udp.routing}")
    private String qrodeupUdpJyRouting;
    @Value("${accq.product.qrode.up.udp.exchange}")
    private String qrodeupUdpJyExchange;


    @Value("${accq.product.qrode.up.udp.query.dead}")
    private String qrodeupUdpJyQueryDead;
    @Value("${accq.product.qrode.up.udp.routing.dead}")
    private String qrodeupUdpJyRoutingDead;

    @Bean(name = "directExchangeBaseqrodeupUdp")
    DirectExchange directExchangeBaseqrodeupUdp() {
        return new DirectExchange(qrodeupUdpJyExchange, true, false);
    }

    @Bean(name = "directQueueqrodeupUdpDead")
    public Queue directQueueqrodeupUdpDead() {
        return new Queue(qrodeupUdpJyQueryDead, true);
    }

    @Bean(name = "bindingDirectBaseqrodeupUdpDead")
    Binding bindingDirectBaseqrodeupUdpDead() {
        return BindingBuilder.bind(directQueueqrodeupUdpDead()).to(directExchangeBaseqrodeupUdp()).with(qrodeupUdpJyRoutingDead);
    }

    @Bean(name = "directQueueqrodeupUdp")
    public Queue directQueueqrodeupUdp() {
        Map<String,Object> args = new HashMap<>();
        args.put("x-message-ttl", 28000);
        args.put("x-dead-letter-exchange", qrodeupUdpJyExchange);
        args.put("x-dead-letter-routing-key", qrodeupUdpJyRoutingDead);
        return new Queue(qrodeupUdpJyQuery, true,false,false,args);
    }

    @Bean(name = "bindingDirectBaseqrodeupUdp")
    Binding bindingDirectBaseqrodeupUdp() {
        return BindingBuilder.bind(directQueueqrodeupUdp()).to(directExchangeBaseqrodeupUdp()).with(qrodeupUdpJyRouting);
    }
//============================================================================================

}
