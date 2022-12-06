package com.dzics.sanymom.cousomer;

import com.alibaba.fastjson.JSONObject;
import com.dzics.sanymom.model.request.agv.AgvTask;
import com.dzics.sanymom.model.request.agv.AutomaticGuidedVehicle;
import com.dzics.sanymom.service.AgvRobackService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AgvFeedBackCusomer {
    /**
     * 延时消费队列
     */
    @Value("${call.direct.agv.queue.delayed}")
    private String repeatTradeQueue;
    @Autowired
    private AgvRobackService agvRobackService;

    /**
     * 向MOM 报工
     *
     * @param msg
     * @param deliveryTag
     * @param channel
     * @throws Throwable
     */

    @RabbitListener(queues = "${call.direct.agv.queue.delayed}")
    public void dzEncasementRecordState(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        try {
            log.info("处理AGV 反馈信息:{},信息：{}", repeatTradeQueue, msg);
            try {
                AutomaticGuidedVehicle vehicle = JSONObject.parseObject(msg, AutomaticGuidedVehicle.class);
                AgvTask task = vehicle.getTask();
                String reqId = task.getReqId();
                agvRobackService.handelAgvMessage(reqId);
            } catch (Throwable throwable) {
                log.error("AGV 反馈发送到机器人错误：{}", throwable.getMessage(), throwable);
            }
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费队列:{},信息:{},失败", repeatTradeQueue, msg);
            channel.basicReject(deliveryTag, false);
        }
    }
}
