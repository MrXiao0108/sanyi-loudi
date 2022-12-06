package com.dzics.udp.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 初始化队列配置
 *
 * @author neverend
 */
@Configuration
public class DirectRabbitConfig {
    /**
     * UDP状态
     */
    @Value("${dzics.udp.queue.state}")
    private String queue;
    @Value("${dzics.udp.exchange.state}")
    private String directExchange;
    @Value("${dzics.udp.routing.state}")
    private String directRouting;

    /**
     * durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
     * exclusive:默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
     * autoDelete:是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。
     * return new Queue("TestDirectQueue",true,true,false);
     *
     * @return 创建队列
     */
    @Bean(name = "udpQuery")
    public Queue udpQuery() {
        return new Queue(queue, true);
    }

    /**
     * 创建交换机
     *
     * @return
     */
    @Bean("udpExchange")
    DirectExchange udpExchange() {
        return new DirectExchange(directExchange, true, false);
    }

    /**
     * 绑定将队列和交换机绑定, 并设置用于匹配键：directRouting
     *
     * @return
     */
    @Bean("udpRouting")
    Binding bindingDirect() {
        return BindingBuilder.bind(udpQuery()).to(udpExchange()).with(directRouting);
    }


    /**
     * UDP信号累计数量
     */
    @Value("${dzics.udp.queue.signal}")
    private String queueSignal;
    @Value("${dzics.udp.exchange.signal}")
    private String directExchangeSignal;
    @Value("${dzics.udp.routing.signal}")
    private String directRoutingSignal;


    @Bean(name = "udpQuerySignal")
    public Queue udpQuerySignal() {
        return new Queue(queueSignal, true);
    }

    /**
     * 创建交换机
     *
     * @return
     */
    @Bean("udpExchangeSignal")
    DirectExchange udpExchangeSignal() {
        return new DirectExchange(directExchangeSignal, true, false);
    }

    /**
     * 绑定将队列和交换机绑定, 并设置用于匹配键：directRouting
     *
     * @return
     */
    @Bean("udpRoutingSignal")
    Binding bindingDirectSignal() {
        return BindingBuilder.bind(udpQuerySignal()).to(udpExchangeSignal()).with(directRoutingSignal);
    }


    /**
     * 实时数量
     */
    @Value("${dzics.udp.queue.realRimeNumber}")
    private String queueRealRimeNumberl;
    @Value("${dzics.udp.exchange.realRimeNumber}")
    private String directExchangeRealRimeNumberl;
    @Value("${dzics.udp.routing.realRimeNumber}")
    private String directRoutingRealRimeNumberl;



    @Bean(name = "udpQueryRealRimeNumber")
    public Queue udpQueryRealRimeNumberl() {
        return new Queue(queueRealRimeNumberl, true);
    }

    /**
     * 创建交换机
     *
     * @return
     */
    @Bean("udpExchangeRealRimeNumber")
    DirectExchange udpExchangeRealRimeNumberl() {
        return new DirectExchange(directExchangeRealRimeNumberl, true, false);
    }

    /**
     * 绑定将队列和交换机绑定, 并设置用于匹配键：directRouting
     *
     * @return
     */
    @Bean("udpRoutingRealRimeNumber")
    Binding bindingDirectRealRimeNumberl() {
        return BindingBuilder.bind(udpQueryRealRimeNumberl()).to(udpExchangeRealRimeNumberl()).with(directRoutingRealRimeNumberl);
    }

}
