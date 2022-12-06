package com.dzics.common.model.request.mom;

import com.dzics.common.util.PageLimitBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Classname AgvLogParms
 * @Description 描述
 * @Date 2022/2/22 19:09
 * @Created by NeverEnd
 */
@Data
public class AgvLogParms extends PageLimitBase {
    @ApiModelProperty("订单号")
    @NotNull(message = "选择查看的订单,默认选择第一个订单")
    private String orderNo;

    @ApiModelProperty("查询日期,默认当天日期")
    private String createDate;

    @ApiModelProperty("MOM订单号")
    private String wipOrderNo;

    @ApiModelProperty("料点编码")
    private String pointCode;


    @ApiModelProperty("请求类型")
    private String brief;

    @ApiModelProperty(value = "开始时间 yyyy-MM-dd HH:mm:ss")
    private String beginTime;

    @ApiModelProperty(value = "结束时间 yyyy-MM-dd HH:mm:ss")
    private String endTime;
}
