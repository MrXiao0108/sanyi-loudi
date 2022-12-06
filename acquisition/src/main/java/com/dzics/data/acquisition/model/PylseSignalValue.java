package com.dzics.data.acquisition.model;

import lombok.Data;

/**
 * 脉冲数量值
 *
 * @author ZhangChengJun
 * Date 2021/2/4.
 * @since
 */
@Data
public class PylseSignalValue {
    /**
     * 产品类型
     */
    private String productType;
    /**
     * 产品批次号
     */
    private String batchCode;
    /**
     * 产品编码
     */
    private String productCode;
    /**
     * 产品数量1  投入数量
     */
    private int quantity1;
    /**
     * 产品数量2  合格数量
     */
    private int quantity2;
    /**
     * 产品数量3  不良品数量
     */
    private int quantity3;
    /**
     * 产品数量4  预留
     */
    private int quantity4;
    /**
     * 触发脉冲
     */
    private int sigFlag;
}
