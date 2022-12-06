package com.dzics.business.service.mq;

/**
 * rabbitmq发送接口
 *
 * @author ZhangChengJun
 * Date 2021/3/18.
 * @since
 */
public interface RabbitmqService {

    void sendQrCodeMqUdp(String cmdStr);

    void refresh(String sub, String ref);

    /**
     * 发送日志信息到队列有队列处理
     *
     * @param toJSONString
     * @return
     */
    boolean sendRabbitmqLog(String toJSONString);

     void sendMsgOrder(String jsonString);

    /**
     * 发送物料信息
     *
     * @param toJSONString
     * @return
     */
    boolean sendRabbitmqLogMaterial(String toJSONString);
}
