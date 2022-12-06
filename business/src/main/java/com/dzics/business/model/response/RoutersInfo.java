package com.dzics.business.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/1/7.
 */
@Data
public class RoutersInfo  implements Serializable {

    @JsonIgnore
    private String id;
    @JsonIgnore
    private String pid;
    /**
     * 当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
     */
    @ApiModelProperty("当设置 noRedirect 的时候该路由在面包屑导航中不可被点击")
    private String redirect;
    /**
     * 路由
     */
    @ApiModelProperty("路由")
    private String path;
    /**
     * 挂载地点
     */
    @ApiModelProperty("挂载地点")
    private String component;
    /**
     * 是否隐藏
     */
    @ApiModelProperty("是否隐藏")
    private Boolean hidden;
    /**
     * 子节点
     */
    @ApiModelProperty("子节点")
    private List<RoutersInfo> children;
    /**
     * 图标信息
     */
    @ApiModelProperty("图标信息")
    private MetaInfo meta;

    /**
     * 设定路由的名字，一定要填写不然使用<keep-alive>时会出现各种问题
     */
    @ApiModelProperty("路由的名字")
    private String name;
    /**
     * 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
     * 只有一个时，会将那个子路由当做根路由显示在侧边栏--如引导页面
     * 若你想不管路由下面的 children 声明的个数都显示你的根路由
     * 你可以设置 alwaysShow: true，这样它就会忽略之前定义的规则，一直显示根路由
     */
    @ApiModelProperty(" alwaysShow: true 一直显示根路由")
    private boolean alwaysShow;
}
