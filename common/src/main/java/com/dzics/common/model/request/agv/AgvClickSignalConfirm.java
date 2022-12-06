package com.dzics.common.model.request.agv;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * agv 到料信号手动点击 参数 触发机器人读取物料信息
 */
@Data
public class AgvClickSignalConfirm {

    @ApiModelProperty(value = "唯一订单号",required = true)
     private String guid;

    /**
     * 物料信息ID
     */
    @ApiModelProperty(value = "物料信息ID",required = true)
    @NotBlank(message = "消息ID必传")
    private String receiveMaterialId;
    /**
     * 物料号
     */
    @ApiModelProperty(value = "物料号",required = true)
    private String materialNo;

    /**
     * 车辆编号
     */
    @ApiModelProperty(value = "车辆编号 A  B  C", required = true)
    @NotBlank(message = "车辆编号必传递")
    private String basketType;

    @ApiModelProperty(value = "物料数量")
    private Integer prodCount;
    /**
     * 订单序号
     */
    @ApiModelProperty("MOM订单号")
    private String momOrderNo;

    @ApiModelProperty(value = "订单号",required = true)
    @NotBlank(message = "订单号必传")
    private String orderNo;

    /**
     * 产线序号
     */
    @ApiModelProperty(value = "产线号",required = true)
    @NotBlank(message = "产线号必传")
    private String lineNo;
}
