package com.dzics.data.acquisition.service;

import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.entity.DzWorkpieceData;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;

import java.util.List;

/**
 * <p>
 * 设备检测数据V2新版记录 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
public interface AccDzWorkpieceDataService {

    DzWorkpieceData queueCheckoutEquipment(RabbitmqMessage rabbitmqMessage);


    DzWorkpieceData maerbiao(String[] split);
}
