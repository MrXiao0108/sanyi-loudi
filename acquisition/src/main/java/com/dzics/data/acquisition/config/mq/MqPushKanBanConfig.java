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
 * 直接PUSH到看板队列配置
 *
 * @author ZhangChengJun
 * Date 2021/11/19.
 * @since
 */
@Configuration
public class MqPushKanBanConfig {
    @Value("${push.kanban.exchange.simple}")
    private String pushExchange;

    @Value("${push.kanban.queue.simple}")
    private String pushQueue;

    @Value("${push.kanban.routing.simple}")
    private String qushRouting;

    @Value("${push.kanban.queue.dead.simple}")
    private String pushDeadQueue;

    @Value("${push.kanban.routing.dead.simple}")
    private String pushDeadRouting;



    @Bean(name = "pushExchange")
    DirectExchange pushExchange() {
        return new DirectExchange(pushExchange, true, false);
    }

    @Bean(name = "pushDeadQueue")
    public Queue pushDeadQueue() {
        return new Queue(pushDeadQueue, true);
    }

    @Bean(name = "pushDeadBinding")
    Binding pushDeadBinding() {
        return BindingBuilder.bind(pushDeadQueue()).to(pushExchange()).with(pushDeadRouting);
    }

    @Bean(name = "pushQueue")
    public Queue pushQueue() {
        Map<String,Object> args = new HashMap<>();
        args.put("x-message-ttl", 5000);
        args.put("x-dead-letter-exchange", pushExchange);
        args.put("x-dead-letter-routing-key", pushDeadRouting);
        return new Queue(pushQueue, true,false,false,args);
    }

    @Bean(name = "pushBinding")
    Binding pushBinding() {
        return BindingBuilder.bind(pushQueue()).to(pushExchange()).with(qushRouting);
    }
}
