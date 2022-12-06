package com.dzics.common.model.response.mom;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.dzics.common.model.write.UpWorkWrite;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class GetWorkingDetailsDo implements Serializable {
    @ExcelProperty("产线名称")
    @ApiModelProperty("产线名称")
    private String lineName;

    @ExcelProperty("工位")
    @ApiModelProperty("工位")
    private String stationName;

    /**
     * 生产节拍
     */
    @ExcelProperty("节拍")
    @ApiModelProperty("节拍")
    private String taktTime;


    @ExcelProperty("MOM订单号")
    @ApiModelProperty("惟一订单号")
    private String workpieceCode;


    @ExcelIgnore
    @ApiModelProperty("MOM订单号")
    private String productAliasProductionLine;


    @ExcelProperty("简码")
    @ApiModelProperty("简码")
    private String productAlias;

    /**
     * 工件二维码
     */
    @ExcelProperty("二维码")
    @ApiModelProperty("二维码")
    private String qrCode;



    @ExcelProperty("生产开始时间")
    @ApiModelProperty("生产开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date startTime;

    @ExcelProperty("生产完成时间")
    @ApiModelProperty("生产完成时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date completeTime;

    /**
     * 日期
     */
//    @ExcelProperty("日期")
//    private String workDate;


    @ExcelProperty("班次")
    private String workName;

    @ExcelIgnore
    @ApiModelProperty("报工开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date startRopertTime;

    @ExcelIgnore
    @ApiModelProperty("报工结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date completeRopertTime;

    @ExcelIgnore
    @ExcelProperty(value = "开始上报状态",converter = UpWorkWrite.class)
    @ApiModelProperty("开始上报状态  0未上报 ,1 已上报 ,3上报异常")
    private Integer startReportingStatus;

    @ExcelIgnore
    @ExcelProperty(value = "完成上报状态",converter = UpWorkWrite.class)
    @ApiModelProperty("完成上报状态  0未上报 ,1 已上报 ,3上报异常")
    private Integer completeReportingStatus;


    @ExcelIgnore
    private String stationId;
    /**
     * 生产任务订单ID
     */
    @ExcelIgnore
    private String proTaskId;

    @ExcelIgnore
    private String orderId;
    @ExcelIgnore
    private String lineId;




}
