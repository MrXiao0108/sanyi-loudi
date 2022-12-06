package com.dzics.queueforwarding.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

/**
 * 初始化队列配置
 *
 * @author neverend
 */
@Configuration
@Slf4j
public class DirectRabbitConfig {

    /**
     * 本地 mq 地址
     *
     * @param host
     * @param port
     * @param username
     * @param password
     * @return
     */
    @Primary
    @Bean(name = "localConnectionFactory")
    public ConnectionFactory localConnectionFactory(@Value("${spring.rabbitmq.local.host}") String host,
                                                    @Value("${spring.rabbitmq.local.port}") int port,
                                                    @Value("${spring.rabbitmq.local.username}") String username,
                                                    @Value("${spring.rabbitmq.local.password}") String password) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }


    @Bean(name = "remoteConnectionFactory")
    public ConnectionFactory remoteConnectionFactory(@Value("${spring.rabbitmq.remote.host}") String host,
                                                     @Value("${spring.rabbitmq.remote.port}") int port,
                                                     @Value("${spring.rabbitmq.remote.username}") String username,
                                                     @Value("${spring.rabbitmq.remote.password}") String password) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }

    //  声明开发服务器rabbitTemplate
    @Primary
    @Bean(name = "localRabbitTemplate")
    public RabbitTemplate localRabbitTemplate(@Qualifier("localConnectionFactory") ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                String msg = new String(correlationData.getReturnedMessage().getBody());
                if (ack) {
                    log.info("设备状态信息->重新根据->交换机和路由键 发送到本地成功 msg:{}", msg);
                } else {
                    log.error("设备状态信息发送到本地失败 发送数据ID：{}, Msg:{},确认情况：{},原因：{}", correlationData.getId(), msg, ack, cause);
                }
            }
        });
        return rabbitTemplate;
    }

    //  声明测试服务器连接 rabbitTemplate
    @Bean(name = "remoteRabbitTemplate")
    public RabbitTemplate remoteRabbitTemplate(@Qualifier("remoteConnectionFactory") ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                String msg = new String(correlationData.getReturnedMessage().getBody());
                if (ack) {
                    log.info("设备状态信息->重新根据->交换机和路由键 发送到远程服务器 成功 msg:{}", msg);
                } else {
                    log.error("数据转发到 远程服务器 失败 发送数据ID：{}, Msg:{},确认情况：{},原因：{}", correlationData.getId(), msg, ack, cause);
                }
            }
        });
        return rabbitTemplate;
    }


    /**
     * 声明dev containerFactory
     *
     * @param rabbitListenerContainerFactoryConfigurer
     * @param connectionFactory
     * @return
     */
    @Bean(name = "localContainerFactory")
    public SimpleRabbitListenerContainerFactory devSimpleRabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer rabbitListenerContainerFactoryConfigurer,
            @Qualifier("localConnectionFactory") ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
        rabbitListenerContainerFactoryConfigurer.configure(containerFactory, connectionFactory);
        return containerFactory;
    }

    /**
     * 声明  test containerFactory
     *
     * @param rabbitListenerContainerFactoryConfigurer
     * @param connectionFactory
     * @return
     */
    @Bean(name = "remoteContainerFactory")
    public SimpleRabbitListenerContainerFactory testSimpleRabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer rabbitListenerContainerFactoryConfigurer,
            @Qualifier("remoteConnectionFactory") ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
        rabbitListenerContainerFactoryConfigurer.configure(containerFactory, connectionFactory);
        containerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return containerFactory;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
