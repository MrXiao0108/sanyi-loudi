package com.dzics.kanban.model.vo.rolemenu;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/1/8.
 */
@Data
public class UpdateRole {
    @ApiModelProperty("角色id")
    @NotNull(message = "请选择角色")
    private Long roleId;
    @ApiModelProperty(value = "角色名称")
    @NotBlank(message = "角色名称必填")
    private String roleName;

    @ApiModelProperty(value = "角色编码")
    @NotBlank(message = "角色编码必填")
    private String roleCode;

    @ApiModelProperty(value = "状态(true-正常,false-冻结)")
    @NotNull(message = "状态必传")
    private Integer status;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty("权限id")
    private List<Long> permissionId;
}
