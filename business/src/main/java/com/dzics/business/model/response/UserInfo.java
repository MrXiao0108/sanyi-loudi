package com.dzics.business.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/1/7.
 */
@Data
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("用户角色列表")
    private List<String> roles;
    @ApiModelProperty("权限列表")
    private List<String> permissions;
    @ApiModelProperty("用户基础信息")
    private UserMessage user;
    @ApiModelProperty("系统编码")
    private String sysConfig;
}
