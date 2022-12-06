package com.dzics.data.acquisition.service;

import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.model.response.Result;

/**
 * 设备状态
 *
 * @author ZhangChengJun
 * Date 2021/3/10.
 * @since
 */
public interface AccqAnalysisStateService {

    /**
     * 刀具信息更新
     * @param rabbitmqMessage
     * @return
     */
    Result getEqToolInfoList(RabbitmqMessage rabbitmqMessage);

    /**
     *处理状态数据 只做推送的数据处理，不进行库的插入更新操作
     * @param rabbitmqMessage
     * @return
     */
    DzEquipment analysisNumStatePush(RabbitmqMessage rabbitmqMessage);
}
