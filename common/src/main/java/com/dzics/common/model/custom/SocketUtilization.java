package com.dzics.common.model.custom;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 稼动相关
 *
 * @author ZhangChengJun
 * Date 2021/3/1.
 */
@Data
@ApiModel(value="SocketUtilization", description="稼动相关")
public class SocketUtilization  {

    @ApiModelProperty("历史稼动率")
    private BigDecimal historyOk;
    @ApiModelProperty("历史故障率")
    private BigDecimal historyNg;

    @ApiModelProperty("当日稼动率")
    private BigDecimal dayOk;
    @ApiModelProperty("当日故障率")
    private BigDecimal dayNg;

    @ApiModelProperty("稼动时间")
    private Long ok;
    @ApiModelProperty("故障时间")
    private Long ng;

    @ApiModelProperty("设备ID")
    private String equimentId;
    @ApiModelProperty("设备编号")
    private String equimentNo;

}
