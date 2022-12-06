package com.dzics.data.acquisition.service;

import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.entity.DzWorkpieceData;
import com.dzics.common.model.response.Result;

public interface BindingQRCodeService {
    /**
     * 检测数据 绑定二维码
     * @param rabbitmqMessage
     * @return
     */
    Result processingData(RabbitmqMessage rabbitmqMessage);

    /**
     * 检测数据 绑定交换码
     * @param rabbitmqMessage
     * @return
     */
    DzWorkpieceData createAuthCode(String rabbitmqMessage);
}
