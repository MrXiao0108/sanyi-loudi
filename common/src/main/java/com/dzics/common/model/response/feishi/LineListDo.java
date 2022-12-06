package com.dzics.common.model.response.feishi;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LineListDo {

    @ApiModelProperty("产线id")
    private String id;
    @ApiModelProperty("产线序号")
    private String lineNo;
    @ApiModelProperty("产线名称")
    private String lineName;
    @ApiModelProperty("设备id")
    private String equipmentId;
    @ApiModelProperty("设备序号")
    private String equipmentNo;
    @ApiModelProperty("设备编码")
    private String equipmentCode;
    @ApiModelProperty("订单编号")
    private String orderNo;
    @ApiModelProperty("站点名称")
    private String departName;


}
