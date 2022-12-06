package com.dzics.business.model.vo.user;

import com.baomidou.mybatisplus.annotation.TableField;
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
public class UpdateUser {
    @ApiModelProperty(value = "用户userId")
    @NotNull(message = "请选择用户")
    private String id;

    @ApiModelProperty(value = "姓名昵称")
    private String realname;

    @ApiModelProperty(value = "UserIdentityEnum.DZ.getCode().intValue()&")
    private String avatar;

    @ApiModelProperty(value = "状态(1-正常,0-冻结)")
    @NotNull(message = "状态必选")
    private Boolean status;

    @ApiModelProperty(value = "可切换换站点id")
    private List<String> departId;

    @ApiModelProperty(value = "角色id", required = false)
    private List<String> roleIds;

    @ApiModelProperty(value = "登录账号")
    @NotBlank(message = "登录账号必填")
    private String username;
}
