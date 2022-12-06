package com.dzics.common.model.response;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.dzics.common.model.entity.DzToolCompensationData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GetToolInfoDataListDo extends DzToolCompensationData {

    @ExcelProperty("订单编号")
    @ApiModelProperty("订单编号")
    private String orderNo;
    @ExcelProperty("产线名称")
    @ApiModelProperty("产线名称")
    private String lineName;

    @ApiModelProperty("站点名称")
    @ExcelIgnore
    private String departName;

}
