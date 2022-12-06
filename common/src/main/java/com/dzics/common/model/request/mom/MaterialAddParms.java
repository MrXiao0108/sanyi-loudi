package com.dzics.common.model.request.mom;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MaterialAddParms {
    @ApiModelProperty(value = "组件物料简码", required = true)
    @NotNull(message = "请填写全部表单参数")
    private String materialAlias;


    @ApiModelProperty(value = "组建物料编号", required = true)
    @NotNull(message = "请填写全部表单参数")
    private String materialNo;

    @ApiModelProperty(value = "组件物料数量")
    private Integer quantity;
}
