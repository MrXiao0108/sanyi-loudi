package com.dzics.common.model.response;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.model.request.SelectEquipmentVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EquipmentDo extends DzEquipment {


    @ApiModelProperty("站点名称")
    @ExcelProperty("归属站点")
    private String departName;

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("产线名称")
    @ExcelProperty("产线名称")
    private String lineName;

    @ApiModelProperty("订单id")
    @ExcelIgnore
    private Long orderId;

    @ApiModelProperty("历史生产总数")
    @ExcelIgnore
    private String totalNum;

}

