package com.dzics.common.model.response.commons;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 产线
 *
 * @author ZhangChengJun
 * Date 2021/5/19.
 * @since
 */
@Data
public class Lines {

    @ApiModelProperty("产线ID")
    private String lineId;

    @ApiModelProperty(value = "订单id")
    private String orderId;

    @ApiModelProperty(value = "产线序号")
    private String lineNo;

    @ApiModelProperty(value = "产线编码")
    private String lineCode;

    @ApiModelProperty(value = "产线名称")
    private String lineName;

    @ApiModelProperty(value = "订单编码")
    private String orderNo;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "产线类型")
    private String lineType;

}
