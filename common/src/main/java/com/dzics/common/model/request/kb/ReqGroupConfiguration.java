package com.dzics.common.model.request.kb;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 编辑接口组关系详情信息
 *
 * @author ZhangChengJun
 * Date 2021/4/27.
 * @since
 */
@Data
public class ReqGroupConfiguration {
    @ApiModelProperty(value = "接口id", required = true)
    @NotNull(message = "接口id必填")
    private String interfaceId;

    @ApiModelProperty(value = "缓存时长", required = true)
    @NotNull(message = "缓存时间必传")
    private Integer cacheDuration;

}
