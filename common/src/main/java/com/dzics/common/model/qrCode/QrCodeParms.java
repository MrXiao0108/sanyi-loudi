package com.dzics.common.model.qrCode;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class QrCodeParms {
    @NotBlank(message = "二维码必填")
    private String qrCode;
    @NotBlank(message = "订单")
    private String orderNo;
    @NotBlank(message = "产线")
    private String lineNo;
}
