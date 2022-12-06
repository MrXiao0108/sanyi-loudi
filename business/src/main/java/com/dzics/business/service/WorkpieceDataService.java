package com.dzics.business.service;

import com.dzics.common.model.entity.DzWorkpieceData;
import com.dzics.common.model.request.UploadProductDetectionVo;
import com.dzics.common.model.response.Result;

public interface WorkpieceDataService {
    Result uploadProductDetectionVo(UploadProductDetectionVo uploadProductDetectionVo);

    /**
     * 向队列中发送获取二维码的请求数据
     */
    void  sendQrCodeMqUdp(DzWorkpieceData dzWorkpieceData);
}
