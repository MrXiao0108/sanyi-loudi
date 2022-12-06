package com.dzics.data.acquisition.config.mq;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * 初始化队列配置
 *
 * @author neverend
 */
@Configuration
public class DirectRabbitConfig {

    /**
     * 本地 mq 地址
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
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        return rabbitTemplate;
    }

    //  声明测试服务器连接 rabbitTemplate
    @Bean(name = "remoteRabbitTemplate")
    public RabbitTemplate remoteRabbitTemplate(@Qualifier("remoteConnectionFactory") ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
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
        return containerFactory;
    }

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(20000);
        factory.setReadTimeout(20000);
        return factory;
    }
}
