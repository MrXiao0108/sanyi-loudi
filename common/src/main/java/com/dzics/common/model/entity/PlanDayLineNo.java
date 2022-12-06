package com.dzics.common.model.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 每日生产数据完成率
 *
 * @author ZhangChengJun
 * Date 2021/2/20.
 * @since
 */
@Data
public class PlanDayLineNo {
    /**
     * 每次生产计划记录id
     */
    private Long planDayId;

    /**
     * 计划id
     */
    private Long planId;
    /**
     * 计划生产数量
     */
    private Integer plannedQuantity;
    /**
     * 已完成数量
     */
    private Integer completedQuantity;
    /**
     * 完成率
     */
    private BigDecimal percentageComplete;

    /**
     * 产出率
     */
    private BigDecimal outputRate;

    /**
     * 合格率
     */
    private BigDecimal passRate;
    /**
     * 产线序号
     */
    private String lineNo;


    /**
     * 统计产线设备id
     */
    private Long statisticsEquimentId;
}
