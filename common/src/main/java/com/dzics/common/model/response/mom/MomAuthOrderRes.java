package com.dzics.common.model.response.mom;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author ZhangChengJun
 * Date 2022/1/22.
 * @since
 */
@Data
public class MomAuthOrderRes {
    @ApiModelProperty("订单ID")
    private String proTaskOrderId;
    @ApiModelProperty("订单号")
    private String wipOrderNo;
    @ApiModelProperty("产品物料号")
    private String productNo;
    @ApiModelProperty("简码")
    private String productAlias;
    @ApiModelProperty("开始状态 110已下达 120进行中")
    private String progressStatus;
    @ApiModelProperty("实际开始时间")
    private String realityStartDate;
    @ApiModelProperty("数量")
    private String quantity;
    @ApiModelProperty("产出数量")
    private String orderOutput;
    @ApiModelProperty("计划开始时间")
    private String scheduledStartDate;
    @ApiModelProperty("实际完成时间")
    private String realityCompleteDate;
}
