package com.dzics.queueforwarding.mq.cousomer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * @author ZhangChengJun
 * Date 2021/4/21
 */
@Component
@Slf4j
public class RabbitMQConsumer {
    /**
     * 指定远程服务器rabbitTemplate
     */
    @Qualifier(value = "remoteRabbitTemplate")
    @Autowired
    private RabbitTemplate remoteRabbitTemplate;

    @Qualifier(value = "localRabbitTemplate")
    @Autowired
    private RabbitTemplate localRabbitTemplate;

    /**
     * 底层读取队列------1
     */
    @Value("${accq.read.cmd.queue.base}")
    private String queueBase;
    @Value("${accq.read.cmd.queue.base.routing}")
    private String queueBaseRouting;
    @Value("${accq.read.cmd.queue.base.exchange}")
    private String queueBaseExchange;

    @RabbitListener(queues = "${accq.read.cmd.queue.base}", containerFactory = "localContainerFactory")
    public void receiveState(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        log.debug("转发：{} 队列的消息:{}", queueBase, msg);
        try {
            String uid = UUID.randomUUID().toString();
            Message message = MessageBuilder.withBody(msg.getBytes("UTF-8"))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding("UTF-8")
                    .setMessageId(uid).build();
            CorrelationData correlationData = new CorrelationData(uid);
            correlationData.setReturnedMessage(message);
            remoteRabbitTemplate.convertAndSend(queueBaseExchange, queueBaseRouting, msg,correlationData);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("转发错误：{}, 队列的消息:{},错误信息：{}", queueBase, msg, e);
            channel.basicReject(deliveryTag, true);
        }
    }

    /**
     * 状态队列配置------2
     */



    @Value("${accq.read.cmd.queue.base.state.handle}")
    private String queueStateHandle;
    @Value("${accq.state.routing.handle}")
    private String directStateRoutingHandle;
    @Value("${accq.read.cmd.queue.base.state}")
    private String baseState;
    @Value("${accq.state.exchange}")
    private String stateExchange;
    @Value("${accq.state.routing}")
    private String stateRouting;
    @Value("${accq.state.routing.copy}")
    private String directStateRoutingCopy;
    @Value("${accq.read.cmd.queue.base.state.copy}")
    private String baseStateCopy;
    @Value("${accq.state.routing}")
    private String directStateRouting;
    @Value("${accq.read.cmd.queue.base.state.dead}")
    private String queueBaseStateDead;
    @Value("${accq.state.routing.dead}")
    private String queueBaseStateRoutingDead;
    @RabbitListener(queues = "${accq.read.cmd.queue.base.state.handle}", containerFactory = "localContainerFactory")
    public void baseStateHandle(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        log.debug("转发：{} 队列的消息:{}", queueStateHandle, msg);
        try {
            String uid = UUID.randomUUID().toString();
            Message message = MessageBuilder.withBody(msg.getBytes("UTF-8"))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding("UTF-8")
                    .setMessageId(uid).build();
            CorrelationData correlationData = new CorrelationData(uid);
            correlationData.setReturnedMessage(message);
            localRabbitTemplate.convertAndSend(stateExchange, directStateRouting, msg,correlationData);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("转发错误：{}, 队列的消息:{},错误信息：{}", queueStateHandle, msg, e);
            channel.basicReject(deliveryTag, true);
        }
    }


    @RabbitListener(queues = "${accq.read.cmd.queue.base.state}", containerFactory = "localContainerFactory")
    public void baseState(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        log.debug("转发：{} 队列的消息:{}", baseState, msg);
        try {
            String uid = UUID.randomUUID().toString();
            Message message = MessageBuilder.withBody(msg.getBytes("UTF-8"))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding("UTF-8")
                    .setMessageId(uid).build();
            CorrelationData correlationData = new CorrelationData(uid);
            correlationData.setReturnedMessage(message);
            remoteRabbitTemplate.convertAndSend(stateExchange, directStateRoutingHandle, msg,correlationData);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("转发错误：{}, 队列的消息:{},错误信息：{}", baseState, msg, e);
            channel.basicReject(deliveryTag, true);
        }
    }


