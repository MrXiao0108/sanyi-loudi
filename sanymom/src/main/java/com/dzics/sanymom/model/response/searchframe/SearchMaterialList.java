package com.dzics.sanymom.model.response.searchframe;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ZhangChengJun
 * Date 2021/11/11.
 * @since
 */
@Data
public class SearchMaterialList implements Serializable {
    /**
     * 生产订单号
     */
    private String wiporderId;
    /**
     * 物料编码
     */
    private String materialNo;
    /**
     * 位置
     */
    private String position;
    /**
     * 数量
     */
    private String quantity;
    /**
     * 产品序列号
     */
    private String serialNo;
    /**
     * 顺序号
     */
    private String sourceSequenceNo;
    /**
     * 工序号
     */
    private String sourceOprSequenceNo;
    /**
     * 预留参数1
     */
    private String paramRsrv1;
    /**
     * 预留参数2
     */
    private String paramRsrv2;

}
