package com.dzics.common.model.response;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EquipmentDataDo {
    @ExcelIgnore
    private Long id;

    @ApiModelProperty(value = "订单编号")
    @ExcelProperty("订单编号")
    private String orderNo;

//    @ApiModelProperty(value = "归属站点")
//    @ExcelProperty("归属站点")
//    private String departName;

    @ApiModelProperty(value = "产线名称")
    @ExcelProperty("产线名称")
    private String lineName;

    @ApiModelProperty(value = "设备序号")
    @ExcelProperty("机器人序号")
    private String equipmentNo;

    @ApiModelProperty(value = "设备编号")
    @ExcelProperty("机器人编号")
    private String equipmentCode;

    @ApiModelProperty(value = "设备名称")
    @ExcelProperty("机器人名称")
    private String equipmentName;

    @ApiModelProperty(value = "班次日期")
    @ExcelProperty("日期")
    private String workData;

    @ApiModelProperty("班次")
    @ExcelProperty("班次")
    private String workName;

    @ApiModelProperty("班次开始时间")
    @ExcelProperty("班次开始时间")
    private String workStartTime;

    @ApiModelProperty("班次结束时间")
    @ExcelProperty("班次结束时间")
    private String workStartEnd;

    @ApiModelProperty("生产数量(当前产量)")
    @ExcelProperty("生产数量")
    private String nowNum;

    @ApiModelProperty("毛坯数量")
    @ExcelProperty("毛坯数量")
    private String roughNum;

    @ApiModelProperty("合格数量")
    @ExcelProperty("合格数量")
    private String qualifiedNum;

    @ApiModelProperty("不良品数量")
    @ExcelProperty("不良品数量")
    private String badnessNum;

    @ApiModelProperty("设备清零状态")
    @ExcelIgnore
    private String clearCountStatus;


}