    @RabbitListener(queues = "${accq.read.cmd.queue.base.state.copy}", containerFactory = "localContainerFactory")
    public void baseStateCopy(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        log.debug("转发：{} 队列的消息:{}", baseStateCopy, msg);
        try {
            String uid = UUID.randomUUID().toString();
            Message message = MessageBuilder.withBody(msg.getBytes("UTF-8"))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding("UTF-8")
                    .setMessageId(uid).build();
            CorrelationData correlationData = new CorrelationData(uid);
            correlationData.setReturnedMessage(message);
            remoteRabbitTemplate.convertAndSend(stateExchange, directStateRoutingCopy, msg,correlationData);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("转发错误：{}, 队列的消息:{},错误信息：{}", baseStateCopy, msg, e);
            channel.basicReject(deliveryTag, true);
        }
    }

    @RabbitListener(queues = "${accq.read.cmd.queue.base.state.dead}", containerFactory = "localContainerFactory")
    public void queueBaseStateDead(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        log.debug("转发：{} 队列的消息:{}", queueBaseStateDead, msg);
        try {
            String uid = UUID.randomUUID().toString();
            Message message = MessageBuilder.withBody(msg.getBytes("UTF-8"))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding("UTF-8")
                    .setMessageId(uid).build();
            CorrelationData correlationData = new CorrelationData(uid);
            correlationData.setReturnedMessage(message);
            remoteRabbitTemplate.convertAndSend(stateExchange, queueBaseStateRoutingDead, msg,correlationData);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("转发错误：{}, 队列的消息:{},错误信息：{}", queueBaseStateDead, msg, e);
            channel.basicReject(deliveryTag, true);
        }
    }

    /**
     * 脉冲信号队列------4
     */
    @Value("${accq.read.cmd.queue.base.pulse.signal}")
    private String basePulseSignal;
    @Value("${accq.pulse.signal.exchange}")
    private String pulseSignalExchange;
    @Value("${accq.pulse.signal.routing}")
    private String pulseSignalRouting;

    @RabbitListener(queues = "${accq.read.cmd.queue.base.pulse.signal}", containerFactory = "localContainerFactory")
    public void basePulseSignal(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        log.debug("转发：{} 队列的消息:{}", basePulseSignal, msg);
        try {
            String uid = UUID.randomUUID().toString();
            Message message = MessageBuilder.withBody(msg.getBytes("UTF-8"))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding("UTF-8")
                    .setMessageId(uid).build();
            CorrelationData correlationData = new CorrelationData(uid);
            correlationData.setReturnedMessage(message);
            remoteRabbitTemplate.convertAndSend(pulseSignalExchange, pulseSignalRouting, msg,correlationData);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("转发错误：{}, 队列的消息:{},错误信息：{}", basePulseSignal, msg, e);
            channel.basicReject(deliveryTag, true);
        }
    }

    /**
     * 检测设备------5
     */
    @Value("${accq.read.cmd.queue.base.checkout.equipment}")
    private String checkoutEquipment;
    @Value("${accq.checkout.equipment.exchange}")
    private String equipmentExchange;
    @Value("${accq.checkout.equipment.routing}")
    private String equipmentRouting;

    @RabbitListener(queues = "${accq.read.cmd.queue.base.checkout.equipment}", containerFactory = "localContainerFactory")
    public void checkoutEquipment(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        log.debug("转发：{} 队列的消息:{}", checkoutEquipment, msg);
        try {
            String uid = UUID.randomUUID().toString();
            Message message = MessageBuilder.withBody(msg.getBytes("UTF-8"))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding("UTF-8")
                    .setMessageId(uid).build();
            CorrelationData correlationData = new CorrelationData(uid);
            correlationData.setReturnedMessage(message);
            remoteRabbitTemplate.convertAndSend(equipmentExchange, equipmentRouting, msg,correlationData);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("转发错误：{}, 队列的消息:{},错误信息：{}", checkoutEquipment, msg, e);
            channel.basicReject(deliveryTag, true);
        }
    }

    /**
     * 实时日志信息队列------6
     */
    @Value("${accq.read.cmd.queue.equipment.realTime}")
    private String equipmentRealTime;
    @Value("${accq.realTime.equipment.exchange}")
    private String equipmentRealTimeExchange;
    @Value("${accq.realTime.equipment.routing}")
    private String equipmentRealTimeRouting;

