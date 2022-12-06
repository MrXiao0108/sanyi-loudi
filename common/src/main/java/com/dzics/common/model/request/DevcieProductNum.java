package com.dzics.common.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 设备生产数量分时请求参数
 *
 * @author ZhangChengJun
 * Date 2021/11/2.
 * @since
 */
@Data
public class DevcieProductNum {
    /**
     * 设备ID
     */
    @ApiModelProperty("设备ID")
    private Long id;

    /**
     * 是否展示
     */
    @ApiModelProperty("是否展示")
    private Boolean isShow = false;
}
