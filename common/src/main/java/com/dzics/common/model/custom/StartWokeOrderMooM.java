package com.dzics.common.model.custom;

import lombok.Data;

/**
 * 当前在做订单
 *
 * @author ZhangChengJun
 * Date 2021/5/19.
 * @since
 */
@Data
public class StartWokeOrderMooM {
    /**
     * 生产任务订单Id
     */
    private String proTaskId;
    /**
     * 订单号
     */
    private String wipOrderNo;
    /**
     * 产品物料号
     */
    private String productNo;

    /**
     * 临时 物料简码+ 类别 = 临时订单号
     */
    private String productAliasProductionLine;
    /**
     * 状态
     */
    private String progressStatus;

    /**
     * 订单状态请求变更结果 1.操作进行中  2.操作完成
     */
    private Integer orderOperationResult;
}
