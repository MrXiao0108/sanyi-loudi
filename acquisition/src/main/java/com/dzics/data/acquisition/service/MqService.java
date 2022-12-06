package com.dzics.data.acquisition.service;

import com.dzics.common.model.custom.ReqWorkQrCodeOrder;

/**
 * 发送队列消息接口
 *
 * @author ZhangChengJun
 * Date 2021/1/27.
 * @since
 */
public interface MqService {

    /**
     * 报工信息发送至队列
     * @param qrCode
     */
    void saveReportWorkHistory(ReqWorkQrCodeOrder qrCode);

    void sendCmdUpIot(String toJSONStringMap);

    void sendDataCenter(String key, String exchange, Object dzWorkpieceData);
}
