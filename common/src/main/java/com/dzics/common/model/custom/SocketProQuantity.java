package com.dzics.common.model.custom;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 机器人生产数量信息
 *
 * @author ZhangChengJun
 * Date 2021/2/26.
 * @since
 */
@ApiModel(value="SocketProQuantity", description="机器人生产数量信息")
@Data
public class SocketProQuantity  {
    /**
     * 当前产量
     */
    @ApiModelProperty("当前产量")
    private Long nowNum;
    /**
     * 投入数量
     */
    @ApiModelProperty("投入数量")
    private Long roughNum;
    /**
     * 不良品数量
     */
    @ApiModelProperty("不良品数量")
    private Long badnessNum;

    @ApiModelProperty("设备ID")
    private String equimentId;
}
