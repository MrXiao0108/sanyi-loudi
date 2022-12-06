package com.dzics.kanban.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * @author xnb
 * @date 2021年12月07日 9:12
 */
@Data
public class UpGpOrMdVo {
    @ApiModelProperty(value = "类型")
    @NotBlank(message = "类型必传")
    private String type;

    @ApiModelProperty(value = "组名称 （type为0，必填）")
    private String groupName;

    @ApiModelProperty(value = "组排序值")
    private String sortCode;

    @ApiModelProperty(value = "接口ID （type为1，必传）")
    private String interfaceId;

    @ApiModelProperty(value = "介绍内容")
    private String methodExplain;

    @ApiModelProperty(value = "简介 （type为1，必传）")
    private String briefIntroduction;

    @ApiModelProperty(value = "容器中类名称 （type为1，必传）")
    private String beanName;

    @ApiModelProperty(value = "缓存时长")
    private String cacheDuration;

    @ApiModelProperty(value = "返回参数名称 （type为1，必传）")
    private String responseName;

    @ApiModelProperty(value = "方法名称 （type为1，必传）")
    private String methodName;

    private String parentId;


}
