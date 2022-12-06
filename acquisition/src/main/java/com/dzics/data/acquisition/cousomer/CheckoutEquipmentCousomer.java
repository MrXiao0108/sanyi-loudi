package com.dzics.data.acquisition.cousomer;

import com.alibaba.fastjson.JSONObject;
import com.dzics.common.model.custom.OrderIdLineId;
import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.custom.ReqWorkQrCodeOrder;
import com.dzics.common.model.entity.DzWorkpieceData;
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
 * 检测设备数据消费处理
 *
 * @author ZhangChengJun
 * Date 2021/9/30.
 * @since
 */
@Component
@Slf4j
public class CheckoutEquipmentCousomer {
    @Autowired
    public RedissonClient redissonClient;
    /**
     * 检测设备
     */
    @Value("${accq.read.cmd.queue.base.checkout.equipment}")
    private String queryName;
    @Value("${accq.read.cmd.queue.base.checkout.equipment.dead}")
    private String queryNameDead;
    @Autowired
    private AccDzWorkpieceDataService accDzWorkpieceDataService;
    @Autowired
    private AccCommunicationLogService accCommunicationLogService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private DeviceStatusPush deviceStatusPush;

    /**
     * 检测设备
     *
     * @param msg
     * @param deliveryTag
     * @param channel
     * @throws Throwable
     */
    @RabbitListener(queues = "${accq.read.cmd.queue.base.checkout.equipment}", containerFactory = "localContainerFactory")
    public void queueCheckoutEquipment(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        log.info("消费：{} 队列的消息:{}", queryName, msg);
        RLock lock = redissonClient.getLock(RedisPrefxKey.DZ_LOCK_DATA_04);
        try {
            lock.lock();
            RabbitmqMessage rabbitmqMessage = JSONObject.parseObject(msg, RabbitmqMessage.class);
            DzWorkpieceData dzWorkpieceData = accDzWorkpieceDataService.queueCheckoutEquipment(rabbitmqMessage);
            try {
                if (dzWorkpieceData != null) {
                    log.info("推送检测数据:{}", dzWorkpieceData);
                    try {
                        String orderNo = dzWorkpieceData.getOrderNo();
                        if (orderNo.equals("DZ-1871") || orderNo.equals("DZ-1872") || orderNo.equals("DZ-1873") ||
                                orderNo.equals("DZ-1874") || orderNo.equals("DZ-1875") || orderNo.equals("DZ-1876") ||
                                orderNo.equals("DZ-1877") || orderNo.equals("DZ-1878") || orderNo.equals("DZ-1879") ||
                                orderNo.equals("DZ-1880")) {
                            deviceStatusPush.sendUploadQualityParam(dzWorkpieceData);
                        }
                    } catch (Throwable throwable) {
                        log.error("检测数据发送到工控机异常：{},dzWorkpieceData: {}", throwable.getMessage(), JSONObject.toJSONString(dzWorkpieceData), throwable);
                    }
                    try {
                        String orderNo = dzWorkpieceData.getOrderNo();
                        String lineNo = dzWorkpieceData.getLineNo();
                        ReqWorkQrCodeOrder codeOrder = new ReqWorkQrCodeOrder();
                        codeOrder.setOrderNo(orderNo);
                        codeOrder.setLineNo(lineNo);
                        codeOrder.setQrCode(dzWorkpieceData.getProducBarcode());
                        OrderIdLineId orderIdLineId = cacheService.getOrderNoLineNoId(orderNo, lineNo);
                        codeOrder.setLineId(orderIdLineId.getLineId());
                        codeOrder.setOrderId(orderIdLineId.getOrderId());
                        deviceStatusPush.getWorkingFlow(codeOrder);
                    } catch (Throwable throwable) {
                        log.error("检测完成重置报工错误：{}, dzWorkpieceData: {} ", throwable.getMessage(), JSONObject.toJSONString(dzWorkpieceData), throwable);
                    }
                    try {
//                    默认检测发送
                        boolean isOk = deviceStatusPush.sendWorkpieceData(dzWorkpieceData);
                    } catch (Throwable throwable) {
                        log.error("推送检测数据到看板异常：:{}", throwable.getMessage(), throwable);
                    }
                    try {
//                    智能检测系统 看板推送
                        boolean isOk = deviceStatusPush.sendIntelligentDetection(dzWorkpieceData);
                    } catch (Throwable throwable) {
                        log.error("智能检测系统异常：{}", throwable.getMessage(), throwable);
                    }
//                    try {
////                    检测记录 看板推送
//                        boolean isOk = deviceStatusPush.sendDetection(dzWorkpieceData.getOrderNo(),dzWorkpieceData.getLineNo(),dzWorkpieceData.getQrCode());
//                    } catch (Throwable throwable) {
//                        log.error("检测记录 推送异常：{}", throwable.getMessage(), throwable);
//                    }
                } else {
                    log.warn("检测数据为空：{}", dzWorkpieceData);
                }
                accCommunicationLogService.saveRabbitmqMessage(rabbitmqMessage, false, true);
            } catch (Throwable throwable) {
                log.error("推送检测数据:{},异常：{}", JSONObject.toJSONString(dzWorkpieceData), throwable.getMessage(), throwable);
            }
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费数据处理失败V1: {} 队列的消息：{}", queryName, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            lock.unlock();
        }
    }

    @RabbitListener(queues = "${accq.read.cmd.queue.base.checkout.equipment.dead}", containerFactory = "localContainerFactory")
    public void queueCheckoutEquipmentDead(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        log.info("消费：{} 队列 超时消息 :{}", queryNameDead, msg);
        RLock lock = redissonClient.getLock(RedisPrefxKey.DZ_LOCK_DATA_04);
        try {
            lock.lock();
            RabbitmqMessage rabbitmqMessage = JSONObject.parseObject(msg, RabbitmqMessage.class);
            DzWorkpieceData dzWorkpieceData = accDzWorkpieceDataService.queueCheckoutEquipment(rabbitmqMessage);
            if (dzWorkpieceData != null) {
                log.info("推送检测数据:{}", dzWorkpieceData);
                try {
                    String orderNo = dzWorkpieceData.getOrderNo();
                    if (orderNo.equals("DZ-1871") || orderNo.equals("DZ-1872") || orderNo.equals("DZ-1873") ||
                            orderNo.equals("DZ-1874") || orderNo.equals("DZ-1875") || orderNo.equals("DZ-1876") ||
                            orderNo.equals("DZ-1877") || orderNo.equals("DZ-1878") || orderNo.equals("DZ-1879") ||
                            orderNo.equals("DZ-1880")) {
                        deviceStatusPush.sendUploadQualityParam(dzWorkpieceData);
                    }
                } catch (Throwable throwable) {
                    log.error("检测数据发送到工控机异常：{},dzWorkpieceData: {}", throwable.getMessage(), JSONObject.toJSONString(dzWorkpieceData), throwable);
                }
            } else {
                log.warn("检测数据为空：{}", dzWorkpieceData);
            }
            accCommunicationLogService.saveRabbitmqMessage(rabbitmqMessage, false, true);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费 超时队列 检测数据处理失败: {} 队列的消息：{}", queryNameDead, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            lock.unlock();
        }
    }


}