    @RabbitListener(queues = "${accq.read.cmd.queue.equipment.realTime}", containerFactory = "localContainerFactory")
    public void equipmentRealTime(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        log.debug("转发：{} 队列的消息:{}", equipmentRealTime, msg);
        try {
            String uid = UUID.randomUUID().toString();
            Message message = MessageBuilder.withBody(msg.getBytes("UTF-8"))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding("UTF-8")
                    .setMessageId(uid).build();
            CorrelationData correlationData = new CorrelationData(uid);
            correlationData.setReturnedMessage(message);
            remoteRabbitTemplate.convertAndSend(equipmentRealTimeExchange, equipmentRealTimeRouting, msg,correlationData);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("转发错误：{}, 队列的消息:{},错误信息：{}", equipmentRealTime, msg, e);
            channel.basicReject(deliveryTag, true);
        }
    }

    /**
     * 刀具检测数据------7
     */
    @Value("${accq.cutting.tool.detection}")
    private String toolDetection;
    @Value("${accq.cutting.tool.detection.exchange}")
    private String detectionExchange;
    @Value("${accq.cutting.tool.detection.routing}")
    private String detectionRouting;

    @RabbitListener(queues = "${accq.cutting.tool.detection}", containerFactory = "localContainerFactory")
    public void toolDetection(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        log.debug("转发：{} 队列的消息:{}", toolDetection, msg);
        try {
            String uid = UUID.randomUUID().toString();
            Message message = MessageBuilder.withBody(msg.getBytes("UTF-8"))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding("UTF-8")
                    .setMessageId(uid).build();
            CorrelationData correlationData = new CorrelationData(uid);
            correlationData.setReturnedMessage(message);
            remoteRabbitTemplate.convertAndSend(detectionExchange, detectionRouting, msg,correlationData);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("转发错误：{}, 队列的消息:{},错误信息：{}", toolDetection, msg, e);
            channel.basicReject(deliveryTag, true);
        }
    }

    /**
     * 机床告警日志------8
     */
    @Value("${accq.tool.alarm.logs}")
    private String toolAlarmLogs;
    @Value("${accq.tool.alarm.logs.routing}")
    private String alarmLogsRouting;
    @Value("${accq.tool.alarm.logs.routing.exchange}")
    private String alarmLogsExchange;

    @RabbitListener(queues = "${accq.tool.alarm.logs}", containerFactory = "localContainerFactory")
    public void toolAlarmLogs(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        log.debug("转发：{} 队列的消息:{}", toolAlarmLogs, msg);
        try {
            String uid = UUID.randomUUID().toString();
            Message message = MessageBuilder.withBody(msg.getBytes("UTF-8"))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding("UTF-8")
                    .setMessageId(uid).build();
            CorrelationData correlationData = new CorrelationData(uid);
            correlationData.setReturnedMessage(message);
            remoteRabbitTemplate.convertAndSend(alarmLogsExchange, alarmLogsRouting, msg,correlationData);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("转发错误：{}, 队列的消息:{},错误信息：{}", toolAlarmLogs, msg, e);
            channel.basicReject(deliveryTag, true);
        }
    }

    /**
     * 报工位置队列------9
     */
    @Value("${accq.product.position.query}")
    private String productPosition;
    @Value("${accq.product.position.routing}")
    private String productPositionRouting;
    @Value("${accq.product.position.exchange}")
    private String productPositionExchange;

    @RabbitListener(queues = "${accq.product.position.query}", containerFactory = "localContainerFactory")
    public void productPosition(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        log.debug("转发：{} 队列的消息:{}", productPosition, msg);
        try {
            String uid = UUID.randomUUID().toString();
            Message message = MessageBuilder.withBody(msg.getBytes("UTF-8"))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding("UTF-8")
                    .setMessageId(uid).build();
            CorrelationData correlationData = new CorrelationData(uid);
            correlationData.setReturnedMessage(message);
            remoteRabbitTemplate.convertAndSend(productPositionExchange, productPositionRouting, msg,correlationData);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("转发错误：{}, 队列的消息:{},错误信息：{}", productPosition, msg, e);
            channel.basicReject(deliveryTag, true);
        }
    }

