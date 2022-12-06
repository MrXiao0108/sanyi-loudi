package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 调用方法类详情
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_interface_method")
@ApiModel(value = "SysInterface对象", description = "调用方法类详情")
public class SysInterfaceMethod implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "接口id",required=true)
    @TableId(value = "interface_id", type = IdType.ASSIGN_ID)
    private String interfaceId;

    @ApiModelProperty(value = "介绍内容", required = true)
    @TableField("method_explain")
    @NotNull(message = "介绍内容必填")
    private String methodExplain;

    @TableField("brief_introduction")
    @ApiModelProperty(value = "简介", required = true)
    @NotNull(message = "简介必填")
    private String briefIntroduction;

    @ApiModelProperty(value = "方法名称", required = true)
    @TableField("method_name")
    @NotBlank(message = "方法名称必填")
    private String methodName;

    @ApiModelProperty(value = "容器中类名称", required = true)
    @TableField("bean_name")
    private String beanName;

    @ApiModelProperty(value = "缓存时长（单位 秒）")
    @TableField("cache_duration")
    private Integer cacheDuration;

    @ApiModelProperty(value = "返回参数名称", required = true)
    @TableField("response_name")
    @NotEmpty(message = "返回参数名称必填")
    private String responseName;

    /**
     * 是否标记
     */
    @ApiModelProperty("是否选中")
    @TableField(exist = false)
    private Integer isShow = 1;

}
