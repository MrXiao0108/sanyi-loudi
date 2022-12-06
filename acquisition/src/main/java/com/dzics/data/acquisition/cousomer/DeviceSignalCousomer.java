package com.dzics.data.acquisition.cousomer;

import com.alibaba.fastjson.JSONObject;
import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.entity.DzEquipmentProNumSignal;
import com.dzics.common.model.constant.LogClientType;
import com.dzics.data.acquisition.config.redis.RedisPrefxKey;
import com.dzics.data.acquisition.service.AccCommunicationLogService;
import com.dzics.data.acquisition.service.AccqAnalysisNumSignalService;
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
 * 脉冲计数处理数据
 *
 * @author ZhangChengJun
 * Date 2021/9/30.
 * @since
 */
@Component
@Slf4j
public class DeviceSignalCousomer {
    /**
     * 脉冲队列
     */
    @Value("${accq.read.cmd.queue.base.pulse.signal}")
    private String queuePylseSignal;

    @Autowired
    public RedissonClient redissonClient;
    @Autowired
    private AccqAnalysisNumSignalService accqAnalysisNumSignalService;
    @Autowired
    private AccCommunicationLogService accCommunicationLogService;
    /**
     * 脉冲生产数据
     *
     * @param msg
     * @param deliveryTag
     * @param channel
     * @throws Throwable
     */
    @RabbitListener(queues = "${accq.read.cmd.queue.base.pulse.signal}", containerFactory = "localContainerFactory")
    public void queuePylseSignal(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        log.debug("消费：{} 队列的消息:{}", queuePylseSignal, msg);
        RLock lock = redissonClient.getLock(RedisPrefxKey.DZ_LOCK_DATA_05);
        try {
            lock.lock();
            RabbitmqMessage rabbitmqMessage = JSONObject.parseObject(msg, RabbitmqMessage.class);
            boolean isCheck = accqAnalysisNumSignalService.queuePylseSignalCheck(rabbitmqMessage);
            rabbitmqMessage.setCheck(isCheck);
            if (isCheck) {
                try {
                    DzEquipmentProNumSignal dzEquipmentProNumSignal = accqAnalysisNumSignalService.queuePylseSignal(rabbitmqMessage);
                    try {
//                    需要补偿的数值
                        if (dzEquipmentProNumSignal != null) {
                            try {
                                accqAnalysisNumSignalService.setRedisSignalValue(dzEquipmentProNumSignal.getEquimentId(), dzEquipmentProNumSignal.getSendSignalTime());
                            } catch (Throwable e) {
                                log.error("设置缓存频率错误：设备ID:{}", dzEquipmentProNumSignal.getEquimentId(), e);
                            }
                            long compensate = accqAnalysisNumSignalService.compensate(dzEquipmentProNumSignal).longValue();
                            if (compensate > 0) {
                                queuePylseSignalCompenState(compensate, rabbitmqMessage, dzEquipmentProNumSignal.getEquimentId());
                            }
                        }

                    } catch (Throwable throwable) {
                        log.error("执行补偿业务流程错误：{}", throwable);
                    }

                } catch (Throwable throwable) {
                    log.error("处理脉冲信号异常：{}", throwable);
                }
            }
            accCommunicationLogService.saveRabbitmqMessage(rabbitmqMessage, true,true);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            Thread.sleep(3000);
            log.error("消费数据处理失败:{} 队列的消息：{}", queuePylseSignal, msg, e);
            channel.basicReject(deliveryTag, true);
        } finally {
            lock.unlock();
        }
    }


    private synchronized void queuePylseSignalCompenState(long compensate, RabbitmqMessage rabbitmqMessage, Long equimentId) {
        log.info("进行补偿次数：{},订单：{},产线序号：{},设备类型：{}，设备序号：{}", compensate, rabbitmqMessage.getOrderCode(), rabbitmqMessage.getLineNo(), rabbitmqMessage.getDeviceType(), rabbitmqMessage.getDeviceCode());
        rabbitmqMessage.setClientId(LogClientType.ACC_SIGNAL);
        int sumProSig = 0;
        try {
            for (long i = 0; i < compensate; i++) {
                try {
                    log.info("开始补偿脉冲      sumProSig :{}", sumProSig);
                    try {
                        DzEquipmentProNumSignal dzEquipmentProNumSignal = accqAnalysisNumSignalService.queuePylseSignal(rabbitmqMessage);
                        sumProSig++;
                        log.info("补偿一次脉冲结束正常  sumProSig :{}", sumProSig);
                    } catch (Throwable throwable) {
                        log.error("补偿一次脉冲结束异常  sumProSig :{}", sumProSig, throwable);
                    }
                    accCommunicationLogService.saveRabbitmqMessage(rabbitmqMessage, true,true);
                } catch (Throwable throwable) {
                    log.error("存储脉冲信号 rabbitmqMessage：{},错误:{}", rabbitmqMessage, throwable);
                }
            }
        } catch (Throwable e) {
            log.error("进行补偿执行 accqAnalysisNumSignalService.queuePylseSignal 错误：{}", e);
        }
        log.info("补偿完成次数：{}", sumProSig);
    }
}