    /**
     * 华培绑定检测数据二维码队列消息底层上发------12
     */
    @Value("${accq.product.qrode.up.query}")
    private String productQrodeUp;
    @Value("${accq.product.qrode.up.routing}")
    private String productQrodeUpRouting;
    @Value("${accq.product.qrode.up.exchange}")
    private String productQrodeUpExchange;

    @RabbitListener(queues = "${accq.product.qrode.up.query}", containerFactory = "localContainerFactory")
    public void productQrodeUp(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        log.debug("转发：{} 队列的消息:{}", productQrodeUp, msg);
        try {
            String uid = UUID.randomUUID().toString();
            Message message = MessageBuilder.withBody(msg.getBytes("UTF-8"))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding("UTF-8")
                    .setMessageId(uid).build();
            CorrelationData correlationData = new CorrelationData(uid);
            correlationData.setReturnedMessage(message);
            remoteRabbitTemplate.convertAndSend(productQrodeUpExchange, productQrodeUpRouting, msg,correlationData);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("转发错误：{}, 队列的消息:{},错误信息：{}", productQrodeUp, msg, e);
            channel.basicReject(deliveryTag, true);
        }
    }

    /**
     * 华培绑定检测数据二维码队列消息底层上发校验吗------13
     */
    @Value("${accq.product.qrode.up.udp.query}")
    private String qrodeUpUdpQuery;
    @Value("${accq.product.qrode.up.udp.routing}")
    private String qrodeUpUdpRouting;
    @Value("${accq.product.qrode.up.udp.exchange}")
    private String qrodeUpUdpExchange;

//    Q,5,1201,1,565646464,DZ-1874,1,W71BHSG,0030,11473081,1000,1061
    @RabbitListener(queues = "${accq.product.qrode.up.udp.query}", containerFactory = "localContainerFactory")
    public void qrodeUpUdpQuery(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        log.debug("转发：{} 队列的消息:{}", qrodeUpUdpQuery, msg);
        try {
            String uid = UUID.randomUUID().toString();
            Message message = MessageBuilder.withBody(msg.getBytes("UTF-8"))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding("UTF-8")
                    .setMessageId(uid).build();
            CorrelationData correlationData = new CorrelationData(uid);
            correlationData.setReturnedMessage(message);
            remoteRabbitTemplate.convertAndSend(qrodeUpUdpExchange, qrodeUpUdpRouting,msg, correlationData);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("转发错误：{}, 队列的消息:{},错误信息：{}", qrodeUpUdpQuery, msg, e);
            channel.basicReject(deliveryTag, true);
        }
    }

    /**
     * 华培绑定检测数据二维码队列消息底层下发udp队列------14
     */
    @Value("${accq.product.qrode.lower.query}")
    private String qrodeLowerQuery;
    @Value("${accq.product.qrode.lower.routing}")
    private String qrodeLowerRouting;
    @Value("${accq.product.qrode.lower.exchange}")
    private String qrodeLowerExchange;

    @RabbitListener(queues = "${accq.product.qrode.lower.query}", containerFactory = "localContainerFactory")
    public void qrodeLowerQuery(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        log.debug("转发：{} 队列的消息:{}", qrodeLowerQuery, msg);
        try {
            String uid = UUID.randomUUID().toString();
            Message message = MessageBuilder.withBody(msg.getBytes("UTF-8"))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding("UTF-8")
                    .setMessageId(uid).build();
            CorrelationData correlationData = new CorrelationData(uid);
            correlationData.setReturnedMessage(message);
            remoteRabbitTemplate.convertAndSend(qrodeLowerExchange, qrodeLowerRouting, msg,correlationData);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("转发错误：{}, 队列的消息:{},错误信息：{}", qrodeLowerQuery, msg, e);
            channel.basicReject(deliveryTag, true);
        }
    }

    /**
     * 触发刷新前端页面指令------15
     */
    @Value("${dzics.html.queue.kanban.Refresh}")
    private String kanbanRefresh;
    @Value("${dzics.html.exchange.kanban.Refresh}")
    private String kanbanRefreshExchange;
    @Value("${dzics.html.routing.kanban.Refresh}")
    private String kanbanRefreshRouting;

