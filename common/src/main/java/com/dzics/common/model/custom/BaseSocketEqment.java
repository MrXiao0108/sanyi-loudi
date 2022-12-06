package com.dzics.common.model.custom;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 设备消息分类定义
 *
 * @author ZhangChengJun
 * Date 2021/2/26.
 * @since
 */
@Data
public class BaseSocketEqment implements Serializable {
    /**
     * 设备ID
     */
    private String equimentId;
    /**
     * 订单
     */
    private String orderNo;
    /**
     * 产线
     */
    private String lineNo;
}
