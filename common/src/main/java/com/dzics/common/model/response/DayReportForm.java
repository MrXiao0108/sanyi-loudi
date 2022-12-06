package com.dzics.common.model.response;

import lombok.Data;

import java.time.LocalDate;

/**
 * 日生产报表数据
 *
 * @author ZhangChengJun
 * Date 2021/6/22.
 * @since
 */
@Data
public class DayReportForm {
    /**
     * 产线名称
     */
    private String lineName;
    /**
     * 设备类型
     */
    private Integer equipmentType;
    /**
     *设备编码
     */
    private String equipmentCode;
    /**
     *设备名称
     */
    private String equipmentName;
    /**
     *班次名称
     */
    private String workName;
    /**
     *班次结束时间
     */
    private String startTime;
    /**
     *班次开始时间
     */
    private String endTime;
    /**
     *班次日期
     */
    private LocalDate workData;
    /**
     *成品数量 =产出数量 = 当前产量
     */
    private Long nowNum;
    /**
     *毛坯数量
     */
    private Long roughNum;
    /**
     *合格数量
     */
    private Long qualifiedNum;
    /**
     *不良品数量
     */
    private Long badnessNum;
    /**
     *设备id
     */
    private Long equimentId;
    /**
     *产线id
     */
    private Long lineId;
    /**
     *产线序号
     */
    private String lineNo;
    /**
     *订单号
     */
    private String orderNo;
}
