package com.dzics.sanymom.service;

import com.dzics.common.model.entity.MonOrder;

/**
 * mom订单接口
 *
 * @author ZhangChengJun
 * Date 2022/1/20.
 * @since
 */
public interface MomOrderService {
    /**
     * @param maStatus 订单叫料状态
     * @return
     */
    MonOrder getCallMaterialStatus(int maStatus);
}
