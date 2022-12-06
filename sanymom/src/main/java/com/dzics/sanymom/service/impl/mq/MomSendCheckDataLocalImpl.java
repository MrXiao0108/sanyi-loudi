package com.dzics.sanymom.service.impl.mq;

import com.alibaba.fastjson.JSONObject;
import com.dzics.common.model.custom.WorkReportDto;
import com.dzics.common.model.entity.DzWorkpieceData;
import com.dzics.sanymom.service.MqSendLocalService;
import com.dzics.sanymom.util.SnowflakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @Classname MomSendReportLocalImpl
 * @Description 向MOM 发送检测数据 先发送到本地队列中
 * @Date 2022/6/14 14:00
 * @Created by NeverEnd
 */
@Slf4j
@Service
public class MomSendCheckDataLocalImpl implements MqSendLocalService<DzWorkpieceData> {

    @Autowired
    public SnowflakeUtil snowflakeUtil;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Override
    public boolean sendMq(DzWorkpieceData dzWorkpieceData, String routing, String exchange,String queryName) {
        try {
            String json = JSONObject.toJSONString(dzWorkpieceData);
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            Message message = MessageBuilder.withBody(bytes)
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding(StandardCharsets.UTF_8.name())
                    .setMessageId(UUID.randomUUID().toString()).build();
            CorrelationData correlationData = new CorrelationData(snowflakeUtil.nextId() + "");
            correlationData.setReturnedMessage(message);
            rabbitTemplate.convertAndSend(exchange, routing, json, correlationData);
            log.debug("转发：{} 队列的消息:{}", queryName, dzWorkpieceData);
            return true;
        } catch (Exception e) {
            log.error("转发队列失败，队列名：{} 队列的消息:{}", queryName, dzWorkpieceData);
            return false;
        }
    }
}
