package com.dzics.common.model.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 数据运行模式参数封装
 *
 * @author ZhangChengJun
 * Date 2021/2/23.
 * @since
 */
@Data
public class RunDataModel implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 班次生产记录表
     */
    private String tableName;
    /**
     * 日计划产量统计生产率
     */
    private String planDay;

    private String runDataModel;
}
