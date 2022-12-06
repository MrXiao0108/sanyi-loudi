package com.dzics.sanymom.config.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZhangChengJun
 * Date 2021/12/14.
 * @since
 */
@Configuration
public class PositionQueryConfig {

    @Value("${mom.accq.product.position.query}")
    private String positionQueue;
    @Value("${mom.accq.product.position.exchange}")
    private String positionExchange;
    @Value("${mom.accq.product.position.routing}")
    private String positionRouting;




    @Bean(name = "positionQueueParam")
    public Queue positionQueueParam() {
        return new Queue(positionQueue, true,false,false);
    }
    /**
     * 创建订单交换机
     * @return
     */
    @Bean("positionExchange")
    DirectExchange positionExchange() {
        return new DirectExchange(positionExchange, true, false);
    }



    @Bean("positionRouting")
    Binding positionRouting() {
        return BindingBuilder.bind(positionQueueParam()).to(positionExchange()).with(positionRouting);
    }

}
