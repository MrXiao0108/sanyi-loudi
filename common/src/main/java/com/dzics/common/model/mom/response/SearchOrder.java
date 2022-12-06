package com.dzics.common.model.mom.response;

import lombok.Data;

/**
 * @author ZhangChengJun
 * Date 2022/1/9.
 * @since
 */
@Data
public class SearchOrder {

    /**
     * 订单号
     */
    private String wipOrderNo;

    /**
     * 工序号
     */
    private String oprSequenceNo;
    /**
     * 序列号
     */
    private String serialNo;
    /**
     * 顺序号(预留字段)
     */
//    private String sequenceNo;

}
