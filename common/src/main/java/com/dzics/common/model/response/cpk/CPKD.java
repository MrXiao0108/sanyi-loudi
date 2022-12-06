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
public class CPKD {
    @ExcelProperty("PPM < LSL")
    private double PPMLessThanLSL;
    @ExcelProperty("PPM > USL")
    private double PPMGreaterThanUSL;
    @ExcelProperty("PPM Total")
    private double PPMGreaterTotal;
    @ExcelProperty("CA")
    private double ca;
}
