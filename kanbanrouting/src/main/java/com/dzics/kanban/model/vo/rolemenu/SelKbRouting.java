package com.dzics.kanban.model.vo.rolemenu;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 获取看板路由信息
 *
 * @author ZhangChengJun
 * Date 2021/4/28.
 * @since
 */
@Data
public class SelKbRouting {
    @ApiModelProperty(value = "父标题名称",required = true)
    private String parentTitle;

    @ApiModelProperty(value = "路由序号")
    private String pathNumber;

}
