package com.dzics.common.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class DictItemVo {
    @ApiModelProperty(value = "id(修改必填,添加不填)")
    private Long id;
    @ApiModelProperty(value = "字典类型id(添加必填,修改不填)")
    private Long dictId;
    @ApiModelProperty(value = "字典项文本")
    @NotEmpty(message = "字典项文本不能为空")
    private String itemText;
    @ApiModelProperty(value = "字典项值")
    @NotEmpty(message = "字典项值不能为空")
    private String itemValue;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "排序码")
    @NotNull(message = "排序码不能为空")
    private Integer sortOrder;
}
