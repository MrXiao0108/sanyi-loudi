package com.dzics.common.model.response.plan;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.TableField;
import com.dzics.common.enums.EquiTypeEnum;
import com.dzics.common.model.write.EquiTypeEnumStringConverter;
import com.dzics.common.model.write.PercentageStringConverter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author ZhangChengJun
 * Date 2021/6/23.
 * @since
 */
@Data
public class DayDailyReportExcel implements Serializable {

    @ApiModelProperty(value = "产线名称")
    @TableField("lineName")
    @ExcelProperty("产线名称")
    @ColumnWidth(25)
    private String linename;

    @ApiModelProperty(value = "设备类型")
    @TableField("equipmentType")
    @ExcelProperty(value = "设备类型", converter = EquiTypeEnumStringConverter.class)
    @ColumnWidth(25)
    private EquiTypeEnum equipmenttype;

    @ApiModelProperty(value = "设备编号")
    @TableField("equipmentCode")
    @ExcelProperty("设备编号")
    @ColumnWidth(25)
    private String equipmentcode;

    @ApiModelProperty(value = "设备名称")
    @TableField("equipmentName")
    @ExcelIgnore
    private String equipmentname;

    @ApiModelProperty(value = "班次")
    @TableField("workName")
    @ExcelProperty("班次")
    @ColumnWidth(10)
    private String workname;

    @ApiModelProperty(value = "班次时间")
    @TableField("time_range")
    @ExcelProperty("班次时间")
    @ColumnWidth(25)
    private String timeRange;


    @ApiModelProperty(value = "成品数量 =产出数量 = 当前产量")
    @TableField("nowNum")
    @ExcelProperty("生产数量")
    @ColumnWidth(13)
    private Long nownum;

    @ApiModelProperty(value = "毛坯数量")
    @TableField("roughNum")
    @ExcelProperty("毛坯数量")
    @ColumnWidth(13)
    private Long roughnum;

    @ApiModelProperty(value = "合格数量")
    @TableField("qualifiedNum")
    @ExcelProperty("合格数量")
    @ColumnWidth(13)
    private Long qualifiednum;

    @ApiModelProperty(value = "不良品数量")
    @TableField("badnessNum")
    @ExcelProperty("不良品数量")
    @ColumnWidth(15)
    private Long badnessnum;

    /**
     * 产出率
     */
    @ApiModelProperty(value = "产出率")
    @TableField("output_rate")
    @ExcelProperty(value = "产出率",converter = PercentageStringConverter.class)
    @ColumnWidth(10)
    private BigDecimal outputRate;

    /**
     * 合格率
     */
    @ApiModelProperty(value = "合格率")
    @TableField("pass_rate")
    @ExcelProperty(value = "合格率",converter = PercentageStringConverter.class)
    @ColumnWidth(10)
    private BigDecimal passRate;


    @ApiModelProperty(value = "日期")
    @TableField("workData")
    @ExcelProperty("日期")
    @ColumnWidth(15)
    private String workdata;
}
