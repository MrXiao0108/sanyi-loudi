package com.dzics.common.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author xnb
 * @date 2021年05月28日 11:47
 */
@Data
public class PutProcessShowVo {
    @NotNull(message = "工序编号不能为空")
    @ApiModelProperty("工序编号")
    private String stationId;
    @NotNull(message = "修改类型不能为空")
    @ApiModelProperty("是否展示工位编号 (1展示0不展示)")
    private Integer onOff;
}
