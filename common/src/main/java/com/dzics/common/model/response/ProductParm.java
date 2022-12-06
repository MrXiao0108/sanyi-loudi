package com.dzics.common.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ZhangChengJun
 * Date 2021/2/6.
 * @since 产品检测配置新增编辑返回参数定义
 */
@Data
public class ProductParm implements Serializable {
    @ApiModelProperty("产品名称")
    private String productName;

    @ApiModelProperty("产品编号")
    private String productNo;

    private String lineType;
}

