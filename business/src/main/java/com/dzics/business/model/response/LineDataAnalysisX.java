package com.dzics.business.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 产线计划分析
 *
 * @author ZhangChengJun
 * Date 2021/2/22.
 * @since
 */
@Data
public class LineDataAnalysisX {

    /**
     * 达成率
     */
    @ApiModelProperty("达成率")
    private List<String> percentageComplete;
    /**
     * 产出率
     */
    @ApiModelProperty("产出率")
    private List<String> outputRate;
    /**
     * 合格率
     */
    @ApiModelProperty("合格率")
    private List<String> passRate;

    /**
     * 日期
     */
    @ApiModelProperty("日期X轴")
    private List<String> detectorTime;

}
