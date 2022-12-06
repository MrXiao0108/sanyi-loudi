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
@Configuration
@Slf4j
@SuppressWarnings("ALL")
public class RabbitConfigTemplateO {

    /**
     * //设置开启Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数
     * rabbitTemplate.setMandatory(true);
     *
     * @param connectionFactory
     * @return
     */
    @Bean(name = "rabbitTemplate1")
    public RabbitTemplate createRabbitTemplate1(ConnectionFactory connectionFactory) {
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
//        没有找到交换机，调用 未找到队列调用
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());

            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate2")
    public RabbitTemplate rbTemp2(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate3")
    public RabbitTemplate rbTemp3(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate4")
    public RabbitTemplate rbTemp4(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate5")
    public RabbitTemplate rbTemp5(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.trace("UDP消息发送队列成功数据ID：{}", correlationData != null ? correlationData.getId() : null);
            } else {
                log.error("UDP消息发送交换机失败数据ID：{},消息：{}，确认情况：{}，原因:{}",
                        correlationData != null ? correlationData.getId() : null,
                        new String(correlationData.getReturnedMessage().getBody()), ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String obg = new String(message.getBody());
            log.error("发送消息到队列失败内容：{},回应码：{},回应信息：{} ,交换机:{},路由键：{}", obg, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    @Bean(name = "rabbitTemplate6")
    public RabbitTemplate rbTemp6(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate7")
    public RabbitTemplate rbTemp7(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate8")
    public RabbitTemplate rbTemp8(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate9")
    public RabbitTemplate rbTemp9(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate10")
    public RabbitTemplate rbTemp10(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate11")
    public RabbitTemplate rbTemp11(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate12")
    public RabbitTemplate rbTemp12(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate13")
    public RabbitTemplate rbTemp13(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate14")
    public RabbitTemplate rbTemp14(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate15")
    public RabbitTemplate rbTemp15(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate16")
    public RabbitTemplate rbTemp16(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate17")
    public RabbitTemplate rbTemp17(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate18")
    public RabbitTemplate rbTemp18(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate19")
    public RabbitTemplate rbTemp19(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate20")
    public RabbitTemplate rbTemp20(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate21")
    public RabbitTemplate rbTemp21(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate22")
    public RabbitTemplate rbTemp22(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate23")
    public RabbitTemplate rbTemp23(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate24")
    public RabbitTemplate rbTemp24(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate25")
    public RabbitTemplate rbTemp25(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate26")
    public RabbitTemplate rbTemp26(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate27")
    public RabbitTemplate rbTemp27(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate28")
    public RabbitTemplate rbTemp28(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate29")
    public RabbitTemplate rbTemp29(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate30")
    public RabbitTemplate rbTemp30(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate31")
    public RabbitTemplate rbTemp31(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate32")
    public RabbitTemplate rbTemp32(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate33")
    public RabbitTemplate rbTemp33(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate34")
    public RabbitTemplate rbTemp34(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate35")
    public RabbitTemplate rbTemp35(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate36")
    public RabbitTemplate rbTemp36(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate37")
    public RabbitTemplate rbTemp37(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate38")
    public RabbitTemplate rbTemp38(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate39")
    public RabbitTemplate rbTemp39(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate40")
    public RabbitTemplate rbTemp40(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate41")
    public RabbitTemplate rbTemp41(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate42")
    public RabbitTemplate rbTemp42(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate43")
    public RabbitTemplate rbTemp43(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate44")
    public RabbitTemplate rbTemp44(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate45")
    public RabbitTemplate rbTemp45(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate46")
    public RabbitTemplate rbTemp46(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate47")
    public RabbitTemplate rbTemp47(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate48")
    public RabbitTemplate rbTemp48(ConnectionFactory connectionFactory) {
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

    @Bean(name = "rabbitTemplate49")
    public RabbitTemplate rbTemp49(ConnectionFactory connectionFactory) {
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
