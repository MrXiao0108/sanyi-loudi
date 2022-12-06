package com.dzics.common.model.request.toolinfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PutToolGroupsVo {

    @ApiModelProperty("刀具组id")
    @NotNull(message = "刀具组id不能为空")
    private Long toolGroupsId;
    @ApiModelProperty("刀具组编号")
    @NotNull(message = "刀具组编号不能为空")
    private Integer groupNo;
}
