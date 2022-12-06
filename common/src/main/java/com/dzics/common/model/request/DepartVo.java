package com.dzics.common.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class DepartVo  {
    @ApiModelProperty(value = "id(添加不填,修改必填)")
    private Long id;
    @ApiModelProperty(value = "站点公司名称")
    @NotEmpty(message = "站点名称必填")
    private String departName;
    @ApiModelProperty(value = "备注")
    private String memo;
    @ApiModelProperty(value = "机构编码(添加必填,修改不填)")
    private String orgCode;
}
