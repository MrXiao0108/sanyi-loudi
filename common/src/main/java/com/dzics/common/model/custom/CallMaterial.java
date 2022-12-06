package com.dzics.common.model.custom;

import lombok.Data;

/**
 * @author ZhangChengJun
 * Date 2021/11/8.
 * 叫料信息
 * @since
 */
@Data
public class CallMaterial {
    private String waitMaterialId;
    /**
     * 系统编码
     */
    private String reqSys;
    /**
     * 工厂编号
     */
    private String facility;
    /**
     * 顺序号
     */
    private String sequenceNo;
    /**
     * 工序号
     */
    private String oprSequenceNo;
    /**
     * 生产订单号
     */
    private String wipOrderNo;
    /**
     * 物料编码
     */
    private String materialNo;
    /**
     * 叫料总数量
     */
    private Integer quantity;
    /**
     * 物料信息
     */
    private String materialName;

    /**
     * 叫料类型
     */
    private String materialType;
    /**
     * 已叫料总数量
     */
    private Integer sucessQuantity;
    /**
     * 剩余叫料总数
     */
    private Integer surplusQuantity;
}
