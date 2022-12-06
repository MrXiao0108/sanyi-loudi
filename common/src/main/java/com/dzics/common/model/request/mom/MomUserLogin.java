package com.dzics.common.model.request.mom;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * mom用户登录信息
 *
 * @author ZhangChengJun
 * Date 2022/1/10.
 * @since
 */
@Data
public class MomUserLogin {
    @ApiModelProperty("人员信息")
    @NotNull(message = "请输入员工号")
    private String employeeNo;

    @ApiModelProperty(value = "登录 UP,退出 LO")
    @NotNull(message = "请输入操作类型")
    private String type;
}
