package com.dzics.data.acquisition.cousomer;

import com.alibaba.fastjson.JSONObject;
import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.entity.DzEquipmentProNum;
import com.dzics.data.acquisition.config.redis.RedisPrefxKey;
import com.dzics.data.acquisition.service.*;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * 数据处理消费
 *
 * @author ZhangChengJun
 * Date 2021/2/9.
 * @since
 */
@Component
@Slf4j
public class AnalysisNumCousomer {

    @Autowired
    public RedissonClient redissonClient;
    @Autowired
    private AccqAnalysisNumService accqAnalysisNumService;

    /**
     * 基础数据底层发送过来的
     */
    @Value("${accq.read.cmd.queue.base}")
    private String queue;

    @Autowired
    private AccCommunicationLogService accCommunicationLogService;

    /**
     * 生产数据
     *
     * @param msg
     * @param deliveryTag
     * @param channel
     * @throws Throwable
     */
    @RabbitListener(queues = "${accq.read.cmd.queue.base}", containerFactory = "localContainerFactory")
    public void analysisNum(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        log.debug("消费：{}, 队列的消息:{}", queue, msg);
        RLock lock = redissonClient.getLock(RedisPrefxKey.LOCK_DATA_SUM_KEY_01);
        try {
            lock.lock();
            RabbitmqMessage rabbitmqMessage = JSONObject.parseObject(msg, RabbitmqMessage.class);
            DzEquipmentProNum dzEquipmentProNum = accqAnalysisNumService.analysisNum(rabbitmqMessage);
            accCommunicationLogService.saveRabbitmqMessage(rabbitmqMessage, true,true);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费:{} 数据处理失败消息：{}", queue, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            lock.unlock();
        }
    }

}
