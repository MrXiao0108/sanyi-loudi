package com.dzics.common.model.response.cpk;

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
public class CPKA {
    @ExcelProperty("平均值")
    private double averageValue;
    @ExcelProperty("最大值")
    private double maxValue;
    @ExcelProperty("最小值")
    private double minValue;

}
