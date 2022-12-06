package com.dzics.common.model.response.down;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.annotation.write.style.OnceAbsoluteMerge;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Data;

import java.net.URL;

/**
 * @author ZhangChengJun
 * Date 2021/7/7.
 * @since
 */
@Data
@ContentRowHeight(170)
@HeadRowHeight(20)
public class ExpCpkImg {
    @ExcelProperty("检测记录图")
    @ColumnWidth(100)
    private URL urlLineObj;

    @ExcelProperty("cpk图")
    @ColumnWidth(100)
    private URL cpkUrlObj;
}
