package com.dzics.kanban.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class PutUserPasswordVo {

    @ApiModelProperty(value = "旧密码")
    @NotEmpty(message = "旧密码必填")
    private String passwordOld;


    @ApiModelProperty(value = "新密码")
    @NotEmpty(message = "新密码必填")
    private String passwordNew;


    @ApiModelProperty(value = "确认密码")
    @NotEmpty(message = "确认密码必填")
    private String passwordRepeat;
}
