package com.dzics.common.model.custom;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.baomidou.mybatisplus.annotation.TableField;
import com.dzics.common.model.entity.DzEquipment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 机加工看板信息
 *
 * @author ZhangChengJun
 * Date 2021/3/11.
 * @since
 */
@Data
public class MachiningMessageStatus implements Serializable {

    /**
     * 贵万江  工作台1[切削/循环]
     */
    private String b527;

    /**
     *贵万江  工作台1[切削/循环]
     */
    private String b526;
    /**
     * 底层上发的 停机次数
     */
    private String a812;

    /**
     * 底层上发的 告警刀具编号
     */
    private String b809;

    /**
     * 底层上发的 运行时间
     */
    private String a541;
    /**
     * 设备ID
     */
    private String equimentId;

    @ApiModelProperty("设备序号")
    private String equipmentNo;
    @ApiModelProperty("设备类型")
    private Integer equipmentType;

    private String x;
    private String y;
    private String z;
    @ApiModelProperty("工作状态")
    private String workStatus;
    @ApiModelProperty("设备名称")
    private String equipmentName;
    /**
     * 压头上下位置
     */
    @ApiModelProperty("压头上下位置")
    private String headPositionUd;
    /**
     * 压头左右位置
     */
    @ApiModelProperty("压头左右位置")
    private String headPostionLr;

    /**
     * 淬火机 移动速度  mm/s
     */
    private String movementSpeed;
    /**
     * 工件转速 Rad/min
     */
    private String workpieceSpeed;
    /**
     * 冷却液温度 ℃
     */
    private String coolantTemperature;
    /**
     * 冷却液压力 MPa
     */
    private String coolantPressure;
    /**
     * 冷却液流量 L/s
     */
    private String coolantFlow;

    @ApiModelProperty("操作模式")
    private String operatorMode;
    @ApiModelProperty("连接状态")
    private String connectState;
    @ApiModelProperty("运行状态")
    private String runStatus;
    @ApiModelProperty("急停状态")
    private String emergencyStatus;
    @ApiModelProperty("告警状态")
    private String alarmStatus;
    @ApiModelProperty("速度倍率")
    private String speedRatio;
    @ApiModelProperty("加工节拍")
    private String machiningTime;

    private String currentLocation;

    @ApiModelProperty("停机次数")
    private Long downSum = 0L;

    /**
     * 进给速度
     */
    @ApiModelProperty("进给速度")
    private String feedSpeed;
    /**
     * 主轴转速
     */
    @ApiModelProperty("主轴转速")
    private String speedOfMainShaft;
    /**
     * 当前产量
     */
    @ApiModelProperty("当日数量")
    private Long dayNum = 0L;
    /**
     * 总数量
     */
    @ApiModelProperty("总数量")
    private Long totalNum = 0L;

    /**
     * 当前产量
     */
    @ApiModelProperty("当前产量")
    private Long nowNum = 0L;
    /**
     * 投入数量
     */
    @ApiModelProperty("投入数量")
    private Long roughNum = 0L;
    /**
     * 不良品数量
     */
    @ApiModelProperty("不良品数量")
    private Long badnessNum = 0L;
    /**
     * 清洗时长
     */
    @ApiModelProperty("清洗时长")
    private String cleanTime = "0";


    @ApiModelProperty("历史稼动率")
    private BigDecimal historyOk = new BigDecimal(0);
    @ApiModelProperty("历史故障率")
    private BigDecimal historyNg= new BigDecimal(0);

    @ApiModelProperty("当日稼动率")
    private BigDecimal dayOk= new BigDecimal(0);
    @ApiModelProperty("当日故障率")
    private BigDecimal dayNg= new BigDecimal(0);

    @ApiModelProperty("稼动时间")
    private Integer ok =0;
    @ApiModelProperty("故障时间")
    private Integer ng=0;

    @ApiModelProperty(value = "待机状态:未待机/待机")
    private String a567;
    @ApiModelProperty("气体流量")
    private String gasFlow;
}
