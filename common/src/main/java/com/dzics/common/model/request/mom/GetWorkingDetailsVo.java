package com.dzics.common.model.request.mom;

import com.dzics.common.util.PageLimit;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class GetWorkingDetailsVo extends PageLimit {
    @ApiModelProperty("产线id")
    private String lineId;

    @ApiModelProperty("订单Id")
    private String orderId;

    @ApiModelProperty("工位ID")
    private String stationId;
    /**
     *
     */
    @ApiModelProperty("二维码")
    private String qrCode;

    @ApiModelProperty("Mom订单号")
    private String workpieceCode;

    @ApiModelProperty("生产开始时间")
    private String workStartTIme;

    @ApiModelProperty("生产结束时间")
    private String workEndTIme;

    @ApiModelProperty("报工开始时间")
//    上报开始时间 为开始
    private String startTime;

    @ApiModelProperty("报工结束时间")
//    上报完成时间 为结束
    private String endTime;

    /**
     * 班次名称
     */
    @ApiModelProperty("班次名称")
    private String workName;

    @ApiModelProperty("生产日期")
    private String workDate;
}
