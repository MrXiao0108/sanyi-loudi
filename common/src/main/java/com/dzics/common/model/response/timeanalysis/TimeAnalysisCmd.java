package com.dzics.common.model.response.timeanalysis;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 设备指令实体类
 *
 * @author ZhangChengJun
 * Date 2021/10/9.
 * @since
 */
@Data
public class TimeAnalysisCmd implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 设备ID
     */
    private Long deviceId;

    private String orderNo;
    private String lineNo;
    private String equipmentNo;
    private Integer equipmentType;

    /**
     * 机器人告警状态
     */
    private String a817;

    /**
     * 机床告警状态
     */
    private String b813;

    /**
     * 安全门状态
     */
    private String s561;
    /**
     * 机器人  连接状态
     */
    private String a561;
    /**
     * 机器人 操作模式
     */
    private String a562;

    /**
     * 机器人运行状态
     */
    private String a563;

    /**
     * 机器人急停状态
     * */
    private String a565;

    /**
     * 机器人告警状态
     */
    private String a566;

    /**
     * 待机状态
     */
    private String a567;

    /**
     * 机床  连接状态
     */
    private String b561;
    /**
     * 机床 运行状态
     */
    private String b562;
    /**
     * 机床  自动/手动模式选择
     */
    private String b565;

    /**
     * 机床 急停状态
     * */
    private String b568;

    /**
     * 机床 告警状态
     */
    private String b569;


    /**
     * 连接状态
     */
    private String h561;
    /**
     * 运行状态
     */
    private String h562;
    /**
     * 告警状态
     */
    private String h565;

    /**
     * 连接状态
     */
    private String k561;
    /**
     * 运行状态
     */
    private String k562;
    /**
     * 告警状态
     */
    private String k565;

    /**
     * 工作状态 -1 未初始化
     */
    private Integer workState = -1;

    /**
     * 安全门状态 0 关门 1 开门，-1未初始化
     */
    private Integer menStatus = -1;

    private String alarmType;

    private Date updateTime;

}
