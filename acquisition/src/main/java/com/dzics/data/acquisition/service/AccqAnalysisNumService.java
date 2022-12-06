package com.dzics.data.acquisition.service;

import com.dzics.common.model.custom.CmdTcp;
import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.model.entity.DzEquipmentProNum;
import com.dzics.data.acquisition.model.PylseSignalValue;

/**
 * 设备数据流程处理存储更新接口
 *
 * @author ZhangChengJun
 * Date 2021/1/20.
 * @since
 */
public interface AccqAnalysisNumService {
    /**
     * 处理生产数据
     *
     * @param msg
     */
    DzEquipmentProNum analysisNum(RabbitmqMessage msg);



    /**
     * @param cmd 处理运行状态数据
     */
    DzEquipment analysisNumRunState(RabbitmqMessage cmd);

    long calculateQuantity(Long nowQuantity, Long upQuantity);

    /**
     *  sendBase 脉冲信号计数
     */
//    void queuePylseSignal(RabbitmqMessage sendBase);

    PylseSignalValue getSingValue(CmdTcp nowDzDq);

    /**
     * 检测设备数据
     *
     * @param rabbitmqMessage
     */
    String queueCheckoutEquipment(RabbitmqMessage rabbitmqMessage);

    DzEquipment analysisNumAlarmState(RabbitmqMessage rabbitmqMessage);
}
