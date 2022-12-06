package com.dzics.common.model.response.device.maintain;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 保养记录
 *
 * @author ZhangChengJun
 * Date 2021/9/29.
 * @since
 */
@Data
public class MaintainDevice {
    @ApiModelProperty("归属产线")
    @ExcelProperty(value = "产线名称")
    private String lineName;

    @ApiModelProperty("设备名称")
    @ExcelProperty(value = "设备名称")
    private String equipmentName;

    @ApiModelProperty("设备编号")
    @ExcelProperty(value = "设备编号")
    private String equipmentNo;

    @ApiModelProperty("出厂日期")
    @ExcelProperty(value = "出厂日期")
    private String dateOfProduction;

    @ApiModelProperty("上次保养日期")
    @ExcelProperty(value = "上次保养日期")
    private String maintainDateBefore;

    @ApiModelProperty("下次保养日期")
    @ExcelProperty(value = "下次保养日期")
    private String maintainDateAfter;

    @ApiModelProperty("保养记录ID")
    @ExcelIgnore
    private String maintainId;

    @ApiModelProperty("状态")
    @ExcelProperty(value = "状态")
    private String states;

    @ApiModelProperty("设备ID")
    @ExcelIgnore
    private String deviceId;

    @ApiModelProperty("产线ID")
    @ExcelIgnore
    private String lineId;

    @ApiModelProperty("年检类型")
    @ExcelProperty(value = "年检时间")
    private String concatUnit;

    @ApiModelProperty(value = "年检类型 年 月 周", required = true)
    @ExcelIgnore
    private String unit;

    @ApiModelProperty(value = "单位倍数,一个单位的基础倍数。例如 单位是年，xx 年1 次，", required = true)
    @ExcelIgnore
    private Integer multiple;

    @ApiModelProperty(value = "次数", required = true)
    @ExcelIgnore
    private Integer frequency;


}
