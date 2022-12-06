package com.dzics.sanymom.service.impl.mq;

import com.alibaba.fastjson.JSONObject;
import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.custom.ReqWorkQrCodeOrder;
import com.dzics.common.model.custom.WorkReportDto;
import com.dzics.sanymom.service.MqSendLocalService;
import com.dzics.sanymom.util.SnowflakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @Classname MomSendReportLocalImpl
 * @Description 向中控 DZICS 服务器 报工的数据发送到本地MQ
 * @Date 2022/6/14 14:00
 * @Created by NeverEnd
 */
@Slf4j
@Service
public class DzicsSendReportLocalImpl implements MqSendLocalService<RabbitmqMessage> {
    @Autowired
    public SnowflakeUtil snowflakeUtil;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public boolean sendMq(RabbitmqMessage rabbitmqMessage, String routing, String exchange, String queryName) {
        try {
            String json = JSONObject.toJSONString(rabbitmqMessage);
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            Message message = MessageBuilder.withBody(bytes)
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding(StandardCharsets.UTF_8.name())
                    .setMessageId(UUID.randomUUID().toString()).build();
            CorrelationData correlationData = new CorrelationData(snowflakeUtil.nextId() + "");
            correlationData.setReturnedMessage(message);
            rabbitTemplate.convertAndSend(exchange, routing, json, correlationData);
            log.debug("转发：{} 队列的消息:{}", queryName, rabbitmqMessage);
            return true;
        } catch (Exception e) {
            log.error("转发队列失败，队列名：{} 队列的消息:{}", queryName, rabbitmqMessage);
            return false;
        }
    }
}
