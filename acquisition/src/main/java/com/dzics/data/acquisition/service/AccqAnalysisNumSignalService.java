package com.dzics.data.acquisition.service;

import com.dzics.common.model.custom.CmdTcp;
import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.entity.DzEquipmentProNumSignal;
import com.dzics.data.acquisition.model.PylseSignalValue;

/**
 * 脉冲信号解析接口
 *
 * @author ZhangChengJun
 * Date 2021/2/23.
 * @since
 */
public interface AccqAnalysisNumSignalService {

    /**
     * 解析脉冲数据
     *
     * @param rabbitmqMessage
     */
    DzEquipmentProNumSignal queuePylseSignal(RabbitmqMessage rabbitmqMessage);

    PylseSignalValue getSingValue(CmdTcp nowDzDq);
    /**
     * 检验数据重复
     * @param rabbitmqMessage
     * @return
     */
    boolean queuePylseSignalCheck(RabbitmqMessage rabbitmqMessage);
    /**
     * 根据设备ID 设置缓存频率
     * @param equimentId 设备ID
     * @param sendSignalTime 触发事件
     */
    void setRedisSignalValue(Long equimentId, Long sendSignalTime);

    /**
     * 处理补偿数据
     * @param dzEquipmentProNumSignal
     */
    Long compensate(DzEquipmentProNumSignal dzEquipmentProNumSignal);

}
