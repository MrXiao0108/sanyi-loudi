package com.dzics.udp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 发送消息回调异常
 *
 * @author neverend
 */
@SuppressWarnings("ALL")
@Configuration
@Slf4j
public class RabbitConfigTemplateT {

    @Bean(name = "rabbitTemplate50")
    public RabbitTemplate rbTemp50(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate51")
    public RabbitTemplate rbTemp51(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate52")
    public RabbitTemplate rbTemp52(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate53")
    public RabbitTemplate rbTemp53(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate54")
    public RabbitTemplate rbTemp54(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate55")
    public RabbitTemplate rbTemp55(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate56")
    public RabbitTemplate rbTemp56(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate57")
    public RabbitTemplate rbTemp57(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate58")
    public RabbitTemplate rbTemp58(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate59")
    public RabbitTemplate rbTemp59(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate60")
    public RabbitTemplate rbTemp60(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate61")
    public RabbitTemplate rbTemp61(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate62")
    public RabbitTemplate rbTemp62(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate63")
    public RabbitTemplate rbTemp63(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate64")
    public RabbitTemplate rbTemp64(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate65")
    public RabbitTemplate rbTemp65(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate66")
    public RabbitTemplate rbTemp66(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate67")
    public RabbitTemplate rbTemp67(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate68")
    public RabbitTemplate rbTemp68(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate69")
    public RabbitTemplate rbTemp69(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate70")
    public RabbitTemplate rbTemp70(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate71")
    public RabbitTemplate rbTemp71(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate72")
    public RabbitTemplate rbTemp72(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate73")
    public RabbitTemplate rbTemp73(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate74")
    public RabbitTemplate rbTemp74(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate75")
    public RabbitTemplate rbTemp75(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate76")
    public RabbitTemplate rbTemp76(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate77")
    public RabbitTemplate rbTemp77(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate78")
    public RabbitTemplate rbTemp78(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate79")
    public RabbitTemplate rbTemp79(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate80")
    public RabbitTemplate rbTemp80(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate81")
    public RabbitTemplate rbTemp81(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate82")
    public RabbitTemplate rbTemp82(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate83")
    public RabbitTemplate rbTemp83(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate84")
    public RabbitTemplate rbTemp84(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate85")
    public RabbitTemplate rbTemp85(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate86")
    public RabbitTemplate rbTemp86(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate87")
    public RabbitTemplate rbTemp87(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate88")
    public RabbitTemplate rbTemp88(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate89")
    public RabbitTemplate rbTemp89(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate90")
    public RabbitTemplate rbTemp90(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate91")
    public RabbitTemplate rbTemp91(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate92")
    public RabbitTemplate rbTemp92(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate93")
    public RabbitTemplate rbTemp93(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate94")
    public RabbitTemplate rbTemp94(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate95")
    public RabbitTemplate rbTemp95(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate96")
    public RabbitTemplate rbTemp96(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate97")
    public RabbitTemplate rbTemp97(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate98")
    public RabbitTemplate rbTemp98(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate99")
    public RabbitTemplate rbTemp99(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate100")
    public RabbitTemplate rbTemp100(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                 log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}", correlationData != null ? correlationData.getId() : null, new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

}
