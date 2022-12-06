package com.dzics.common.model.custom;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 数据唯一熟悉类
 *
 * @author ZhangChengJun
 * Date 2021/1/27.
 * @since
 */
@Data
public class TcpDataIDBase {
    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    @TableField("order_number")
    private String orderNumber;
    /**
     * 产线序号
     */
    @ApiModelProperty(value = "产线序号")
    @TableField("production_line_number")
    private String productionLineNumber;
    /**
     * 设备类型
     */
    @ApiModelProperty(value = "设备类型")
    @TableField("device_type")
    private String deviceType;

    /**
     * 设备号
     */
    @ApiModelProperty(value = "设备号")
    @TableField("device_number")
    private String deviceNumber;
}
