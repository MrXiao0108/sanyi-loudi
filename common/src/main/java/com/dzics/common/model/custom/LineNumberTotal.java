package com.dzics.common.model.custom;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 产线日产
 *
 * @author ZhangChengJun
 * Date 2021/5/23.
 * @since
 */
@Data
public class LineNumberTotal implements Serializable {
    /**
     * 设备id
     */
    @ApiModelProperty("设备id")
    private String equimentId;
    /**
     * 当前产量
     */
    @ApiModelProperty("当日数量")
    private Long dayNum;

    @ApiModelProperty("日产量")
    private String dateStr;
}
