package com.dzics.common.model.response;

import com.dzics.common.enums.DeviceSocketSendStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 设备基础字段信息
 *
 * @author ZhangChengJun
 * Date 2021/3/2.
 * @since
 */
@Data
public class JCEquiment {
    @JsonIgnore
    private String lineNo;
    @JsonIgnore
    private String orderNo;
    @ApiModelProperty("设备名称")
    private String equipmentName;

    @ApiModelProperty("设备序号")
    private String equipmentNo;
    @ApiModelProperty("设备类型")
    private Integer equipmentType;

    @ApiModelProperty("停机次数")
    private Long downSum = 0L;
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

    @ApiModelProperty("位置")
    @JsonIgnore
    private String currentLocation = "";
    @ApiModelProperty("操作模式")
    private String operatorMode = "";
    @ApiModelProperty("连接状态")
    private String connectState = "";
    @ApiModelProperty("运行状态")
    private String runStatus = "";
    @ApiModelProperty("急停状态")
    private String emergencyStatus = "";
    @ApiModelProperty("告警状态")
    private String alarmStatus = "";

    private String x;
    private String y;
    private String z;

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
}
