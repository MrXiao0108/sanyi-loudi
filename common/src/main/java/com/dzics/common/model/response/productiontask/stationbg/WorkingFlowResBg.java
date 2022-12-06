package com.dzics.common.model.response.productiontask.stationbg;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 最近检测工件
 *
 * @author ZhangChengJun
 * Date 2021/5/20.
 * @since
 */
@Data
public class WorkingFlowResBg {
    /**
     * 二维码
     */
    private String qrCode;

    /**
     * 工件编码
     */
    private String workpieceCode;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    private String taktTime = "--";
    /**
     * 格式化后的时间
     */
    @ApiModelProperty(value = "格式化后的时间")
    private String updateTimeUse;
    @ApiModelProperty(value = "初始时间")
    private String updateTimeUseAfter;
}