    @RabbitListener(queues = "${dzics.html.queue.kanban.Refresh}", containerFactory = "localContainerFactory")
    public void kanbanRefresh(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        log.debug("转发：{} 队列的消息:{}", kanbanRefresh, msg);
        try {
            String uid = UUID.randomUUID().toString();
            Message message = MessageBuilder.withBody(msg.getBytes("UTF-8"))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding("UTF-8")
                    .setMessageId(uid).build();
            CorrelationData correlationData = new CorrelationData(uid);
            correlationData.setReturnedMessage(message);
            remoteRabbitTemplate.convertAndSend(kanbanRefreshExchange, kanbanRefreshRouting, msg,correlationData);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("转发错误：{}, 队列的消息:{},错误信息：{}", kanbanRefresh, msg, e);
            channel.basicReject(deliveryTag, true);
        }
    }

    /**
     * 指令数据信息转发IOT------16
     */
    @Value("${dzics.cmd.iot.query}")
    private String cmdIotQuery;
    @Value("${dzics.cmd.iot.exchange}")
    private String cmdIotQueryExchange;
    @Value("${dzics.cmd.iot.routing}")
    private String cmdIotQueryRouting;

    @RabbitListener(queues = "${dzics.cmd.iot.query}", containerFactory = "localContainerFactory")
    public void cmdIotQuery(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        log.debug("转发：{} 队列的消息:{}", cmdIotQuery, msg);
        try {
            String uid = UUID.randomUUID().toString();
            Message message = MessageBuilder.withBody(msg.getBytes("UTF-8"))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding("UTF-8")
                    .setMessageId(uid).build();
            CorrelationData correlationData = new CorrelationData(uid);
            correlationData.setReturnedMessage(message);
            remoteRabbitTemplate.convertAndSend(cmdIotQueryExchange, cmdIotQueryRouting, msg,correlationData);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("转发错误：{}, 队列的消息:{},错误信息：{}", cmdIotQuery, msg, e);
            channel.basicReject(deliveryTag, true);
        }
    }

//--------------------------------------------死亡队列--------------------------21-10-08 新增消息转发

    /**
     * 刀具检测数据转发-----2
     *
     */
    @Value("${accq.cutting.tool.detection.dead}")
    private String toolDetectionDead;
    @Value("${accq.cutting.tool.detection.exchange}")
    private String toolDetectionExchangeDead;
    @Value("${accq.cutting.tool.detection.routing.dead}")
    private String toolDetectionRoutingDead;

    @RabbitListener(queues = "${accq.cutting.tool.detection.dead}", containerFactory = "localContainerFactory")
    public void toolDetectionDead(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        log.debug("转发：{} 队列的消息:{}", toolDetectionDead, msg);
        try {
            String uid = UUID.randomUUID().toString();
            Message message = MessageBuilder.withBody(msg.getBytes("UTF-8"))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding("UTF-8")
                    .setMessageId(uid).build();
            CorrelationData correlationData = new CorrelationData(uid);
            correlationData.setReturnedMessage(message);
            remoteRabbitTemplate.convertAndSend(toolDetectionExchangeDead, toolDetectionRoutingDead, msg,correlationData);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("转发错误：{}, 队列的消息:{},错误信息：{}", toolDetectionDead, msg, e);
            channel.basicReject(deliveryTag, true);
        }
    }

    /**
     * 华培绑定检测数据二维码队列消息底层下发udp队列 转发-----3
     *
     */
    @Value("${accq.product.qrode.lower.query.dead}")
    private String qrodeLowerQueryDead;
    @Value("${accq.product.qrode.lower.exchange}")
    private String qrodeLowerQueryExchangeDead;
    @Value("${accq.product.qrode.lower.routing.dead}")
    private String qrodeLowerQueryRoutingDead;

    @RabbitListener(queues = "${accq.product.qrode.lower.query.dead}", containerFactory = "localContainerFactory")
    public void qrodeLowerQueryDead(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        log.debug("转发：{} 队列的消息:{}", qrodeLowerQueryDead, msg);
        try {
            String uid = UUID.randomUUID().toString();
            Message message = MessageBuilder.withBody(msg.getBytes("UTF-8"))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding("UTF-8")
                    .setMessageId(uid).build();
            CorrelationData correlationData = new CorrelationData(uid);
            correlationData.setReturnedMessage(message);
            remoteRabbitTemplate.convertAndSend(qrodeLowerQueryExchangeDead, qrodeLowerQueryRoutingDead, msg,correlationData);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("转发错误：{}, 队列的消息:{},错误信息：{}", qrodeLowerQueryDead, msg, e);
            channel.basicReject(deliveryTag, true);
        }
    }

