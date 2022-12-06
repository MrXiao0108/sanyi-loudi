package com.dzics.common.model.response;

import com.alibaba.excel.annotation.ExcelProperty;
import com.dzics.common.model.entity.DzOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DzOrderDo extends DzOrder {

//    @ApiModelProperty("站点id")

    @ApiModelProperty("站点名称")
    @ExcelProperty(value = "归属站点")
    private String departName;

}
