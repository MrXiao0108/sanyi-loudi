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
public class CPKC {
    @ExcelProperty("CPK")
    private double cpk;
    @ExcelProperty("CP")
    private double cp;
    @ExcelProperty("CPL")
    private double cpl;
    @ExcelProperty("CPU")
    private double cpu;

}