    /**
     * 华培绑定检测数据二维码队列消息底层上发校验吗 转发-----4
     *
     */
    @Value("${accq.product.qrode.up.udp.query.dead}")
    private String qrodeUpUdpQueryDead;
    @Value("${accq.product.qrode.up.udp.exchange}")
    private String qrodeUpUdpQueryExchangeDead;
    @Value("${accq.product.qrode.up.udp.routing.dead}")
    private String qrodeUpUdpQueryRoutingDead;

    @RabbitListener(queues = "${accq.product.qrode.up.udp.query.dead}", containerFactory = "localContainerFactory")
    public void qrodeUpUdpQueryDead(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        log.debug("转发：{} 队列的消息:{}", qrodeUpUdpQueryDead, msg);
        try {
            String uid = UUID.randomUUID().toString();
            Message message = MessageBuilder.withBody(msg.getBytes("UTF-8"))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding("UTF-8")
                    .setMessageId(uid).build();
            CorrelationData correlationData = new CorrelationData(uid);
            correlationData.setReturnedMessage(message);
            remoteRabbitTemplate.convertAndSend(qrodeUpUdpQueryExchangeDead, qrodeUpUdpQueryRoutingDead, msg,correlationData);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("转发错误：{}, 队列的消息:{},错误信息：{}", qrodeUpUdpQueryDead, msg, e);
            channel.basicReject(deliveryTag, true);
        }
    }

    /**
     * 检测设备 转发-----5
     *
     */
    @Value("${accq.read.cmd.queue.base.checkout.equipment.dead}")
    private String checkoutEquipmentDead;
    @Value("${accq.checkout.equipment.exchange}")
    private String checkoutEquipmentExchangeDead;
    @Value("${accq.checkout.equipment.routing.dead}")
    private String checkoutEquipmentRoutingDead;

    @RabbitListener(queues = "${accq.read.cmd.queue.base.checkout.equipment.dead}", containerFactory = "localContainerFactory")
    public void checkoutEquipmentDead(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        log.debug("转发：{} 队列的消息:{}", checkoutEquipmentDead, msg);
        try {
            String uid = UUID.randomUUID().toString();
            Message message = MessageBuilder.withBody(msg.getBytes("UTF-8"))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding("UTF-8")
                    .setMessageId(uid).build();
            CorrelationData correlationData = new CorrelationData(uid);
            correlationData.setReturnedMessage(message);
            remoteRabbitTemplate.convertAndSend(checkoutEquipmentExchangeDead, checkoutEquipmentRoutingDead, msg,correlationData);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("转发错误：{}, 队列的消息:{},错误信息：{}", checkoutEquipmentDead, msg, e);
            channel.basicReject(deliveryTag, true);
        }
    }

    /**
     * 实时日志信息队列 转发-----6
     *
     */
    @Value("${accq.read.cmd.queue.equipment.realTime.dead}")
    private String equipmentRealTimeDead;
    @Value("${accq.realTime.equipment.exchange}")
    private String equipmentRealTimeExchangeDead;
    @Value("${accq.realTime.equipment.routing.dead}")
    private String equipmentRealTimeRoutingDead;

    @RabbitListener(queues = "${accq.read.cmd.queue.equipment.realTime.dead}", containerFactory = "localContainerFactory")
    public void equipmentRealTimeDead(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        log.debug("转发：{} 队列的消息:{}", equipmentRealTimeDead, msg);
        try {
            String uid = UUID.randomUUID().toString();
            Message message = MessageBuilder.withBody(msg.getBytes("UTF-8"))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding("UTF-8")
                    .setMessageId(uid).build();
            CorrelationData correlationData = new CorrelationData(uid);
            correlationData.setReturnedMessage(message);
            remoteRabbitTemplate.convertAndSend(equipmentRealTimeExchangeDead, equipmentRealTimeRoutingDead, msg,correlationData);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("转发错误：{}, 队列的消息:{},错误信息：{}", equipmentRealTimeDead, msg, e);
            channel.basicReject(deliveryTag, true);
        }
    }

 }
