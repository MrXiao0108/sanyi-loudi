package com.dzics.data.acquisition.cousomer;

import com.dzics.data.acquisition.service.DeviceStatusPush;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @author ZhangChengJun
 * Date 2021/5/24.
 * @since
 */
@Component
@Slf4j
public class RefreshCousomer {
    @Value("${dzics.html.queue.kanban.Refresh}")
    private String queueRefresh;
    @Autowired
    private DeviceStatusPush deviceStatusPush;

    @RabbitListener(queues = "${dzics.html.queue.kanban.Refresh}")
    public void dzRefresh(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        try {
            log.debug("消费刷新页面指令信息:{}, 队列的消息:{}", queueRefresh, msg);
            deviceStatusPush.dzRefresh(msg);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费刷新页面指令信息:{},队列的消息：{}", queueRefresh, msg, e);
            channel.basicReject(deliveryTag, false);
        }
    }

}
