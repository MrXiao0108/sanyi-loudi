package com.dzics.common.model.request.agv;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * agv 到料信号手动点击 参数 触发机器人读取物料信息
 */
@Data
public class AgvClickSignal {

    /**
     * 车辆编号
     */
    @ApiModelProperty(value = "车辆编号 A B C", required = true)
    @NotBlank(message = "小车编号必填")
    private String basketType;

    /**
     * 订单序号
     */
    @ApiModelProperty(value = "订单号",required = true)
    @NotBlank(message = "订单号必传")
    private String orderNo;

    /**
     * 产线序号
     */
    @ApiModelProperty(value = "产线号",required = true)
    @NotBlank(message = "产线号必传")
    private String lineNo;

    /**
     * 料框编码
     */
    @ApiModelProperty("料框编码")
    private String palletNo;
}
