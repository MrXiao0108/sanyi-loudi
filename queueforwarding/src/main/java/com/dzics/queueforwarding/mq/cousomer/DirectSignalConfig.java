package com.dzics.queueforwarding.mq.cousomer;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZhangChengJun
 * Date 2021/9/30.
 * @since 脉冲信号队列配置
 */
@Configuration
public class DirectSignalConfig {

    /**
     * 脉冲信号队列
     */
    @Value("${accq.read.cmd.queue.base.pulse.signal}")
    private String queuePylseSignal;
    @Value("${accq.pulse.signal.routing}")
    private String directPylseSignalRouting;
    @Value("${accq.pulse.signal.exchange}")
    private String directPylseSignalExchange;

    @Bean("couponDirectSignalExchange")
    DirectExchange couponDirectSignalExchange() {
        return new DirectExchange(directPylseSignalExchange, true, false);
    }

    @Bean(name = "carDirectPylseSignal")
    public Queue carDirectPylseSignal() {
        return new Queue(queuePylseSignal, true, false, false);
    }

    @Bean("bindingDirectSignalRouting")
    Binding bindingDirectSignalRouting() {
        return BindingBuilder.bind(carDirectPylseSignal()).to(couponDirectSignalExchange()).with(directPylseSignalRouting);
    }

}
