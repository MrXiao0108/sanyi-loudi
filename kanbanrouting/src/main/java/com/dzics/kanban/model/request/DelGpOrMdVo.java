package com.dzics.kanban.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author xnb
 * @date 2021年12月06日 15:01
 */
@Data
public class DelGpOrMdVo {
    @NotBlank(message = "类型不能为空")
    @ApiModelProperty(value = "类型",required = true)
    private String type;
    @ApiModelProperty(value = "组ID（删除组、删除方法 必传）")
    private String methodGroupId;
    @ApiModelProperty(value = "接口ID（删除方法 必传）")
    private String interfaceId;
}
