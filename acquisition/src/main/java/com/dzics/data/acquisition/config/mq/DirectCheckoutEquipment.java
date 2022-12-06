package com.dzics.data.acquisition.config.mq;

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
 * 检测设备数据队列 配置
 * @author ZhangChengJun
 * Date 2021/9/30.
 * @since
 */
@Configuration
public class DirectCheckoutEquipment {
    //============================================================================================
    /**
     * 检测设备队列
     */
    @Value("${accq.read.cmd.queue.base.checkout.equipment}")
    private String queueCheckoutEquipment;
    @Value("${accq.checkout.equipment.routing}")
    private String directCheckoutEquipmentRouting;

    @Value("${accq.checkout.equipment.exchange}")
    private String directCheckoutEquipmentExchange;

    @Value("${accq.read.cmd.queue.base.checkout.equipment.dead}")
    private String queueCheckoutEquipmentDead;
    @Value("${accq.checkout.equipment.routing.dead}")
    private String directCheckoutEquipmentRoutingDead;
    @Bean("couponDirectCheckoutExchange")
    DirectExchange couponDirectCheckoutExchange() {
        return new DirectExchange(directCheckoutEquipmentExchange, true, false);
    }

    @Bean(name = "carDirectCheckoutEquipmentDead")
    public Queue carDirectCheckoutEquipmentDead() {
        return new Queue(queueCheckoutEquipmentDead, true);
    }
    @Bean("bindingDirectCheckoutRoutingDead")
    Binding bindingDirectCheckoutRoutingDead() {
        return BindingBuilder.bind(carDirectCheckoutEquipmentDead()).to(couponDirectCheckoutExchange()).with(directCheckoutEquipmentRoutingDead);
    }



    @Bean(name = "carDirectCheckoutEquipment")
    public Queue carDirectCheckoutEquipment() {
        Map<String,Object> args = new HashMap<>();
        args.put("x-message-ttl", 3000);
        args.put("x-dead-letter-exchange", directCheckoutEquipmentExchange);
        args.put("x-dead-letter-routing-key", directCheckoutEquipmentRoutingDead);
        return new Queue(queueCheckoutEquipment, true,false,false,args);
    }

    @Bean("bindingDirectCheckoutRouting")
    Binding bindingDirectCheckoutRouting() {
        return BindingBuilder.bind(carDirectCheckoutEquipment()).to(couponDirectCheckoutExchange()).with(directCheckoutEquipmentRouting);
    }

}
