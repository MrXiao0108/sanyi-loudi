package com.dzics.common.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@ApiModel("组件物料")
@Data
public class MaterialVo {
    @ApiModelProperty(value = "组件物料简码", required = true)
    @NotEmpty(message = "组件物料简码不能为空")
    private String materialAlias;

    @ApiModelProperty(value = "组件物料编号", required = true)
    @NotEmpty(message = "组件物料编号不能为空")
    private String materialNo;

}
