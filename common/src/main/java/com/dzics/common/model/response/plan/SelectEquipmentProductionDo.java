package com.dzics.common.model.response.plan;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SelectEquipmentProductionDo {

    @ExcelProperty("订单编号")
    @ApiModelProperty("订单编号")
    private String orderNo;
    @ExcelProperty("产线名称")
    @ApiModelProperty("产线名称")
    private String lineName;
    @ExcelProperty("设备名称")
    @ApiModelProperty("设备名称")
    private String equipmentName;
    @ExcelProperty("设备编号")
    @ApiModelProperty("设备编号")
    private String equipmentCode;
    @ExcelProperty("设备序号")
    @ApiModelProperty("设备序号")
    private String equipmentNo;
    @ExcelProperty("总生产数量")
    @ApiModelProperty("总生产数量")
    private Long productionQuantity;
    @ExcelProperty("日期")
    @ApiModelProperty("日期")
    private String workDate;
    @ExcelIgnore
    @ApiModelProperty("设备id")
    private Long equimentId;
}
