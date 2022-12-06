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
 * 刀具检测数据队列配置
 *
 * @author ZhangChengJun
 * Date 2021/9/30.
 * @since
 */
@Configuration
public class DirectCuttingToolConfig {
    /**
     * 刀具检测队列
     */
    @Value("${accq.cutting.tool.detection}")
    private String toolInfo;
    @Value("${accq.cutting.tool.detection.routing}")
    private String toolInfoRouting;
    @Value("${accq.cutting.tool.detection.exchange}")
    private String toolInfoRoutingExchange;

    @Value("${accq.cutting.tool.detection.dead}")
    private String toolInfoDead ;
    @Value("${accq.cutting.tool.detection.routing.dead}")
    private String toolInfoRoutingDead ;


    @Bean("toolInfoDirectExchange")
    DirectExchange toolInfoDirectExchange() {
        return new DirectExchange(toolInfoRoutingExchange, true, false);
    }

    @Bean(name = "toolInfoDead")
    public Queue toolInfoDead() {
        return new Queue(toolInfoDead, true);
    }

    @Bean("toolInfoRoutingExchangeDead")
    Binding toolInfoRoutingExchangeDead() {
        return BindingBuilder.bind(toolInfoDead()).to(toolInfoDirectExchange()).with(toolInfoRoutingDead);
    }

    @Bean(name = "toolInfo")
    public Queue toolInfo() {
        Map<String,Object> args = new HashMap<>();
        args.put("x-message-ttl", 3000);
        args.put("x-dead-letter-exchange", toolInfoRoutingExchange);
        args.put("x-dead-letter-routing-key", toolInfoRoutingDead);
        return new Queue(toolInfo, true,false,false,args);
    }

    @Bean("toolInfoRoutingExchange")
    Binding toolInfoRoutingExchange() {
        return BindingBuilder.bind(toolInfo()).to(toolInfoDirectExchange()).with(toolInfoRouting);
    }



}
