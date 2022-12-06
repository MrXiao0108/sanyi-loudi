package com.dzics.data.acquisition.cousomer;

import com.alibaba.fastjson.JSONObject;
import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.response.Result;
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
 * 监听二维码上传，绑定产品检测数据
 */
@Component
@Slf4j
public class BindingQRCodeCousomer {
    @Value("${accq.product.qrode.up.query}")
    private String bindingqrcode;
    @Autowired
    public RedissonClient redissonClient;
    @Autowired
    public BindingQRCodeService bindingqrcodeservice;

    @Autowired
    private AccCommunicationLogService accCommunicationLogService;


    /**
     * 获取检测数据二维码 队列
     *
     * @param msg
     * @param deliveryTag
     * @param channel
     * @throws Throwable
     */
    @RabbitListener(queues = "${accq.product.qrode.up.query}", containerFactory = "localContainerFactory")
    public void dzEncasementRecordState(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        log.debug("消费队列: {} 的消息:{}", bindingqrcode, msg);
        RLock lock = redissonClient.getLock(RedisPrefxKey.DZ_LOCK_DATA_03);
        try {
            lock.lock();
            RabbitmqMessage rabbitmqMessage = JSONObject.parseObject(msg, RabbitmqMessage.class);
            try {
                Result result = bindingqrcodeservice.processingData(rabbitmqMessage);
            } catch (Throwable throwable) {
                log.error("处理二维码错误：{}", throwable.getMessage(), throwable);
            }
            accCommunicationLogService.saveRabbitmqMessage(rabbitmqMessage, true,true);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费数据处理失败队列:{} ,消息: {}", bindingqrcode, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            lock.unlock();
        }
    }


}
