package com.dzics.data.acquisition.cousomer;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.api.R;
import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.entity.MomReceiveMaterial;
import com.dzics.common.model.entity.SysRealTimeLogs;
import com.dzics.common.model.constant.LogType;
import com.dzics.common.model.request.agv.AgvClickSignalConfirmV2;
import com.dzics.common.model.response.Result;
import com.dzics.data.acquisition.service.AccRealTimeLogsService;
import com.dzics.data.acquisition.service.DeviceStatusPush;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

/**
 * 日志处理
 *
 * @author ZhangChengJun
 * Date 2021/4/6.
 * @since
 */
@Component
@Slf4j
public class RealTimeLogsCousomer {
    @Value("${accq.read.cmd.queue.equipment.realTime}")
    private String queueRealTimeEquipment;

    @Value("${accq.read.cmd.queue.equipment.realTime.dead}")
    private String queueRealTimeEquipmentDead;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AccRealTimeLogsService accRealTimeLogsService;

    @Autowired
    private DeviceStatusPush deviceStatusPush;
    @Value("${business.robot.material.click.ok.path}")
    private String materialOk;
    @Value("${business.robot.ip}")
    private String busIpPort;

    @RabbitListener(queues = "${accq.read.cmd.queue.equipment.realTime}", containerFactory = "localContainerFactory")
    public void dzEncasementRecordState(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        try {
            log.debug("消费队列的消息:{},传送日志信息:{},", queueRealTimeEquipment, msg);
            try {
                RabbitmqMessage rabbitmqMessage = JSONObject.parseObject(msg, RabbitmqMessage.class);
                SysRealTimeLogs b = accRealTimeLogsService.saveRealTimeLog(rabbitmqMessage);
                try {
                    deviceStatusPush.sendReatimLogs(b);
                    log.debug("推送到看板消息:{}", b);
                } catch (Throwable throwable) {
                    log.error("推送到看板消息异常:{}", b);
                }
                try {
                    if (LogType.logType_MA.equals(rabbitmqMessage.getClientId())) {
                        String message = rabbitmqMessage.getMessage();
                        SysRealTimeLogs sysRealTimeLogs = JSONObject.parseObject(message, SysRealTimeLogs.class);
                        MomReceiveMaterial dzOrderCheck = JSONObject.parseObject(sysRealTimeLogs.getMessage(), MomReceiveMaterial.class);
                        deviceStatusPush.sendMomReceiveMaterial(dzOrderCheck);
//                      新增  现在在触发校验物料后再模拟人工确认物料接口调用
                        boolean okMaterial = sendOkMaterialhttp(dzOrderCheck);

                    }
                } catch (Throwable throwable) {
                    log.warn("处理来料信息发送到页面错误：{} ", throwable.getMessage(), throwable);
                }

            } catch (Throwable e) {
                log.error("处理日志信息异常", e);
            }
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费队列的消息:{},传送日志信息:{},失败", queueRealTimeEquipment, msg);
            channel.basicReject(deliveryTag, false);
        }
    }

    private boolean sendOkMaterialhttp(MomReceiveMaterial dzOrderCheck) {
        try {
            String url = busIpPort + materialOk;
            AgvClickSignalConfirmV2 confirmV2 = new AgvClickSignalConfirmV2();
            confirmV2.setReceiveMaterialId(dzOrderCheck.getReceiveMaterialId());
            confirmV2.setOkNg(true);
            ResponseEntity<Result> resultResponseEntity = restTemplate.postForEntity(url, confirmV2, Result.class);
            Result body = resultResponseEntity.getBody();
            log.info("二次确认物料 url:{} confirmV2:{}, 响应信息 body：{}", url, JSONObject.toJSONString(confirmV2), JSONObject.toJSONString(body));
            return true;
        } catch (Throwable throwable) {
            log.error("二次触发确认物料 dzOrderCheck：{}", JSONObject.toJSONString(dzOrderCheck));
        }
        return false;
    }


    @RabbitListener(queues = "${accq.read.cmd.queue.equipment.realTime.dead}", containerFactory = "localContainerFactory")
    public void dzEncasementRecordStateDead(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        try {
            log.debug("死亡队列的消息:{},传送日志信息:{},", queueRealTimeEquipmentDead, msg);
            try {
                RabbitmqMessage rabbitmqMessage = JSONObject.parseObject(msg, RabbitmqMessage.class);
                SysRealTimeLogs b = accRealTimeLogsService.saveRealTimeLog(rabbitmqMessage);
            } catch (Throwable e) {
                log.error("死亡处理日志信息异常", e);
            }
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("死亡消费队列的消息:{},传送日志信息:{},失败", queueRealTimeEquipmentDead, msg);
            channel.basicReject(deliveryTag, false);
        }
    }

}
