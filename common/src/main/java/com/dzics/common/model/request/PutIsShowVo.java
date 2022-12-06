package com.dzics.common.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class PutIsShowVo {
    @NotNull(message = "id不能为空")
    private Long id;
    @ApiModelProperty("0不显示，1显示")
    @NotNull(message = "isShow不能为空")
    @Min(0)
    @Max(1)
    private Integer isShow;
}
