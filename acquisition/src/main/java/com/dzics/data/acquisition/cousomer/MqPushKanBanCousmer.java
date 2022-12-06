package com.dzics.data.acquisition.cousomer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dzics.data.acquisition.constant.PushEnumType;
import com.dzics.data.acquisition.model.PushKanbanBase;
import com.dzics.data.acquisition.model.SocketDowmSum;
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
 * 发送到看板消息监听
 *
 * @author ZhangChengJun
 * Date 2021/11/19.
 * @since
 */
@Component
@Slf4j
public class MqPushKanBanCousmer {

    @Value("${push.kanban.queue.simple}")
    private String pushQueue;

    @Autowired
    private DeviceStatusPush deviceStatusPush;

    @RabbitListener(queues = "${push.kanban.queue.simple}", containerFactory = "localContainerFactory")
    public void pushKanbanQueue(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        try {
            log.debug("处理队列: {} 数据:{}", pushQueue, msg);
            PushKanbanBase kanbanBase = JSONObject.parseObject(msg, PushKanbanBase.class);
            if (PushEnumType.DOWN_SUM.equals(kanbanBase.getType())) {
                SocketDowmSum dowmSum =  JSONObject.parseObject(JSON.toJSONString(kanbanBase.getData()),SocketDowmSum.class);
                deviceStatusPush.senddeviceStopStatusPush(dowmSum);
            }
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("处理队列: {} 数据:{} 失败:{}", pushQueue, msg, e.getMessage(), e);
            channel.basicReject(deliveryTag, false);
        }
    }


    @Value("${push.kanban.queue.dead.simple}")
    private String pushDeadQueue;

    @RabbitListener(queues = "${push.kanban.queue.dead.simple}", containerFactory = "localContainerFactory")
    public void pushKanbanQueueDead(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        try {
            log.warn("死亡队列丢弃: {},处理发送到看板数据:{}", pushDeadQueue, msg);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("死亡队列丢弃: {},处理发送到看板数据:{} 失败:{}", pushDeadQueue, msg, e.getMessage(), e);
            channel.basicReject(deliveryTag, false);
        }
    }

}
