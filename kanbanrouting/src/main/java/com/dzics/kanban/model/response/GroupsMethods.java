package com.dzics.kanban.model.response;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

/**
 * @author xnb
 * @date 2021年12月03日 10:25
 */
@Data
public class GroupsMethods{
    @ApiModelProperty(value = "组ID")
    private String methodGroupId;

    @ApiModelProperty(value = "方法组名称")
    private String groupName;

    @ApiModelProperty(value = "组排序值")
    private String sortCode;

    @ApiModelProperty(value = "接口ID")
    private String interfaceId;

    @ApiModelProperty(value = "介绍内容")
    private String methodExplain;

    @ApiModelProperty(value = "简介")
    private String briefIntroduction;

    @ApiModelProperty(value = "方法名称")
    private String methodName;

    @ApiModelProperty(value = "容器中类名称")
    private String beanName;

    @ApiModelProperty(value = "缓存时长（单位 秒）")
    private Integer cacheDuration;

    @ApiModelProperty(value = "返回参数名称")
    private String responseName;

    private String parentId;

    private String menuId;

    private String type;

    private List<GroupsMethods>children;

}
