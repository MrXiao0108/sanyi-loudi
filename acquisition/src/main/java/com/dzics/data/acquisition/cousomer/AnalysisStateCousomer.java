package com.dzics.data.acquisition.cousomer;

import com.alibaba.fastjson.JSONObject;
import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.model.constant.SysConfigDepart;
import com.dzics.data.acquisition.service.*;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 设备状态解析
 *
 * @author ZhangChengJun
 * Date 2021/10/8.
 * @since
 */
@Component
@Slf4j
public class AnalysisStateCousomer {

    @Autowired
    public RedissonClient redissonClient;

    /**
     * 状态
     */
    @Value("${accq.read.cmd.queue.base.state}")
    private String queueState;

    @Value("${accq.read.cmd.queue.base.state.copy}")
    private String queueStateCopy;

    @Value("${accq.read.cmd.queue.base.state.dead}")
    private String queueStateDead;

    @Autowired
    private AccqAnalysisStateService accqAnalysisStateService;

    @Autowired
    private DeviceStatusPush deviceStatusPush;

    @Autowired
    private AccCommunicationLogService accCommunicationLogService;
    @Autowired
    private AccStorageLocationService accStorageLocationService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Value("${equipment.status.change.routing}")
    private String equipmentStatusRoutingKey;
    @Value("${equipment.status.change.exchange}")
    private String equipmentStatusTopicExchange;


    @RabbitListener(queues = "${accq.read.cmd.queue.base.state.dead}", containerFactory = "localContainerFactory")
    public void analysisNumStateDead(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        log.warn("消费死亡队列：{} 队列的消息:{}", queueStateDead, msg);
//        try {
//            RabbitmqMessage rabbitmqMessage = JSONObject.parseObject(msg, RabbitmqMessage.class);
//            try {
//                accCommunicationLogService.saveRabbitmqMessage(rabbitmqMessage, true, true);
//            }catch (Throwable e){
//                log.error("死信dzics-dev-gather-v1-state-dead 保存设备状态队列日志或更新指令表状态异常：{}", e.getMessage(), e);
//            }
//        }catch(Throwable e){
//            log.error("消费数据处理失败队列: {} 的消息：{} ", queueState, msg, e);
//            channel.basicReject(deliveryTag, false);
//        }
        channel.basicAck(deliveryTag, true);
    }

    /**
     * 更新设备状态，处理数据不存储更新，只用于发送到前端
     *
     * @param msg
     * @param deliveryTag
     * @param channel
     * @throws Throwable
     */
    @RabbitListener(queues = "${accq.read.cmd.queue.base.state}", containerFactory = "localContainerFactory")
    public void analysisNumState(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        log.debug("消费：{} 队列的消息:{}", queueState, msg);
        try {
            RabbitmqMessage rabbitmqMessage = JSONObject.parseObject(msg, RabbitmqMessage.class);
            try {
//                String singleOrCluster = cacheService.getSystemConfigModel();
//                if (SysConfigDepart.RUN_MODEL.equals(singleOrCluster)) {
                accCommunicationLogService.saveRabbitmqMessage(rabbitmqMessage, true, true);
//                }
            } catch (Throwable e) {
                log.error("保存设备状态队列日志或更新指令表状态异常：{}", e.getMessage(), e);
            }
            try {
                DzEquipment dzEquipment = accqAnalysisStateService.analysisNumStatePush(rabbitmqMessage);
                if (dzEquipment != null) {
                    try {
                        deviceStatusPush.sendStateEquiment(dzEquipment);
                    } catch (Throwable e) {
                        log.error("推送设备状态到看板异常:{}", e.getMessage(), e);
                    }
                    try {
                        List<RabbitmqMessage> rabbitmqMessageList = accStorageLocationService.createRealTimeLogsDevice(dzEquipment);
                        deviceStatusPush.sendRabbitmqRealTimeLogsPush(rabbitmqMessageList);
                    } catch (Throwable e) {
                        log.error("发送设备状态变更日志到队列异常：{}", e.getMessage(), e);
                    }
                    try {
                        String depart = cacheService.getSystemConfigDepart();
                        //当前站点是菲仕 转发到菲仕MES设备状态上发队列
                        if (SysConfigDepart.FEISHI.equals(depart)) {
                            rabbitTemplate.convertAndSend(equipmentStatusTopicExchange, equipmentStatusRoutingKey, JSONObject.toJSONString(dzEquipment));
                        }
                    } catch (Throwable e) {
                        log.error("设备状态，转发到数据中心异常:{}", e.getMessage(), e);
                    }
                }
            } catch (Throwable throwable) {
                log.error("设置状态解析或发送到客户端错误:{}", throwable.getMessage(), throwable);
            }
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费数据处理失败队列: {} 的消息：{} ", queueState, msg, e);
            channel.basicReject(deliveryTag, false);
        }
    }


    /**
     * 更新设备状态 只处理数据，不发送到前端
     *
     * @param msg
     * @param deliveryTag
     * @param channel
     * @throws Throwable
     */
    @RabbitListener(queues = "${accq.read.cmd.queue.base.state.copy}", containerFactory = "localContainerFactory")
    public void analysisNumStateCopy(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        log.debug("消费：{} 队列的消息:{}", queueStateCopy, msg);
        try {
//            RabbitmqMessage rabbitmqMessage = JSONObject.parseObject(msg, RabbitmqMessage.class);
//            accCommunicationLogService.saveRabbitmqMessage(rabbitmqMessage, true, true);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费数据处理失败队列: {} 的消息：{} ", queueStateCopy, msg, e);
            channel.basicReject(deliveryTag, false);
        }
    }

//    @RabbitListener(queues = "dzics-dev-gather-v1-run-state", containerFactory = "localContainerFactory")
    public void anaysisRunState(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        try {
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            channel.basicReject(deliveryTag, false);
        }
    }
}
