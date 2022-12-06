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
 * 设备状态队列配置
 *
 * @author ZhangChengJun
 * Date 2021/10/8.
 * @since
 */
@Configuration
public class DirectStateConfig {

    /**
     * 状态数据队列
     */
    @Value("${accq.read.cmd.queue.base.state}")
    private String queueState;
    @Value("${accq.state.routing}")
    private String directStateRouting;

    @Value("${accq.read.cmd.queue.base.state.dead}")
    private String queueStateDead;
    @Value("${accq.state.routing.dead}")
    private String directStateRoutingDead;

    @Value("${accq.state.exchange}")
    private String directStateExchange;

    @Value("${accq.read.cmd.queue.base.state.copy}")
    private String queueStateCopy;

    @Value("${accq.read.cmd.queue.base.state.handle}")
    private String queueStateHandle;
    @Value("${accq.state.routing.handle}")
    private String directStateRoutingHandle;


    /**
     * 创建交换机
     *
     * @return
     */
    @Bean("couponDirectExchange")
    DirectExchange couponDirectExchange() {
        return new DirectExchange(directStateExchange, true, false);
    }


    @Bean(name = "carDirectQueueeHandle")
    public Queue carDirectQueueeHandle() {
        return new Queue(queueStateHandle, true);
    }
    @Bean("bindingDirectHandle")
    Binding bindingDirectHandle() {
        return BindingBuilder.bind(carDirectQueueeHandle()).to(couponDirectExchange()).with(directStateRoutingHandle);
    }

    /**
     * durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
     * exclusive:默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
     * autoDelete:是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。
     * return new Queue("TestDirectQueue",true,true,false);
     *
     * @return 创建队列
     */
    @Bean(name = "carDirectQueue")
    public Queue carDirectQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 3000);
        args.put("x-dead-letter-exchange", directStateExchange);
        args.put("x-dead-letter-routing-key", directStateRoutingDead);
        return new Queue(queueState, true, false, false, args);
    }

    @Bean(name = "carDirectQueueeCopy")
    public Queue carDirectQueueeCopy() {
        return new Queue(queueStateCopy, true);
    }
    @Bean(name = "carDirectQueueDead")
    public Queue carDirectQueueDead() {
        return new Queue(queueStateDead, true);
    }

    @Bean("bindingDirectDead")
    Binding bindingDirectDead() {
        return BindingBuilder.bind(carDirectQueueDead()).to(couponDirectExchange()).with(directStateRoutingDead);
    }

    /**
     * 绑定将队列和交换机绑定, 并设置用于匹配键：directRouting
     *
     * @return
     */
    @Bean("bindingDirect")
    Binding bindingDirect() {
        return BindingBuilder.bind(carDirectQueue()).to(couponDirectExchange()).with(directStateRouting);
    }

    @Bean("bindingDirectCopy")
    Binding bindingDirectCopy() {
        return BindingBuilder.bind(carDirectQueueeCopy()).to(couponDirectExchange()).with(directStateRouting);
    }
}
