package com.dzics.common.model.response.sany;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class SanyDeviceData {

    @ExcelProperty("三一设备ID")
    @ApiModelProperty(value = "第三方设备id")
    private Long deviceId;

    @ApiModelProperty("设备ID")
    @ExcelIgnore
    private String equipmentId;

    @ExcelProperty("公司代码")
    @ApiModelProperty(value = "公司代码")
    private String companyCode;
    @ExcelProperty("工厂代码")
    @ApiModelProperty(value = "工厂代码")
    private String factoryCode;
    @ExcelProperty("设备名称")
    @ApiModelProperty(value = "设备名称")
    private String deviceName;
    @ExcelProperty("设备类型")
    @ApiModelProperty(value = "设备类型（1.搬运机器人，2.焊接机器人，3.数控设备，4.清洗机，5.检测设备）")
    private Integer deviceType;

    @ExcelProperty("设备类型编号")
    @ApiModelProperty(value = "设备类型编号")
    private String deviceTypeCode;

    @ExcelProperty("资产编码")
    @ApiModelProperty(value = "资产编码")
    private String assetsEncoding;
    @ExcelProperty("系统型号")
    @ApiModelProperty(value = "系统型号")
    private String systemProductName;

    @ExcelProperty("序列号")
    @ApiModelProperty(value = "序列号")
    private String serNum;
    @ExcelProperty("软件版本")
    @ApiModelProperty(value = "软件版本")
    private String ncVer;
    @ExcelProperty("焊接类型")
    @ApiModelProperty(value = "焊接类型(1.连续焊，2.组对点）")
    private Integer solderingType;

    @ApiModelProperty("产线ID")
    private String lineId;
    /**
     * 订单号
     */
    @ExcelProperty("订单号编号")
    @ApiModelProperty("订单号编号")
    private String orderNo;
    @ExcelIgnore
    @ApiModelProperty("产线编号")
    private String lineNo;

    @ExcelProperty("产线名称")
    @ApiModelProperty("产线名称")
    private String lineName;

    @ExcelIgnore
    @ApiModelProperty("订单ID")
    private String orderId;

    @ExcelIgnore
    @ApiModelProperty(value = "主键")
    private String deviceKey;
}
