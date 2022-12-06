package com.dzics.common.model.response.down;

import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * cpk分析导出
 *
 * @author ZhangChengJun
 * Date 2021/7/7.
 * @since
 */
@Data
public class ExpCpkChart {
    /**
     * 检测记录曲线图
     */
    @ApiModelProperty("检测记录曲线图")
    private String lineUrl;
    /**
     * cpk曲线图
     */
    @ApiModelProperty("cpk图")
    private String cpkUrl;

}
