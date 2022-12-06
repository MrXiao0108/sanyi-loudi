package com.dzics.data.acquisition.service.mq;

import com.dzics.data.acquisition.model.PushKanbanBase;

/**
 * rabbitmq发送接口
 *
 * @author ZhangChengJun
 * Date 2021/3/18.
 * @since
 */
public interface RabbitmqService {


    boolean sendRabbitmqLog(String toJSONString);

    void sendQrCodeMqUdp(String toJSONString);

    /**
     * 发送停机次数
     * @param dowmSum
     */
    void sendDeviceDownSum(PushKanbanBase dowmSum);

    void sendMsgOrder(String toJSONString);

    boolean sendForwardPsotionQuery(String postionExchangeTwo, String routingPostionTwo, String msg);
}
