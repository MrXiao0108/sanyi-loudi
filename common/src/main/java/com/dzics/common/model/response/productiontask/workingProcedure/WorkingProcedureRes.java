package com.dzics.common.model.response.productiontask.workingProcedure;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 工序列表
 *
 * @author ZhangChengJun
 * Date 2021/5/18.
 * @since
 */
@Data
public class WorkingProcedureRes {
    @ExcelProperty("订单编号")
    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("站点名称")
    @ExcelProperty("归属站点")
    private String departName;

    @ApiModelProperty("产线编号")
    @ExcelIgnore
    private String lineNo;

    @ApiModelProperty("产线名称")
    @ExcelProperty("产线名称")
    private String lineName;

    @ExcelProperty("工序编号")
    @ApiModelProperty("工序编号")
    private String workCode;

    @ExcelProperty("工序名称")
    @ApiModelProperty("工序名称")
    private String workName;


    @ApiModelProperty("工序id")
    @ExcelIgnore
    private String workingProcedureId;
    @ApiModelProperty("产线id")
    @ExcelIgnore
    private String lineId;
    @ExcelIgnore
    @ApiModelProperty("订单id")
    private String orderId;
    @ExcelIgnore
    @ApiModelProperty("站点id")
    private String departId;

    @ExcelProperty("排序码")
    @ApiModelProperty("排序")
    private Integer sortCode;
}
