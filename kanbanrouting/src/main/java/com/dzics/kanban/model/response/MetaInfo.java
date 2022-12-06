package com.dzics.kanban.model.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ZhangChengJun
 * Date 2021/1/7.
 */
@Data
public class MetaInfo implements Serializable {
    /**
     * 如果设置为true，则不会被 <keep-alive> 缓存(默认 false)
     */
    private boolean noCache;
    /**
     * 设置该路由的图标，支持 svg-class，也支持 el-icon-x element-ui 的 icon
     */
    private String icon;
    /**
     * 设置该路由在侧边栏和面包屑中展示的名字
     */
    private String title;

    public MetaInfo(boolean noCache, String icon, String title) {
        this.noCache = noCache;
        this.icon = icon;
        this.title = title;
    }
}
