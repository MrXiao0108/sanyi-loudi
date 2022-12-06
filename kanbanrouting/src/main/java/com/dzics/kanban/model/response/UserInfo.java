package com.dzics.kanban.model.response;

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
    @ApiModelProperty("用户角色列表")
    private List<String> roles;
    @ApiModelProperty("权限列表")
    private List<String> permissions;
    @ApiModelProperty("用户基础信息")
    private UserMessage user;
}
