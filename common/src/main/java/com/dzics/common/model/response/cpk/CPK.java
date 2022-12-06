package com.dzics.common.model.response.cpk;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * @author ZhangChengJun
 * Date 2021/7/7.
 * @since
 */
@Data
@ColumnWidth(20)
public class CPK {
    @ExcelProperty("平均值")
    private double averageValue;
    @ExcelProperty("最大值")
    private double maxValue;
    @ExcelProperty("最小值")
    private double minValue;
    @ExcelProperty("+3sigma")
    private double sigma32;
    @ExcelProperty("-3sigma")
    private double sigma31;
    @ExcelProperty("STDEV")
    private double stdev;
    @ExcelProperty("CPK")
    private double cpk;
    @ExcelProperty("CP")
    private double cp;
    @ExcelProperty("CPL")
    private double cpl;
    @ExcelProperty("CPU")
    private double cpu;
    @ExcelProperty("PPM < LSL")
    private double PPMLessThanLSL;
    @ExcelProperty("PPM > USL")
    private double PPMGreaterThanUSL;
    @ExcelProperty("PPM Total")
    private double PPMGreaterTotal;
    @ExcelProperty("CA")
    private double ca;
    @ExcelIgnore
    private String info;
}
