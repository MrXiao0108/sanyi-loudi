package com.dzics.business.model.vo.rolemenu;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

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
}
