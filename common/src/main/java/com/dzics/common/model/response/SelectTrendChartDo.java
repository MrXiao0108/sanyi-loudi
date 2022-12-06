package com.dzics.common.model.response;

import com.dzics.common.model.response.cpk.AnalysisDataVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SelectTrendChartDo implements Serializable {
    @ApiModelProperty("设备序号")
    private String equipmentNo;
    @ApiModelProperty("产品名称")
    private String productName;
    @ApiModelProperty("标准值")
    private BigDecimal standardValue;
    @ApiModelProperty("上限值")
    private BigDecimal upperValue;
    @ApiModelProperty("下线值")
    private BigDecimal lowerValue;

    @ApiModelProperty("检测项1名称")
    private String nameOne;
    @ApiModelProperty("检测项2名称")
    private String nameTwo;
    @ApiModelProperty("检测项1检测值")
    private List<BigDecimal> data;
    @ApiModelProperty("检测项2检测值")
    private List<BigDecimal> data2;
    /**
     * CPK
     */
    @ApiModelProperty("CPK")
    private AnalysisDataVO analysisDataVO;
    /**
     * 获取检测项字段的方法名称
     */
    private String fieldMethodName;
}

