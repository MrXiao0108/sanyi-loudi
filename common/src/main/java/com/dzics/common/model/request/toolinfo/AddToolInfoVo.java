package com.dzics.common.model.request.toolinfo;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;

@Data
public class AddToolInfoVo {
    @ApiModelProperty("刀具组id")
    @NotNull(message = "刀具组id不能为空")
    private Long toolGroupId;
    @ApiModelProperty("新增刀具编号")
    @NotNull(message = "新增刀具编号不能为空")
    private Integer toolNo;
}
