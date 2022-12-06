package com.dzics.common.model.custom;

import lombok.Data;

@Data
public class DzOrderNoLineNo {
    /**
     * 大正订单号
     */
    private String orderNo;
    /**
     * 大正产线号
     */
    private String lineNo;
}
