package com.dzics.common.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author xnb
 * @date 2021年06月01日 16:45
 */
@Data
public class ProductionVo {


    @ApiModelProperty(value = "产线id",required = true)
    @NotNull(message = "请选择产线")
    private Long lineId;

    @ApiModelProperty("日期")
    private String startTime;
}
