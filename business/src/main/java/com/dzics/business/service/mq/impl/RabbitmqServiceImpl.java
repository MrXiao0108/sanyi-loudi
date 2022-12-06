package com.dzics.business.service.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.dzics.business.service.mq.RabbitmqService;
import com.dzics.business.util.SnowflakeUtil;
import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.constant.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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
    /**
     * 指定远程服务器rabbitTemplate
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    public SnowflakeUtil snowflakeUtil;

    @Value("${accq.product.qrode.lower.routing}")
    private String qrodeLowerRouting;
    @Value("${accq.product.qrode.lower.exchange}")
    private String qrodeLowerExchange;


    @Value("${dzics.html.exchange.kanban.Refresh}")
    private String directExchangeRefresh;
    @Value("${dzics.html.routing.kanban.Refresh}")
    private String directRoutingRefresh;

    @Value("${accq.realTime.equipment.routing}")
    private String directRealTimeEquipmentRouting;
    @Value("${accq.realTime.equipment.exchange}")
    private String directRealTimeEquipmentExchange;


    /**
     * 订单交换机
     */
    @Value("${car.direct.order.exchange}")
    private String directExchangeOrder;

    @Value("${car.direct.order.routing.deadLetterRouting}")
    private String deadLetterRouting;

    @Override
    public void sendQrCodeMqUdp(String cmdStr) {
        try {
//        状态发送
            CorrelationData correlationData = new CorrelationData(snowflakeUtil.nextId() + "");
            MessageProperties messageProperties = new MessageProperties();
            Message message = new Message(cmdStr.getBytes("UTF-8"), messageProperties);
            correlationData.setReturnedMessage(message);
            rabbitTemplate.send(qrodeLowerExchange, qrodeLowerRouting, message);
            log.debug("向队列中发送二维码触发到UDP {}", cmdStr);
        } catch (Throwable throwable) {
            log.error("向队列中发送二维码触发到UDP错误：{}", throwable.getMessage(), throwable);
        }

    }

    @Override
    public void refresh(String sub, String cmdStr) {
        long start = System.currentTimeMillis();
//       实时数量发送
        CorrelationData correlationDataNumber = new CorrelationData(snowflakeUtil.nextId() + "");
        MessageProperties messagePropertiesNumber = new MessageProperties();
        Message messageNumber = new Message(cmdStr.getBytes(), messagePropertiesNumber);
        correlationDataNumber.setReturnedMessage(messageNumber);
        rabbitTemplate.send(directExchangeRefresh, directRoutingRefresh, messageNumber);
        long end = System.currentTimeMillis();
        log.info("用户：{}，刷新看板:{},消息:{}", sub, (end - start), cmdStr);
    }

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
            rabbitTemplate.send(directRealTimeEquipmentExchange, directRealTimeEquipmentRouting, message);
            log.debug("发送日志信息到队列有队列处理 {}", toJSONString);
            return false;
        } catch (Throwable throwable) {
            log.error("发送日志信息到队列有队列处理: {}", throwable.getMessage(), throwable);
            return false;
        }

    }

    @Override
    public boolean sendRabbitmqLogMaterial(String js) {
        try {
            RabbitmqMessage rabbitmqMessage = new RabbitmqMessage();
            rabbitmqMessage.setMessage(js);
            rabbitmqMessage.setClientId(LogType.logType_MA);
            String toJSONString = JSONObject.toJSONString(rabbitmqMessage);
            CorrelationData correlationData = new CorrelationData(snowflakeUtil.nextId() + "");
            MessageProperties messageProperties = new MessageProperties();
            Message message = new Message(toJSONString.getBytes("UTF-8"), messageProperties);
            correlationData.setReturnedMessage(message);
            rabbitTemplate.send(directRealTimeEquipmentExchange, directRealTimeEquipmentRouting, message);
            log.debug("发送日志信息到队列有队列处理 {}", toJSONString);
            return false;
        } catch (Throwable throwable) {
            log.error("发送日志信息到队列有队列处理:{}", throwable.getMessage(), throwable);
            return false;
        }

    }


    @Override
    public void sendMsgOrder(String jsonString) {
        try {
            CorrelationData correlationDataNumber = new CorrelationData(snowflakeUtil.nextId() + "");
            MessageProperties messagePropertiesNumber = new MessageProperties();
            Message messageNumber = new Message(jsonString.getBytes("UTF-8"), messagePropertiesNumber);
            correlationDataNumber.setReturnedMessage(messageNumber);
            rabbitTemplate.send(directExchangeOrder, deadLetterRouting, messageNumber);
        } catch (Exception e) {
            log.error("订单发送至延时队列失败：errorMsg:" + e.getMessage() + "->msg：" + jsonString);
        }
    }
}
