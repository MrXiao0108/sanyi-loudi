package com.dzics.common.model.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;


@Data
public class PutUserInfoVo {

    @ApiModelProperty(value = "真实姓名")
    @NotEmpty(message = "用户名称必填")
    private String realname;

    @ApiModelProperty(value = "性别(0-默认未知,1-男,2-女)")
    @TableField("sex")
    @JsonIgnore
    @Min(0)
    @Max(2)
    private Integer sex;

    @ApiModelProperty(value = "电子邮件")
    @TableField("email")
    @NotEmpty(message = "邮箱必填")
    private String email;

    @ApiModelProperty(value = "电话")
    @TableField("phone")
    @NotEmpty(message = "电话必填")
    private String phone;





}
