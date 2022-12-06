package com.dzics.common.model.request.dzcheck;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * {
 * "palletNo":"A163",
 * "orderNo":"86500012563",
 * "workNo":"0030",
 * "material":"11473081",
 * "prodCount":"10"
 * }
 */
@Data
public class DzOrderCheck {
    /**
     * MOM 唯一订单号
     */
    private String guid;
    /**
     * 订单
     */
    @ApiModelProperty(value = " 大正订单号", required = true)
    private String orderCode;

    /**
     * 产线
     */
    @ApiModelProperty(value = " 大正产线序号", required = true)
    private String lineNo;

    /**
     * 订单号
     */
    @ApiModelProperty(value = " MOM订单号", required = true)
    private String momOrderNo;
    /**
     * 物料号
     */
    @ApiModelProperty(value = " 物料号", required = true)
    private String material;
    /**
     * 工序号
     */
    @ApiModelProperty(value = "工序号", required = true)
    private String workNo;

    /**
     * 托盘编号 料框编号
     */
    @ApiModelProperty(value = "托盘编号 料框编号", required = true)
    private String palletNo;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量", required = true)
    private String prodCount;

    /**
     * 车辆编号
     */
    @ApiModelProperty(value = "车辆编号 A B C", required = true)
    private String basketType;

}
