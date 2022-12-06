package com.dzics.common.model.response.productiontask.stationbg;

import lombok.Data;

/**
 * 检测项
 *
 * @author ZhangChengJun
 * Date 2021/5/28.
 * @since
 */
@Data
public class CheckItems {
    /**
     * 工位ID
     */
    private String stationId;

    /**
     * 表格字段值
     */
    private String tableColVal;

    /**
     * 检测内容 列名
     */
    private String tableColCon;

    /**
     * 检测项 配置模板ID
     */
    private String detectionId;
}
