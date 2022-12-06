package com.dzics.common.model.request.mom;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LineIdWorkStation {
    @ApiModelProperty("产线ID")
    @NotBlank(message = "选择产线")
    private String lineId;
}
