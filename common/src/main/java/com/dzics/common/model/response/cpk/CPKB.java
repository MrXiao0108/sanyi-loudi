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
public class CPKB {
    @ExcelProperty("+3sigma")
    private double sigma32;
    @ExcelProperty("-3sigma")
    private double sigma31;
    @ExcelProperty("STDEV")
    private double stdev;
}
