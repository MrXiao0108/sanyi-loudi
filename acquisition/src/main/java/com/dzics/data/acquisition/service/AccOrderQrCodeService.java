package com.dzics.data.acquisition.service;

import com.dzics.common.model.entity.MonOrder;

public interface AccOrderQrCodeService {
    /**
     * 二维码和订单绑定
     * @param qrcode
     * @param startOrder
     * @param orderCode
     * @param lineNo
     * @return 返回该订单数量
     */
    Integer bandMomOrderQrCode(String qrcode, MonOrder startOrder, String orderCode, String lineNo);
}
