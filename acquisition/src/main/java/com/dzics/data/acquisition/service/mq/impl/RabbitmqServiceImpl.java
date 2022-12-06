package com.dzics.data.acquisition.service.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.constant.LogType;
import com.dzics.data.acquisition.model.PushKanbanBase;
import com.dzics.data.acquisition.service.mq.RabbitmqService;
import com.dzics.data.acquisition.util.SnowflakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author ZhangChengJun
 * Date 2020/7/20.
 */
@Slf4j
@Component
@Scope("singleton")
public class RabbitmqServiceImpl implements RabbitmqService {
    @Qualifier("localRabbitTemplate")
    @Autowired
    private RabbitTemplate localRabbitTemplate;

    @Autowired
    private SnowflakeUtil snowflakeUtil;

    @Value("${accq.realTime.equipment.routing}")
    private String directRealTimeEquipmentRouting;
    @Value("${accq.realTime.equipment.exchange}")
    private String directRealTimeEquipmentExchange;

    @Value("${accq.product.qrode.lower.routing}")
    private String qrodeLowerRouting;
    @Value("${accq.product.qrode.lower.exchange}")
    private String qrodeLowerExchange;

    @Override
    public boolean sendRabbitmqLog(String js) {
        try {
            RabbitmqMessage rabbitmqMessage = new RabbitmqMessage();
            rabbitmqMessage.setMessage(js);
            rabbitmqMessage.setClientId(LogType.logType);
            String toJSONString = JSONObject.toJSONString(rabbitmqMessage);
            CorrelationData correlationData = new CorrelationData(snowflakeUtil.nextId() + "");
            MessageProperties messageProperties = new MessageProperties();
            Message message = new Message(toJSONString.getBytes("UTF-8"), messageProperties);
            correlationData.setReturnedMessage(message);
            localRabbitTemplate.send(directRealTimeEquipmentExchange, directRealTimeEquipmentRouting, message);
            log.debug("发送日志信息到队列有队列处理 {}", toJSONString);
            return false;
        } catch (Throwable throwable) {
            log.error("发送日志信息到队列有队列处理: {}", throwable.getMessage(), throwable);
            return false;
        }
    }

    @Override
    public void sendQrCodeMqUdp(String cmdStr) {
        try {
//        状态发送
            CorrelationData correlationData = new CorrelationData(snowflakeUtil.nextId() + "");
            MessageProperties messageProperties = new MessageProperties();
            Message message = new Message(cmdStr.getBytes("UTF-8"), messageProperties);
            correlationData.setReturnedMessage(message);
            localRabbitTemplate.send(qrodeLowerExchange, qrodeLowerRouting, message);
            log.debug("向队列中发送二维码触发到UDP {}", cmdStr);
        } catch (Throwable throwable) {
            log.error("向队列中发送二维码触发到UDP错误：{}", throwable.getMessage(), throwable);
        }
    }


    @Value("${push.kanban.exchange.simple}")
    private String pushExchange;

    @Value("${push.kanban.routing.simple}")
    private String qushRouting;
    @Value("${push.kanban.queue.simple}")
    private String pushQueue;
    /**
     * 订单交换机
     */
    @Value("${car.direct.order.exchange}")
    private String directExchangeOrder;

    @Value("${car.direct.order.routing.deadLetterRouting}")
    private String deadLetterRouting;

    /**
     * 发送停机次数到MQ
     *
     * @param dowmSum
     */
    @Override
    public void sendDeviceDownSum(PushKanbanBase dowmSum) {
        try {
            String string = JSONObject.toJSONString(dowmSum);
            CorrelationData correlationData = new CorrelationData(snowflakeUtil.nextId() + "");
            MessageProperties messageProperties = new MessageProperties();
            Message message = new Message(string.getBytes("UTF-8"), messageProperties);
            correlationData.setReturnedMessage(message);
            localRabbitTemplate.send(pushExchange, qushRouting, message);
            log.debug("向对列:{} 发送当日停机次数完成 dowmSum:{}:", pushQueue, string);
        } catch (Throwable throwable) {
            log.debug("向对列:{} 发送当日停机次数 : dowmSum:{} ,发送失败：{}", pushQueue, dowmSum, throwable.getMessage(), throwable);
        }
    }

    @Override
    public void sendMsgOrder(String toJSONString) {
        try {
            CorrelationData correlationDataNumber = new CorrelationData(snowflakeUtil.nextId() + "");
            MessageProperties messagePropertiesNumber = new MessageProperties();
            Message messageNumber = new Message(toJSONString.getBytes("UTF-8"), messagePropertiesNumber);
            correlationDataNumber.setReturnedMessage(messageNumber);
            localRabbitTemplate.send(directExchangeOrder, deadLetterRouting, messageNumber);
        } catch (Throwable e) {
            log.error("订单发送至延时队列失败 msg ：{}", toJSONString, e);
        }
    }

    @Override
    public boolean sendForwardPsotionQuery(String postionExchangeTwo, String routingPostionTwo, String toJSONString) {
        try {
            CorrelationData correlationDataNumber = new CorrelationData(snowflakeUtil.nextId() + "");
            MessageProperties messagePropertiesNumber = new MessageProperties();
            Message messageNumber = new Message(toJSONString.getBytes("UTF-8"), messagePropertiesNumber);
            correlationDataNumber.setReturnedMessage(messageNumber);
            localRabbitTemplate.send(postionExchangeTwo, routingPostionTwo, messageNumber);
            return true;
        } catch (Throwable e) {
            log.error("报工信息发送至延时队列失败 msg ：{}", toJSONString, e);
            return false;
        }
    }
}
