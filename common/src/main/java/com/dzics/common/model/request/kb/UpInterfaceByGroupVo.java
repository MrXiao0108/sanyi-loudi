package com.dzics.common.model.request.kb;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author xnb
 * @date 2021年12月03日 16:48
 */
@Data
public class UpInterfaceByGroupVo {

    @NotBlank(message = "组ID必选")
    @ApiModelProperty(value = "组ID",required = true)
    private String methodGroupId;

    @NotBlank(message = "接口ID必传")
    @ApiModelProperty(value = "接口ID",required = true)
    private String interfaceId;

    @ApiModelProperty(value = "容器中类名称", required = false)
    private String beanName;

    @ApiModelProperty(value = "缓存时长",required = false)
    private String cacheDuration;

    @ApiModelProperty(value = "方法名称", required = true)
    @NotBlank(message = "方法名称必填")
    private String methodName;

    @ApiModelProperty(value = "返回参数名称", required = true)
    @NotBlank(message = "返回参数名称必填")
    private String responseName;

    @ApiModelProperty(value = "简介", required = false)
    private String briefIntroduction;

    @ApiModelProperty(value = "介绍内容", required = false)
    private String methodExplain;
}
