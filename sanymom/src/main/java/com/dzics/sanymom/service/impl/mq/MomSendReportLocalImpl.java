package com.dzics.sanymom.service.impl.mq;

import com.alibaba.fastjson.JSONObject;
import com.dzics.common.model.custom.ReqWorkQrCodeOrder;
import com.dzics.common.model.custom.WorkReportDto;
import com.dzics.sanymom.service.MqSendLocalService;
import com.dzics.sanymom.util.SnowflakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
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
 * @Description 向MOM报工的数据发送到本地MQ
 * @Date 2022/6/14 14:00
 * @Created by NeverEnd
 */
@Slf4j
@Service
public class MomSendReportLocalImpl implements MqSendLocalService<WorkReportDto> {
    @Autowired
    public SnowflakeUtil snowflakeUtil;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public boolean sendMq(WorkReportDto qrCode, String routing, String exchange, String queryName) {
        try {
            String groupId = qrCode.getGroupId();
            String json = JSONObject.toJSONString(qrCode);
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            Message message = MessageBuilder.withBody(bytes)
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding(StandardCharsets.UTF_8.name())
                    .setMessageId(UUID.randomUUID().toString()).build();
            CorrelationData correlationData = new CorrelationData(snowflakeUtil.nextId() + "-" + groupId);
            correlationData.setReturnedMessage(message);
            rabbitTemplate.convertAndSend(exchange, routing, json, correlationData);
            if (log.isDebugEnabled()) {
                log.debug("转发：{} 队列的消息:{}", queryName, qrCode);
            }
            return true;
        } catch (Throwable e) {
            log.error("发送产品检测完成的二维码触发报工流程失败:{}", e.getMessage(), e);
            return false;
        }
    }


    public boolean reportWorkMq(ReqWorkQrCodeOrder qrCode, String routing, String exchange, String queryName) {
        try {
            String json = JSONObject.toJSONString(qrCode);
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            Message message = MessageBuilder.withBody(bytes)
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding(StandardCharsets.UTF_8.name())
                    .setMessageId(UUID.randomUUID().toString()).build();
            CorrelationData correlationData = new CorrelationData(snowflakeUtil.nextId() + "");
            correlationData.setReturnedMessage(message);
            rabbitTemplate.convertAndSend(exchange, routing, json, correlationData);
            return true;
        } catch (Throwable e) {
            log.error("发送产品检测完成的二维码触发报工流程失败:{}", e.getMessage(), e);
            return false;
        }
    }
}
