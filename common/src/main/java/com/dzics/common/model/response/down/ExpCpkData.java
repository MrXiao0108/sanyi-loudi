package com.dzics.common.model.response.down;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * cpk分析导出
 *
 * @author ZhangChengJun
 * Date 2021/7/7.
 * @since
 */
@Data
public class ExpCpkData {
    @ExcelProperty("检测值")
    private double number;
}
