package com.dzics.data.acquisition.cousomer;

import com.alibaba.fastjson.JSONObject;
import com.dzics.common.model.entity.MonOrder;
import com.dzics.common.service.MomOrderService;
import com.dzics.data.acquisition.service.CacheService;
import com.dzics.data.acquisition.service.DeviceStatusPush;
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
public class OrderCousomer {
    @Value("${car.direct.order.queue.delayed}")
    private String queueOrder;
    @Value("${accq.product.qrode.lower.query.dead}")
    private String udpSendDataDead;
    @Autowired
    private MomOrderService momOrderService;
    @Autowired
    private DeviceStatusPush deviceStatusPush;
    @Autowired
    private CacheService cacheService;
    /**
     * 下发到UDP端数据，下发超时时进入的死亡队列。在此处理死亡队列的数据
     *
     * @param msg
     * @param deliveryTag
     * @param channel
     * @throws Throwable
     */
    @RabbitListener(queues = "${accq.product.qrode.lower.query.dead}", containerFactory = "localContainerFactory")
    public void cuttingToolDetectionDead(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        log.warn("处理死亡队列：{},下发到UDP端数据处理超时. 队列的消息:{}", udpSendDataDead, msg);
        channel.basicAck(deliveryTag, true);
    }
    @RabbitListener(queues = "${car.direct.order.queue.delayed}", containerFactory = "localContainerFactory")
    public void dzEncasementRecordState(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        try {
            log.debug("消费队列:{},信息:{}", queueOrder, msg);
            MonOrder monOrder = JSONObject.parseObject(msg, MonOrder.class);
            MonOrder byId = momOrderService.getById(monOrder.getProTaskOrderId());
            if (byId.getOrderOperationResult() == 1) {
//               超过两分钟订单执行超时，自动回复之前的状态
                String orderOldState = byId.getOrderOldState();
                MonOrder mom = new MonOrder();
                mom.setProTaskOrderId(byId.getProTaskOrderId());
                mom.setOrderOperationResult(2);
                mom.setProgressStatus(orderOldState);
                momOrderService.updateById(mom);
                mom.setOrderId(byId.getOrderId());
                mom.setLineId(byId.getLineId());
                log.warn("订单执行超时，自动回复之前的状态:恢复前:{},恢复后:{}", JSONObject.toJSONString(byId), JSONObject.toJSONString(mom));
                deviceStatusPush.sendMomOrderRef(mom, 1);
            }
            try {
                cacheService.delNowOrder();
            }catch (Throwable e ){
              log.error("清除当前生产订单缓存失败:{}",e.getMessage(),e);
            }
//           处理订单状态
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("延迟处理订单状态错误，队列:{},信息:{}", queueOrder, msg, e);
            channel.basicReject(deliveryTag, false);
        }
    }

}
