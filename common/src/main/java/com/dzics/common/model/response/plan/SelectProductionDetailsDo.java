package com.dzics.common.model.response.plan;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class SelectProductionDetailsDo {
    @ExcelProperty("订单编号")
    @ApiModelProperty("订单编号")
    private String orderNo;

    @ExcelProperty("产线名称")
    @ApiModelProperty("产线名称")
    private String lineName;

    @ExcelProperty("成品编号")
    @ApiModelProperty("产品编号(id)")
    private String productNo;

    @ExcelProperty("产出数量(件)")
    @ApiModelProperty("产出数量")
    private Long totalNum;

    @ExcelProperty("毛坯(件)")
    @ApiModelProperty("毛坯")
    private Long roughNum;

    @ExcelProperty("不合格(件)")
    @ApiModelProperty("不合格")
    private Long badnessNum;

    @ExcelProperty("合格(件)")
    @ApiModelProperty("合格")
    private Long qualifiedNum;

    @ExcelProperty("日期")
    @ApiModelProperty("生产日期")
    private String workDate;

    @ExcelIgnore
    @ApiModelProperty("站点id")
    private String departId;
    @ExcelIgnore
    @ApiModelProperty("产品名称")
    private String productName;
    @ExcelIgnore
    @ApiModelProperty("产品类型")
    private String productType;


    @ExcelIgnore
    private String lineNo;

}
