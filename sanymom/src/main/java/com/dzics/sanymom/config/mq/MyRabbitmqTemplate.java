package com.dzics.sanymom.config.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname MyRabbitmqTemplate
 * @Description 描述
 * @Date 2022/5/27 13:27
 * @Created by NeverEnd
 */
@Configuration
@Slf4j
public class MyRabbitmqTemplate {
    @Bean(name = "rabbitTemplate")
    public RabbitTemplate getRabbitmqTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("发送到队列成功：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                log.error("发送到交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }
}
