package com.dzics.data.acquisition.cousomer;

import com.alibaba.fastjson.JSONObject;
import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.response.Result;
import com.dzics.data.acquisition.config.redis.RedisPrefxKey;
import com.dzics.data.acquisition.netty.SocketServerTemplate;
import com.dzics.data.acquisition.service.AccCommunicationLogService;
import com.dzics.data.acquisition.service.AccqAnalysisStateService;
import com.dzics.data.acquisition.service.DeviceStatusPush;
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
 * 刀具队列数据处理类
 *
 * @author ZhangChengJun
 * Date 2021/9/30.
 * @since
 */
@Component
@Slf4j
public class CuttingToolCusomer {
    @Autowired
    private SocketServerTemplate socketServerTemplate;
    @Autowired
    private AccqAnalysisStateService accqAnalysisStateService;
    @Autowired
    public RedissonClient redissonClient;
    @Autowired
    private AccCommunicationLogService accCommunicationLogService;
    @Autowired
    private DeviceStatusPush deviceStatusPush;
    /**
     * 刀具检测
     */
    @Value("${accq.cutting.tool.detection}")
    private String toolDetection;

    @Value("${accq.cutting.tool.detection.dead}")
    private String toolDetectionDead;

    @RabbitListener(queues = "${accq.cutting.tool.detection.dead}", containerFactory = "localContainerFactory")
    public void cuttingToolDetectionDead(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        log.warn("处理刀具死亡队列消息: {} 队列的消息:{}", toolDetectionDead, msg);
        channel.basicAck(deliveryTag, true);
    }

    /**
     * 刀具信息
     *
     * @param msg
     * @param deliveryTag
     * @param channel
     * @throws Throwable
     */
    @RabbitListener(queues = "${accq.cutting.tool.detection}", containerFactory = "localContainerFactory")
    public void cuttingToolDetection(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        log.debug("消费: {} 队列的消息:{}", toolDetection, msg);
        RLock lock = redissonClient.getLock(RedisPrefxKey.DZ_LOCK_DATA_10);
        try {
            lock.lock();
            RabbitmqMessage rabbitmqMessage = JSONObject.parseObject(msg, RabbitmqMessage.class);
            Result result = accqAnalysisStateService.getEqToolInfoList(rabbitmqMessage);
            try {
                if (result != null) {

                    try {
                        boolean isOk = deviceStatusPush.sendToolDetection(rabbitmqMessage, result);
                    } catch (Throwable throwable) {
                        log.error("单项刀具检测推送前端错误：", throwable);
                    }
                    //推送看板
//                    socketServerTemplate.sendMessage(SocketMessageType.TOOL_TEST_DATA,SocketMessageType.TOOL_TEST_DATA+rabbitmqMessage.getOrderCode()+rabbitmqMessage.getLineNo(), result);
                }
            } catch (Throwable e) {
                log.error("推送消息到看板失败：", e);
            }
            //刀具日志保存
            accCommunicationLogService.saveRabbitmqMessage(rabbitmqMessage, true,true);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费数据处理失败: {} 队列的消息：{}", toolDetection, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            lock.unlock();
        }

    }


}
