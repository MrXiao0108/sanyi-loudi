package com.dzics.common.model.custom;

import com.dzics.common.model.constant.OrderCode;
import lombok.Data;

/**
 * 队列接收的数据
 *
 * @author ZhangChengJun
 * Date 2021/1/21.
 * @since
 */
@Data
public class RabbitmqMessage {

    /**
     * DeviceType : 2
     * Message : A561|11#A563|2#A565|0#A562|0#A802|33.465#A521|0#A501|[0,0,0,0,30,0]#A502|[2085.705,0,2160,0.5,0,0.866]#
     * OrderCode : DZ-1882
     * ClientId : DZROBOT
     * DeviceCode : 01
     * QueueName : dzics-dev-gather-v1-queue
     * Timestamp : 2021-01-21 15:00:49.1387
     * LineNo : 1
     * MessageId : 661f0350-b6ab-4336-9170-e56c3a42e16f
     */
    private String DeviceType;
    private String Message;
    private String OrderCode;
    private String ClientId;
    private String DeviceCode;
    private String QueueName;
    private String Timestamp;
    private String LineNo;
    private String MessageId;
    private Boolean check;


}
