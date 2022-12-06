package com.dzics.common.model.response.down;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * 样本数
 *
 * @author ZhangChengJun
 * Date 2021/7/7.
 * @since
 */
@Data
public class ExpCpkOne {
    /**
     * 总样本数
     */
    @ExcelProperty("总样本数")
    private Integer numberAll;

    /**
     * 标准值
     */
    @ExcelProperty("标准值")
    private Double standValue;
    /**
     *下限值
     */
    @ExcelProperty("下限值")
    private Double lowerLimitValue;
    /**
     *上限值
     */
    @ExcelProperty("上限值")
    private Double upperLimitValue;

}
