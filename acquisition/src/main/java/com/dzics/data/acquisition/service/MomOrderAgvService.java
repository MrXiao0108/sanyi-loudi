package com.dzics.data.acquisition.service;

import com.dzics.common.model.entity.MonOrder;

public interface MomOrderAgvService {

    /**
     * udp 回传设备控制指令状态处理
     *
     * @param split
     */
    void udpCmdControl(String[] split);

    void startMomOrderV2(MonOrder momOder, String orderNo, String lineNo);


    void updateOrderStateSum(String cmdTypeInner, String value, String orderNo, String lineNo, MonOrder monOrderSel, Integer sum);
}
