package com.dzics.business.model.vo.detectordata;

import com.dzics.common.util.PageLimit;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 产品检测配置列表查询参数定义
 *
 * @author ZhangChengJun
 * Date 2021/2/5.
 * @since
 */
@Data
public class ProDuctCheck extends PageLimit {
    @ApiModelProperty("产品名称")
    private String productName;
    /**
     * 订单ID
     */
    @ApiModelProperty("订单ID")
    private String orderId;
    /**
     * 产线ID
     */
    @ApiModelProperty("产线Id")
    private String lineId;
}
