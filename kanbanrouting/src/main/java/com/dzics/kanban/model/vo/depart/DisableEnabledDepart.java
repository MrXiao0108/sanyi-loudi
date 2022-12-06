package com.dzics.kanban.model.vo.depart;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author ZhangChengJun
 * Date 2021/1/8.
 */
@Data
public class DisableEnabledDepart {
    @ApiModelProperty(value = "站点id",required = true)
    @NotNull(message = "请选择站点")
    private Long departId;
    @ApiModelProperty(value = "状态(1启用，0不启用)",required = true)
    @NotNull(message = "请设置状态")
    private Integer status;
}
