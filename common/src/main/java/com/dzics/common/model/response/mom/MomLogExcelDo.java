package com.dzics.common.model.response.mom;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@Data
public class MomLogExcelDo {
    @ApiModelProperty("请求类型")
    @ExcelProperty("请求类型")
    private String details;

    @ApiModelProperty("Mom订单号")
    @ExcelProperty("Mom订单号")
    private String wipOrderNo;

    @ApiModelProperty("料点编码")
    @ExcelProperty("料点编码")
    private String pointCode;

    @ApiModelProperty("调用状态")
    @ExcelProperty("调用状态")
    private String invokStatus;

    @ApiModelProperty("异常信息")
    @ExcelProperty("异常信息")
    private String abnormal;

    @ApiModelProperty("请求报文")
    @ExcelProperty("请求报文")
    private String insideParm;

    @ApiModelProperty("响应信息")
    @ExcelProperty("响应信息")
    private String invokReturn;

    @ApiModelProperty("开始时间")
    @ExcelProperty("开始时间")
    private String startTime;

    @ApiModelProperty("结束时间")
    @ExcelProperty("结束时间")
    private String endTime;

    @ApiModelProperty("耗时")
    @ExcelProperty("耗时(秒)")
    private BigDecimal invokCost;

    @ApiModelProperty("日期")
    @ExcelProperty("日期")
    private String createDate;
}
