package com.dzics.kanban.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/1/6.
 */
@Data
public class RegisterVo {

    @ApiModelProperty("1大正用户 2直属大正站点用户,3 其他用户")
    private Integer userIdentity=3;

    @ApiModelProperty(value = "账号", required = true)
    @NotBlank(message = "账号必填")
    @Pattern(regexp = "^[a-zA-Z0-9]{5,}$", message = "账号长度不能小于5,必须是数字或字母")
    private String username;

    @ApiModelProperty(value = "姓名", required = true)
    @NotBlank(message = "名称必填")
    private String realname;

    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message = "密码必填")
    private String password;

    @ApiModelProperty(value = "归属站点id", required = false)
    private Long affiliationDepartId;

    @ApiModelProperty(value = "可切换换站点id")
    private List<Long> departId;

    @ApiModelProperty(value = "角色id", required = false)
    private List<Long> roleIds;
}
