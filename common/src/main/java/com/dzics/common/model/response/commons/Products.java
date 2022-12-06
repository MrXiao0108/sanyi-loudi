package com.dzics.common.model.response.commons;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 公共所有产品
 *
 * @author ZhangChengJun
 * Date 2021/5/18.
 * @since
 */
@Data
public class Products {
    @ApiModelProperty("产品Id")
    private String productId;
    @ApiModelProperty("订单Id")
    private String orderId;
    @ApiModelProperty("订单序号")
    private String orderNo;
    @ApiModelProperty("站点Id")
    private String departId;
    @ApiModelProperty("产品名称")
    private String productName;
    @ApiModelProperty("产品编号")
    private String productNo;
    @ApiModelProperty("产品图片")
    private String picture;
    @ApiModelProperty("产品类型")
    private String lineType;
}
